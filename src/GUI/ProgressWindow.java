package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class ProgressWindow {

    static JFrame window = new JFrame("Import Progress");
    static JLabel message = new JLabel("Checking if data is new...", SwingConstants.CENTER);
    static JButton ok_button = new JButton("OK");

    String [] entrants;
    Match [] results;

    public ProgressWindow(String[] fed_entrants, Match [] fed_results) {

        // Attach fed in arguments
        entrants = fed_entrants;
        results = fed_results;

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

        // Pack items into window
        window.add(message);
        window.add(ok_button);

        // Set final window attributes
        window.setSize(320,150);
    }

    public void Launch(String[] entrants, Match [] results) {
        window.setVisible(true);
        API.addBracketData(entrants, results);
        message.setText("Done!");
    }
}
