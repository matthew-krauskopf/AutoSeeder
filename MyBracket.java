import MyUtils.*;

class MyBracket
{
    public static void main(String [] args)
    {
        // Grab entrants
        String[] entrants = ReadFile.read_file("SampleData/sample_entrants.txt");
        // Grab Rankings
        String[] rankings = ReadFile.read_file("SampleData/sample_rankings.txt");
        // Seed Bracket
        Bracket.seed_bracket(entrants);
        // Show initial assignments
        Bracket.show_bracket(entrants);
    }
}