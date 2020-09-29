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

    public static int AddBracketData(String url, String [] entrants) {
        int id = WebData.grab_tourney_id(url);
        // Make sure imported bracket is new
        int status = db.check_bracket_data_new(id);
        if (status == 1) {
            db.add_players(entrants);
            Match [] results = WebData.grab_results(url);
            db.add_history(results);

        }
        return status;
    }

    public static String [][] GetRankings() {
        return db.get_rankings();
    }
}