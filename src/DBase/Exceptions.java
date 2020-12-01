package DBase;

import java.sql.*;
import java.io.IOException;

public class Exceptions {

    public static String database_name;
    private static String table_name = "Exceptions";
    private static Statement stmt;

    public Exceptions(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   PlayerID int," +
                        "   OpponentID int, " +
                        "   PRIMARY KEY (PlayerID, OpponentID));",
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public void addException(int player_id, int opponent_id) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (PlayerID, OpponentID) VALUES (%d, %d);",
                                        database_name, table_name, player_id, opponent_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String [] getExceptions(int player_id) {
        try {
            String sql =  String.format("SELECT y.Player " +
                                        "FROM %s.%s x INNER JOIN %s.%s y ON x.OpponentID=y.ID " +
                                        "WHERE x.PlayerID=%d;",
                                         database_name, table_name, IDs.database_name, IDs.table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int amt = 0;
            if (r.last()) {
                amt = r.getRow();
            }
            // Allocate opponent array
            String [] exceptions = new String[amt];
            // Reset back to first element
            r.beforeFirst();
            int i = 0;
            while (r.next()) {
                // Record exceptions
                exceptions[i++] = r.getString(1);
            }
            return exceptions;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new String [0];
    }

    public void deleteException(int player_id, int opponent_id) {
        try {
            // Add player
            String sql = String.format("DELETE FROM %s.%s WHERE PlayerID=%d AND OpponentID=%d;",
                                        database_name, table_name, player_id, opponent_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
