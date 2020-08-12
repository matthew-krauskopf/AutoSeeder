package MyUtils;
import java.lang.Math;

public class Bracket {
    public static void seed_bracket(String[] entrants) {
        return;
    }

    public static void show_bracket(String[] entrants) {
        int n_entrants = entrants.length;
        // Make bracket 2^n size and figure out byes
        int help_size = (int) Math.ceil(Math.log(n_entrants)/Math.log(2));
        // Set top and bottom seeds for matchups
        int top = 0, bottom = (int) Math.pow(2.0, (double)help_size);
        // Print matches
        while (bottom-top >= 1) {
            System.out.println(entrants[top++] + " vs " + (bottom-- <= n_entrants ? entrants[bottom] : "Bye") );
        }
        return;
    }
}