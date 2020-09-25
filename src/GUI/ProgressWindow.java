package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class ProgressWindow {

    static JFrame popup = new JFrame("Import Progress");
    static JLabel message = new JLabel("Checking if data is new...", SwingConstants.CENTER);
    static JButton ok_button = new JButton("OK");

    public void Launch(String[] entrants, String url, DBManager db) {

        message.setBounds(0, 10, 300, 20);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok_button.setBounds(70, 50, 160, 50);
        ok_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                ReadFile.clean_tmp_files();
            }
        });

        popup.setLayout(null);
        popup.add(message); popup.add(ok_button);
        popup.setSize(320,150);
        popup.setVisible(true);

        int id = WebData.grab_tourney_id(url);
        // Make sure imported bracket is new
        int status = db.check_bracket_data_new(id);
        if (status == 1) {
            message.setText("Adding new players to database...");
            db.add_players(entrants);

            message.setText("Adding matchup results to database...");
            Match [] results = WebData.grab_results(url);
            db.add_history(results);

            message.setText("Done!");
        }
        else if (status == 0) {
            message.setText("Tourney results already exist in database!");
        }
        else {
            message.setText("Unknown Error");
        }
    }
}