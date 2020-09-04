package DBase;

import java.sql.*;
import MyUtils.Match;

public class DBManager {
    // Driver name and database url
    static final String JDBC_Driver = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost";

    // Database credentials 
    static final String USER = "myuser";
    static final String PASS = "pass";

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

    public static void create_db() {
        // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);
            stmt.execute("DROP DATABASE BracketResults;");
            // Create Database
            stmt.execute("CREATE DATABASE IF NOT EXISTS BracketResults;");
            // Use Database
            stmt.execute("USE BracketResults;");
            // Create players table
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "   Player varchar(255)," +
                        "   Wins int, " +
                        "   Sets int, " +
                        "   Score int, " +
                        "   PRIMARY KEY (Player));");            
            // Create matchup history table
            stmt.execute("CREATE TABLE IF NOT EXISTS history (" +
                        "   Player varchar(255), " +
                        "   Opponent varchar(255), " +
                        "   Player_Wins int, " +
                        "   Sets int, " +
                        "   Last_played Date, " +
                        "   PRIMARY KEY(Player, Opponent));");
            // Create bracket history table
            stmt.execute("CREATE TABLE IF NOT EXISTS results (" +
                        "   Player varchar(255), " +
                        "   Day Date, " +
                        "   Place int, " +
                        "   Entrants int, " +
                        "   PRIMARY KEY(Player));");
            stmt.execute("CREATE TABLE IF NOT EXISTS tournies (" +
                        "   ID int, " +
                        "   PRIMARY KEY(ID));");
            // Close connections
        } catch (SQLException ex) {
            ex.printStackTrace();
        } // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
    }

    public static String sanitize(String sql) {
        return sql.replaceAll("[/\\ _%$&`~;#@'*!<>?\"]|(DROP|DELETE|SELECT|INSERT|UPDATE|WHERE).*", "");
    }

    public static int check_bracket_data_new(int ID) {
        // Ensure bracket has not been entered into db before
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);
            String sql = String.format("SELECT 1 FROM tournies where ID = %d;", ID);
            ResultSet r = stmt.executeQuery(sql);
            // Data already exists
            if (r.next()) {
                return 0;
            }
            // New data: proceed with import
            else {
                return 1;
            }
        // Error handling
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static void add_players(String [] players) {
        // Adds players to database if new
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);

            // Add player
            String sql = "";
            for (int i = 0; i < players.length; i++) {
                // Check if player record already exists
                sql = String.format("SELECT 1 FROM Players where Player = '%s';", sanitize(players[i]));
                ResultSet r = stmt.executeQuery(sql);
                if (!r.next()) {
                    sql = String.format("INSERT INTO Players (Player, Wins, Sets, Score) VALUES ('%s', 0, 0, 1200);", sanitize(players[i]));
                    stmt.execute(sql);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void add_history(Match [] results) {
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);

            String sql = "";

            // Ensure bracket has not been entered into db before
            sql = String.format("SELECT 1 FROM tournies where ID = %d;", results[0].tourney_ID);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
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
                    // Check for matchup history
                    sql = String.format("SELECT 1 FROM history where Player = '%s' AND Opponent = '%s';",
                                                sanitize(results[i].winner), sanitize(results[i].loser));
                    r = stmt.executeQuery(sql);
                    // No history
                    if (!r.next()) {
                        // Winner data entry
                        sql = String.format("INSERT INTO history (Player, Opponent, Player_Wins, Sets, Last_played) VALUES ('%s', '%s', 0, 0, '%s');",
                                                    sanitize(results[i].winner), sanitize(results[i].loser), results[i].date);
                        stmt.execute(sql);
                        // Loser data entry
                        sql = String.format("INSERT INTO history (Player, Opponent, Player_Wins, Sets, Last_played) VALUES ('%s', '%s', 0, 0, '%s');",
                                                    sanitize(results[i].loser), sanitize(results[i].winner), results[i].date);
                        stmt.execute(sql);
                    }
                    // Add new results
                    // Winner result
                    sql = String.format("UPDATE history SET Player_Wins = Player_Wins + 1, Sets = Sets + 1 WHERE " +
                                        "PLAYER = '%s' AND OPPONENT = '%s';", sanitize(results[i].winner), sanitize(results[i].loser));
                    stmt.execute(sql);
                    sql = String.format("UPDATE PLAYERS SET Wins = Wins + 1, Sets = Sets + 1 WHERE " +
                                        "PLAYER = '%s';", sanitize(results[i].winner));
                    stmt.execute(sql);
                    // Loser result
                    sql = String.format("UPDATE history SET Sets = Sets + 1 WHERE " +
                                        "PLAYER = '%s' AND OPPONENT = '%s';", sanitize(results[i].loser), sanitize(results[i].winner));
                    stmt.execute(sql);
                    sql = String.format("UPDATE PLAYERS SET Sets = Sets + 1 WHERE " +
                                        "PLAYER = '%s';", sanitize(results[i].loser));
                    stmt.execute(sql);
                    // Update ELO scores
                    update_scores(stmt, sanitize(results[i].winner), sanitize(results[i].loser));
                }
            // Mark tourney as recorded
            sql = String.format("INSERT INTO tournies (ID) VALUES (%d);", results[0].tourney_ID);
            stmt.execute(sql);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void add_results() {

    }

    public static void update_scores(Statement stmt, String winner, String loser) {
        String sql1 = String.format("SELECT SCORE, Sets FROM PLAYERS WHERE PLAYER = '%s';", winner);
        String sql2 = String.format("SELECT SCORE, Sets FROM PLAYERS WHERE PLAYER = '%s';", loser);
        try {
            // Get winner data
            ResultSet w_data = stmt.executeQuery(sql1);
            w_data.next();
            int w_cur_score = w_data.getInt(1), w_sets = w_data.getInt(2);
            // Get loser data
            ResultSet l_data = stmt.executeQuery(sql2);
            l_data.next();
            int l_cur_score = l_data.getInt(1), l_sets = l_data.getInt(2);
            // Calculate new elo scores
            int w_new_score = ((w_cur_score * (w_sets-1)) + l_cur_score + 400) / w_sets;
            int l_new_score = ((l_cur_score * (l_sets-1)) + w_cur_score - 400) / l_sets;
            // Format sql strings
            sql1 = String.format("UPDATE PLAYERS SET SCORE = %d WHERE " +
                                    "PLAYER = '%s';", w_new_score, winner);
            sql2 = String.format("UPDATE PLAYERS SET SCORE = %d WHERE " +
                                    "PLAYER = '%s';", l_new_score, loser);
            // Execute update queries
            stmt.execute(sql1);
            stmt.execute(sql2);

        } catch (SQLException ex) {
            System.out.println("    " + sql1 + " " + sql2);
            ex.printStackTrace();
        }
    }

    public static int [] grab_scores(String [] entrants) {
        try {
            // Get connection
            Connection conn = get_conn();
            Statement stmt = c_state(conn);
            // Allocate rankings
            int [] rankings = new int[entrants.length];
            for (int i = 0; i < entrants.length; i++) {
                // Create query
                String sql = String.format("SELECT SCORE FROM PLAYERS WHERE PLAYER = '%s';", sanitize(entrants[i]));
                // Check if player has entered before. If not, score of 0
                ResultSet r = stmt.executeQuery(sql);
                if (r.next()) {
                    rankings[i] = r.getInt(1);
                }
                else {
                    rankings[i] = 0;
                }
            }
            return rankings;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}