package DBase;

import java.sql.*;
import java.io.IOException;

public class Exceptions {

    private static String table_name = "Exceptions";
    private static Statement stmt;

    public Exceptions(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "   PlayerID int," +
                        "   OpponentID int, " +
                        "   PRIMARY KEY (PlayerID, OpponentID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addException(int player_id, int opponent_id) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s (PlayerID, OpponentID) VALUES (%d, %d);", table_name, player_id, opponent_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumExceptions(int player_id) {
        try {
            String sql = String.format("select COUNT(OpponentID) from %s where PlayerID=%d ", table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String [] getExceptions(int player_id, int num_exceptions) {
        String [] exceptions = new String[num_exceptions];
        try {
            String sql =  String.format("SELECT y.Player " +
                                        "FROM %s x INNER JOIN %s y ON x.OpponentID=y.ID " +
                                        "WHERE x.PlayerID=%d ORDER BY SCORE DESC;",
                                        table_name, IDs.table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                // Record exceptions
                exceptions[i++] = r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return exceptions;
    }

    public void deleteException(int player_id, int opponent_id) {
        try {
            // Add player
            String sql = String.format("DELETE FROM %s WHERE PlayerID=%d, OpponentID=%d;", table_name, player_id, opponent_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}