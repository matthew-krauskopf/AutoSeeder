package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tournies {

    public static String database_name;
    public static String table_name = "Tournies";
    private static Statement stmt;

    public Tournies(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   ID int, " +
                        "   Name VARCHAR(255), " +
                        "   Entrants int, " +
                        "   Day Date, " +
                        "   PRIMARY KEY(ID));",
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

    public int getNumTournies() {
        try {
            String sql = String.format("SELECT COUNT(ID) FROM %s.%s",
                                        database_name, table_name);
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

    public String getLatestDate() {
        try {
            String sql = String.format("SELECT Day FROM %s.%s ORDER BY Day DESC LIMIT 1",
                                        database_name, table_name);
            ResultSet r = stmt.executeQuery(sql);
            if (r.next()) {
                return r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "0000-00-00";
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public void recordTourney(int id, String name, String date, int num_entrants) {
        try {
            String sql = String.format("INSERT INTO %s.%s (ID, Name, Day, Entrants) VALUES (%d, '%s', '%s', %d);",
                                        database_name, table_name, id, name, date, num_entrants);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int checkBracketDataNew(int ID) {
        // Ensure bracket has not been entered into db before
        try {
            String sql = String.format("SELECT 1 FROM %s.%s where ID = %d;",
                                        database_name, table_name, ID);
            ResultSet r = stmt.executeQuery(sql);
            // Data already exists
            if (r.next()) {
                return 0;
            }
            // New data: proceed with import
            else {
                return 1;
            }
        // Error handling
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
