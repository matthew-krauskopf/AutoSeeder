package DBase;

import java.sql.*;
import java.io.IOException;

public class Alias {

    public static String table_name = "Alias";
    private static Statement stmt;

    public Alias(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
            "   Alias varchar(255), " +
            "   MainPlayer varchar(255),  " +
            "   PRIMARY KEY(Alias) );", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Boolean checkAlias(String player) {
        try {
            String sql = String.format("SELECT 1 FROM %s WHERE Alias = '%s';", table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                // Found alias: return real name
                return true;
            }
        // Error catch
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public int getNumAliases(String player) {
        try {
            String sql = String.format("select COUNT(Alias) from %s where MainPlayer='%s';",
                                       table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String [] getPlayerAliases(String player, int num) {
        String [] aliases = new String[num];
        try {
            String sql = String.format("select Alias from %s where MainPlayer='%s';",
                                        table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                aliases[i++] = r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return aliases;
    }

    public String getAlias(String player) {
        // Player not found in database: Check for known alias
        try {
            String sql = String.format("SELECT MainPlayer FROM %s WHERE Alias = '%s';", table_name, player);
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

    public void updateAlias(String old_name, String new_name) {
        try {
            String sql = String.format("UPDATE %s SET MainPlayer = '%s' WHERE " +
                                        "MainPlayer = '%s';", table_name, new_name, old_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addAlias(String alias, String player) {
        try {
            String sql = String.format("INSERT INTO %s (Alias, MainPlayer) VALUES ('%s','%s');",
                                table_name, alias, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
