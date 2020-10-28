package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class RankingsWindow {

    static String columns[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
    JFrame window = new JFrame();
    JTable jt;
    JScrollPane sc_pane;

    String [][] rankings;

    public RankingsWindow() {
        // Construct JComponents
        rankings = API.getRankings();
        jt = new JTable(rankings, columns);
        sc_pane = new JScrollPane(jt);
        // Set Window Attributes

        // Set fonts and colors

        // Set component sizes
        window.setSize(600, 800);

        // Set component locations

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        // Set misc settings
        jt.setEnabled(false);

        // Pack items into window
        window.add(sc_pane);
    }

    public void launch() {
        window.setVisible(true);
    }
}
