package GUI;

import java.lang.Math;

public class Utils {
    public static Boolean isIn(String s, String [] arr) {
        for (String el : arr) {
            if (s.equals(el)) return true;
        }
        return false;
    }

    public static Boolean validURL(String URL) {
        // Check to see if URL is valid or not without having to try it
        String[] url_segs = URL.toLowerCase().split("/");
        // Format
        //  [0] "https:"
        //  [1] ""
        //  [2] "challonge.com"
        //  [3] "{tourney_id}"
        if (url_segs.length == 4) {
            // Check if pointing to challonge.com
            if (url_segs[2].equals("challonge.com")) {
                // Make sure url begins with https://
                if (url_segs[0].equals("https:")) {
                    // Check if pointing to bracket and not just home page
                    if (!url_segs[3].equals("")) return true;
                }
            }
        }
        return false;
    }

    public static int min(int a, int b) {
        return (a < b ? a : b);
    }

    public static String addSuffix(String num) {
        if (num.endsWith("11") || num.endsWith("12") || num.endsWith("13")) return num+"th";
        if (num.endsWith("1")) return num + "st";
        if (num.endsWith("2")) return num + "nd";
        if (num.endsWith("3")) return num + "rd";
        return num+"th";
    }

    public static int getPercentage(int wins, int losses) {
        int per = (int) ( ((double)wins/(double)(wins+losses)) * 100.0);
        return per;
    }

    public static void quickSort(String[][] arr, int index, int order, int start, int end) {
        // Return if nothing to sort
        if (end-start <= 0) {
            return;
        }
        // Set pivot as mid-point
        int pivot = (int) Math.ceil((end+start)/2.0);
        String pivot_val = arr[pivot][index];
        // Move pivot to end
        swap(arr, pivot, end);
        int left = end, right = end+1;
        while (right > left) {
            // Move left bound to first value greater than or equal to pivot
            for (int i = start; i <= end-1; i++) {
                // If sorting and values are the same, sort secondary by name
                if (arr[i][index].compareTo(pivot_val)*order > 0) {
                    left = i;
                    break;
                }
            }
            // Move right bound to left until crosses left bound or finds value less than pivot
            for (int i = end-1; i >= left-1; i--) {
                int compare_val = arr[i][index].compareTo(pivot_val)*order;
                if (arr[i][index].compareTo(pivot_val)*order < 0 || i == left-1 || i == 0) {
                    right = i;
                    break;
                }
            }
            // Check if bounds crossed
            if (right <= left) {
                // Move pivot to final spot
                swap(arr, left, end);
            }
            else {
                // Swap these values
                swap(arr, left, right);
            }
        }
        // Do left and right partitions (left is location of pivot)
        quickSort(arr, index, order, start, left-1);
        quickSort(arr, index, order, left+1, end);
        return;
    }

    public static void swap(String[][] arr, int i, int j) {
        // Swap 2 elements in array
        String [] temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        return;
    }

    private static String getVal(String[] data, int index) {
        // Return score of Entrant
        return data[index];
    }

    public static Boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }
}
