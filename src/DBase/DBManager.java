package DBase;

import java.sql.*;
import java.util.Arrays;

import MyUtils.Match;
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

    public DBManager(String user, String pass) {
        USER = user;
        PASS = pass;
        conn = get_conn();
        stmt = c_state(conn);
        players_table = new Players(stmt);
        history_table = new History(stmt);
        tourneyID_table = new Tournies(stmt);
        alias_table = new Alias(stmt);
        create_db();
    }

    private Connection get_conn() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL+":"+PORT, USER, PASS);
            return conn;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Statement c_state(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("USE BRACKETRESULTS;");
            return stmt;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
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

    public void create_db() {
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

    public String [] get_unknown_entrants(String [] players) {
        String [] unknown_entrants = new String[players.length];
        int unknown_count = 0;
        for (int i = 0; i < players.length; i++) {
            // Initialize to -1
            unknown_entrants[i] = "";
            String player = sanitize(players[i]);
            // First, check if player has an alias
            if (!alias_table.check_alias(player)) {
                // Flag to ask user for actual tag of player
                unknown_entrants[unknown_count++] = players[i];
            }
            // Else: No action needed
        }
        return Arrays.copyOf(unknown_entrants, unknown_count);
    }

    public void add_players(String [] players) {
        // Adds players to database if new
        for (int i = 0; i < players.length; i++) {
            String player = sanitize(players[i]);
            // If player is unknown, add name to database of players
            if (!alias_table.check_alias(player)) {
                // Each main name will have itself as alias
                alias_table.add_alias(player, player);
                players_table.add_player(player);
            }
        }
    }

    public void add_history(Match [] results) {
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
            if (history_table.check_history(winner, loser) == 0) {
                // Winner data entry
                history_table.add_history(winner, loser, date);
                history_table.add_history(loser, winner, date);
            }
            // Add new results
            history_table.update_stats(winner, loser, 1);
            history_table.update_stats(loser, winner, 0);
            players_table.update_stats(winner, 1);
            players_table.update_stats(loser, 0);

            // Update ELO scores
            update_scores(stmt, winner, loser);
        }
    // Mark tourney as recorded
    tourneyID_table.record_id(results[0].tourney_ID);
    }

    public String [][] get_rankings() {
        int n_players = players_table.get_number_players();
        return players_table.get_rankings(n_players);
    }

    public void update_scores(Statement stmt, String winner, String loser) {
        // Get data
        int [] w_data = players_table.get_elo_data(winner);
        int [] l_data = players_table.get_elo_data(loser);

        // Calculate new elo scores
        int w_new_score = ((w_data[0] * (w_data[1]-1)) + l_data[0] + 400) / w_data[1];
        int l_new_score = ((l_data[0] * (l_data[1]-1)) + w_data[0] - 400) / l_data[1];

        // Update scores
        players_table.update_elo(winner, w_new_score);
        players_table.update_elo(loser, l_new_score);
    }

    public int [] get_scores(String [] entrants) {
        // Allocate scores
        int [] scores = new int[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            scores[i] = players_table.get_score(sanitize(entrants[i]));
        }
        return scores;
    }

    // Used to allow privatization of database classes
    public int check_bracket_data_new(int id) {
        return tourneyID_table.check_bracket_data_new(id);
    }

    public void add_alias(String alias, String player) {
        alias_table.add_alias(alias, player);
        // Check if added alias is an existing player. If not, also add that player
        if (!players_table.check_player(player)) {
            players_table.add_player(player);
        }
    }
}
