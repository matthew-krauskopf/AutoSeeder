package MyUtils;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class ReadFile
{
    public static String[] read_file(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scan = new Scanner(f);
            // Grab num lines from top of file
            int num_lines = (int) scan.nextInt();
            // Skip to next line
            scan.nextLine();
            // Allocate lines array
            String lines[] = new String[num_lines];
            int counter = 0;
            while(scan.hasNextLine()) {
                lines[counter++] = scan.nextLine().trim();
            }
            scan.close();
            return lines;
        } catch (FileNotFoundException e) {
            System.out.println("File " + file_name + " not found");
            return new String[0];
        }
    }

    public static String read_match_html(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scan = new Scanner(f);
            String line = "";
            Boolean maybe_this_line = false;
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // This precedes match data
                if (cur_line.equals("//<![CDATA[")) {
                    maybe_this_line = true;
                }
                else if (maybe_this_line == true) {
                    // Only time in html has "requested plotter" is same line as tourney ID
                    if (cur_line.contains("requested_plotter")) {
                        line = cur_line;
                        break;
                    }
                    maybe_this_line = false;
                }
            }
            scan.close();
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File " + file_name + " not found");
            return "";
        }
    }

    public static String[] read_entrants_html(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scan = new Scanner(f);
            String lines = "";
            Boolean record = false;
            Boolean this_line = false;
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if (!record && line.matches(".*Match History.*")) {
                    record = true;
                }
                // Blacklist line errors
                else if (record && line.matches(".*<span>.*")) {
                    //System.out.println(line);
                    this_line = true;
                }
                else if (record && this_line) {
                    lines += line.replaceAll("<[^>]*>", "").trim()+"\n";
                    this_line = false;
                }
            }
            scan.close();
            String [] entrants = lines.split("\n");
            return Arrays.copyOfRange(entrants, 0, entrants.length);
        } catch (FileNotFoundException e) {
            System.out.println("File " + file_name + " not found");
            return new String[0];
        }
    }

    public static String read_date_html(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scan = new Scanner(f);
            String date_line = "";
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.matches(".*created_at.*")) {
                    date_line = line;
                    break;
                }
            }
            scan.close();
            String date = date_line.split("created_at\":\"")[1].substring(0, 10);
            return date;
        } catch (FileNotFoundException e) {
            System.out.println("File " + file_name + " not found");
            return "";
        }
    }

    public static void clean_tmp_files() {
        try {
            File f = new File("tmp/tmp_bracket_results.html");
            if (f.exists()) f.delete();

            f = new File("tmp/tmp_log.html");
            if (f.exists()) f.delete();

            f = new File("tmp/tmp_standings.html");
            if (f.exists()) f.delete();
        } catch (Exception e) {
            System.out.println("Failed to delete tmp files...");
        }
    }
}
