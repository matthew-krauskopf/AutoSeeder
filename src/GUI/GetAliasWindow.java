package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class GetAliasWindow {

    ProgressWindow PG_Window = new ProgressWindow();
    JFrame window = new JFrame("Get Alias");
    JLabel message = new JLabel("Add alias", SwingConstants.CENTER);
    JButton continue_button = new JButton("Continue");
    JButton skip_button = new JButton("Skip");
    String [] column_names = {"Entrant", "Real Tag"};

    public GetAliasWindow() {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
    }

    public void Launch(String[] entrants, Match[] results, String[] unknown_entrants) {

        // Create table
        int size = unknown_entrants.length;
        String [][] alias_table = new String[size][2];
        for (int i = 0; i < size; i++) {
            alias_table[i][0] = unknown_entrants[i];
            alias_table[i][1] = "";
        }

        JTable jt = new JTable(alias_table, column_names);
        jt.setEnabled(true);
        
        JScrollPane sc_pane = new JScrollPane(jt);

        message.setBounds(0, 10, 300, 20);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        skip_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                PG_Window.Launch(entrants, results);
            }
        });

        continue_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                set_true_names(jt, unknown_entrants);
                PG_Window.Launch(entrants, results);
            }
        });

        sc_pane.setBounds(0,0, 400, 250);
        skip_button.setBounds(0, sc_pane.getHeight()+50, sc_pane.getWidth()/2, 40);
        continue_button.setBounds(sc_pane.getWidth()/2, sc_pane.getHeight()+50, sc_pane.getWidth()/2, 40);

        window.add(sc_pane);
        window.setSize(sc_pane.getWidth()+25, sc_pane.getHeight()+skip_button.getHeight()+100);
        window.setLayout(null);
        window.add(message); window.add(skip_button); window.add(continue_button);
        window.setVisible(true);
    }

    public void set_true_names(JTable jt, String [] unknown_entrants) {
        for (int i = 0; i < unknown_entrants.length; i++) {
            String true_name = jt.getValueAt(i, 1).toString();
            if (!true_name.equals("")) API.AddAlias(unknown_entrants[i],true_name);
        }
    }
}