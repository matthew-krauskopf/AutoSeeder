package DBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Backend.Match;
import Backend.MatchUp;
import Backend.Utils;


public class DBManager {
    // Driver name and database url
    static final String DB_URL = "jdbc:h2:~/br_data";

    // DBase connections
    static Connection conn;
    static Statement stmt;

    // Tables
    static Players players_table;
    static History history_table;
    static Tournies tourneyID_table;
    static Alias alias_table;
    static Placings placings_table;
    static IDs ids_table;
    static Exceptions exceptions_table;
    static Seasons seasons_table;

    static String USER;
    static String PASS;

    static String prefix = "BR_";
    static String metadata = prefix+"METADATA";

    public static Boolean bootUp() {
        try {
            USER = Credentials.USER;
            PASS = Credentials.PASS;
            setConn();
            setStmt();
            players_table = new Players(stmt);
            history_table = new History(stmt);
            tourneyID_table = new Tournies(stmt);
            alias_table = new Alias(stmt);
            placings_table = new Placings(stmt);
            ids_table = new IDs(stmt);
            exceptions_table = new Exceptions(stmt);
            seasons_table = new Seasons(stmt);
            printAllDBase();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void setConn() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void setStmt() {
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void shutdown() {
        try {
            stmt.close();
            conn.close();
            System.out.println("Shutdown complete");
        } catch (Exception e) {
            System.out.println("Error! Shutdown failed.");
        }
    }

    private static String sanitize(String sql) {
        return sql.replaceAll("[/\\_%$&`~;#@'*!<>?,\"]", "");
    }

    private static String dbase_sanitize(String sql) {
        return sql.replaceAll("[/\\_ %$&`~;#@'*!<>?,\"]", "");
    }

    private static String salt(String sql) {
        return prefix+dbase_sanitize(sql);
    }

    private static Boolean printAllDBase() {
        try {
            ResultSet r = conn.getMetaData().getCatalogs();
            while (r.next()) {
                String found_dbase = r.getString(1).strip();
                System.out.println(found_dbase);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setMetadata() {
        if (!checkDBaseExists(metadata)) {
            createMetadata();
        } else {
            alias_table.setDatabase(metadata);
            ids_table.setDatabase(metadata);
            exceptions_table.setDatabase(metadata);
            seasons_table.setDatabase(metadata);
        }
    }

    public void purgeMetadata() {
        alias_table.dropTable(metadata);
        ids_table.dropTable(metadata);
        exceptions_table.dropTable(metadata);
        seasons_table.dropTable(metadata);
        try {
            stmt.execute(String.format("DROP SCHEMA IF EXISTS %s;",metadata));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createMetadata() {
        try {
            stmt.execute(String.format("CREATE SCHEMA %s;", metadata));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        alias_table.create(metadata);
        ids_table.create(metadata);
        exceptions_table.create(metadata);
        seasons_table.create(metadata);
    }

    public Boolean checkDBaseExists(String dbase_name) {
        try {
            ResultSet r = conn.getMetaData().getSchemas();
            while (r.next()) {
                String found_dbase = r.getString(1).strip();
                if (found_dbase.equals(dbase_name)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public int checkBracketDataNew(int id) {
        return tourneyID_table.checkBracketDataNew(id);
    }

    public void createSeason(String fed_season_name) {
        String season_name = sanitize(fed_season_name);
        // Utilize hash code to prevent collision in season names after updating the name
        String season_id;
        do {
            season_id = salt(Integer.toString(Math.abs(fed_season_name.hashCode())));
        }
        while (seasons_table.idInUse(season_id));
        if (!checkDBaseExists(season_id)) {
            try {
                stmt.execute(String.format("CREATE SCHEMA %s;", season_id));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            players_table.create(season_id);
            history_table.create(season_id);
            tourneyID_table.create(season_id);
            placings_table.create(season_id);
            // Get date for season table
            String day = Utils.getTodaysDate();
            seasons_table.addSeason(season_id, season_name, day);
        }
    }

    public void deleteSeason(String season_name) {
        String season_id = seasons_table.getSeasonID(sanitize(season_name));
        players_table.dropTable(season_id);
        history_table.dropTable(season_id);
        tourneyID_table.dropTable(season_id);
        placings_table.dropTable(season_id);
        try {
            stmt.execute(String.format("DROP SCHEMA IF EXISTS %s;", season_id));
            seasons_table.deleteSeason(season_id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void selectSeason(String season_name) {
        String season_id = seasons_table.getSeasonID(sanitize(season_name));
        players_table.setDatabase(season_id);
        history_table.setDatabase(season_id);
        tourneyID_table.setDatabase(season_id);
        placings_table.setDatabase(season_id);
    }

    public Boolean checkSeasonExists(String season_name) {
        return seasons_table.checkSeasonExists(season_name);
    }

    public void addPlayers(String [] players) {
        // Adds players to database if new
        for (int i = 0; i < players.length; i++) {
            String player = sanitize(players[i]);
            // If player is unknown, add name to database of players
            if (!alias_table.checkAlias(player)) {
                // Each main name will have itself as alias
                alias_table.addAlias(player, player);
                if (ids_table.addPlayer(player)) {
                    int id = ids_table.getID(player);
                    players_table.addPlayer(id);
                }
            } else { // Has alias: check if player in current season
                int player_id = ids_table.getID(alias_table.getAlias(player));
                // If player not in current season, add
                if (!players_table.checkPlayer(player_id)) {
                    players_table.addPlayer(player_id);
                }
            }
        }
    }

    public void addPlacings(String [] entrants, int tourney_id) {
        // Add placings in tourney to database
        // Below generates placings pattern (1, 2, 3, 4, 5, 7, 9, 13, 17, etc...)
        for (int i = 0, barrier = 2, place = 0, x2 = 0; i < entrants.length; i++) {
            if (i < 5) {
                place = i+1;
            }
            else {
                if (place + barrier == i+1) {
                    place = i+1;
                    if (x2++ % 2 == 1) barrier *= 2;
                }
            }
            // Get main name of entrant
            String main_name = alias_table.getAlias(sanitize(entrants[i]));
            placings_table.addPlacing(ids_table.getID(main_name), place, tourney_id);
        }
    }

    public void addHistory(Match [] results) {
        // Add results
        for (int i = 0; i < results.length; i++) {
            System.out.println("    Adding match " + (i+1));
            // Check if match was a forfeit
            if (results[i].winner == "" && results[i].loser == "") {
                continue;
            }

            // Make my life easier
            int winner_id = getAliasedID(results[i].winner);
            int loser_id = getAliasedID(results[i].loser);
            String date = results[i].date;

            // Check for matchup history
            // No history
            if (history_table.checkHistory(winner_id, loser_id) == 0) {
                // Winner data entry
                history_table.addHistory(winner_id, loser_id, date);
                history_table.addHistory(loser_id, winner_id, date);
            }
            String last_played = laterDate(date, history_table.getLastPlayed(winner_id, loser_id));
            // Add new results
            history_table.updateStats(winner_id, loser_id, 1, last_played);
            history_table.updateStats(loser_id, winner_id, 0, last_played);
            players_table.updateStats(winner_id, 1);
            players_table.updateStats(loser_id, 0);

            // Update ELO scores
            updateScores(winner_id, loser_id);
        }
    }

    public void addBracketInfo(int tourney_id, String name, String date, int num_entrants) {
        tourneyID_table.recordTourney(tourney_id, name, date, num_entrants);
    }

    public void addAlias(String alias, String player) {
        // Check if player is an alias also. If so, get that real name
        String real_name = sanitize(player);
        if (alias_table.checkAlias(player)) {
            real_name = alias_table.getAlias(player);
        }
        alias_table.addAlias(sanitize(alias), real_name);
        // Check if added alias is an existing player. If not, also add that player
        if (!ids_table.checkPlayer(real_name)) {
            if(ids_table.addPlayer(real_name)) {
                int id = ids_table.getID(real_name);
                players_table.addPlayer(id);
            }
        }
    }

    public void addException(String player, String opponent) {
        int player_id = getAliasedID(player);
        int opponent_id = getAliasedID(opponent);
        exceptions_table.addException(player_id, opponent_id);
        exceptions_table.addException(opponent_id, player_id);
    }

    private void updateScores(int winner_id, int loser_id) {
        // Winner and Loser have already had names aliased to main name
        // Get data
        int [] w_data = players_table.getEloData(winner_id);
        int [] l_data = players_table.getEloData(loser_id);

        // Don't use actual score of player until they have played at least 5 sets
        int winner_score = (w_data[1] >= 5 ? w_data[0] : players_table.getBaseELO());
        int loser_score = (l_data[1] >= 5 ? l_data[0] : players_table.getBaseELO());

        // Calculate new elo scores
        int w_new_score = ((w_data[0] * (w_data[1]-1)) + loser_score + 400) / w_data[1];
        int l_new_score = ((l_data[0] * (l_data[1]-1)) + winner_score - 400) / l_data[1];

        // Update scores
        players_table.updateElo(winner_id, w_new_score);
        players_table.updateElo(loser_id, l_new_score);
    }

    public void updateName(String old_name, String new_name) {
        String o_name = sanitize(old_name);
        String n_name = sanitize(new_name);
        ids_table.updatePlayerName(o_name, n_name);
        alias_table.updateAlias(o_name, n_name);
    }

    public void updateSeasonName(String old_name, String new_name) {
        String n_name = sanitize(new_name);
        seasons_table.updateSeasonName(old_name, n_name);
    }

    public String [] getUnknownEntrants(String [] players) {
        String [] unknown_entrants = new String[players.length];
        int unknown_count = 0;
        for (int i = 0; i < players.length; i++) {
            // Initialize to -1
            unknown_entrants[i] = "";
            String player = sanitize(players[i]);
            // First, check if player has an alias
            if (!alias_table.checkAlias(player)) {
                // Flag to ask user for actual tag of player
                unknown_entrants[unknown_count++] = players[i];
            }
            // Else: No action needed
        }
        return Utils.splice(unknown_entrants, unknown_count);
    }

    public String [][] getRankings() {
        return players_table.getRankings();
    }

    public int getNumTournies() {
        return tourneyID_table.getNumTournies();
    }

    public String getDayCreated(String season_name) {
        String season_id = seasons_table.getSeasonID(season_name);
        return seasons_table.getDayCreated(season_id);
    }

    public String [][] getTourneyHistory(String player) {
        // No need to find alias of player since passed in player is from player's table, which only has sanitized main names
        int player_id = ids_table.getID(player);
        return placings_table.getPlacings(player_id);
    }

    public String [][] getMatchupHistory(String player) {
        // No need to find alias of player since passed in player is from player's table, which only has sanitized main names
        int player_id = ids_table.getID(player);
        return history_table.getMatchupHistory(player_id);
    }

    public String [][] getFilteredRankings(String fed_filter) {
        String filter = sanitize(fed_filter);
        int size = players_table.getNumFilteredRankings(filter);
        return players_table.getFilteredRankings(filter, size);
    }

    public int [] getScores(String [] entrants) {
        // Allocate scores
        int [] scores = new int[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            scores[i] = players_table.getScore(getAliasedID(entrants[i]));
        }
        return scores;
    }

    public String [] getAliases(String main_player) {
        String main_name = sanitize(main_player);
        return alias_table.getPlayerAliases(main_name);
    }

    public MatchUp [] getRecentMatchups(String [] entrants) {
        MatchUp [] matchups = new MatchUp[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            String player = alias_table.getAlias(sanitize(entrants[i]));
            int player_id = ids_table.getID(player);
            // Get last 2 tourney dates player entered.
            String [] last_dates = history_table.getLastDates(player_id, 2);
            String [] opponents = history_table.getOpponents(player_id, last_dates);
            matchups[i] = new MatchUp(player, opponents);
        }
        return matchups;
    }

    public MatchUp [] getExceptions(String [] entrants) {
        MatchUp [] matchups = new MatchUp[entrants.length];
        for (int i = 0; i < entrants.length; i++) {
            String player = alias_table.getAlias(sanitize(entrants[i]));
            int player_id = ids_table.getID(player);
            String [] opponents = exceptions_table.getExceptions(player_id);
            matchups[i] = new MatchUp(player, opponents);
        }
        return matchups;
    }

    public String [] getExceptions(String player) {
        int player_id = getAliasedID(player);
        return exceptions_table.getExceptions(player_id);
    }

    private int getAliasedID(String player) {
        return ids_table.getID(alias_table.getAlias(sanitize(player)));
    }

    public String [] getSeasons() {
        return seasons_table.getSeasons();
    }

    public void deleteAlias(String alias, String player) {
        alias_table.deleteAlias(alias, player);
    }

    public void deleteException(String player, String opponent) {
        int player_id = getAliasedID(player);
        int opponent_id = getAliasedID(opponent);
        exceptions_table.deleteException(player_id, opponent_id);
        exceptions_table.deleteException(opponent_id, player_id);
    }

    private String laterDate(String d1, String d2) {
        String [] temp1 = d1.split("-");
        String [] temp2 = d2.split("-");
        int [] ymd1 = new int [] {Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1]), Integer.parseInt(temp1[2])};
        int [] ymd2 = new int [] {Integer.parseInt(temp2[0]), Integer.parseInt(temp1[1]), Integer.parseInt(temp1[2])};
        if (ymd1[0] == ymd2[0]) {
            if (ymd1[1] == ymd2[1]) {
                return (ymd1[2] > ymd2[2] ? d1 : d2);
            }
            else return (ymd1[1] > ymd2[1] ? d1 : d2);
        }
        else return (ymd1[0] > ymd2[0] ? d1 : d2);
    }
}
