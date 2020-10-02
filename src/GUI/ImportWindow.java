package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class ImportWindow extends GetLink {

    String title = "Import Results";
    GetAliasWindow GA_window = new GetAliasWindow();
    
    public ImportWindow() {
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
        // Checks if valid URL: if yes, also grabs entrants
        String [] entrants = API.GetEntrants(url);
        if (entrants.length==0) {
            error.setText("Entered url is invalid. Please try again");
            error.setVisible(true);
            return;
        }
        // Check is bracket has not been entered before
        if (!API.CheckBracketNew(url)) {
            error.setText("Bracket data already imported!");
            error.setVisible(true);
            return;
        }
        // All good: grab results
        Match [] results = API.GetResults(url);
        // Look for new names and ask if alias
        int [] unknown_entrant_indices = API.CheckUnknownNames(entrants);
        // -1 is default value. If not -1, there are unknown names
        GA_window.Launch(entrants, results, unknown_entrant_indices);
        window.setVisible(false);
    }
}