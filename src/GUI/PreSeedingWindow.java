package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class PreSeedingWindow extends GetLink {

    static String title = "Seed Bracket";
    SeedingWindow S_Window;
    JCheckBox shake_seeding = new JCheckBox("Shuffle seeding");
    
    public PreSeedingWindow() {
        window.setTitle(title);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
        shake_seeding.setBounds(225, 115, 20, 20);
        window.add(shake_seeding);
    }

    @Override
    public void action() {
        String url = area.getText().trim();
        if (shake_seeding.isSelected()) {
            System.out.println("Yes, shake it up!");
        }
        String [] entrants = API.GetBracket(url, shake_seeding.isSelected());
        // No entrants: Wrong URL?
        if (entrants.length<=1) {
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