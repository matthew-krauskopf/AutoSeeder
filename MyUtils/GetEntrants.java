package MyUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class GetEntrants {
   
    public static String[] grab_entrants(String my_url) {
        String raw_data = grab_html_data(my_url);
        if (raw_data != "") {
            // Parse names from data
            return grab_names(raw_data);
        }
        else {
            // Return empty array if failed
            return new String[0];
        }
    }

    public static String [] grab_names(String raw_data) {
        // Trim away all data before first entrant
        String step1 = raw_data.split("UserMatchHistory1")[1];
        // Split every entrant into own array element
        String [] entrants = step1.split("&ndash;");
        // Return all entrants. Last array element is not needed
        return Arrays.copyOfRange(entrants, 0, entrants.length-1);
    }

    // Code Shamelessly taken from webpage: https://www.tutorialspoint.com/how-to-read-the-contents-of-a-webpage-into-a-string-in-java
    public static String grab_html_data(String my_url) {
        //Instantiating the URL class.
        try {
            // Fixes 403 error
            System.setProperty("http.agent", "Chrome");
            URL url = new URL(my_url);
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuffer sb = new StringBuffer();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }
            sc.close();
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
            //Removing the HTML tags
            result = result.replaceAll("<[^>]*>", "");
            // Return raw data
            return result;
        } catch(IOException e) {
            System.out.println("Error! " + my_url + " is invalid.");
            return "";
        }
    }
}