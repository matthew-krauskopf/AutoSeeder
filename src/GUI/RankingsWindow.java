package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class RankingsWindow {
    
    static String columns[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
    static JFrame window = new JFrame();

    public RankingsWindow() {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //popup.dispose();
                window.dispose();
            }
        });
    }

    public static void Launch(DBManager db) {
        String [][] rankings = db.get_rankings();
        JTable jt = new JTable(rankings, columns);
        jt.setEnabled(false);
        JScrollPane sc_pane = new JScrollPane(jt);
        
        window.add(sc_pane);
        window.setSize(600, 800);
        window.setVisible(true);
    }
}