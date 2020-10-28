package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class ImportWindow extends GetLink {

    String title = "Import Results";
    GetAliasWindow GA_window;
    ProgressWindow PG_Window;
    JLabel dup_label = new JLabel("Bracket data already imported!");

    public ImportWindow() {
        // Set Window Attributes
        window.setTitle(title);

        // Set fonts and colors
        dup_label.setFont(font3);
        dup_label.setForeground(Color.WHITE);

        // Set component sizes
        dup_label.setSize(getTextWidth(dup_label), 20);
        window.setSize((2*offset)+field.getWidth()+edge, submit.getY()+80+offset);

        // Set component locations
        dup_label.setLocation(getCenter(dup_label), example.getHeight()+example.getY());

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
        // Pack items into window
        window.add(dup_label);
    }

    public void launch() {
        error.setVisible(false);
        f_error.setVisible(false);
        dup_label.setVisible(false);
        window.setVisible(true);
    }

    public void launchProgressWindow(String url, String [] entrants) {
        if (!url.equals("test")) {
            Match [] results = API.getResults(url);
            PG_Window = new ProgressWindow(entrants, results);
            PG_Window.launch(entrants, results);
        }
    }

    @Override
    public void action() {
        // Check if URL seems to be valid
        String url = field.getText().trim();
        String [] entrants;
        if (url.equals("test")) {
            entrants = API.getSampleEntrants();
            Match [] results = new Match[0];
        }
        else {
            if (!API.validURL(url)) {
                dup_label.setVisible(false);
                error.setVisible(false);
                f_error.setVisible(true);
                return;
            }
            // Checks if URL indeed works. If so, grabs entrants too
            // TODO: Due to slow speed for htmlunit, get entrants and results all at once
            entrants = API.getEntrants(url);
            if (entrants.length==0) {
                dup_label.setVisible(false);
                f_error.setVisible(false);
                error.setVisible(true);
                return;
            }
            // Check if bracket has not been entered before
            if (!API.checkBracketNew(url)) {
                error.setVisible(false);
                f_error.setVisible(false);
                dup_label.setVisible(true);
                return;
            }
        }
        // Look for new names and ask if alias
        String [] unknown_entrants = API.checkUnknownNames(entrants);
        if (unknown_entrants.length != 0) {
            GA_window = new GetAliasWindow(unknown_entrants);
            // Add action listener to GA window so this window closes at same time
            GA_window.window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    window.setVisible(false);
                    GA_window.dispose();
                    launchProgressWindow(url, entrants);
                }
            });
            GA_window.launch();
        }
        else {
            window.setVisible(false);
            launchProgressWindow(url, entrants);
        }
    }
}