package MyUtils;

import DBase.DBManager;

public class API {

    private static DBManager db = new DBManager();

    public static void setMetadata() {
        db.setMetadata();
    }

    public static void remakeDatabase() {
        String [] seasons = db.getSeasons();
        for (String season: seasons) db.deleteSeason(season);
        db.purgeMetadata();
        db.createMetadata();
    }

    public static void selectSeason(String season_name) {
        db.selectSeason(season_name);
    }

    public static void createSeason(String season_name) {
        db.createSeason(season_name);
    }

    public static void deleteSeason(String season_name) {
        db.deleteSeason(season_name);
    }

    public static Boolean checkSeasonExists(String season_name) {
        return db.checkSeasonExists(season_name);
    }

    public static int checkBracketNew() {
        int id = WebData.getTourneyID();
        // Error happened
        if (id < 0) return id;
        // Make sure imported bracket is new
        int status = db.checkBracketDataNew(id);
        if (status == 1) {
            return id;
        }
        return -1;
    }

    public static void wakeUpHTML() {
        HTML.wakeUp();
    }

    public static void closeHTML() {
        HTML.closeHTML();
    }

    public static void cleanTmpFiles() {
        ReadFile.cleanTmpFiles();
    }

    public static void makeStandingsFile(String url) {
        HTML.makeStandingsFile(url);
    }

    public static void makeResultsFile(String url) {
        HTML.makeResultsFile(url);
    }

    public static void makeLogFile(String url) {
        HTML.makeLogFile(url);
    }

    public static String [][] getTourneyHistory(String player) {
        return db.getTourneyHistory(player);
    }

    public static String [][] getMatchupHistory(String player) {
        return db.getMatchupHistory(player);
    }

    public static Match[] getMatches() {
        return WebData.getMatches();
    }

    public static String getTourneyName() {
        return ReadFile.readTourneyNameHTML();
    }

    public static String [] getSeasons() {
        return db.getSeasons();
    }

    public static String [][] getRankings() {
        return db.getRankings();
    }

    public static String [][] getFilteredRankings(String filter) {
        return db.getFilteredRankings(filter);
    }

    public static String[] getEntrants() {
        return ReadFile.readEntrantsHTML();
    }

    public static String getDayCreated(String season_name) {
        return db.getDayCreated(season_name);
    }

    public static int getNumTournies() {
        return db.getNumTournies();
    }

    public static Set[] getSets (String [] entrants) {
        Set[] sets = Bracket.getSets(entrants);
        Utils.sortSets(sets);
        return sets;
    }

    public static String [] getAliases(String main_name) {
        return db.getAliases(main_name);
    }

    public static String [] getExceptions(String player) {
        return db.getExceptions(player);
    }

    public static void addPlayerData(String [] entrants) {
        db.addPlayers(entrants);
        return;
    }

    public static void addHistoryData(Match [] results) {
        db.addHistory(results);
        return;
    }

    public static void addBracketData(int num_entrants, String date, int tourney_id, String tourney_name) {
        db.addBracketInfo(tourney_id, tourney_name, date, num_entrants);
        return;
    }

    public static void addPlacingsData(String [] entrants, int tourney_id) {
        db.addPlacings(entrants, tourney_id);
        return;
    }

    public static void addAlias(String alias, String true_name) {
        db.addAlias(alias, true_name);
    }

    public static void addException(String player, String opponent) {
        db.addException(player, opponent);
    }

    public static String[] checkUnknownNames(String [] entrants) {
        return db.getUnknownEntrants(entrants);
    }

    public static void updateName(String old_name, String new_name) {
        db.updateName(old_name, new_name);
    }

    public static void updateSeasonName(String old_name, String new_name) {
        db.updateSeasonName(old_name, new_name);
    }

    public static void deleteException(String player, String opponent) {
        db.deleteException(player, opponent);
    }

    public static void deleteAlias(String alias, String player) {
        db.deleteAlias(alias, player);
    }

    public static BracketData makeBracket(String [] entrants, int shake_rounds) {
        // Error out early if no entrants
        if (entrants.length==0) {
            return new BracketData(entrants);
        }
        int [] scores = db.getScores(entrants);
        Bracket.seedBracket(entrants, scores);
        if (shake_rounds >= 0) {
            // First, reseed to avoid explicit exceptions
            MatchUp [] exceptions = db.getExceptions(entrants);
            Bracket.shakeupBracket(entrants, exceptions, shake_rounds);
            // Now redo the work with recent matchups and exceptions
            MatchUp [] recent_matchups = db.getRecentMatchups(entrants);
            MatchUp [] matchups_a_exceptions = Utils.merge(exceptions, recent_matchups);
            Bracket.shakeupBracket(entrants, matchups_a_exceptions, shake_rounds);
            // Used to check if any conflicts still exist
            int [][] conflicts = Bracket.sanityCheck(entrants, matchups_a_exceptions, (shake_rounds == 0 ? 3 : shake_rounds));
            System.out.println("Num conflicts: " + conflicts.length);
            return new BracketData(entrants, conflicts);
        }
        else return new BracketData(entrants);
    }
}
