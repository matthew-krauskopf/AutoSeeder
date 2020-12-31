package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sets {

    public static String database_name;
    private static String table_name = "Sets";
    private static Statement stmt;

    public Sets(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
            "   WinnerID int, " +
            "   LoserID int, " +
            "   TourneyID int, " +
            "   SetNum int, " +
            "   PRIMARY KEY(TourneyID, SetNum));",
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

    public void addSet(int winner_id, int loser_id, int tourney_id, int set_num) {
        try {
            String sql = "";
            sql = String.format("INSERT INTO %s.%s (WinnerID, LoserID, TourneyID, SetNum) VALUES (%d, %d, %d, %d);",
                                 database_name, table_name, winner_id, loser_id, tourney_id, set_num);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int [][] getAllSets() {
        try {
            String sql = String.format("select x.WinnerID, x.LoserID from %s.%s x INNER JOIN %s.%s y ON x.TourneyID=Y.ID " +
                                       "ORDER BY y.Day, x.SetNum ASC;",
                                        database_name, table_name, Tournies.database_name, Tournies.table_name);
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int amt = 0;
            if (r.last()) {
                amt = r.getRow();
            }
            // Reset to beginning of data
            r.beforeFirst();
            int [][] data = new int[amt][2];
            int i = 0;
            while (r.next()) {
                data[i][0] = r.getInt(1);
                data[i++][1] = r.getInt(2);
            }
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new int [0][2];
    }
}
