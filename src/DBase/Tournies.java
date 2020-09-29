package DBase;

import java.sql.*;
import java.io.IOException;

public class Tournies {

    private static String table_name = "Tournies";
    private static Statement stmt;

    public Tournies(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "   ID int, " +
                        "   PRIMARY KEY(ID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void record_id(int id) {
        try {
            String sql = String.format("INSERT INTO %s (ID) VALUES (%d);", table_name, id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int check_bracket_data_new(int ID) {
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
