package DBase;

import java.sql.*;
import java.io.IOException;

public class Placings {

    public static String database_name;
    public static String table_name = "Placings";
    private static Statement stmt;

    public Placings(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   PlayerID int, " +
                        "   Place int, " +
                        "   Tourney_ID int, " +
                        "   PRIMARY KEY (PlayerID, Tourney_ID));",
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public void addPlacing(int player_id, int place, int tourney_id) {
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (PlayerID, Place, Tourney_ID) VALUES (%d, %d, %d);",
                                        database_name, table_name, player_id, place, tourney_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String [][] getPlacings(int player_id) {
        try {
            String sql = String.format("select Name, Day, Place, Entrants from %s.%s x INNER JOIN %s.%s y ON x.ID = y.Tourney_ID where y.PlayerID=%d;",
                                        Tournies.database_name, Tournies.table_name, database_name, table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int num_tournies = 0;
            if (r.last()) {
                num_tournies = r.getRow();
            }
            String [][] data = new String[num_tournies][4];
            // Reset back to first element
            r.beforeFirst();
            int i = 0;
            while (r.next()) {
                data[i][0] = r.getString(1);
                data[i][1] = r.getString(2);
                data[i][2] = r.getString(3);
                data[i][3] = r.getString(4);
                i++;
            }
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new String [0][0];
    }
}
