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
    SpinnerModel sp_model = new SpinnerNumberModel(2, //initial value
                                                   1, //minimum value
                                                   5, //maximum value
                                                   1); //step
    JSpinner rounds_val = new JSpinner(sp_model);
    
    public PreSeedingWindow() {
        window.setTitle(title);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
        shake_seeding.setBounds(225, 115, 20, 20);
        rounds_val.setBounds(225, 135, 30, 25);
        window.add(shake_seeding);
        window.add(rounds_val);
    }

    @Override
    public void action() {
        String url = area.getText().trim();
        int shake_rounds = 0;
        if (shake_seeding.isSelected()) {
            System.out.println("Yes, shake it up!");
            try {
                rounds_val.commitEdit();
                shake_rounds = (Integer) rounds_val.getValue();
            } catch (Exception e) {};
        }
        String [] entrants = API.GetBracket(url, shake_rounds);
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