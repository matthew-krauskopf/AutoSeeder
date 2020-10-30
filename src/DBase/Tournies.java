package DBase;

import java.sql.*;
import java.io.IOException;

public class Tournies {

    public static String table_name = "Tournies";
    private static Statement stmt;

    public Tournies(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "   ID int, " +
                        "   Name VARCHAR(255), " +
                        "   Entrants int, " +
                        "   PRIMARY KEY(ID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void recordTourney(int id, String name, int num_entrants) {
        try {
            String sql = String.format("INSERT INTO %s (ID, Name, Entrants) VALUES (%d, '%s', %d);", table_name, id, name, num_entrants);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int checkBracketDataNew(int ID) {
        // Ensure bracket has not been entered into db before
        try {
            String sql = String.format("SELECT 1 FROM %s where ID = %d;", table_name, ID);
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
}
