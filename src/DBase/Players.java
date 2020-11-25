package DBase;

import java.sql.*;
import java.io.IOException;

public class Players {

    private static String table_name = "Players";
    private static Statement stmt;

    public Players(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create() {
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "   PlayerID int," +
                        "   Wins int, " +
                        "   Sets int, " +
                        "   Score int, " +
                        "   PRIMARY KEY (PlayerID));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addPlayer(int player_id) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s (PlayerID, Wins, Sets, Score) VALUES (%d, 0, 0, 1200);", table_name, player_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateStats(int player_id, int wins) {
        try {
            String sql = String.format("UPDATE %s SET Wins = Wins + %d, Sets = Sets + 1 WHERE " +
                                        "PlayerID = %d;", table_name, wins, player_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static String getLosses(String n1, String n2) {
        return Integer.toString(Integer.parseInt(n2) - Integer.parseInt(n1));
    }

    public String [][] getRankings(int n_players) {
        try {
            String [][] player_info = new String[n_players][5];
            String sql =  String.format("SELECT y.Player, x.Wins, (x.Sets-x.Wins), x.Score " +
                                        "FROM %s x INNER JOIN %s y ON x.PlayerID=y.ID ORDER BY SCORE DESC;",
                                        table_name, IDs.table_name);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                // Set chart
                player_info[i][0] = Integer.toString(i+1);
                player_info[i][1] = r.getString(1);
                player_info[i][2] = r.getString(2);
                player_info[i][3] = r.getString(3);
                player_info[i][4] = r.getString(4);
                i++;
            }
            return player_info;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0];
        }
    }

    public String [][] getFilteredRankings(int n_players, String filter) {
        try {
            String [][] player_info = new String[n_players][5];
            String sql =  String.format("SELECT y.Player, x.Wins, x.Sets-x.Wins, x.Score " +
                                        "FROM %s x INNER JOIN %s y ON x.PlayerID=y.ID ORDER BY SCORE DESC;",
                                        table_name, IDs.table_name);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            int tot = 0;
            while (r.next()) {
                // Set chart
                String name = r.getString(1).toLowerCase();
                if (name.matches(String.format(".*%s.*",filter))) {
                    player_info[i][0] = Integer.toString(tot+1);
                    player_info[i][1] = r.getString(1);
                    player_info[i][2] = r.getString(2);
                    player_info[i][3] = r.getString(3);
                    player_info[i][4] = r.getString(4);
                    i++;
                }
                tot++;
            }
            return player_info;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0];
        }
    }

    public int[] getEloData(int player_id) {
        try {
            String sql = String.format("SELECT Score, Sets FROM %s WHERE PlayerID = %d;",
                                       table_name, player_id);
            ResultSet w_data = stmt.executeQuery(sql);
            w_data.next();
            // [Current score, Sets played]
            int [] data = {w_data.getInt(1), w_data.getInt(2)};
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new int[2];
        }
    }

    public void updateElo(int player_id, int score) {
        try {
            String sql = String.format("UPDATE %s x SET SCORE = %d WHERE " +
                                    "PlayerID = %d;", table_name, score, player_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getScore(int player_id) {
        try {
            String sql = String.format("SELECT SCORE FROM %s WHERE PlayerID = %d;", table_name, player_id);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
