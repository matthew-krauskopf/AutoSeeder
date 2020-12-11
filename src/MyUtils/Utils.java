package MyUtils;

import java.util.Arrays;

public class Utils {

    public static int[] getVisualOrder(int i, int size) {
	// If bottom half seed, no next round opponent. Return
        if (i >= size/2) return new int[] {i};
        else {
            int cur_size = size;
            int [] cur_ans = new int [] {i};
            // Keep finding next round opponents until become a lower seed
            while (i < cur_size/2) {
                cur_ans = mergeArrays(cur_ans, getVisualOrder( ((cur_size-1)-i), size));
                cur_size/=2;
            }
            return cur_ans;
        }
    }

    public static Boolean isIn(String s, String [] arr) {
        for (String el : arr) {
            if (s.equals(el)) return true;
        }
        return false;
    }

    public static MatchUp[] merge(MatchUp [] exceptions, MatchUp [] recent_matchups) {
        MatchUp [] ans = new MatchUp[exceptions.length];
        for (int i = 0; i < exceptions.length; i++) {
            ans[i] = new MatchUp(exceptions[i].player, mergeArrays(exceptions[i].opponents, recent_matchups[i].opponents) );
        }
        return ans;
    }

    public static String[] mergeArrays(String [] arr1, String[] arr2) {
        String [] concat = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, arr2.length);
        return concat;
    }

    public static int[] mergeArrays(int[] arr1, int[] arr2) {
        int [] concat = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, arr2.length);
        return concat;
    }

    public static void sortSets(Set [] sets) {
        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        int end = sq_entrants/2;
        int [] set_order;
        Set [] temp_copy;

        // Sort winner's sets
        while (tot < sq_entrants-1) {
            set_order = getVisualOrder(0, end);
            temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                sets[cur+tot] = temp_copy[set_order[cur]];
            }
            tot += end;
            end /= 2;
        }

        // Sort loser's sets
        // Sort first round just like winner's, then use that to build rest of rounds
        end = sq_entrants/4;
        set_order = getVisualOrder(0, end);
        temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
        for (int cur = 0; cur < end ; cur++) {
            sets[cur+tot] = temp_copy[set_order[cur]];
        }
        tot += end;

        // Now do rest of rounds
        int round = 2;
        while (tot < sets.length) {
            temp_copy = Arrays.copyOfRange(sets, tot, tot+end);
            int bot_seed = temp_copy[0].h_seed;
            for (int cur = 0; cur < end ; cur++) {
                // Even rounds: drop down from winners
                if (round % 2 == 0) {
                    int prev_seed = sets[(tot-end)+cur].h_seed - 1;
                    int [] l_path = Bracket.getOpponentSeeds(prev_seed, sq_entrants);
                    int next_seed = l_path[l_path.length-1]+1;
                    sets[cur+tot] = temp_copy[next_seed-bot_seed];
                }
                // Odd rounds: prev two come together
                else {
                    // Get the highest seed from the two matches that feed in
                    int prev_seed_1 = sets[(tot-(end*2))+(cur*2)].h_seed - 1;
                    int prev_seed_2 = sets[(tot-(end*2))+(cur*2)+1].h_seed - 1;
                    int prev_seed = (prev_seed_1 < prev_seed_2 ? prev_seed_1 : prev_seed_2);
                    // Move set of highest seed to correct spot
                    sets[cur+tot] = temp_copy[(prev_seed+1)-bot_seed];
                }
            }
            tot += end;
            round++;
            // Number of sets in loser's only cuts in half every other round
            if (round % 2 == 1) end /= 2;
        }
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

    private static int getVal(String s) {
        // Return score of Entrant
        int val = Integer.parseInt(s.split(" ")[0]);
        return val;
    }

    public static void reverse (String[] entrants) {
        int top = entrants.length;
        for (int i = 0; i < top; i++, top--) {
            swap(entrants, i, top-1);
        }
    }

    public static Boolean isSquare(int num) {
        int help_size = (int) Math.floor(Math.log(num)/Math.log(2));
        Boolean x = (num == (int) Math.pow(2,help_size));
        return x;
    }

    public static int getIndex(int num, int [] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (num == arr[i]) return i;
        }
        return -1;
    }
}
