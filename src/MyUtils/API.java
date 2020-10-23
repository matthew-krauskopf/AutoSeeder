package MyUtils;

import DBase.DBManager;
import DBase.Credentials.*;
import java.util.Arrays;

public class API {

    private static DBManager db = new DBManager();   // DBase.Credentials.USER, DBase.Credentials.PASS);

    public static String [] get_sample_entrants() {
        return ReadFile.read_file("sample_entrants.txt");
    }

    public static String [] GetBracket(String url, int shake_rounds) {
        String [] entrants = WebData.grab_entrants(url);
        // Error out early if no entrants
        if (entrants.length==0) {
            return entrants;
        }
        int [] scores = db.get_scores(entrants);
        Bracket.seed_bracket(entrants, scores);
        if (shake_rounds > 0) {
            MatchUp [] recent_matchups = db.get_recent_matchups(entrants);
            Bracket.shakeup_bracket(entrants, recent_matchups, shake_rounds);
            // Used to check if any conflicts still exist
            Bracket.sanity_check(entrants, recent_matchups);
        }
        return entrants;
    }

    public static Set[] GetSets (String [] entrants) {
        Set[] sets = Bracket.grab_sets(entrants);
        SortSets(sets);
        return sets;
    }

    public static void SortSets(Set [] sets) {
        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        int end = sq_entrants/2;
        int [] set_order;
        Set [] temp_copy;
        // Sort winner's sets

        while (tot < sq_entrants-1) {
            set_order = get_visual_order(0, end);
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
        set_order = get_visual_order(0, end);
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
                    int [] l_path = Bracket.get_opp_seeds(prev_seed, sq_entrants);
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

    public static Boolean CheckBracketNew(String url) {
        int id = WebData.grab_tourney_id(url);
        // Make sure imported bracket is new
        int status = db.check_bracket_data_new(id);
        if (status == 1) {
            return true;
        }
        return false;
    }

    public static Match[] GetResults(String url) {
       return WebData.grab_results(url);
    }

    public static void AddBracketData(String [] entrants, Match [] results) {
        db.add_players(entrants);
        db.add_history(results);
        return;
    }

    public static String[] CheckUnknownNames(String [] entrants) {
        String [] unknown_entrants = db.get_unknown_entrants(entrants);
        System.out.println("These players are new: ");
        for (int i = 0; i < unknown_entrants.length; i++) {
            System.out.println(unknown_entrants[i]);
        }
        return unknown_entrants;
    }

    public static String [][] GetRankings() {
        return db.get_rankings();
    }

    public static String[] GetEntrants (String url) {
        return WebData.grab_entrants(url);
    }

    public static void AddAlias(String alias, String true_name) {
        db.add_alias(alias, true_name);
    }

    public static Boolean valid_URL(String URL) {
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

    public static int[] get_visual_order(int i, int size) {
	// If bottom half seed, no next round opponent. Return
        if (i >= size/2) return new int[] {i};
        else {
            int cur_size = size;
            int [] cur_ans = new int [] {i};
            // Keep finding next round opponents until become a lower seed
            while (i < cur_size/2) {
                cur_ans = Bracket.merge_arrays(cur_ans, get_visual_order( ((cur_size-1)-i), size));
                cur_size/=2;
            }
            return cur_ans;
        }
    }
}
