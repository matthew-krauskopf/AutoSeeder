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
        dup_label.setSize(get_text_width(dup_label), 20);
        window.setSize((2*offset)+field.getWidth()+edge, submit.getY()+80+offset);

        // Set component locations
        dup_label.setLocation(get_center(dup_label), example.getHeight()+example.getY());

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

    public void Launch() {
        error.setVisible(false);
        f_error.setVisible(false);
        dup_label.setVisible(false);
        window.setVisible(true);
    }

    @Override
    public void action() {
        // Check if URL seems to be valid
        String url = field.getText().trim();
        String [] entrants;
        if (url.equals("test")) {
            entrants = API.get_sample_entrants();
            Match [] results = new Match[0];
            window.setVisible(false);
        }
        else {
            if (!API.valid_URL(url)) {
                dup_label.setVisible(false);
                error.setVisible(false);
                f_error.setVisible(true);
                return;
            }
            // Checks if URL indeed works. If so, grabs entrants too
            entrants = API.GetEntrants(url);
            if (entrants.length==0) {
                dup_label.setVisible(false);
                f_error.setVisible(false);
                error.setVisible(true);
                return;
            }
            // Check if bracket has not been entered before
            if (!API.CheckBracketNew(url)) {
                error.setVisible(false);
                f_error.setVisible(false);
                dup_label.setVisible(true);
                return;
            }
        }
        // Look for new names and ask if alias
        String [] unknown_entrants = API.CheckUnknownNames(entrants);
        // -1 is default value. If not -1, there are unknown names
        if (unknown_entrants.length != 0) {
            GA_window = new GetAliasWindow(unknown_entrants);
            GA_window.Launch();
        }
        // TODO: Find way to wait til Alias window is done

        // All good: grab results
        if (!url.equals("test")) {
            Match [] results = API.GetResults(url);
            PG_Window = new ProgressWindow();
            PG_Window.Launch(entrants, results);
        }
        window.setVisible(false);
    }
}