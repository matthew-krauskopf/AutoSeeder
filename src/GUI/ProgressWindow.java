package GUI;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class ProgressWindow {

    static JFrame window = new JFrame("Import Progress");
    static JLabel message = new JLabel("Checking if data is new...", SwingConstants.CENTER);
    static JButton ok_button = new JButton("OK");

    SwingWorker<Boolean, Integer> worker;

    String [] entrants;
    Match [] results;
    int tourney_id;
    String tourney_name;

    public ProgressWindow(String[] fed_entrants, Match [] fed_results, int fed_tourney_id, String fed_tourney_name) {

        // Attach fed in arguments
        entrants = fed_entrants;
        results = fed_results;
        tourney_id = fed_tourney_id;
        tourney_name = fed_tourney_name;

        // Set Window Attributes
        window.setLayout(null);

        // Set fonts and colors

        // Set component sizes
        message.setSize(300, 20);
        ok_button.setSize(160, 50);

        // Set component locations
        message.setLocation(0, 10);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok_button.setLocation(70, 50);

        // Add action listeners
        ok_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                window.dispose();
            }
        });

        // Add background workers
        worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                API.addBracketData(entrants, results, tourney_id, tourney_name);
                return true;
            }

            @Override
            protected void done() {
                //boolean status;
                //try{
                //    status = get();
                    message.setText("Done!");
                //}
                //catch (InterruptedException e) {}
                //catch (ExecutionException e) {}
            }
        };

        // Pack items into window
        window.add(message);
        window.add(ok_button);

        // Set final window attributes
        window.setSize(320,150);
    }

    public void launch() {
        window.setVisible(true);
        message.setText("Working...");
        worker.execute();
    }
}
