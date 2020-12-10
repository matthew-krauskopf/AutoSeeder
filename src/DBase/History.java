package DBase;

import java.sql.*;
import java.io.IOException;

public class History {

    public static String database_name;
    private static String table_name = "History";
    private static Statement stmt;

    public History(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
            "   PlayerID int, " +
            "   OpponentID int, " +
            "   Wins int, " +
            "   Sets int, " +
            "   Last_played Date, " +
            "   PRIMARY KEY(PlayerID, OpponentID));",
                database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public int checkHistory(int winner_id, int loser_id) {
        try {
            String sql = String.format("SELECT 1 FROM %s. %s where PlayerID = %d AND OpponentID = %d;",
                                        database_name, table_name, winner_id, loser_id);
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

    public void addHistory(int winner_id, int loser_id, String date) {
        try {
            String sql = "";
            sql = String.format("INSERT INTO %s.%s (PlayerID, OpponentID, Wins, Sets, Last_played) VALUES (%d, %d, 0, 0, '%s');",
                                 database_name, table_name, winner_id, loser_id, date);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateStats(int winner_id, int loser_id, int wins, String last_played) {
        try {
            String sql = String.format("UPDATE %s.%s SET Wins = Wins + %d, Sets = Sets + 1, Last_played = '%s' " +
                                       "WHERE PlayerID = %d AND OpponentID = %d;",
                                       database_name, table_name, wins, winner_id, last_played, loser_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getLastPlayed(int player_id, int opponent_id) {
        try {
            String sql = String.format("select Last_played from %s.%s where PlayerID=%d and OpponentID=%d",
                                        database_name, table_name, player_id, opponent_id);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String [] getLastDates(int player_id, int num_dates) {
        String [] dates = new String[num_dates];
        try {
            String sql = String.format("select distinct Last_played from %s.%s where PlayerID=%d " +
                                       "order by Last_played DESC limit %d;",
                                        database_name, table_name, player_id, num_dates);
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

    public String [][] getMatchupHistory(int player_id) {
        try {
            String sql = String.format("select y.Player, x.Wins, (x.Sets-x.Wins), x.Last_played " +
                                       "from %s.%s x INNER JOIN %s.%s y ON x.OpponentID=y.ID where x.PlayerID=%d " +
                                       "order by x.OpponentID;",
                                        database_name, table_name, IDs.database_name, IDs.table_name, player_id);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int amt = 0;
            if (r.last()) {
                amt = r.getRow();
            }
            String [][] data = new String[amt][4];
            // Reset to beginning of data
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

    public String [] getOpponents(int player_id, String [] dates) {
        String [] opponents;
        try {
            // TODO Modify to handle any number of dates
            String date1 = (dates[0] != null ? dates[0] : "1900-01-01");
            String date2 = (dates[1] != null ? dates[1] : "1900-01-02");
            String sql = String.format("select y.Player from %s.%s x INNER JOIN %s.%s y ON x.OpponentID=y.ID " +
                                       "where x.PlayerID=%d AND (Last_played='%s' OR Last_played='%s') " +
                                       "order by Last_played DESC;",
                                        database_name, table_name, IDs.database_name, IDs.table_name, player_id, date1, date2);
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
