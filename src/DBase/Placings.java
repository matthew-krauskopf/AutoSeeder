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
                        "   Player varchar(255), " +
                        "   Place int, " +
                        "   Tourney_ID int, " +
                        "   PRIMARY KEY (Player, Tourney_ID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addPlacing(String player, int place, int tourney_id) {
        try {
            // Add player
            String sql = String.format("INSERT INTO %s (Player, Place, Tourney_ID) VALUES ('%s', %d, %d);", 
                                        table_name, player, place, tourney_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public int getNumberPlacings(String player) {
        try {
            String sql = String.format("select COUNT(x.ID) from %s x INNER JOIN %s y ON x.ID = y.Tourney_ID where y.Player='%s';",
                                       Tournies.table_name, table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    public String [][] getPlacings(String player, int num_tournies) {
        String [][] data = new String[num_tournies][4];
        try {
            String sql = String.format("select Name, Place, Entrants from %s x INNER JOIN %s y ON x.ID = y.Tourney_ID where y.Player='%s';",
                                       Tournies.table_name, table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                data[i][0] = r.getString(1);
                data[i][1] = "Today";
                data[i][2] = r.getString(2);
                data[i][3] = r.getString(3);
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
