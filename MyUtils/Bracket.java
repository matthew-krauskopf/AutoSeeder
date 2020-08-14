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

        // Print winners' matches
        // Set top and bottom seeds for matchups
        int top = 0, bottom = (int) Math.pow(2.0, (double)help_size) - 1;
        for (int i = 1; (int) Math.pow(2, i-1) < bottom; i+=1) {
            print_winners_round(entrants, top, (int) (bottom/Math.pow(2, i-1)), i);
        }

        // Print Loser's Matches
        // Reset top and bottom seeds for Loser's Round 1
        top = ((bottom+1)/2);
        bottom = (int) (Math.pow(2.0, (double)help_size)) - 1;
        for (int i = 1; bottom != top ; i+= 1) {
            print_losers_round(entrants, top, bottom, i);
            // Remove eliminated from bracket
            bottom -= (int) (Math.ceil((bottom-top)/2.0));
            // Top seed halves every 2 rounds
            if (i % 2 == 1) {
                top /= 2;
            }
        }
        return;
    }

    public static void print_winners_round(String[] entrants, int top, int bottom, int round) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("-".repeat(20) + "Winner's Round " + round + "-".repeat(20));
        // Converge top and bottom
        while (bottom-top >= 1) {
            System.out.println(entrants[top] + " vs " + (bottom <= entrants.length ? entrants[bottom] : "Bye") );
            top++; bottom--;
        }
        System.out.println("-".repeat(56));
    }

    public static void print_losers_round(String[] entrants, int top, int bottom, int round) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("-".repeat(20) + "Loser's Round " + round + "-".repeat(21));
        // Need to maintain original top and bottom. Make copies here
        int cur_top = top, cur_bot = bottom;
        while (cur_bot-cur_top >= 1) {
            // Flip seeds around to avoid double jeopardy
            if (round % 2 == 0) {
                // Determine threshold for flipping
                int middle_seed = ((bottom+top+1)/2);
                int devi = ((bottom-top+1)/4);
                // Print match
                System.out.print(entrants[cur_top] + " vs ");
                if (cur_top < (middle_seed-devi)) {
                    System.out.println(((cur_bot-devi) <= entrants.length ? entrants[cur_bot-devi]: "Bye"));
                }
                else {
                    System.out.println(((cur_bot+devi) <= entrants.length ? entrants[cur_bot+devi]: "Bye"));
                }
            }
            // Straight-forward placing matches
            else {
                System.out.println(entrants[cur_top] + " vs " + (cur_bot <= entrants.length ? entrants[cur_bot] : "Bye") );
            }
            cur_top++; cur_bot--;
        }
        System.out.println("-".repeat(56));
    }

    public static int[] s_to_i(String[] rankings) {
        int[] new_ranks = new int[rankings.length];
        for (int i = 0; i < rankings.length; i++) {
            new_ranks[i] = Integer.parseInt(rankings[i]);
        }
        return new_ranks;
    }
}