package Backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile
{
    static String tmp_directory = "tmp/";
    static String bracket_page = tmp_directory+"tmp_bracket_results.html";
    static String log_page = tmp_directory+"tmp_log.html";
    static String standings_page = tmp_directory+"tmp_standings.html";

    public static Boolean createTmpFolder() {
        File tmp_folder = new File(tmp_directory);
        if (!tmp_folder.mkdir() && !tmp_folder.exists()) {
            System.out.println("Error! tmp/ folder could not be created. Check write permissions. Aborting...");
            return false;
        }
        else return true;
    }

    public static void deleteTmpFolder() {
        if (!new File(tmp_directory).delete()) {
            System.out.println("Error! tmp/ folder could not be deleted.");
        }
    }

    public static Boolean checkNeedJavaScript(String file_name) {
        // Trying to minimlaize the pages parsed with javascript enabled
        try {
            File f = new File(file_name);
            Scanner scan = new Scanner(f);
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // This precedes match data
                if (cur_line.equals("<title>")) {
                    String target_line = scan.nextLine().trim();
                    scan.close();
                    return target_line.equals("Site verification");
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + bracket_page + " not found");
        }
        return false;
    }

    public static String readMatchHTML() {
        try {
            File f = new File(bracket_page);
            Scanner scan = new Scanner(f);
            String line = "";
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // Only time in html has "requested plotter" is same line as tourney ID
                if (cur_line.matches(".*requested_plotter.*")) {
                    line = cur_line;
                    break;
                }
            }
            scan.close();
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File " + bracket_page + " not found");
            return "";
        }
    }

    public static String readBracketStatus() {
        try {
            File f = new File(bracket_page);
            Scanner scan = new Scanner(f);
            String state = "";
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // Only time in html has "requested plotter" is same line as tourney ID
                if (cur_line.matches(".*requested_plotter.*")) {
                    state = cur_line.split("\"state\"")[1].split("\"")[1];
                    break;
                }
            }
            scan.close();
            return state;
        } catch (FileNotFoundException e) {
            System.out.println("File " + bracket_page + " not found");
            return "";
        }
    }

    public static String readTourneyIDHTML() {
        try {
            File f = new File(standings_page);
            Scanner scan = new Scanner(f);
            String line = "";
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // This precedes match data
                if (cur_line.matches(".*data-tournament-id=.*")) {
                    line = cur_line.split("data-tournament-id=")[1].split("\"")[1];
                    break;
                }
            }
            scan.close();
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File " + standings_page + " not found");
            return "";
        }
    }

    public static String[] readEntrantsHTML() {
        try {
            File f = new File(standings_page);
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
            return Utils.spliceRange(entrants, 0, entrants.length);
        } catch (FileNotFoundException e) {
            System.out.println("File " + standings_page + " not found");
            return new String[0];
        }
    }

    public static String readDateHTML() {
        try {
            File f = new File(log_page);
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
            System.out.println("File " + log_page + " not found");
            return "";
        }
    }

    public static String readTourneyNameHTML() {
        try {
            File f = new File(log_page);
            Scanner scan = new Scanner(f);
            String name_line = "";
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.matches(".*data-tournament-name=.*")) {
                    name_line = line;
                    break;
                }
            }
            scan.close();
            String name = name_line.split("data-tournament-name=\"")[1].split("\"")[0];
            return name;
        } catch (FileNotFoundException e) {
            System.out.println("File " + log_page + " not found");
            return "";
        }
    }

    public static void cleanTmpFiles() {
        try {
            File f = new File(bracket_page);
            if (f.exists()) f.delete();

            f = new File(log_page);
            if (f.exists()) f.delete();

            f = new File(standings_page);
            if (f.exists()) f.delete();
        } catch (Exception e) {
            System.out.println("Failed to delete tmp files...");
        }
    }
}

/* Unused Code. Keeping just in case

public static String[] readFile(String file_name) {
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

*/
