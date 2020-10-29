package MyUtils;

import java.util.Scanner;
import java.net.URL;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.WebClient;

public class HTML {

    // Support optional file name via overloading
    public static String htmlToFile(String my_url) {
        return htmlToFileHelper(my_url, "tmp/tmp_bracket_results.html");
    }

    public static String htmlToFile(String my_url, String tmp_file) {
        return htmlToFileHelper(my_url, tmp_file);
    }

    public static String htmlToFileHelper(String my_url, String tmp_file) {
        //Instantiating the URL class.
        File myFile = new File(tmp_file);
        try {
            URL url = new URL(my_url);
            //Retrieving the contents of the specified page
            // Bunch of settings I don't understand
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.setJavaScriptTimeout(1);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setCssEnabled(false);
            // This takes sooooooooo long
            HtmlPage myPage = ((HtmlPage) webClient.getPage(url));
            // Open new file to dump html to
            myFile.createNewFile();
            // Write to file
            FileWriter fWrite = new FileWriter(myFile);
            BufferedWriter bWrite = new BufferedWriter(fWrite);
            bWrite.write(myPage.asXml());
            // Close buffer
            bWrite.close();
            webClient.close();
            return tmp_file;
        } catch(IOException e) {
            return "";
        }
    }
}

/* Unused code. Keeping just in case

    public static String htmlToString(String my_url) {
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
            return "";
        }
    }
*/