import MyUtils.*;
import DBase.*;

class MyBracket
{
    public static void main(String [] args) {
        // Grab entrants
        //String[] entrants = ReadFile.read_file("SampleData/sample_entrants.txt");
        //String[] rankings = ReadFile.read_file("SampleData/sample_rankings.txt");
        ReadFile.clean_tmp_files();
        System.out.println(WebData.grab_tourney_id(args[0]));
        System.exit(0);
        if (args.length == 0) return;
        String [] entrants = WebData.grab_entrants(args[0]);
        // Check if entrants were pulled correctly
        if (entrants.length == 0) {
            System.out.println("Error! Failed to pull entrants list. Aborting....");
            System.exit(1);
        }
        int [] rankings = DBManager.grab_scores(entrants);
        DBManager.create_db();
        DBManager.add_players(entrants);
        // Seed Bracket
        Bracket.seed_bracket(entrants, rankings);
        // Show initial assignments
        Bracket.show_bracket(entrants);
        System.out.println("Grabbing bracket results...");
        Match[] matches = WebData.grab_results(args[0]);
        System.out.println("Adding bracket results...");
        DBManager.add_history(matches);
        // Clean tmp files
        ReadFile.clean_tmp_files();
    }
}