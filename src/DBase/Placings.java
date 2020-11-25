package DBase;

import java.sql.*;
import java.io.IOException;

public class Placings {

    public static String table_name = "Placings";
    private static Statement stmt;

    public Placings(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "   PlayerID int, " +
                        "   Place int, " +
                        "   Tourney_ID int, " +
                        "   PRIMARY KEY (PlayerID, Tourney_ID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addPlacing(int player_id, int place, int tourney_id) {
        try {
            // Add player
            String sql = String.format("INSERT INTO %s (PlayerID, Place, Tourney_ID) VALUES (%d, %d, %d);",
                                        table_name, player_id, place, tourney_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberPlacings(int player_id) {
        try {
            String sql = String.format("select COUNT(x.ID) from %s x INNER JOIN %s y ON x.ID = y.Tourney_ID where y.PlayerID=%d;",
                                       Tournies.table_name, table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String [][] getPlacings(int player_id, int num_tournies) {
        String [][] data = new String[num_tournies][4];
        try {
            String sql = String.format("select Name, Day, Place, Entrants from %s x INNER JOIN %s y ON x.ID = y.Tourney_ID where y.PlayerID=%d;",
                                       Tournies.table_name, table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                data[i][0] = r.getString(1);
                data[i][1] = r.getString(2);
                data[i][2] = r.getString(3);
                data[i][3] = r.getString(4);
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
