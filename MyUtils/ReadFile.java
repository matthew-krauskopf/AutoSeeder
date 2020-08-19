package MyUtils;

import java.io.*;
import java.util.Scanner;

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
                //System.out.println(lines[counter-1]);
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
            while(scan.hasNextLine()) {
                String cur_line = scan.nextLine().trim();
                // This precedes match data
                if (cur_line.equals("//<![CDATA[")) {
                    line = scan.nextLine().trim();
                    break;
                }
            }
            scan.close();
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File " + file_name + " not found");
            return "";
        }
    }
}