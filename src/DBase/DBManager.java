package DBase;

import java.sql.*;
import java.util.Arrays;

import MyUtils.Match;
import MyUtils.MatchUp;
import java.io.IOException;

public class DBManager {
    // Driver name and database url
    //static final String JDBC_Driver = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost";
    static final String PORT = "3306";

    // DBase connections
    static Connection conn;
    static Statement stmt;

    // Tables
    static Players players_table;
    static History history_table;
    static Tournies tourneyID_table;
    static Alias alias_table;

    static String USER;
    static String PASS;

    public static Boolean bootUp() {
        try {
            USER = Credentials.USER;
            PASS = Credentials.PASS;
            setConn();
            setStmt();
            selectDbase();
            players_table = new Players(stmt);
            history_table = new History(stmt);
            tourneyID_table = new Tournies(stmt);
            alias_table = new Alias(stmt);
            //createDbase();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void selectDbase() {
        try {
            stmt.execute("CREATE DATABASE IF NOT EXISTS BracketResults;");
            stmt.execute("USE bracketresults;");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void setConn() {
        try {
            conn = DriverManager.getConnection(DB_URL+":"+PORT, USER, PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void setStmt() {
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void shutdown() {
        try {
            stmt.close();
            conn.close();
            Runtime.getRuntime().exec(String.format("MySQL\\bin\\mysqladmin.exe -u %s shutdown", USER));
            System.out.println("Shutdown complete");
        } catch (Exception e) {
            System.out.println("Error! Shutdown failed. mysqld.exe zombie processes likely");
        }
    }

    public static void createDbase() {
        try {
            stmt.execute("DROP DATABASE BracketResults;");
            // Create Database
            stmt.execute("CREATE DATABASE IF NOT EXISTS BracketResults;");
            // Use Database
            stmt.execute("USE BracketResults;");
            // Create tables
            players_table.create();
            history_table.create();
            tourneyID_table.create();
            alias_table.create();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static String sanitize(String sql) {
        return sql.replaceAll("[/\\ _%$&`~;#@'*!<>?,\"]|(DROP|DELETE|SELECT|INSERT|UPDATE|WHERE).*", "");
    }

    public String [] getUnknownEntrants(String [] players) {
        String [] unknown_entrants = new String[players.length];
        int unknown_count = 0;
        for (int i = 0; i < players.length; i++) {
            // Initialize to -1
            unknown_entrants[i] = "";
            String player = sanitize(players[i]);
            // First, check if player has an alias
            if (!alias_table.checkAlias(player)) {
                // Flag to ask user for actual tag of player
                unknown_entrants[unknown_count++] = players[i];
            }
            // Else: No action needed
        }
        return Arrays.copyOf(unknown_entrants, unknown_count);
    }

    public void addPlayers(String [] players) {
        // Adds players to database if new
        for (int i = 0; i < players.length; i++) {
            String player = sanitize(players[i]);
            // If player is unknown, add name to database of players
            if (!alias_table.checkAlias(player)) {
                // Each main name will have itself as alias
                alias_table.addAlias(player, player);
                players_table.addPlayer(player);
            }
        }
    }

    public void addHistory(Match [] results) {
        // Add results
        for (int i = 0; i < results.length; i++) {
            System.out.println("    Adding match " + (i+1));
            // Check if match was a forfeit
            if (results[i].winner == "" && results[i].loser == "") {
                continue;
            }

            // Make my life easier
            String winner = sanitize(results[i].winner);
            String loser = sanitize(results[i].loser);
            String date = results[i].date;

            // Check for matchup history
            // No history
            if (history_table.checkHistory(winner, loser) == 0) {
                // Winner data entry
                history_table.addHistory(winner, loser, date);
                history_table.addHistory(loser, winner, date);
            }
            // Add new results
            history_table.updateStats(winner, loser, 1);
            history_table.updateStats(loser, winner, 0);
            players_table.updateStats(winner, 1);
            players_table.updateStats(loser, 0);

            // Update ELO scores
            updateScores(stmt, winner, loser);
        }
    // Mark tourney as recorded
    tourneyID_table.recordID(results[0].tourney_ID);
    }

    public String [][] getRankings() {
        int n_players = players_table.getNumberPlayers();
        return players_table.getRankings(n_players);
    }

    public void updateScores(Statement stmt, String winner, String loser) {
        // Get data
        int [] w_data = players_table.getEloData(winner);
        int [] l_data = players_table.getEloData(loser);

        // Calculate new elo scores
        int w_new_score = ((w_data[0] * (w_data[1]-1)) + l_data[0] + 400) / w_data[1];
        int l_new_score = ((l_data[0] * (l_data[1]-1)) + w_data[0] - 400) / l_data[1];

        // Update scores
        players_table.updateElo(winner, w_new_score);
        players_table.updateElo(loser, l_new_score);
    }

    public int [] getScores(String [] entrants) {
        // Allocate scores
        int [] scores = new int[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            scores[i] = players_table.getScore(sanitize(entrants[i]));
        }
        return scores;
    }

    // Used to allow privatization of database classes
    public int checkBracketDataNew(int id) {
        return tourneyID_table.checkBracketDataNew(id);
    }

    public void addAlias(String alias, String player) {
        alias_table.addAlias(alias, player);
        // Check if added alias is an existing player. If not, also add that player
        if (!players_table.checkPlayer(player)) {
            players_table.addPlayer(player);
        }
    }

    public MatchUp [] getRecentMatchups(String [] entrants) {
        MatchUp [] matchups = new MatchUp[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            String player = sanitize(entrants[i]);
            String [] last_dates = history_table.getLastDates(player, 2);
            String [] opponents = history_table.getOpponents(player, last_dates);
            matchups[i] = new MatchUp(player, opponents);
        }
        return matchups;
    }
}
