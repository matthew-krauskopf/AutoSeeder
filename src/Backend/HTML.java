package Backend;

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class HTML {

    public static void makeStandingsFile(String url) {
        makeHTMLFile(url+"/standings", ReadFile.standings_page);
    }

    public static void makeResultsFile(String url) {
        makeHTMLFile(url, ReadFile.bracket_page);
    }

    public static void makeLogFile(String url) {
        makeHTMLFile(url+"/log", ReadFile.log_page);
    }

    public static void makeHTMLFile(String url, String file_name) {
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        try {
            System.setProperty("http.agent", "Chrome");
            //Retrieving the contents of the specified page
            System.out.println("Grabbing " + url);
            URL cur_url = new URL(url);
            InputStream is = cur_url.openConnection().getInputStream();
            File myFile = new File(file_name);
            // Write to file
            FileWriter fWrite = new FileWriter(myFile);
            BufferedWriter bWrite = new BufferedWriter(fWrite);
            int i = 0;
            while ((i = is.read()) != -1) {
                bWrite.write((char) i);
            }
            // Close buffer
            bWrite.close();
            endTime = System.nanoTime();
            System.out.println("Time: " + ((endTime-startTime)/10000000));
            return;
        } catch(IOException e) {
            System.out.println("HTML Error occurred... Unknown why");
            return;
        }
    }
}

/* Unused code. Keeping just in case

    // Support optional file name via overloading
    public static String htmlToFile(String my_url) {
        return htmlToFileHelper(my_url, "tmp/tmp_bracket_results.html");
    }


    public static void closeHTML() {
        webClient.close();
    }

    public static String htmlToFile(String my_url, String tmp_file) {
        return htmlToFileHelper(my_url, tmp_file);
    }

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

    public static void makeHTMLFiles(String url, int num_needed) {
        //Instantiating the URL class.
        String [][] data = {{url+"/standings", "tmp/tmp_standings.html"},
                            {url, "tmp/tmp_bracket_results.html"},
                            {url+"/log", "tmp/tmp_log.html"}};
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        try {
            //Retrieving the contents of the specified page
            // Bunch of settings I don't understand
            for (int i = 0; i < num_needed; i++) {
                System.out.println("Grabbing " + data[i][0]);
                URL cur_url = new URL(data[i][0]);
                File myFile = new File(data[i][1]);
                HtmlPage myPage = ((HtmlPage) webClient.getPage(cur_url));
                // Open new file to dump html to
                myFile.createNewFile();
                // Write to file
                FileWriter fWrite = new FileWriter(myFile);
                BufferedWriter bWrite = new BufferedWriter(fWrite);
                bWrite.write(myPage.asXml());
                // Close buffer
                bWrite.close();
            }
            endTime = System.nanoTime();
            System.out.println("Time: " + ((endTime-startTime)/10000000));
            return;
        } catch(IOException e) {
            return;
        }
    }

    /*public static String htmlToFileHelper(String my_url, String tmp_file) {
        //Instantiating the URL class.
        System.out.println("Called");
        File myFile = new File(tmp_file);
        // If file already exists, just return
        if (myFile.exists()) {
            System.out.println("    Well that was pointless");
            return tmp_file;
        }
        try {
            URL url = new URL(my_url);
            //Retrieving the contents of the specified page
            // Bunch of settings I don't understand
            //WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //webClient.setJavaScriptTimeout(1);
            //webClient.getOptions().setThrowExceptionOnScriptError(false);
            //webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            //webClient.getOptions().setCssEnabled(false);
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
            //webClient.close();
            return tmp_file;
        } catch(IOException e) {
            return "";
        }
    }*/

    //static WebClient webClient;

    /*public static void wakeUp() {
        // Call creation of webClient at launch of program to save time during actual import
        setWebClient();
        try {
            URL url = new URL("https://challonge.com");
            HtmlPage myPage = ((HtmlPage) webClient.getPage(url));
            return;
        } catch(IOException e) {
            return;
        }
    }*/

    /*public static void stopWakeUp() {
        webClient.close();
        setWebClient();
    }*/

    /*public static void setWebClient() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setJavaScriptTimeout(1);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
    }

//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.WebClient;

    //HtmlPage myPage = ((HtmlPage) webClient.getPage(cur_url));
    // Open new file to dump html to
    //myFile.createNewFile();
    //bWrite.write(myPage.asXml());

*/