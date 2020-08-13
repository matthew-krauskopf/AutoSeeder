import MyUtils.*;

class MyBracket
{
    public static void main(String [] args)
    {
        // Grab entrants
        String[] entrants = ReadFile.read_file("SampleData/sample_entrants.txt");
        // Grab Rankings
        String[] rankings = ReadFile.read_file("SampleData/sample_rankings.txt");
        // Convert string rankings to integers
        int[] ranks = Bracket.s_to_i(rankings);
        // Seed Bracket
        Bracket.seed_bracket(entrants, ranks);
        // Show initial assignments
        Bracket.show_bracket(entrants);
    }
}