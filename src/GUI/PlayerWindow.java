package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import MyUtils.*;

public class PlayerWindow extends TemplateWindow {

    JLabel player_label;
    JLabel rank_label;
    JLabel set_count_label;
    JTabbedPane tab_pane;
    JScrollPane tourney_pane;
    JScrollPane h2h_pane;
    JTable tourney_table;
    JTable h2h_table;

    Font font = new Font("Helvetica", Font.BOLD, 40);
    Font table_font = new Font("Acumin", 0, 16);

    int table_width = 35 * 16;

    String player;
    String rank;
    String set_count;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public PlayerWindow(String fed_player, String fed_rank, String fed_set_count) {
        // Attach passed in arguments
        player = fed_player;
        rank = fed_rank;
        set_count = fed_set_count;

        // Construct JComponents
        player_label = new JLabel((player.length() > 18 ? player.substring(0, 15)+"..." : player));
        rank_label = new JLabel(rank);
        set_count_label = new JLabel(set_count);

        makeTourneyTable();
        tourney_pane = new JScrollPane(tourney_table);

        makeH2HTable();
        h2h_pane = new JScrollPane(h2h_table);

        tab_pane = new JTabbedPane();
        tab_pane.addTab("Tournies", tourney_pane);
        tab_pane.addTab("H2H", h2h_pane);

        // Set Window Attributes
        window.setTitle("Player Info");
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        player_label.setFont(new Font("Helvetica", Font.BOLD, 50));
        player_label.setForeground(Color.WHITE);

        rank_label.setFont(font);
        rank_label.setForeground(Color.WHITE);

        set_count_label.setFont(new Font("Helvetica", 0, 30));
        set_count_label.setForeground(Color.WHITE);

        // Set component sizes
        player_label.setSize(getTextWidth(player_label), 60);
        rank_label.setSize(getTextWidth(rank_label), 42);
        set_count_label.setSize(getTextWidth(set_count_label), 42);
        window.setSize(table_width+28, screenSize.height/2);
        tab_pane.setSize(table_width+12, window.getHeight()*3/5);

        // Set component locations
        tab_pane.setLocation(0, window.getHeight()-tab_pane.getHeight()-40);
        set_count_label.setLocation((window.getWidth()/2)-(set_count_label.getWidth()/2), tab_pane.getY() - set_count_label.getHeight());
        player_label.setLocation((window.getWidth()/2)-(player_label.getWidth()/2), set_count_label.getY() - player_label.getHeight());
        rank_label.setLocation(window.getWidth()-rank_label.getWidth()-25, 10);

        // Set misc attributes
        tourney_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        h2h_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add action listeners

        // Pack items into window
        window.add(player_label);
        window.add(rank_label);
        window.add(set_count_label);
        window.add(tab_pane);
    }

    private void makeTourneyTable() {
        String [] columns = {"Name","Date","Finish","Entrants"};
        String [][] data = API.getTourneyHistory(player);
        tourney_table = new JTable(data, columns);
        configureTable(tourney_table);
        int [] column_sizes = {15, 10, 5, 5};
        resizeTable(tourney_table, column_sizes);
    }

    private void makeH2HTable() {
        String [] columns = {"Opponent","Wins","Loses", "Last Played"};
        String [][] data = API.getMatchupHistory(player);
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
}