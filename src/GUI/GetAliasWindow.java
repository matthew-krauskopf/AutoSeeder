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

    int edge = 16;
    Font font = new Font("Acumin", 0, 16);
    Color bg_color = new Color(46, 52, 61);

    private void make_table() {
        // Create table
        int size = unknown_entrants.length;
        alias_table = new String[size][2];
        for (int i = 0; i < size; i++) {
            alias_table[i][0] = unknown_entrants[i];
            alias_table[i][1] = "";
        }
    }

    public void resize_table() {
        final TableColumnModel columnModel = jt.getColumnModel();
        for (int col = 0; col < column_names.length; col++) {
            columnModel.getColumn(col).setWidth(100);
        }
        //jt.setColumnWidth(100);
        jt.setRowHeight(32);
    }

    public GetAliasWindow(String [] fed_unknown_entrants) {
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
        resize_table();
        sc_pane = new JScrollPane(jt);

        // Set Window Attributes
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        // TODO Find how to center align text
        jt.setFont(font);
        window.getContentPane().setBackground(bg_color);
        sc_pane.setForeground(bg_color);
        message.setForeground(Color.WHITE);

        // Set component sizes
        sc_pane.setSize(300, jt.getRowHeight()*unknown_entrants.length+20);
        skip_button.setSize(sc_pane.getWidth()/2-20, 40);
        continue_button.setSize(skip_button.getWidth(), skip_button.getHeight());
        message.setSize(300, 20);

        // Set component locations
        sc_pane.setLocation(edge,edge);
        skip_button.setLocation(edge+6, sc_pane.getY()+sc_pane.getHeight()+10);
        continue_button.setLocation(skip_button.getX()+skip_button.getWidth()+12, skip_button.getY());
        message.setLocation(0, 10);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Set Scrollbar Policies
        sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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

        // Set final window attributes
        window.setSize(sc_pane.getX()+sc_pane.getWidth()+(edge*2), skip_button.getY()+(skip_button.getHeight()*2)+edge);
        // TODO find way to restrict size of window. This does not work
        window.setMaximumSize(new Dimension(2, 2));
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
