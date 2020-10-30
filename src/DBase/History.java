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
            "   Wins int, " +
            "   Sets int, " +
            "   Last_played Date, " +
            "   PRIMARY KEY(Player, Opponent));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int checkHistory(String winner, String loser) {
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

    public void addHistory(String winner, String loser, String date) {
        try {
            String sql = "";
            sql = String.format("INSERT INTO %s (Player, Opponent, Wins, Sets, Last_played) VALUES ('%s', '%s', 0, 0, '%s');",
                                table_name, winner, loser, date);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateStats(String winner, String loser, int wins) {
        try {
            String sql = String.format("UPDATE %s SET Wins = Wins + %d, Sets = Sets + 1 WHERE " +
                                      "PLAYER = '%s' AND OPPONENT = '%s';", table_name, wins, winner, loser);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String [] getLastDates(String player, int num_dates) {
        String [] dates = new String[num_dates];
        try {
            String sql = String.format("select distinct Last_played from %s where player='%s' " +
                                       "order by Last_played DESC limit %d;", table_name, player, num_dates);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                dates[i++] = r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dates;
    }

    public int getNumberOpponents(String player) {
        try {
            String sql = String.format("select COUNT(Opponent) from %s where player='%s' ", table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String [][] getMatchupHistory(String player, int num_opponents) {
        String [][] data = new String[num_opponents][4];
        try {
            String sql = String.format("select Opponent, Wins, (Sets-Wins), Last_played from %s where player='%s' " +
                                       "order by Last_played DESC;", table_name, player);
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

    public String [] getOpponents(String player, String [] dates) {
        String [] opponents;
        try {
            // TODO Modify to handle any number of dates
            String date1 = (dates[0] != null ? dates[0] : "1900-01-01");
            String date2 = (dates[1] != null ? dates[1] : "1900-01-02");
            String sql = String.format("select Opponent from %s where player='%s' AND (Last_played='%s' OR Last_played='%s') " +
                                       "order by Last_played DESC;", table_name, player, date1, date2);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int num_opp = 0;
            if (r.last()) {
                num_opp = r.getRow();
            }
            // Allocate opponent array
            opponents = new String[num_opp];
            // Reset back to first element
            r.beforeFirst();
            // Fill opponents array
            int i = 0;
            while (r.next()) {
                opponents[i++] = r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Set to size 0 if error
            opponents = new String[0];
        }
        return opponents;
    }
}
