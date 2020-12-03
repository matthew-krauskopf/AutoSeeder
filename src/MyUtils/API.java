package MyUtils;

import DBase.DBManager;
import DBase.Credentials.*;
import java.util.Arrays;

public class API {

    private static DBManager db = new DBManager();   // DBase.Credentials.USER, DBase.Credentials.PASS);

    public static String [] getSampleEntrants() {
        return ReadFile.readFile("sample_entrants.txt");
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
            MatchUp [] matchups_a_exceptions = merge(exceptions, recent_matchups);
            Bracket.shakeupBracket(entrants, matchups_a_exceptions, shake_rounds);
            // Used to check if any conflicts still exist
            int [][] conflicts = Bracket.sanityCheck(entrants, matchups_a_exceptions, (shake_rounds == 0 ? 3 : shake_rounds));
            System.out.println("Num conflicts: " + conflicts.length);
            return new BracketData(entrants, conflicts);
        }
        else return new BracketData(entrants);
    }

    private static MatchUp[] merge(MatchUp [] exceptions, MatchUp [] recent_matchups) {
        MatchUp [] ans = new MatchUp[exceptions.length];
        for (int i = 0; i < exceptions.length; i++) {
            ans[i] = new MatchUp(exceptions[i].player, mergeArrays(exceptions[i].opponents, recent_matchups[i].opponents) );
        }
        return ans;
    }

    public static String[] mergeArrays(String [] arr1, String[] arr2) {
        String [] concat = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, arr2.length);
        return concat;
    }

    public static void setMetadata() {
        db.setMetadata();
    }

    public static void remakeDatabase() {
        db.purgeDatabase();
        db.createMetadata();
    }

    public static String [] getSeasons() {
        return db.getSeasons();
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

    public static String [] getAliases(String main_name) {
        return db.getAliases(main_name);
    }

    public static Set[] getSets (String [] entrants) {
        Set[] sets = Bracket.getSets(entrants);
        sortSets(sets);
        return sets;
    }

    public static void sortSets(Set [] sets) {
        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        int end = sq_entrants/2;
        int [] set_order;
        Set [] temp_copy;

        // Sort winner's sets
        while (tot < sq_entrants-1) {
            set_order = getVisualOrder(0, end);
            temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                sets[cur+tot] = temp_copy[set_order[cur]];
            }
            tot += end;
            end /= 2;
        }

        // Sort loser's sets
        // Sort first round just like winner's, then use that to build rest of rounds
        end = sq_entrants/4;
        set_order = getVisualOrder(0, end);
        temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
        for (int cur = 0; cur < end ; cur++) {
            sets[cur+tot] = temp_copy[set_order[cur]];
        }
        tot += end;

        // Now do rest of rounds
        int round = 2;
        while (tot < sets.length) {
            temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
            int bot_seed = temp_copy[0].h_seed;
            for (int cur = 0; cur < end ; cur++) {
                // Even rounds: drop down from winners
                if (round % 2 == 0) {
                    int prev_seed = sets[(tot-end)+cur].h_seed - 1;
                    int [] l_path = Bracket.getOpponentSeeds(prev_seed, sq_entrants);
                    int next_seed = l_path[l_path.length-1]+1;
                    sets[cur+tot] = temp_copy[next_seed-bot_seed];
                }
                // Odd rounds: prev two come together
                else {
                    // Get the highest seed from the two matches that feed in
                    int prev_seed_1 = sets[(tot-(end*2))+(cur*2)].h_seed - 1;
                    int prev_seed_2 = sets[(tot-(end*2))+(cur*2)+1].h_seed - 1;
                    int prev_seed = (prev_seed_1 < prev_seed_2 ? prev_seed_1 : prev_seed_2);
                    // Move set of highest seed to correct spot
                    sets[cur+tot] = temp_copy[(prev_seed+1)-bot_seed];
                }
            }
            tot += end;
            round++;
            // Number of sets in loser's only cuts in half every other round
            if (round % 2 == 1) end /= 2;
        }
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

    public static void makeHTMLFiles(String url) {
        HTML.makeHTMLFiles(url, 3);
    }

    public static void makeHTMLFiles(String url, int num_needed) {
        HTML.makeHTMLFiles(url, num_needed);
    }

    public static void cleanTmpFiles() {
        ReadFile.cleanTmpFiles();
    }

    public static String [][] getTourneyHistory(String player) {
        return db.getTourneyHistory(player);
    }

    public static String [][] getMatchupHistory(String player) {
        return db.getMatchupHistory(player);
    }

    public static Match[] getResults() {
        return WebData.getResults();
    }

    public static String getTourneyName() {
        return WebData.getTourneyName();
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

    public static String[] checkUnknownNames(String [] entrants) {
        return db.getUnknownEntrants(entrants);
    }

    public static String [][] getRankings() {
        return db.getRankings();
    }

    public static String [][] getFilteredRankings(String filter) {
        return db.getFilteredRankings(filter);
    }

    public static String[] getEntrants () {
        return WebData.getEntrants();
    }

    public static void addAlias(String alias, String true_name) {
        db.addAlias(alias, true_name);
    }

    public static void updateName(String old_name, String new_name) {
        db.updateName(old_name, new_name);
    }

    public static void addException(String player, String opponent) {
        db.addException(player, opponent);
    }

    public static void deleteException(String player, String opponent) {
        db.deleteException(player, opponent);
    }

    public static String [] getExceptions(String player) {
        return db.getExceptions(player);
    }

    public static Boolean validURL(String URL) {
        // Check to see if URL is valid or not without having to try it
        String[] url_segs = URL.toLowerCase().split("/");
        // Format
        //  [0] "https:"
        //  [1] ""
        //  [2] "challonge.com"
        //  [3] "{tourney_id}"
        if (url_segs.length == 4) {
            // Check if pointing to challonge.com
            if (url_segs[2].equals("challonge.com")) {
                // Make sure url begins with https://
                if (url_segs[0].equals("https:")) {
                    // Check if pointing to bracket and not just home page
                    if (!url_segs[3].equals("")) return true;
                }
            }
        }
        return false;
    }

    public static int[] getVisualOrder(int i, int size) {
	// If bottom half seed, no next round opponent. Return
        if (i >= size/2) return new int[] {i};
        else {
            int cur_size = size;
            int [] cur_ans = new int [] {i};
            // Keep finding next round opponents until become a lower seed
            while (i < cur_size/2) {
                cur_ans = Bracket.mergeArrays(cur_ans, getVisualOrder( ((cur_size-1)-i), size));
                cur_size/=2;
            }
            return cur_ans;
        }
    }
}
