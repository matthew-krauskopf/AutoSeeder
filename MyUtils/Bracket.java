package MyUtils;
import java.lang.Math;

public class Bracket {
    public static void seed_bracket(String[] entrants, int[] rankings) {
        rank_players(entrants, rankings);
        quick_sort(entrants, 0, entrants.length-1);
        return;
    }

    public static void rank_players(String[] entrants, int[] rankings) {
        // Attach rank of player in rankings to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = Integer.toString(rankings[i]) + " " + entrants[i];
        }
        return;
    }

    public static void quick_sort(String[] entrants, int start, int end) {
        // Return if nothing to sort
        if (end-start <= 0) {
            return;
        }
        // Set pivot as mid-point
        int pivot = (int) Math.ceil((end+start)/2.0), pivot_val = get_val(entrants[pivot]);
        // Move pivot to end
        swap(entrants, pivot, end);
        int left = end, right = end+1;
        while (right > left) {
            // Move left bound to first value greater than or equal to pivot
            for (int i = start; i <= end-1; i++) {
                if (get_val(entrants[i]) >= pivot_val) {
                    left = i;
                    break;
                }
            }
            // Move right bound to left until crosses left bound or finds value less than pivot
            for (int i = end-1; i >= left-1; i--) {
                if (get_val(entrants[i]) < pivot_val || i == left-1 || i == 0) {
                    right = i;
                    break;
                }
            }
            // Check if bounds crossed
            if (right <= left) {
                // Move pivot to final spot
                swap(entrants, left, end);
            }
            else {
                // Swap these values
                swap(entrants, left, right);
            }
        }
        // Do left and right partitions (left is location of pivot)
        quick_sort(entrants, start, left-1);
        quick_sort(entrants, left+1, end);
        return;
    }

    public static int get_val(String s) {
        // Return Rank of Entrant
        int val = Integer.parseInt(s.split(" ")[0]);
        return val;
    }

    public static void swap(String[] entrants, int i, int j) {
        // Swap 2 elements in array
        String temp = entrants[i];
        entrants[i] = entrants[j];
        entrants[j] = temp;
        return;
    }

    public static void show_bracket(String[] entrants) {
        // Make bracket 2^n size and figure out byes
        int help_size = (int) Math.ceil(Math.log(entrants.length)/Math.log(2));
        // Set top and bottom seeds for matchups
        int top = 0, bottom = (int) Math.pow(2.0, (double)help_size);
        // Print matches
        print_round(entrants, top, bottom, "Winner's Round 1");
        // Reset top and bottom seeds for Loser's Round 1
        bottom = (int) (Math.pow(2.0, (double)help_size));
        top = (bottom/2);
        print_round(entrants, top, bottom, "Loser's Predicted Round 1");
        return;
    }

    public static void print_round(String[] entrants, int top, int bottom, String caption) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("--------------------" + caption + "--------------------");
        while (bottom-top >= 1) {
            System.out.println(entrants[top++] + " vs " + (bottom-- <= entrants.length ? entrants[bottom] : "Bye") );
        }
        System.out.println("--------------------" + "-".repeat(caption.length()) + "--------------------");
    }

    public static int[] s_to_i(String[] rankings) {
        int[] new_ranks = new int[rankings.length];
        for (int i = 0; i < rankings.length; i++) {
            new_ranks[i] = Integer.parseInt(rankings[i]);
        }
        return new_ranks;
    }
}
