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
                        "   Player varchar(255)," +
                        "   Wins int, " +
                        "   Sets int, " +
                        "   Score int, " +
                        "   PRIMARY KEY (Player));", table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkPlayer(String player) {
        // Adds player to database if new
        try {
            // Add player
            String sql = "";
            // Check if player record already exists
            sql = String.format("SELECT 1 FROM %s where Player = '%s';", table_name, player);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                // No player found: check for alias
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void addPlayer(String player) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s (Player, Wins, Sets, Score) VALUES ('%s', 0, 0, 1200);", table_name, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateStats(String player, int wins) {
        try {
            String sql = String.format("UPDATE %s x INNER JOIN %s y ON x.Player = y.Main_Player SET Wins = Wins + %d, Sets = Sets + 1 WHERE " +
                                        "y.Alias = '%s';", table_name, Alias.table_name, wins, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberPlayers() {
        try {
            String sql = String.format("SELECT COUNT(PLAYER) FROM %s;", table_name);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getNumberFilteredPlayers(String filter) {
        try {
            String sql = String.format("SELECT COUNT(PLAYER) FROM %s WHERE PLAYER LIKE '%s';", table_name, '%'+filter+'%');
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private static String getLosses(String n1, String n2) {
        return Integer.toString(Integer.parseInt(n2) - Integer.parseInt(n1));
    }

    public String [][] getRankings(int n_players) {
        try {
            String [][] player_info = new String[n_players][5];
            String sql =  String.format("SELECT * FROM %s ORDER BY SCORE DESC;", table_name);
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                // Set chart
                player_info[i][0] = Integer.toString(i+1);
                player_info[i][1] = r.getString(1);
                player_info[i][2] = r.getString(2);
                player_info[i][3] = getLosses(r.getString(2),r.getString(3));
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
            String sql =  String.format("SELECT * FROM %s WHERE PLAYER LIKE '%s' ORDER BY SCORE DESC;", table_name, '%'+filter+'%');
            ResultSet r = stmt.executeQuery(sql);
            int i = 0;
            while (r.next()) {
                // Set chart
                player_info[i][0] = Integer.toString(i+1);
                player_info[i][1] = r.getString(1);
                player_info[i][2] = r.getString(2);
                player_info[i][3] = getLosses(r.getString(2),r.getString(3));
                player_info[i][4] = r.getString(4);
                i++;
            }
            return player_info;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0];
        }
    }

    public int[] getEloData(String player) {
        try {
            String sql = String.format("SELECT Score, Sets FROM %s x INNER JOIN %s y on x.Player = y.Main_Player WHERE y.Alias = '%s';",
                                       table_name, Alias.table_name, player);
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

    public void updateElo(String player, int score) {
        try {
            String sql = String.format("UPDATE %s x INNER JOIN %s y ON x.Player = y.Main_Player SET SCORE = %d WHERE " +
                                    "y.Alias = '%s';", table_name, Alias.table_name, score, player);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getScore(String player) {
        try {
            String sql = String.format("SELECT SCORE FROM %s x INNER JOIN %s y ON x.Player = y.Main_Player WHERE y.Alias = '%s';", table_name, Alias.table_name, player);
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
