package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IDs {

    public static String database_name;
    public static String table_name = "IDs";
    private static Statement stmt;

    public IDs(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   ID int not null auto_increment," +
                        "   Player VARCHAR(255), " +
                        "   PRIMARY KEY (ID));",
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public boolean checkPlayer(String player) {
        // Adds player to database if new
        try {
            // Add player
            String sql = "";
            // Check if player record already exists
            sql = String.format("SELECT 1 FROM %s.%s where Player = '%s';",
                                 database_name, table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                // No player found: check for alias
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Boolean addPlayer(String player) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (Player) VALUES ('%s');",
                                        database_name, table_name, player);
            stmt.execute(sql);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void updatePlayerName(String old_name, String new_name) {
        try {
            String sql = String.format("UPDATE %s.%s SET Player = '%s' WHERE Player = '%s';",
                                        database_name, table_name, new_name, old_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getPlayerName(int id) {
        try {
            String sql = String.format("SELECT Player FROM %s.%s WHERE ID = %d;",
                                        database_name, table_name, id);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public int getID(String player) {
        try {
            String sql = String.format("SELECT ID FROM %s.%s WHERE Player = '%s';",
                                        database_name, table_name, player);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}