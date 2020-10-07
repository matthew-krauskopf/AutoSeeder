package MyUtils;
import java.lang.Math;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Bracket {
    public static void seed_bracket(String[] entrants, int[] scores) {
        score_players(entrants, scores);
        quick_sort(entrants, 0, entrants.length-1);
        unscore_players(entrants);
        // Reverse order of array
        reverse(entrants);
        return;
    }

    public static void shakeup_bracket(String[] entrants, MatchUp[] recent_matchups) {
        /*for (int i = 0; i < recent_matchups.length; i++) {
            System.out.println("Player: " + recent_matchups[i].player);
            System.out.println("Opponents: ");
            for (int j = 0; j < recent_matchups[i].opponents.length; j++) {
                System.out.println("       " + recent_matchups[i].opponents[j]);
            }
        }*/
        int help_size = (int) Math.ceil(Math.log(entrants.length)/Math.log(2));
        int sq_size = (int) Math.pow(2.0, (double)help_size);
        int act_size = entrants.length;

        // Go through and check to see if anyone should be shuffled around
        for (int seed = 0; seed < act_size; seed++) {
            // Get all slated opponents based on seed
            int [] opp_indices = get_opp_seeds(seed, sq_size);
            // Use k to limit number of checked matchups
            for (int j = 0, k = 0; j < opp_indices.length && k < 2; j++){
                // Ignore all byes
                if (!(opp_indices[j] >= act_size)) {
                    if (is_in(recent_matchups[opp_indices[j]].player, recent_matchups[seed].opponents)) {
                        // TODO Write code to shift player up or down in bracket
                        System.out.println(String.format("%d %s Should try to avoid playing against %s", k, recent_matchups[seed].player, entrants[opp_indices[j]]));
                    }
                    // Increment k since a matchup was checked
                    k++;
                }
            }
        }
    }

    public static int [] get_opp_seeds(int seed, int sq_size) {
        if (seed < (sq_size/2)) return get_winners_path(seed, sq_size);
        // If eliminated in first round, handle a little differently
        else return merge_arrays(new int[] {(sq_size-1)-seed}, get_losers_path(seed, sq_size, true));
    }

    private static int [] get_winners_path(int seed, int cur_size) { 
        // Base case for 1st seed
        if (cur_size == 1) return new int [0];
        // Still winners to do 
        else if (seed < cur_size/2) return merge_arrays(new int[]{(cur_size-1)-seed} , get_winners_path(seed, cur_size/2));
        // Down to losers
        else return merge_arrays(new int[]{(cur_size-1)-seed}, get_losers_path(seed, (cur_size*3/2), false));
    }

    private static int [] get_losers_path(int seed, int cur_size, Boolean first_round) {
        // No work to do when size <= 2
        if (cur_size <= 2) return new int[0];
        if (first_round) {
            if (seed >= (cur_size*3/4)) return new int[] {(cur_size-1)-(seed-(cur_size/2))};
            else {
                return merge_arrays(new int[] {(cur_size-1)-(seed-(cur_size/2))}, get_losers_path(seed, ((cur_size*3/4)), false));
            }
        }
        else {
            if (is_square(cur_size)) {
                // Play it straight
                int top = (cur_size/2);
                // Eliminated this round
                if (seed >= cur_size*.75) {
                    return new int[] {(cur_size-1) - (seed-top)};
                }
                // Will be eliminated next round
                else {
                    return merge_arrays(new int[] {(cur_size-1) - (seed-top)}, get_losers_path(seed, (cur_size*3/4), false));
                }
            }
            // Time to get Funky
            else {
                int [] loser_order = get_losers_order(IntStream.rangeClosed((cur_size*2/3), cur_size-1).toArray());
                // If loser, get index location of seed in loser order, then add to best seed in round to get opponent
                if (seed >= (cur_size*2/3)) {
                    return new int [] {((cur_size*1/3)) + get_index(seed, loser_order)};
                }
                else {
                    return merge_arrays(new int[] {loser_order[seed-(cur_size*1/3)]}, get_losers_path(seed, (cur_size*2/3), false));
                }
            }
        }
    }

    public static int get_index(int num, int [] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (num == arr[i]) return i;
        }
        return -1;
    }

    public static Boolean is_square(int num) {
        int help_size = (int) Math.floor(Math.log(num)/Math.log(2));
        Boolean x = (num == (int) Math.pow(2,help_size));
        return x;
    }

    public static Boolean is_in(String player, String [] opponents) {
        for (int i = 0; i < opponents.length; i++) {
            if (player.equals(opponents[i])) return true;
        }
        return false;
    }

    public static void reverse (String[] entrants) {
        int top = entrants.length;
        for (int i = 0; i < top; i++, top--) {
            swap(entrants, i, top-1);
        }
    }

    public static void score_players(String[] entrants, int[] scores) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = Integer.toString(scores[i]) + " " + entrants[i];
        }
        return;
    }

    public static void unscore_players(String[] entrants) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = entrants[i].split(" ",2)[1];
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
        // Return score of Entrant
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
            System.out.println((top+1) + " " + entrants[top] + " vs " + (bottom < entrants.length ? (bottom+1) + " " + entrants[bottom] : "Bye") );
            top++; bottom--;
        }
        System.out.println("-".repeat(56));
    }

    // Overload to enable default val for dir
    public static int[] get_losers_order(int[] seeds) {
        return get_losers_order(seeds, 0);
    }

    public static int[] get_losers_order(int[] seeds, int dir) {
        // 0 = left, 1 = right
        // If only one seed, return
        if (seeds.length == 1) {
            return seeds;
        }
        int mid_point = seeds.length/2;
        if (dir == 0) { // Left in
            return merge_arrays(get_losers_order(Arrays.copyOfRange(seeds, 0, mid_point),1),
                                get_losers_order(Arrays.copyOfRange(seeds, mid_point, seeds.length),1));

        }
        else { // Right in
            return merge_arrays(get_losers_order(Arrays.copyOfRange(seeds, mid_point, seeds.length),0),
                                get_losers_order(Arrays.copyOfRange(seeds, 0, mid_point),0));
        }

    }

    public static int[] merge_arrays(int[] arr1, int[] arr2) {
        int [] concat = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, arr2.length);
        return concat;
    }

    public static void print_losers_round(String[] entrants, int top, int bottom, int round) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("-".repeat(20) + "Loser's Round " + round + "-".repeat(21));
        if (round % 2 == 0) {
            // Flip seeds around to avoid double jeopardy
            int middle_seed = ((bottom+top+1)/2);
            int[] bot_half = IntStream.rangeClosed( (int) middle_seed+1, bottom+1).toArray();
            // Get order of loser match seeds
            int [] loser_seeds = get_losers_order(bot_half);
            for (int i = 0; i < loser_seeds.length; i++){
                System.out.print((top+i+1) + " " + entrants[top+i] + " vs ");
                System.out.println((((loser_seeds[i]-1) < entrants.length ? loser_seeds[i] + " " + entrants[loser_seeds[i]-1]: (loser_seeds[i]) + " Bye ")));
            }
        }
        else {
            // Need to maintain original top and bottom. Make copies here
            int cur_top = top, cur_bot = bottom;
            while (cur_bot-cur_top >= 1) {
                // Straight-forward placing matches
                System.out.println((cur_top+1) + " " + (cur_top < entrants.length ? entrants[cur_top] : "Bye") + " vs " + (cur_bot < entrants.length ? (cur_bot + 1) + " " + entrants[cur_bot] : (cur_bot+1) +  " Bye") );
                cur_top++; cur_bot--;
            }
        }
        System.out.println("-".repeat(56));
    }

    public static int[] s_to_i(String[] scores) {
        int[] new_scores = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            new_scores[i] = Integer.parseInt(scores[i]);
        }
        return new_scores;
    }

    public static Set[] grab_sets(String[] entrants) {
        // Make bracket 2^n size and figure out byes
        int help_size = (int) Math.ceil(Math.log(entrants.length)/Math.log(2));
        Set[] sets = new Set[(( ( (int) (Math.pow(2.0, (double)help_size)) ) - 1) * 2) - 1];

        // Add winner and loser sets
        add_winners_sets(sets, entrants, help_size);
        add_losers_sets(sets, entrants, help_size);
        return sets;
    }

    public static void add_winners_sets(Set[] sets, String[] entrants, int help_size) {
        // Converge top and bottom
        int set_count = 0, top = 0, bottom = (int) Math.pow(2.0, (double)help_size) - 1;
        for (int i = 1; (int) Math.pow(2, i-1) < bottom; i+=1) {
            top = 0;
            int cur_bot = (int) (bottom/Math.pow(2, i-1));
            while (cur_bot-top >= 1) {
                sets[set_count++] = new Set(entrants[top], (cur_bot < entrants.length ? entrants[cur_bot] : "Bye" ), top+1, cur_bot+1);
                top++; cur_bot--;
            }
        }
    }

    public static void add_losers_sets(Set[] sets, String[] entrants, int help_size) {
        // Prints matchups based on starting and ending seed logic
        int bottom = (int) (Math.pow(2.0, (double)help_size)) - 1;
        int top = ((bottom+1)/2);
        // Starting adding sets after all winners sets+
        int set_count = bottom;
        for (int round = 1; bottom != top; round+=1) {
            if (round % 2 == 0) {
                // Flip seeds around to avoid double jeopardy
                int middle_seed = ((bottom+top+1)/2);
                int[] bot_half = IntStream.rangeClosed( (int) middle_seed+1, bottom+1).toArray();
                // Get order of loser match seeds
                int [] loser_seeds = get_losers_order(bot_half);
                for (int i = 0; i < loser_seeds.length; i++){
                    sets[set_count++] = new Set(entrants[top+i], (loser_seeds[i]-1) < entrants.length ? entrants[loser_seeds[i]-1] : "Bye", top+i+1, loser_seeds[i]);
                }
            }
            else {
                // Need to maintain original top and bottom. Make copies here
                int cur_top = top, cur_bot = bottom;
                while (cur_bot-cur_top >= 1) {
                    // Straight-forward placing matches
                    sets[set_count++] = new Set((cur_top < entrants.length ? entrants[cur_top] : "Bye"), (cur_bot < entrants.length ? entrants[cur_bot] : "Bye"), cur_top+1, cur_bot+1);
                    cur_top++; cur_bot--;
                }
            }
            bottom -= (int) (Math.ceil((bottom-top)/2.0));
            // Top seed halves every 2 rounds
            if (round % 2 == 1) {
                top /= 2;
            }
        }
    }
}