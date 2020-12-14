package DBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Seasons {

    public static String database_name;
    public static String table_name = "Seasons";
    private static Statement stmt;

    public Seasons(Statement fed_stmt) {
        stmt = fed_stmt;
    }

    public void create(String dbase_name) {
        setDatabase(dbase_name);
        try {
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "   SeasonID VARCHAR(255) NOT NULL," +
                        "   SeasonName VARCHAR(255), " +
                        "   DayCreated VARCHAR(255), " +
                        "   PRIMARY KEY (SeasonID));", 
                            database_name, table_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatabase(String dbase_name) {
        database_name = dbase_name;
    }

    public void addSeason(String season_id, String season_name, String date) {
        // Adds player to database if new
        try {
            // Add player
            String sql = String.format("INSERT INTO %s.%s (SeasonID, SeasonName, DayCreated) VALUES ('%s', '%s', '%s');", 
                                        database_name, table_name, season_id, season_name, date);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteSeason(String season_id) {
        try {
            String sql = String.format("DELETE FROM %s.%s WHERE SeasonID='%s';",
                                        database_name, table_name, season_id);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Boolean idInUse(String season_id) {
        try {
            String sql = String.format("SELECT 1 FROM %s.%s WHERE SeasonID='%s';", 
                                        database_name, table_name, season_id);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            if (r.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Boolean checkSeasonExists(String season_name) {
        try {
            String sql = String.format("SELECT 1 FROM %s.%s WHERE SeasonName='%s';",
                                        database_name, table_name, season_name);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            if (r.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void updateSeasonName(String old_name, String new_name) {
        try {
            String sql = String.format("UPDATE %s.%s SET SeasonName = '%s' WHERE SeasonName = '%s';", 
                                        database_name, table_name, new_name, old_name);
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getSeasonID(String season_name) {
        try {
            String sql = String.format("SELECT SeasonID FROM %s.%s WHERE SeasonName='%s';", 
                                        database_name, table_name, season_name);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            if (r.next()) {
                return r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getDayCreated(String season_id) {
        try {
            String sql = String.format("SELECT DayCreated FROM %s.%s WHERE SeasonID='%s';", 
                                        database_name, table_name, season_id);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            if (r.next()) {
                return r.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String [] getSeasons() {
        try {
            String sql = String.format("SELECT SeasonName FROM %s.%s;", 
                                        database_name, table_name);
            // Check if player has entered before. If not, score of 0
            ResultSet r = stmt.executeQuery(sql);
            // Get size of data
            int amt = 0;
            if (r.last()) {
                amt = r.getRow();
            }
            String [] seasons = new String[amt];
            r.beforeFirst();
            int i = 0;
            while (r.next()) {
                seasons[i++] = r.getString(1);
            }
            return seasons;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new String [0];
    }
}