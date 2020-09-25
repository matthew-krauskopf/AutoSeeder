package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class ImportWindow extends GetLink {


    String title = "Import Results";
    static ProgressWindow PG_window = new ProgressWindow();
    
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
    public void action(DBManager db) {
        String url = area.getText().trim();
        // Checks if valid URL: if yes, also grabs entrants
        String [] entrants = WebData.grab_entrants(url);
        if (entrants.length==0) {
            error.setVisible(true);
            return;
        }
        window.setVisible(false);
        PG_window.Launch(entrants, url, db);
    }
}