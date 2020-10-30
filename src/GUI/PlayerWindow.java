package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import MyUtils.*;

public class PlayerWindow {

    JFrame window = new JFrame("Player Info");
    JLabel player_label;
    JTabbedPane tab_pane;
    JScrollPane tourney_pane;
    JScrollPane h2h_pane;
    JTable tourney_table;
    JTable h2h_table;

    Color bg_color = new Color(46, 52, 61);
    Font font = new Font("Helvetica", Font.BOLD, 40);
    Font table_font = new Font("Acumin", 0, 16);

    int table_width = 35 * 16;

    String player;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public PlayerWindow(String fed_player) {
        // Attach passed in arguments
        player = fed_player;

        // Construct JComponents
        player_label = new JLabel(String.format("%s", player));

        makeTourneyTable();
        tourney_pane = new JScrollPane(tourney_table);

        makeH2HTable();
        h2h_pane = new JScrollPane(h2h_table);

        tab_pane = new JTabbedPane();
        tab_pane.addTab("Tournies", tourney_pane);
        tab_pane.addTab("H2H", h2h_pane);

        // Set Window Attributes
        window.setLayout(null);
        //window.setResizeable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        player_label.setFont(font);
        player_label.setForeground(Color.WHITE);

        // Set component sizes
        player_label.setSize(getTextWidth(player_label), 32);
        window.setSize(table_width+28, screenSize.height/2);
        tab_pane.setSize(table_width+12, window.getHeight()/2);

        // Set component locations
        player_label.setLocation((window.getWidth()/2)-(player_label.getWidth()/2), 10);
        tab_pane.setLocation(0, window.getHeight()-tab_pane.getHeight()-40);

        // Set misc attributes
        tourney_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        h2h_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add action listeners

        // Pack items into window
        window.add(player_label);
        window.add(tab_pane);
    }

    private void makeTourneyTable() {
        String [] columns = {"Name","Date","Finish","Entrants"};
        String [][] data = {{"BigAL2Ult","Today", "1", "0"}}; //API.getTourneyHistory();
        tourney_table = new JTable(data, columns);
        configureTable(tourney_table);
        int [] column_sizes = {15, 10, 5, 5};
        resizeTable(tourney_table, column_sizes);
    }

    private void makeH2HTable() {
        String [] columns = {"Opponent","Wins","Loses", "Last Played"};
        String [][] data = {{"Me", "All", "None", "Right now"}}; //API.getMatchupHistory();
        h2h_table = new JTable(data, columns);
        configureTable(h2h_table);
        int [] column_sizes = {15, 5, 5, 10};
        resizeTable(h2h_table, column_sizes);
    }

    private void configureTable(JTable jt) {
        jt.setFont(table_font);
        jt.setEnabled(false);
        // Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);
    }

    private void resizeTable(JTable jt, int [] column_sizes) {
        for (int col = 0; col < jt.getColumnCount(); col++) {
            TableColumn column = jt.getColumnModel().getColumn(col);
            column.setMinWidth(16*column_sizes[col]);
            column.setMaxWidth(column.getMinWidth());
        }
        jt.setRowHeight(32);
    }

    public int getTextWidth(JLabel l) {
        return l.getFontMetrics(l.getFont()).stringWidth(l.getText());
    }

    public void launch() {
        window.setVisible(true);
    }
}
