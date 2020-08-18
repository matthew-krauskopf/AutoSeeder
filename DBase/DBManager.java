package DBase;

import java.sql.*;

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
            // Create Database
            stmt.execute("CREATE DATABASE IF NOT EXISTS BracketResults;");
            // Use Database
            stmt.execute("USE BracketResults;");
            // Create players table
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "   Player varchar(255)," +
                        "   Score int, " +
                        "   PRIMARY KEY (Player));");            
            // Create matchup history table
            stmt.execute("CREATE TABLE IF NOT EXISTS history (" +
                        "   Player varchar(255), " +
                        "   Opponent varchar(255), " +
                        "   Player_Wins int, " +
                        "   Sets_played int, " +
                        "   Last_played Date, " +
                        "   PRIMARY KEY(Player, Opponent));");
            // Create bracket history table
            stmt.execute("CREATE TABLE IF NOT EXISTS results (" +
                        "  Player varchar(255), " +
                        "  Day Date, " +
                        "  Place int, " +
                        "  Entrants int, " +
                        "  Score int, " +
                        "  PRIMARY KEY(Player));");
            // Close connections
        } catch (SQLException ex) {
            ex.printStackTrace();
        } // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
    }

    public static void add_players(String [] players) {
        // Adds players to database if new
        try {
            Connection conn = get_conn();
            Statement stmt = c_state(conn);

            // Add player
            // TODO add sanitizing
            stmt.execute("USE BracketResults;");
            for (int i = 0; i < players.length; i++) {
                // Check if player record already exists
                ResultSet r = stmt.executeQuery("SELECT 1 FROM Players where Player = '" + players[i] + "';");
                if (!r.next()) {
                    stmt.execute("INSERT INTO Players (Player) VALUES ('" + players[i] + "');");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}