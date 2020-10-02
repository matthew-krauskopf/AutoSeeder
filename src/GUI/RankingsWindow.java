package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class RankingsWindow {
    
    static String columns[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
    JFrame window = new JFrame();

    public RankingsWindow() {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
    }

    public void Launch() {
        String [][] rankings = API.GetRankings();
        JTable jt = new JTable(rankings, columns);
        jt.setEnabled(false);
        JScrollPane sc_pane = new JScrollPane(jt);
        
        window.add(sc_pane);
        window.setSize(600, 800);
        window.setVisible(true);
    }
}