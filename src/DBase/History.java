package DBase;

import java.sql.*;
import java.io.IOException;

public class History {

    private static String table_name = "History";
    private static Statement stmt;

    public History(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
            "   Player varchar(255), " +
            "   Opponent varchar(255), " +
            "   Player_Wins int, " +
            "   Sets int, " +
            "   Last_played Date, " +
            "   PRIMARY KEY(Player, Opponent));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int check_history(String winner, String loser) {
        try {
            String sql = String.format("SELECT 1 FROM %s where Player = '%s' AND Opponent = '%s';",
                                        table_name, winner, loser);
            ResultSet r = stmt.executeQuery(sql);
            if (!r.next()) {
                // No history
                return 0;
            }
            else {
                // Existing history
                return 1;
            }
        // Error catch
        } catch (SQLException ex) {
            return -1;
        }
    }

    public void add_history(String winner, String loser, String date) {
        try {
            String sql = "";
            sql = String.format("INSERT INTO %s (Player, Opponent, Player_Wins, Sets, Last_played) VALUES ('%s', '%s', 0, 0, '%s');",
                                table_name, winner, loser, date);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update_stats(String winner, String loser, int wins) {
        try {
            String sql = String.format("UPDATE %s SET Player_Wins = Player_Wins + %d, Sets = Sets + 1 WHERE " +
                                      "PLAYER = '%s' AND OPPONENT = '%s';", table_name, wins, winner, loser);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
