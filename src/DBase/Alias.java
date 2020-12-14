package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Alias {

    public static String database_name;
    public static String table_name = "Alias";
    private static Statement stmt;

    public Alias(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                                    "   Alias varchar(255), " +
                                    "   MainPlayer varchar(255),  " +
                                    "   PRIMARY KEY(Alias) );",
                                        database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public Boolean checkAlias(String player) {
        try {
            String sql = String.format("SELECT 1 FROM %s.%s WHERE Alias = '%s';",
                                        database_name, table_name, player);
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

    public String [] getPlayerAliases(String player) {
        try {
            String sql = String.format("select Alias from %s.%s where MainPlayer='%s';",
                                        database_name, table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int amt = 0;
            if (r.last()) {
                amt = r.getRow();
            }
            // Allocate opponent array
            String [] aliases = new String[amt];
            // Reset back to first element
            r.beforeFirst();
            int i = 0;
            while (r.next()) {
                aliases[i++] = r.getString(1);
            }
            return aliases;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new String [0];
    }

    public String getAlias(String player) {
        // Player not found in database: Check for known alias
        try {
            String sql = String.format("SELECT MainPlayer FROM %s.%s WHERE Alias = '%s';",
                                        database_name, table_name, player);
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
        }
        return "";
    }

    public void updateAlias(String old_name, String new_name) {
        try {
            String sql = String.format("UPDATE %s.%s SET MainPlayer = '%s' WHERE MainPlayer = '%s';",
                                        database_name, table_name, new_name, old_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addAlias(String alias, String player) {
        try {
            String sql = String.format("INSERT INTO %s.%s (Alias, MainPlayer) VALUES ('%s','%s');",
                                        database_name, table_name, alias, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteAlias(String alias, String player) {
        try {
            String sql = String.format("DELETE FROM %s.%s WHERE MainPlayer='%s' and Alias='%s';",
                                        database_name, table_name, player, alias);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
