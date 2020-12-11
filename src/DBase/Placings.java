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
                        "   TourneyID int, " +
                        "   PRIMARY KEY (PlayerID, TourneyID));",
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public void addPlacing(int player_id, int place, int TourneyID) {
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (PlayerID, Place, TourneyID) VALUES (%d, %d, %d);",
                                        database_name, table_name, player_id, place, TourneyID);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String [][] getPlacings(int player_id) {
        try {
            String sql = String.format("select y.Name, y.Day, x.Place, y.Entrants " +
                                       "from %s.%s x INNER JOIN %s.%s y ON x.ID = y.TourneyID " +
                                       "where y.PlayerID=%d;",
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
