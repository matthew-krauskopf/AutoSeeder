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

    public void Launch(String[] entrants, Match[] results, int [] unknown_entrant_indices) {

        // Create table
        int size = unknown_entrant_indices.length;
        String [][] alias_table = new String[size][2];
        for (int i = 0; i < size; i++) {
            alias_table[i][0] = entrants[unknown_entrant_indices[i]];
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
                set_true_names(jt, entrants, unknown_entrant_indices);
                PG_Window.Launch(entrants, results);
            }
        });

        sc_pane.setBounds(0,0, 400, 500);
        skip_button.setBounds(0, 500, 200, 40);
        continue_button.setBounds(201, 500, 200, 40);

        window.add(sc_pane);
        window.setSize(400, 600);
        window.setLayout(null);
        window.add(message); window.add(skip_button); window.add(continue_button);
        window.setVisible(true);
    }

    public void set_true_names(JTable jt, String [] entrants, int [] spots) {
        for (int i = 0; i < spots.length; i++) {
            String true_name = jt.getValueAt(i, 1).toString();
            if (!true_name.equals("")) API.AddAlias(entrants[spots[i]],true_name); //entrants[spots[i]] = true_name;
        }
    }
}