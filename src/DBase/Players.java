package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Players {

    public static String database_name;
    private static String table_name = "Players";
    private static Statement stmt;

    private int base_elo = 1200;

    public Players(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   PlayerID int," +
                        "   Wins int, " +
                        "   Sets int, " +
                        "   Score int, " +
                        "   PRIMARY KEY (PlayerID));",
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void dropTable(String dbase_name) {
        try {
            String sql = String.format("DROP TABLE IF EXISTS %s.%s;",
                            dbase_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public int getBaseELO() {
        return base_elo;
    }

    public void addPlayer(int player_id) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (PlayerID, Wins, Sets, Score) VALUES (%d, 0, 0, %d);",
                                        database_name, table_name, player_id, base_elo);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkPlayer(int id) {
        // Adds player to database if new
        try {
            // Add player
            String sql = "";
            // Check if player record already exists
            sql = String.format("SELECT 1 FROM %s.%s where PlayerID = '%s';",
                                 database_name, table_name, id);
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

    public void updateStats(int player_id, int wins) {
        try {
            String sql = String.format("UPDATE %s.%s SET Wins = Wins + %d, Sets = Sets + 1 WHERE PlayerID = %d;",
                                        database_name, table_name, wins, player_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void resetStats() {
        try {
            String sql = String.format("UPDATE %s.%s SET Wins = 0, Sets = 0, Score = %d;",
                                        database_name, table_name, base_elo);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String [][] getRankings() {
        try {
            String sql =  String.format("SELECT y.Player, x.Wins, (x.Sets-x.Wins), x.Score " +
                                        "FROM %s.%s x INNER JOIN %s.%s y ON x.PlayerID=y.ID ORDER BY SCORE DESC;",
                                         database_name, table_name, IDs.database_name, IDs.table_name);
            ResultSet r = stmt.executeQuery(sql);

            // Get size of data
            int n_players = 0;
            if (r.last()) {
                n_players = r.getRow();
            }
            String [][] player_info = new String[n_players][5];
            // Reset back to first element
            r.beforeFirst();
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
        }
        return new String[0][0];
    }

    public int getNumFilteredRankings(String filter) {
        try {
            String sql =  String.format("SELECT COUNT(y.Player) " +
                                        "FROM %s.%s x INNER JOIN %s.%s y ON x.PlayerID=y.ID " +
                                        "WHERE lower(y.Player) LIKE lower('%s')",
                                         database_name, table_name, IDs.database_name, IDs.table_name, "%"+filter+"%");
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public String [][] getFilteredRankings(String filter, int size) {
        try {
            String sql =  String.format("SELECT y.Player, x.Wins, x.Sets-x.Wins, x.Score " +
                                        "FROM %s.%s x INNER JOIN %s.%s y ON x.PlayerID=y.ID " +
                                        "ORDER BY SCORE DESC;",
                                         database_name, table_name, IDs.database_name, IDs.table_name);
            ResultSet r = stmt.executeQuery(sql);

            String [][] player_info = new String[size][5];
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
        }
        return new String[0][0];
    }

    public int[] getEloData(int player_id) {
        try {
            String sql = String.format("SELECT Score, Sets FROM %s.%s WHERE PlayerID = %d;",
                                        database_name, table_name, player_id);
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
            String sql = String.format("UPDATE %s.%s x SET SCORE = %d WHERE PlayerID = %d;",
                                        database_name, table_name, score, player_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getScore(int player_id) {
        try {
            String sql = String.format("SELECT SCORE FROM %s.%s WHERE PlayerID = %d;",
                                        database_name, table_name, player_id);
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
