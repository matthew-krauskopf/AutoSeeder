package MyUtils;

public class WebData {

    static String main_page = "tmp/tmp_bracket_results.html";
    static String log_page = "tmp/tmp_log.html";
    static String standings_page = "tmp/tmp_standings.html";

    public static String[] getEntrants() {
        String [] entrants = ReadFile.readEntrantsHTML(standings_page);
        return entrants;
    }

    public static Match[] getResults() {
        Match [] matches = getMatches();
        return matches;
    }

    public static String getTourneyName() {
        String name = ReadFile.readTourneyNameHTML(log_page);
        return name;
    }

    public static int getTourneyID() {
        String id = ReadFile.readTourneyIDHTML(standings_page);
        // Couldn't find an ID.. return -2 error code
        if (id.equals("")) return -2;
        return Integer.parseInt(id);
    }

    private static Match[] getMatches() {
        // Parse HTML
        String raw_matches = ReadFile.readMatchHTML(main_page);
        String date = ReadFile.readDateHTML(log_page);
        // Split up each match entry
        String [] each_match = raw_matches.split("\"tournament_id\":");
        // Allocate curated data array
        Match [] matches = new Match[each_match.length-1];
        // Iterate through each match data
        for (int i = 1; i < each_match.length; i++) {
            matches[i-1] = parseMatchData(each_match[i], date);
        }
        return matches;
    }

    private static Match parseMatchData(String match_data, String date) {
        String [] fields = match_data.split(",\"");
        String p1 = "", p2 = "";
        int p1_score = 0, p2_score = 0, ID = 0;
        // Since we split data on tourney ID, ID is in the first row
        ID = Integer.parseInt(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            if (fields[i].startsWith("display_name\"")) {
                // Get Entrants data
                String data = fields[i].split(":")[1];
                // Remove "" at end of name
                data = data.substring(1, data.length() - (data.endsWith("\"") ? 1 : 0));
                if (p1 == "") p1 = data;
                else p2 = data;
            }
            else if (fields[i].startsWith("scores\"")) {
                String [] data = fields[i].split(":")[1].split(",");
                p1_score = Integer.parseInt(data[0].replace("[",""));
                p2_score = Integer.parseInt(data[1].replace("]",""));
            }
        }
        // Set marker in case match was a forfeit
        if (p1_score < 0 || p2_score < 0) {
            p1 = "";
            p2 = "";
        }
        return (p1_score > p2_score ? new Match(p1, p2, date, ID) : new Match(p2, p1, date, ID));
    }
}