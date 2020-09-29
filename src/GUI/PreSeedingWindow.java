package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class PreSeedingWindow extends GetLink {

    static String title = "Seed Bracket";
    SeedingWindow S_Window;
    
    public PreSeedingWindow() {
        window.setTitle(title);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
    }

    @Override
    public void action() {
        String url = area.getText().trim();
        String [] entrants = API.GetBracket(url);
        // No entrants: Wrong URL?
        if (entrants.length==0) {
            error.setVisible(true);
            return;
        }
        // Close window
        window.dispose();
        // Show initial assignments
        Set[] sets = API.GetSets(entrants);
        S_Window = new SeedingWindow();
        S_Window.MakeSeedingWindow(entrants, sets);
        S_Window.Launch();
    }
}