package DBase;

import java.sql.*;

public class DBManager {
    // Driver name and database url
    static final String JDBC_Driver = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost";

    // Database credentials 
    static final String USER = "myuser";
    static final String PASS = "pass";

    public static void create_db() {
        try (
            // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306", USER, PASS);
            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
        ) 
        {
            stmt.execute("CREATE DATABASE IF NOT EXISTS BracketResults;");
            stmt.execute("USE BracketResults;");
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "   Player varchar(255) ," +
                        "   Score int, " +
                        "   PRIMARY KEY (Player));");            
            stmt.execute("INSERT INTO PLAYERS (Player, Score) VALUES ('Sakuto', 500);");
 
        } catch(SQLException ex) {
             ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
    }
}