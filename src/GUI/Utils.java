package GUI;

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
}