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
    JLabel search_desc = new JLabel("Filter:");
    JTextField search_field = new JTextField();
    JButton search_button = new JButton("Go");

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

    public void makeTable(String [][] rankings) {
        //rankings = API.getRankings();
        jt = new JTable(rankings, column_names);
        resizeTable();
        jt.setFont(font);
        // Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);
    }

    public RankingsWindow() {
        // Construct JComponents
        String [][] rankings = API.getRankings();
        makeTable(rankings);
        sc_pane = new JScrollPane(jt);

        // Set Window Attributes
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        jt.setFont(font);

        search_field.setFont(font);

        search_desc.setFont(font);
        search_desc.setForeground(Color.WHITE);

        // Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);

        // Set component sizes
        int table_height = jt.getRowCount()*jt.getRowHeight();
        window.setSize(tot_width+28, (screen_height < table_height ? screen_height : table_height));
        int sc_pane_height = (window.getHeight()-90 < jt.getRowCount()*jt.getRowHeight()+23 ? window.getHeight()-90 : jt.getRowCount()*jt.getRowHeight()+23);
        sc_pane.setSize(tot_width+12,sc_pane_height);
        search_desc.setSize(getTextWidth(search_desc), 24);
        search_field.setSize(sc_pane.getWidth()/3, 24);
        search_button.setSize(search_field.getHeight(), search_field.getHeight());

        // Set component locations
        sc_pane.setLocation(0, 50);
        search_field.setLocation((sc_pane.getWidth()*2/3)-30, 13);
        search_desc.setLocation(search_field.getX()-search_desc.getWidth()-10, search_field.getY());
        search_button.setLocation(sc_pane.getWidth()-search_button.getWidth()-3, search_field.getY());

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        search_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filter(search_field.getText().trim());
            }
        });

        // Set misc settings
        jt.setEnabled(false);
        sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Pack items into window
        window.add(sc_pane);
        window.add(search_desc);
        window.add(search_field);
        window.add(search_button);
    }

    private void filter(String filter) {
        // Remake table based on filter
        window.remove(sc_pane);
        String [][] rankings = (filter.equals("") ? API.getRankings() : API.getFilteredRankings(filter));
        makeTable(rankings);
        // Set location of scroll pane
        sc_pane = new JScrollPane(jt);
        int sc_pane_height = (window.getHeight()-90 < jt.getRowCount()*jt.getRowHeight()+23 ? window.getHeight()-90 : jt.getRowCount()*jt.getRowHeight()+23);
        sc_pane.setSize(tot_width+12,sc_pane_height);
        sc_pane.setLocation(0, 50);
        sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        window.add(sc_pane);
        // Hack to refresh screen
        window.setVisible(false);
        window.setVisible(true);
    }

    public int getTextWidth(JLabel l) {
        return l.getFontMetrics(l.getFont()).stringWidth(l.getText());
    }

    public void launch() {
        window.setVisible(true);
    }
}
