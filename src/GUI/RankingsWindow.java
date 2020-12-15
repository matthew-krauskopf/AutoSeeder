package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import MyUtils.API;

public class RankingsWindow extends TemplateWindow {

    static String column_names[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
    JTable jt;
    JScrollPane sc_pane;
    JLabel search_desc = new JLabel("Search:");
    JLabel nodata_label = new JLabel("No data");
    JTextField search_field = new JTextField();
    JButton search_button = new JButton("Go");

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
        jt = new JTable(rankings, column_names);
        resizeTable();
        jt.setFont(acumin16);
        jt.setEnabled(false);
        // Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);

        // Add table action listener
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Get double click
                if (evt.getClickCount() == 2) {
                    int row = jt.rowAtPoint(evt.getPoint());
                    String player = (String)jt.getValueAt(row,1);
                    String rank = Utils.addSuffix((String)jt.getValueAt(row,0));
                    int wins = Integer.parseInt( (String)jt.getValueAt(row,2) );
                    int losses = Integer.parseInt( (String)jt.getValueAt(row,3) );
                    String set_count = String.format("%d - %d (%d", wins, losses, Utils.getPercentage(wins, losses))+"%)";
                    PlayerWindow player_window = new PlayerWindow(player, rank, set_count);
                    // Add action listener with call to update rankings window
                    player_window.addCustomListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            player_window.updateAliasTab();
                            filter(search_field.getText().trim().toLowerCase());
                        }
                    });
                    player_window.launch();
                }
            }
        });
    }

    public RankingsWindow() {
        // Construct JComponents
        String [][] rankings = API.getRankings();
        makeTable(rankings);
        sc_pane = new JScrollPane(jt);

        // Set Window Attributes
        window.setTitle("Rankings");
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        jt.setFont(acumin16);

        search_field.setFont(acumin16);

        search_desc.setFont(acumin16);
        search_desc.setForeground(fg_color);

        nodata_label.setFont(helveticaB40);
        nodata_label.setForeground(fg_color);

        // Map images to buttons
        search_button.setIcon(new ImageIcon("img/search.png"));
        search_button.setOpaque(false);
        search_button.setContentAreaFilled(false);
        search_button.setBorderPainted(false);

        //// Center text in table
        DefaultTableCellRenderer cR = new DefaultTableCellRenderer();
        cR.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jt.getColumnCount(); i++) jt.getColumnModel().getColumn(i).setCellRenderer(cR);

        // Set component sizes
        int table_height = (jt.getRowCount()+2)*jt.getRowHeight();
        window.setSize(tot_width+28, Utils.min(SCREEN_HEIGHT, table_height+50));
        int sc_pane_height = Utils.min(window.getHeight()-90, jt.getRowCount()*jt.getRowHeight()+23);
        sc_pane.setSize(tot_width+12,sc_pane_height);
        search_desc.setSize(getTextWidth(search_desc), 24);
        search_field.setSize(sc_pane.getWidth()/3, 24);
        search_button.setSize(search_field.getHeight(), search_field.getHeight());
        nodata_label.setSize(getTextWidth(nodata_label), 50);

        // Set component locations
        sc_pane.setLocation(0, 50);
        search_field.setLocation((sc_pane.getWidth()*2/3)-30, 13);
        search_desc.setLocation(setLeftOf(search_field, search_desc)-edge, search_field.getY());
        search_button.setLocation(setLeft(sc_pane, search_button)-3, search_field.getY());
        nodata_label.setLocation(setCenter(nodata_label)-edge, edge);

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        ActionListener search_action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filter(search_field.getText().trim().toLowerCase());
            }
        };

        search_button.addActionListener(search_action);
        search_field.addActionListener(search_action);

        // Set misc settings
        sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Redesign window if no data
        if (jt.getRowCount() == 0) {
            sc_pane.setVisible(false);
            search_field.setVisible(false);
            search_button.setVisible(false);
            search_desc.setVisible(false);
            // Resize window in case of no data
            window.setSize(window.getWidth(), setBelow(nodata_label)+50);
        } else nodata_label.setVisible(false);

        // Pack items into window
        window.add(sc_pane);
        window.add(search_desc);
        window.add(search_field);
        window.add(search_button);
        window.add(nodata_label);
    }

    private void filter(String filter) {
        // Remake table based on filter
        window.remove(sc_pane);
        String [][] rankings = (filter.equals("") ? API.getRankings() : API.getFilteredRankings(filter));
        makeTable(rankings);
        // Set location of scroll pane
        sc_pane = new JScrollPane(jt);
        int sc_pane_height = Utils.min(window.getHeight()-90, jt.getRowCount()*jt.getRowHeight()+23);
        sc_pane.setSize(tot_width+12,sc_pane_height);
        sc_pane.setLocation(0, 50);
        sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        window.add(sc_pane);
        // Repaint the screen to show change
        window.repaint();
    }
}
