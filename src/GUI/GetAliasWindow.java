package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import MyUtils.*;

public class GetAliasWindow {

    JFrame window = new JFrame("Get Alias");
    JLabel message = new JLabel("Add alias", SwingConstants.CENTER);
    JButton continue_button = new JButton("Continue");
    JButton skip_button = new JButton("Skip");
    String [] column_names = {"Entrant", "Real Tag"};
    JTable jt;
    JScrollPane sc_pane;

    String [] unknown_entrants;
    String [][] alias_table;

    private void make_table() {
        // Create table
        int size = unknown_entrants.length;
        alias_table = new String[size][2];
        for (int i = 0; i < size; i++) {
            alias_table[i][0] = unknown_entrants[i];
            alias_table[i][1] = "";
        }
    }

    public GetAliasWindow(String [] fed_unknown_entrants) {
        window.setLayout(null);
        // Attach fed in arguments
        unknown_entrants = fed_unknown_entrants;

        // Construct JComponents
        make_table();

        // Not sure how to fix error.
        // Disables entrants names column
        DefaultTableModel tableModel = new DefaultTableModel(alias_table, column_names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                }
                return true;
            }
        };
        jt = new JTable(tableModel);

        sc_pane = new JScrollPane(jt);

        // Set Window Attributes

        // Set fonts and colors

        // Set component sizes
        sc_pane.setSize(400, 250);
        skip_button.setSize(sc_pane.getWidth()/2, 40);
        continue_button.setSize(sc_pane.getWidth()/2, 40);
        message.setSize(300, 20);
        window.setSize(sc_pane.getWidth()+25, sc_pane.getHeight()+skip_button.getHeight()+100);

        // Set component locations
        sc_pane.setLocation(0,0);
        skip_button.setLocation(0, sc_pane.getHeight()+50);
        continue_button.setLocation(sc_pane.getWidth()/2, sc_pane.getHeight()+50);
        message.setLocation(0, 10);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add action listeners
        skip_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                window.dispose();
            }
        });

        continue_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                set_true_names(jt, unknown_entrants);
                window.dispose();
            }
        });

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        // Pack items into window
        window.add(sc_pane);
        window.add(message);
        window.add(skip_button);
        window.add(continue_button);
    }

    public void Launch() {
        window.setVisible(true);
    }

    public void set_true_names(JTable jt, String [] unknown_entrants) {
        for (int i = 0; i < unknown_entrants.length; i++) {
            String true_name = jt.getValueAt(i, 1).toString();
            if (!true_name.equals("")) API.AddAlias(unknown_entrants[i],true_name);
        }
    }
}
