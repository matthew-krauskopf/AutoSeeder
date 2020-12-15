package Backend;

public class Bracket {

    private static int i_shake_rounds = 0;

    public static void seedBracket(String[] entrants, int[] scores) {
        scorePlayers(entrants, scores);
        Utils.quickSort(entrants, 0, entrants.length-1);
        unscorePlayers(entrants);
        // Reverse order of array
        Utils.reverse(entrants);
        return;
    }

    public static void shakeupBracket(String[] entrants, MatchUp[] recent_matchups, int shake_rounds) {
        i_shake_rounds = shake_rounds;
        int act_size = entrants.length;
        // Go through every entrant and check to see if anyone should be shuffled around
        for (int seed = act_size-1; seed >= 0; seed--) {
            // Get all slated opponents based on seed
            int [] opp_indices = getOpponentSeeds(seed, act_size);
            // Go through every k slated matchups for current entrant
            for (int j = 0, k = 0; j < opp_indices.length && k < i_shake_rounds; j++){
                // Ignore all byes
                if (!(opp_indices[j] >= act_size)) {
                    if (Utils.isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed].opponents)) {
                        //System.out.println(String.format("\n%d %s Should try to avoid playing against %s", k, recent_matchups[seed].player, entrants[opp_indices[j]]));
                        //Boolean success = false;
                        for (int dist = 1; dist <= i_shake_rounds; dist++) {
                            if (shiftSeed(entrants, recent_matchups, opp_indices, seed, dist)) {
                                //success = true;
                                break;
                            }
                            else if (shiftSeed(entrants, recent_matchups, opp_indices, seed, 0-dist)) {
                                //success = true;
                                break;
                            }
                        }
                        //if (!success) System.out.println(String.format("%s is stuck playing against %s", entrants[seed], recent_matchups[opp_indices[j]].player));
                        break;
                    }
                    // Increment k since a matchup was checked
                    k++;
                }
            }
        }
    }

    public static int[][] sanityCheck(String[] entrants, MatchUp[] recent_matchups, int shake_rounds) {
        int [][] conflicts = new int[getNumSets(entrants.length)][2];
        int i_conflict = 0;
        //System.out.println("\n\nTime to sanity check!");
        for (int i = 0; i < recent_matchups.length; i++) {
            //System.out.println("Player: " + recent_matchups[i].player);
            //System.out.println("Opponents: ");
            for (int j = 0; j < recent_matchups[i].opponents.length; j++) {
                //System.out.println("       " + recent_matchups[i].opponents[j]);
            }
        }
        int act_size = entrants.length;
        // Go through and check to see if anyone should be shuffled around
        for (int seed = act_size-1; seed >= 0; seed--) {
            // Get all slated opponents based on seed
            int [] opp_indices = getOpponentSeeds(seed, act_size);
            // Use k to limit number of checked matchups
            for (int j = 0, k = 0; j < opp_indices.length && k < shake_rounds; j++){
                // Ignore all byes
                if (!(opp_indices[j] >= act_size)) {
                    if (Utils.isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed].opponents)) {
                        // Trick to only record conflicts once
                        if (seed < opp_indices[j]) {
                            conflicts[i_conflict][0] = seed;
                            conflicts[i_conflict++][1] = opp_indices[j];
                            //System.out.println(String.format("\n%d %s Should try to avoid playing against %s", k, recent_matchups[seed].player, entrants[opp_indices[j]]));
                        }
                    }
                    // Increment k since a matchup was checked
                    k++;
                }
            }
        }
        return Utils.splice(conflicts, i_conflict);
    }

    public static Boolean shiftSeed(String [] entrants, MatchUp [] recent_matchups, int[] opp_indices, int seed, int dist) {
        // Check if moving current player up dist seeds resolves matchup conflicts. If not, return false
        // Return false if seed is out of bounds
        if ((seed + dist >= entrants.length) || (seed + dist < 0)) return false;
        // If in bounds, can check higher value seeds
        int [] new_opp_indices = getOpponentSeeds(seed+dist, entrants.length);
        // Go through and check matchup conflicts for current player and proposed swapped player
        for (int j = 0, k = 0; j < new_opp_indices.length && k <= i_shake_rounds; j++) {
            if (!(new_opp_indices[j] >= entrants.length)) {
                //System.out.print(String.format("\nChecking if %s vs %s is okay...",entrants[seed], recent_matchups[new_opp_indices[j]].player));
                if (Utils.isIn(recent_matchups[new_opp_indices[j]].player, recent_matchups[seed].opponents) ||
                    recent_matchups[new_opp_indices[j]].player.equals(entrants[seed])) {
                    //System.out.print(".. NO!");
                    // Still a conflict.... return
                    return false;
                }
                //System.out.print(".. YES!");
            }
            if ((j < opp_indices.length) && (!(opp_indices[j] >= entrants.length))) {
                // Check for conflicts for swapped player with current player's path
                //System.out.print(String.format("\nChecking if %s vs %s is okay...", entrants[seed+dist], recent_matchups[opp_indices[j]].player));
                if (Utils.isIn(recent_matchups[opp_indices[j]].player, recent_matchups[seed+dist].opponents) ||
                    recent_matchups[opp_indices[j]].player.equals(entrants[seed+dist])) {
                    // Would create new conflict.... return
                    //System.out.print(".. NO!");
                    return false;
                }
                //System.out.print(".. YES!");
            }
            k++;
        }
        // If got this far, should be good to swap
        //System.out.println(String.format("%s Swap %s with %s to avoid vs %s", seed, entrants[seed], entrants[seed+dist], blocked));
        Utils.swap(entrants, seed, seed+dist);
        Utils.swap(recent_matchups, seed, seed+dist);
        return true;
    }

    public static int [] getOpponentSeeds(int seed, int size) {
        int help_size = Utils.get2Power(size);
        int sq_size = Utils.get2ToPower(help_size);
        if (seed < (sq_size/2)) return getWinnersPath(seed, sq_size);
        // If eliminated in first round, handle a little differently
        else return Utils.mergeArrays(new int[] {(sq_size-1)-seed}, getLosersPath(seed, sq_size, true));
    }

    private static int [] getWinnersPath(int seed, int cur_size) {
        // Base case for 1st seed
        if (cur_size == 1) return new int [0];
        // Still winners to do
        else if (seed < cur_size/2) return Utils.mergeArrays(new int[]{(cur_size-1)-seed} , getWinnersPath(seed, cur_size/2));
        // Down to losers
        else return Utils.mergeArrays(new int[]{(cur_size-1)-seed}, getLosersPath(seed, (cur_size*3/2), false));
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
                return Utils.mergeArrays(new int[] {(cur_size-1)-(seed-(cur_size/2))}, getLosersPath(seed, ((cur_size*3/4)), false));
            }
        }
        else {
            if (Utils.isSquare(cur_size)) {
                // Play it straight
                int top = (cur_size/2);
                // Eliminated this round
                if (seed >= cur_size*.75) {
                    return new int[] {(cur_size-1) - (seed-top)};
                }
                // Wins this round
                else {
                    return Utils.mergeArrays(new int[] {(cur_size-1) - (seed-top)}, getLosersPath(seed, (cur_size*3/4), false));
                }
            }
            // Time to get Funky
            else {
                int [] loser_order = getLosersOrder(Utils.getClosedRange((cur_size*2/3), cur_size-1));
                // If loser, get index location of seed in loser order, then add to best seed in round to get opponent
                if (seed >= (cur_size*2/3)) {
                    return new int [] {((cur_size*1/3)) + Utils.getIndex(seed, loser_order)};
                }
                else {
                    return Utils.mergeArrays(new int[] {loser_order[seed-(cur_size*1/3)]}, getLosersPath(seed, (cur_size*2/3), false));
                }
            }
        }
    }

    private static void scorePlayers(String[] entrants, int[] scores) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = Integer.toString(scores[i]) + " " + entrants[i];
        }
        return;
    }

    private static void unscorePlayers(String[] entrants) {
        // Attach score of player in scores to entrant
        for (int i = 0; i < entrants.length; i++) {
            entrants[i] = entrants[i].split(" ",2)[1];
        }
        return;
    }

    // Overload to enable default val for dir
    private static int[] getLosersOrder(int[] seeds) {
        return getLosersOrder(seeds, 0);
    }

    private static int[] getLosersOrder(int[] seeds, int dir) {
        // 0 = left, 1 = right
        // If only one seed, return
        if (seeds.length == 1) {
            return seeds;
        }
        int mid_point = seeds.length/2;
        if (dir == 0) { // Left in
            return Utils.mergeArrays(getLosersOrder(Utils.spliceRange(seeds, 0, mid_point),1),
                                getLosersOrder(Utils.spliceRange(seeds, mid_point, seeds.length),1));

        }
        else { // Right in
            return Utils.mergeArrays(getLosersOrder(Utils.spliceRange(seeds, mid_point, seeds.length),0),
                                getLosersOrder(Utils.spliceRange(seeds, 0, mid_point),0));
        }

    }

    public static Set[] getSets(String[] entrants) {
        // Make bracket 2^n size and figure out byes
        int help_size = Utils.get2Power(entrants.length);
        Set[] sets = new Set[(Utils.get2ToPower(help_size)*2)-3];

        // Add winner and loser sets
        addWinnersSets(sets, entrants, help_size);
        addLosersSets(sets, entrants, help_size);
        return sets;
    }

    public static int getNumSets(int num_entrants) {
        int help_size = Utils.get2Power(num_entrants);
        return (Utils.get2ToPower(help_size)*2)-3;
    }

    private static void addWinnersSets(Set[] sets, String[] entrants, int help_size) {
        // Converge top and bottom
        int set_count = 0, top = 0, bottom = Utils.get2ToPower(help_size) - 1;
        for (int i = 1; Utils.get2ToPower(i-1) < bottom; i++) {
            top = 0;
            int cur_bot = (int) (bottom/Utils.get2ToPower(i-1));
            while (cur_bot-top >= 1) {
                sets[set_count++] = new Set(entrants[top], (cur_bot < entrants.length ? entrants[cur_bot] : "Bye" ), top+1, cur_bot+1);
                top++; cur_bot--;
            }
        }
    }

    private static void addLosersSets(Set[] sets, String[] entrants, int help_size) {
        // Prints matchups based on starting and ending seed logic
        int bottom = Utils.get2ToPower(help_size) - 1;
        int top = ((bottom+1)/2);
        // Starting adding sets after all winners sets+
        int set_count = bottom;
        for (int round = 1; bottom != top; round+=1) {
            if (round % 2 == 0) {
                // Flip seeds around to avoid double jeopardy
                int middle_seed = ((bottom+top+1)/2);
                int[] bot_half = Utils.getClosedRange(middle_seed+1, bottom+1);
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
            bottom -= Utils.roundUp((bottom-top)/2.0);
            // Top seed halves every 2 rounds
            if (round % 2 == 1) {
                top /= 2;
            }
        }
    }
}

/*  Unused Code. Keeping for now just in case

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

    private static void printWinnersRound(String[] entrants, int top, int bottom, int round) {
        // Prints matchups based on starting and ending seed logic
        System.out.println("-".repeat(20) + "Winner's Round " + round + "-".repeat(20));
        // Converge top and bottom
        while (bottom-top >= 1) {
            System.out.println((top+1) + " " + entrants[top] + " vs " + (bottom < entrants.length ? (bottom+1) + " " + entrants[bottom] : "Bye") );
            top++; bottom--;
        }
        System.out.println("-".repeat(56));
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
*/
