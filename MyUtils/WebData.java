package MyUtils;

import java.util.Arrays;

public class WebData {

    public static String[] grab_entrants(String my_url) {
        String raw_data = HTML.html_to_string(my_url+"/standings");
        if (raw_data != "") {
            // Parse names from data
            return grab_names(raw_data);
        }
        else {
            // Return empty array if failed
            return new String[0];
        }
    }

    public static String [] grab_names(String raw_data) {
        // Trim away all data before first entrant
        String step1 = raw_data.split("UserMatchHistory1")[1];
        // Split every entrant into own array element
        String [] entrants = step1.split("&ndash;");
        for (int i = 0; i < entrants.length; i++) {
            // Remove result elements
            entrants[i] = entrants[i].replace("W&nbsp;", "").replace("L&nbsp;", "");
            // Remove ranking if at front of name
            if (entrants[i].startsWith(Integer.toString(i+1))) {
                entrants[i] = entrants[i].substring(Integer.toString(i+1).length());
            }
        }
        // Return all entrants. Last array element is not needed
        return Arrays.copyOfRange(entrants, 0, entrants.length-1);
    }

    public static Match[] grab_results(String my_url) {
        String file_name = HTML.html_to_file(my_url);
        if (file_name != "") {
            System.out.println("******************\n" + file_name);
            return grab_matches(file_name);
        }
        else {
            System.out.println("Something went wrong....");
            return new Match[0];
        }
    }

    public static Match[] grab_matches(String file_name) {
        // Parse HTML
        String raw_matches = ReadFile.read_match_html(file_name);
        // Split up each match entry
        String [] each_match = raw_matches.split("\"tournament_id\":");
        // Allocate curated data array
        Match [] matches = new Match[each_match.length-1];
        // Iterate through each match data
        for (int i = 1; i < each_match.length; i++) {
            matches[i-1] = parse_match_data(each_match[i]); 
        }
        return matches;
    }

    public static Match parse_match_data(String match_data) {
        String [] fields = match_data.split(",\"");
        String p1 = "", p2 = "", date = "";
        int p1_score = 0, p2_score = 0, ID = 0;
        // Since we split data on tourney ID, ID is in the first row
        ID = Integer.parseInt(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            if (fields[i].startsWith("underway_at\"") ) {
                String data = fields[i].split(":")[1];
                date = (data.startsWith("null") ? "null" : data.substring(1, 11)); 
            }
            else if (fields[i].startsWith("display_name\"")) {
                // Get Entrants data
                String data = fields[i].split(":")[1].replace(" ", "");
                if (p1 == "") p1 = data.substring(1, data.length()-1);
                else p2 = data.substring(1, data.length()-1);
            }
            else if (fields[i].startsWith("scores\"")) {
                String data = fields[i].split(":")[1];
                p1_score = (int) data.charAt(1);
                p2_score = (int) data.charAt(3);
            }
        }
        return (p1_score > p2_score ? new Match(p1, p2, date, ID) : new Match(p2, p1, date, ID));
    }
}