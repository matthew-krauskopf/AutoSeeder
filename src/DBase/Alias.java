package DBase;

import java.sql.*;
import java.io.IOException;

public class Alias {

    private static String table_name = "Alias";
    private static Statement stmt;

    public Alias(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
            "   Alias varchar(255), " +
            "   Main_Player varchar(255),  " +
            "   PRIMARY KEY(Alias) );", table_name);         
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String get_alias(String player) {
        // Player not found in database: Check for known alias
        try {
            String sql = String.format("SELECT Main_Player FROM %s WHERE Alias = '%s';", table_name, player);        
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                // Found alias: return real name
                return r.getString(1);
            }
            else {
                // No alias: new name
                return "";
            }
        // Error catch
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void add_alias(String alias, String player) {
        try {
            String sql = String.format("INSERT INTO %s (Alias, Main_Player) VALUES ('%s','%s');",
                                alias, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}