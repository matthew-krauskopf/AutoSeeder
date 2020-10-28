package MyUtils;
import java.lang.Math;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Bracket {

    private static int i_shake_rounds = 0;

    public static void seedBracket(String[] entrants, int[] scores) {
        scorePlayers(entrants, scores);
        quickSort(entrants, 0, entrants.length-1);
        unscorePlayers(entrants);
        // Reverse order of array
        reverse(entrants);
        return;
    }

    public static void shakeupBracket(String[] entrants, MatchUp[] recent_matchups, int shake_rounds) {
        i_shake_rounds = shake_rounds;
        int act_size = entrants.length;
        // Go through and check to see if anyone should be shuffled around
        for (int seed = act_size-1; seed >= 0; seed--) {
            // Get all slated opponents based on seed
            int [] opp_indices = getOpponentSeeds(seed, act_size);
            // Use k to limit number of checked matchups
            for (int j = 0, k = 0; j < opp_indices.length && k < i_shake_rounds; j++){
                // Ignore all byes
                if (!(opp_indices[j] >= act_size)) {
                    if (isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed].opponents)) {
                        //System.out.println(String.format("\n%d %s Should try to avoid playing against %s", k, recent_matchups[seed].player, entrants[opp_indices[j]]));
                        //Boolean success = false;
                        for (int dist = 1; dist <= i_shake_rounds; dist++) {
                            if (shiftSeed(entrants, recent_matchups, opp_indices, seed, dist)) {
                                //System.out.println("Only place to go is up by " + dist  + " !");
                                //success = true;
                                break;
                            }
                            else if (shiftSeed(entrants, recent_matchups, opp_indices, seed, 0-dist)) {
                                //System.out.println("Shifting down by " + dist  + " solves it!");
                                //success = true;
                                break;
                            }
                        }
                        //if (!success) System.out.println("No good resolution found...");
                    }
                    // Increment k since a matchup was checked
                    k++;
                }
            }
        }
    }

    public static void sanityCheck(String[] entrants, MatchUp[] recent_matchups) {
        System.out.println("\n\nTime to sanity check!");
        for (int i = 0; i < recent_matchups.length; i++) {
            System.out.println("Player: " + recent_matchups[i].player);
            System.out.println("Opponents: ");
            for (int j = 0; j < recent_matchups[i].opponents.length; j++) {
                System.out.println("       " + recent_matchups[i].opponents[j]);
            }
        }
        int act_size = entrants.length;
        // Go through and check to see if anyone should be shuffled around
        for (int seed = act_size-1; seed >= 0; seed--) {
            // Get all slated opponents based on seed
            int [] opp_indices = getOpponentSeeds(seed, act_size);
            // Use k to limit number of checked matchups
            for (int j = 0, k = 0; j < opp_indices.length && k < i_shake_rounds; j++){
                // Ignore all byes
                if (!(opp_indices[j] >= act_size)) {
                    if (isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed].opponents)) {
                        System.out.println(String.format("\n%d %s Should try to avoid playing against %s", k, recent_matchups[seed].player, entrants[opp_indices[j]]));
                    }
                    // Increment k since a matchup was checked
                    k++;
                }
            }
        }
    }

    public static Boolean shiftSeed(String [] entrants, MatchUp [] recent_matchups, int[] opp_indices, int seed, int dist) {
        // Check if moving current player up dist seeds resolves matchup conflicts. If not, return false
        // Return false if seed is out of bounds
        if ((seed + dist >= entrants.length) || (seed + dist < 0)) return false;
        // If in bounds, can check higher value seeds
        int [] new_opp_indices = getOpponentSeeds(seed+dist, entrants.length);
        // Go through and check matchup conflicts for current player and proposed swapped player
        for (int j = 0, k = 0; j < new_opp_indices.length && k < i_shake_rounds; j++) {
            if (!(new_opp_indices[j] >= entrants.length)) {
                if (isIn(recent_matchups[new_opp_indices[j]].player, recent_matchups[seed].opponents)) {
                    // Still a conflict.... return
                    return false;
                }
            }
            if ((j < opp_indices.length) && (!(opp_indices[j] >= entrants.length))) {
                // Check for conflicts for swapped player with current player's path
                if (isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed+dist].opponents)) {
                    // Would create new conflict.... return
                    return false;
                }
            }
            k++;
        }
        // If got this far, should be good to swap
        //System.out.println("Should be fine to swap " + entrants[seed] + " with " + entrants[seed+dist]);
        swap(entrants, seed, seed+dist);
        swap(recent_matchups, seed, seed+dist);
        return true;
    }

    public static int [] getOpponentSeeds(int seed, int size) {
        int help_size = (int) Math.ceil(Math.log(size)/Math.log(2));
        int sq_size = (int) Math.pow(2.0, (double)help_size);
        if (seed < (sq_size/2)) return getWinnersPath(seed, sq_size);
        // If eliminated in first round, handle a little differently
        else return mergeArrays(new int[] {(sq_size-1)-seed}, getLosersPath(seed, sq_size, true));
    }

    private static int [] getWinnersPath(int seed, int cur_size) { 
        // Base case for 1st seed
        if (cur_size == 1) return new int [0];
        // Still winners to do 
        else if (seed < cur_size/2) return mergeArrays(new int[]{(cur_size-1)-seed} , getWinnersPath(seed, cur_size/2));
        // Down to losers
        else return mergeArrays(new int[]{(cur_size-1)-seed}, getLosersPath(seed, (cur_size*3/2), false));
    }

    private static int [] getLosersPath(int seed, int cur_size, Boolean first_round) {
        // No work to do when size <= 2
        if (cur_size <= 2) return new int[0];
        // First round of loser's is special: handle accordingly
        if (first_round) {
            // Projected to go 0-2
            if (seed >= (cur_size*3/4)) return new int[] {(cur_size-1)-(seed-(cur_size/2))};
            // Wins loser's round 1 match
            else {
                return mergeArrays(new int[] {(cur_size-1)-(seed-(cur_size/2))}, getLosersPath(seed, ((cur_size*3/4)), false));
            }
        }
        else {
            if (isSquare(cur_size)) {
                // Play it straight
                int top = (cur_size/2);
                // Eliminated this round
                if (seed >= cur_size*.75) {
                    return new int[] {(cur_size-1) - (seed-top)};
                }
                // Wins this round
                else {
                    return mergeArrays(new int[] {(cur_size-1) - (seed-top)}, getLosersPath(seed, (cur_size*3/4), false));
                }
            }
            // Time to get Funky
            else {
                int [] loser_order = getLosersOrder(IntStream.rangeClosed((cur_size*2/3), cur_size-1).toArray());
                // If loser, get index location of seed in loser order, then add to best seed in round to get opponent
                if (seed >= (cur_size*2/3)) {
                    return new int [] {((cur_size*1/3)) + getIndex(seed, loser_order)};
                }
                else {
                    return mergeArrays(new int[] {loser_order[seed-(cur_size*1/3)]}, getLosersPath(seed, (cur_size*2/3), false));
                }
            }
        }
    }

    public static int getIndex(int num, int [] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (num == arr[i]) return i;
        }
        return -1;
    }

    public static Boolean isSquare(int num) {
        int help_size = (int) Math.floor(Math.log(num)/Math.log(2));
        Boolean x = (num == (int) Math.pow(2,help_size));
        return x;
    }

    public static Boolean isIn(String player, String [] opponents) {
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

    public static void scorePlayers(String[] entrants, int[] scores) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = Integer.toString(scores[i]) + " " + entrants[i];
        }
        return;
    }

    public static void unscorePlayers(String[] entrants) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = entrants[i].split(" ",2)[1];
        }
        return;
    }

    public static void quickSort(String[] entrants, int start, int end) {
        // Return if nothing to sort
        if (end-start <= 0) {
            return;
        }
        // Set pivot as mid-point
        int pivot = (int) Math.ceil((end+start)/2.0), pivot_val = getVal(entrants[pivot]);
        // Move pivot to end
        swap(entrants, pivot, end);
        int left = end, right = end+1;
        while (right > left) {
            // Move left bound to first value greater than or equal to pivot
            for (int i = start; i <= end-1; i++) {
                if (getVal(entrants[i]) >= pivot_val) {
                    left = i;
                    break;
                }
            }
            // Move right bound to left until crosses left bound or finds value less than pivot
            for (int i = end-1; i >= left-1; i--) {
                if (getVal(entrants[i]) < pivot_val || i == left-1 || i == 0) {
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
        quickSort(entrants, start, left-1);
        quickSort(entrants, left+1, end);
        return;
    }

    public static int getVal(String s) {
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

    public static void swap(MatchUp[] matchups, int i, int j) {
        // Swap 2 elements in array
        MatchUp temp = matchups[i];
        matchups[i] = matchups[j];
        matchups[j] = temp;
        return;
    }

    public static void showBracket(String[] entrants) {
        // Make bracket 2^n size and figure out byes
        int help_size = (int) Math.ceil(Math.log(entrants.length)/Math.log(2));

        // Print winners' matches
        // Set top and bottom seeds for matchups
        int top = 0, bottom = (int) Math.pow(2.0, (double)help_size) - 1;
        for (int i = 1; (int) Math.pow(2, i-1) < bottom; i+=1) {
            printWinnersRound(entrants, top, (int) (bottom/Math.pow(2, i-1)), i);
        }

        // Print Loser's Matches
        // Reset top and bottom seeds for Loser's Round 1
        top = ((bottom+1)/2);
        bottom = (int) (Math.pow(2.0, (double)help_size)) - 1;
        for (int i = 1; bottom != top ; i+= 1) {
            printLosersRound(entrants, top, bottom, i);
            // Remove eliminated from bracket
            bottom -= (int) (Math.ceil((bottom-top)/2.0));
            // Top seed halves every 2 rounds
            if (i % 2 == 1) {
                top /= 2;
            }
        }
        return;
    }

    public static void printWinnersRound(String[] entrants, int top, int bottom, int round) {
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
    public static int[] getLosersOrder(int[] seeds) {
        return getLosersOrder(seeds, 0);
    }

    public static int[] getLosersOrder(int[] seeds, int dir) {
        // 0 = left, 1 = right
        // If only one seed, return
        if (seeds.length == 1) {
            return seeds;
        }
        int mid_point = seeds.length/2;
        if (dir == 0) { // Left in
            return mergeArrays(getLosersOrder(Arrays.copyOfRange(seeds, 0, mid_point),1),
                                getLosersOrder(Arrays.copyOfRange(seeds, mid_point, seeds.length),1));

        }
        else { // Right in
            return mergeArrays(getLosersOrder(Arrays.copyOfRange(seeds, mid_point, seeds.length),0),
                                getLosersOrder(Arrays.copyOfRange(seeds, 0, mid_point),0));
        }

    }

    public static int[] mergeArrays(int[] arr1, int[] arr2) {
        int [] concat = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, arr2.length);
        return concat;
    }

    public static void printLosersRound(String[] entrants, int top, int bottom, int round) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("-".repeat(20) + "Loser's Round " + round + "-".repeat(21));
        if (round % 2 == 0) {
            // Flip seeds around to avoid double jeopardy
            int middle_seed = ((bottom+top+1)/2);
            int[] bot_half = IntStream.rangeClosed( (int) middle_seed+1, bottom+1).toArray();
            // Get order of loser match seeds
            int [] loser_seeds = getLosersOrder(bot_half);
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

    public static int[] stringToInteger(String[] scores) {
        int[] new_scores = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            new_scores[i] = Integer.parseInt(scores[i]);
        }
        return new_scores;
    }

    public static Set[] getSets(String[] entrants) {
        // Make bracket 2^n size and figure out byes
        int help_size = (int) Math.ceil(Math.log(entrants.length)/Math.log(2));
        Set[] sets = new Set[(( ( (int) (Math.pow(2.0, (double)help_size)) ) - 1) * 2) - 1];

        // Add winner and loser sets
        addWinnersSets(sets, entrants, help_size);
        addLosersSets(sets, entrants, help_size);
        return sets;
    }

    public static void addWinnersSets(Set[] sets, String[] entrants, int help_size) {
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

    public static void addLosersSets(Set[] sets, String[] entrants, int help_size) {
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
                int [] loser_seeds = getLosersOrder(bot_half);
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