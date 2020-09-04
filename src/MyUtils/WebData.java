package MyUtils;

public class WebData {

    public static String[] grab_entrants(String my_url) {
        String file_name = HTML.html_to_file(my_url+"/standings", "tmp/tmp_standings.html");
        // Ensure url worked
        if (file_name.equals("")) return new String[0];
        String [] entrants = ReadFile.read_entrants_html(file_name);
        return entrants;
    }

    public static Match[] grab_results(String my_url) {
        String main_file = HTML.html_to_file(my_url);
        String log_file = HTML.html_to_file(my_url+"/log", "tmp/tmp_log.html");
        if (!main_file.equals("") && !log_file.equals("")) {
            return grab_matches(main_file, log_file);
        }
        else {
            System.out.println("Something went wrong....");
            return new Match[0];
        }
    }

    public static int grab_tourney_id(String url) {
        System.out.println(url);
        String log_file = HTML.html_to_file(url);
        String raw_data = ReadFile.read_match_html(log_file);
        // This works: trust me
        String id = raw_data.split("\"tournament_id\":")[1].split(",\"")[0];
        return Integer.parseInt(id);
    }

    public static Match[] grab_matches(String main_file, String log_file) {
        // Parse HTML
        String raw_matches = ReadFile.read_match_html(main_file);
        String date = ReadFile.read_date_html(log_file);
        System.out.println("Date is " + date);
        // Split up each match entry
        String [] each_match = raw_matches.split("\"tournament_id\":");
        // Allocate curated data array
        Match [] matches = new Match[each_match.length-1];
        // Iterate through each match data
        for (int i = 1; i < each_match.length; i++) {
            matches[i-1] = parse_match_data(each_match[i], date);
        }
        return matches;
    }

    public static Match parse_match_data(String match_data, String date) {
        String [] fields = match_data.split(",\"");
        String p1 = "", p2 = "";
        int p1_score = 0, p2_score = 0, ID = 0;
        // Since we split data on tourney ID, ID is in the first row
        ID = Integer.parseInt(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            if (fields[i].startsWith("display_name\"")) {
                // Get Entrants data
                String data = fields[i].split(":")[1].replace(" ", "");
                if (p1 == "") p1 = data.substring(1, data.length()-1);
                else p2 = data.substring(1, data.length()-1);
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