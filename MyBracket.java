import MyUtils.*;
import DBase.*;

class MyBracket
{
    public static void main(String [] args) {
        // Grab entrants
        String[] entrants = ReadFile.read_file("SampleData/sample_entrants.txt");
        //String [] entrants = WebData.grab_entrants(args[0]);
        // Check if entrants were pulled correctly
        if (entrants.length == 0) {
            System.exit(1);
        }
        //DBManager.add_players(entrants);
        // Grab Rankings
        String[] rankings = ReadFile.read_file("SampleData/sample_rankings.txt");
        // Convert string rankings to integers
        int[] ranks = Bracket.s_to_i(rankings);
        // Seed Bracket
        Bracket.seed_bracket(entrants, ranks);
        // Show initial assignments
        Bracket.show_bracket(entrants);
        Match[] results = WebData.grab_results(args[0]);
        DBManager.create_db();
        DBManager.add_history(results);
    }
}