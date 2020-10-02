package MyUtils;

import DBase.DBManager;
import DBase.Credentials.*;

public class API {

    private static DBManager db = new DBManager(DBase.Credentials.USER, DBase.Credentials.PASS);

    public static String [] GetBracket(String url) {
        String [] entrants = WebData.grab_entrants(url);
        // Error out early if no entrants
        if (entrants.length==0) {
            return entrants;
        }
        int [] scores = db.get_scores(entrants);
        Bracket.seed_bracket(entrants, scores);
        return entrants;
    }

    public static Set[] GetSets (String [] entrants) {
        return Bracket.grab_sets(entrants);
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
        //int id = WebData.grab_tourney_id(url);
        // Make sure imported bracket is new
        //int status = db.check_bracket_data_new(id);
        //if (status == 1) {
        db.add_players(entrants);
        //Match [] results = WebData.grab_results(url);
        db.add_history(results);
        //}
        return;
    }

    public static int[] CheckUnknownNames(String [] entrants) {
        int [] unknown_entrant_indices = db.get_override_names(entrants);
        System.out.println("These players are new: ");
        for (int i = 0; i < unknown_entrant_indices.length; i++) {
            if (unknown_entrant_indices[i] == -1) break;
            else {
                System.out.println(entrants[unknown_entrant_indices[i]]);
            }
        }
        return unknown_entrant_indices;
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
}