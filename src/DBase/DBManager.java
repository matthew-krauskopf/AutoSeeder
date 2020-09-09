package DBase;

import java.sql.*;
import MyUtils.Match;
import java.io.IOException;

public class DBManager {
    // Driver name and database url
    static final String JDBC_Driver = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost";

    // DBase connections
    static Connection conn;
    static Statement stmt;

    // Database credentials 
    static final String USER = "root";
    static final String PASS = "";

    // Tables
    public static Players players_table;
    public static History history_table;
    public static Tournies tourneyID_table;

    public DBManager() {
        conn = get_conn();
        stmt = c_state(conn);
        players_table = new Players(stmt);
        history_table = new History(stmt);
        tourneyID_table = new Tournies(stmt);
    }


    public static Connection get_conn() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASS);
            return conn;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Statement c_state(Connection conn) {
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
            Runtime.getRuntime().exec("MySQL\\bin\\mysqladmin.exe -u root shutdown");
            System.out.println("Shutdown complete");
        } catch (Exception e) {
            System.out.println("Error! Shutdown failed. mysqld.exe zombie processes likely");
        }
    }

    public static void create_db() {
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static String sanitize(String sql) {
        return sql.replaceAll("[/\\ _%$&`~;#@'*!<>?\"]|(DROP|DELETE|SELECT|INSERT|UPDATE|WHERE).*", "");
    }

    public static void add_players(String [] players) {
        // Adds players to database if new
        for (int i = 0; i < players.length; i++) {
            players_table.add_player(sanitize(players[i]));
        }
    }

    public static void add_history(Match [] results) {
        if (tourneyID_table.check_bracket_data_new(results[0].tourney_ID) == 0) {
            System.out.println("Tournament results already exist, skipping..");
            return;
        }
        // New bracket entry: update records
        else {
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
                update_scores(stmt, sanitize(results[i].winner), sanitize(results[i].loser));
            }
        // Mark tourney as recorded
        tourneyID_table.record_id(results[0].tourney_ID);
        }
    }

    /*public static String [][] get_rankings() {
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);

            String sql = "";
            sql = "SELECT COUNT(PLAYER) FROM PLAYERS;";
            ResultSet r = stmt.executeQuery(sql);
            int n_players = 0;
            if (r.next()) {
                n_players = r.getInt(1);
            }
            String [][] player_info = new String[n_players][5];
            sql =  "SELECT * FROM PLAYERS ORDER BY SCORE DESC;";
            r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                // Set chart
                player_info[i][0] = Integer.toString(i+1);
                player_info[i][1] = r.getString(1);
                player_info[i][2] = r.getString(2);
                player_info[i][3] = get_losses(r.getString(2),r.getString(3));
                player_info[i][4] = r.getString(4);
                i++;
            }
            return player_info;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0];
        }
    }*/

    public static String get_losses(String n1, String n2) {
        return Integer.toString(Integer.parseInt(n2) - Integer.parseInt(n1));
    }

    public static void update_scores(Statement stmt, String winner, String loser) {
        // Get data
        int [] w_data = players_table.get_elo(winner);
        int [] l_data = players_table.get_elo(loser);

        // Calculate new elo scores
        int w_new_score = ((w_data[0] * (w_data[1]-1)) + l_data[0] + 400) / w_data[1];
        int l_new_score = ((l_data[0] * (l_data[1]-1)) + w_data[0] - 400) / l_data[1];
        
        // Update scores
        players_table.update_elo(winner, w_new_score);
        players_table.update_elo(loser, l_new_score);
    }

    public static int [] grab_scores(String [] entrants) {
        // Allocate scores
        int [] scores = new int[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            scores[i] = players_table.get_score(sanitize(entrants[i]));
        }
        return scores;
    }
}