package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import MyUtils.*;

public class RankingsWindow {

    static String column_names[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
    JFrame window = new JFrame("Rankings");
    JTable jt;
    JScrollPane sc_pane;

    String [][] rankings;

    Font font = new Font("Acumin", 0, 16);
    Color bg_color = new Color(46, 52, 61);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screen_height = screenSize.height*24/25;

    int tot_width;

    private void resizeTable() {
        int [] column_sizes = {3, 15, 3, 3, 4};
        tot_width = 0;
        for (int col = 0; col < column_names.length; col++) {
            TableColumn column = jt.getColumnModel().getColumn(col);
            column.setMinWidth(16*column_sizes[col]);
            column.setMaxWidth(column.getMinWidth());
            tot_width += 16*column_sizes[col];
        }
        jt.setRowHeight(32);
    }

    public RankingsWindow() {
        // Construct JComponents
        rankings = API.getRankings();
        jt = new JTable(rankings, column_names);
        sc_pane = new JScrollPane(jt);
        // Set Window Attributes
        window.setLayout(null);

        // Set fonts and colors
        jt.setFont(font);
        window.getContentPane().setBackground(bg_color);

        // Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);

        // Set component sizes
        resizeTable();
        int table_height = jt.getRowCount()*jt.getRowHeight();
        window.setSize(tot_width+28, (screen_height < table_height ? screen_height : table_height));
        sc_pane.setSize(tot_width+12,window.getHeight()-90);

        // Set component locations
        sc_pane.setLocation(0, 50);

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
