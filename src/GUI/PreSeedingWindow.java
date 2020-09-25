package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class PreSeedingWindow extends GetLink {

    String title = "Seed Bracket";
    SeedingWindow S_Window;
    
    public PreSeedingWindow() {
        window.setTitle(title);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
                S_Window.window.dispose();
            }
        });
    }

    @Override
    public void action(DBManager db) {
        String url = area.getText().trim();
        String [] entrants = WebData.grab_entrants(url);
        if (entrants.length==0) {
            error.setVisible(true);
            return;
        }
        window.dispose();
        int [] scores = db.get_scores(entrants);
        Bracket.seed_bracket(entrants, scores);
        // Show initial assignments
        Bracket.show_bracket(entrants);
        Set[] sets = Bracket.grab_sets(entrants);
        S_Window = new SeedingWindow();
        S_Window.MakeSeedingWindow(entrants, sets);
        S_Window.Launch();
    }
}