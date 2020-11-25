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
    JScrollPane alias_sc_pane;
    JTable tourney_table;
    JTable h2h_table;
    JPanel alias_panel;
    JList<String> alias_list;
    JButton apply_button;
    JButton add_name_button;
    JTextField alias_text_field;

    DefaultListModel<String> lm_aliases;

    int table_width = 35 * 16;

    String player;
    String rank;
    String set_count;

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

        makeAliasTab();

        tab_pane = new JTabbedPane();
        tab_pane.addTab("Tournies", tourney_pane);
        tab_pane.addTab("H2H", h2h_pane);
        tab_pane.addTab("Tags", alias_panel);

        // Set Window Attributes
        window.setTitle("Player Info");
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        player_label.setFont(helveticaB50);
        player_label.setForeground(Color.WHITE);

        rank_label.setFont(helveticaB40);
        rank_label.setForeground(Color.WHITE);

        set_count_label.setFont(helvetica30);
        set_count_label.setForeground(Color.WHITE);

        alias_text_field.setFont(acumin16);
        alias_list.setFont(acumin16);

        // Set component sizes
        player_label.setSize(getTextWidth(player_label), 60);
        rank_label.setSize(getTextWidth(rank_label), 42);
        set_count_label.setSize(getTextWidth(set_count_label), 42);
        window.setSize(table_width+28, screenSize.height/2);
        tab_pane.setSize(table_width+12, window.getHeight()*3/5);
        alias_text_field.setSize(tab_pane.getWidth()/4, 30);
        apply_button.setSize(((tab_pane.getWidth()-alias_text_field.getWidth())/2) - 10, 30);
        add_name_button.setSize(apply_button.getWidth(), 30);
        alias_sc_pane.setSize(tab_pane.getWidth()-5, tab_pane.getHeight()-apply_button.getHeight()-apply_button.getY()-28);

        // Set component locations
        tab_pane.setLocation(0, window.getHeight()-tab_pane.getHeight()-40);
        set_count_label.setLocation((window.getWidth()/2)-(set_count_label.getWidth()/2), tab_pane.getY() - set_count_label.getHeight());
        player_label.setLocation((window.getWidth()/2)-(player_label.getWidth()/2), set_count_label.getY() - player_label.getHeight());
        rank_label.setLocation(window.getWidth()-rank_label.getWidth()-25, 10);
        alias_text_field.setLocation(1, 0);
        add_name_button.setLocation(alias_text_field.getWidth()+alias_text_field.getX()+9, 0);
        apply_button.setLocation(add_name_button.getX() + add_name_button.getWidth()+5,0);
        alias_sc_pane.setLocation(0, apply_button.getHeight()+apply_button.getY());

        // Set misc attributes
        tourney_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        h2h_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        alias_sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        apply_button.setEnabled(false);

        // Add action listeners
        apply_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateInfo(alias_list.getSelectedValue());
            }
        });

        add_name_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String new_alias = alias_text_field.getText().trim();
                if (!new_alias.equals("")) {
                    API.addAlias(new_alias, player);
                    lm_aliases.addElement(new_alias);
                    alias_text_field.setText("");
                }
            }
        });

        alias_list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    apply_button.setEnabled((alias_list.getSelectedIndex() > 0 ? true : false));
                }
            }
        });

        // Pack items into window
        window.add(player_label);
        window.add(rank_label);
        window.add(set_count_label);
        window.add(tab_pane);
    }

    private void updateInfo(String new_name) {
        // TODO: Update playerID table
        //       Update alias table
        String old_name = player;
        player = new_name;
        // Reconfigure player label
        player_label.setText((player.length() > 18 ? player.substring(0, 15)+"..." : player));
        player_label.setSize(getTextWidth(player_label), 60);
        player_label.setLocation((window.getWidth()/2)-(player_label.getWidth()/2), set_count_label.getY() - player_label.getHeight());
        // Update list of aliases to have main name on top
        lm_aliases.setElementAt(player, 0);
        lm_aliases.setElementAt(old_name, alias_list.getSelectedIndex());
        // Clear selection and disable change button
        alias_list.clearSelection();
        apply_button.setEnabled(false);
        // Update database tables to reflect change
        API.updateName(old_name, player);
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

    private void makeAliasTab() {
        alias_panel = new JPanel();
        alias_panel.setLayout(null);

        String [] aliases = API.getAliases(player);
        lm_aliases = new DefaultListModel<>();
        lm_aliases.addElement(player);
        for (int i = 0; i < aliases.length; i++) {
            if (!aliases[i].equals(player)) lm_aliases.addElement(aliases[i]);
        }
        alias_list = new JList<>(lm_aliases);
        alias_sc_pane = new JScrollPane(alias_list);

        add_name_button = new JButton("Add Tag");
        apply_button = new JButton("Make Main Tag");
        alias_text_field = new JTextField();

        alias_panel.add(alias_text_field);
        alias_panel.add(add_name_button);
        alias_panel.add(apply_button);
        alias_panel.add(alias_sc_pane);
    }

    private void configureTable(JTable jt) {
        jt.setFont(acumin16);
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
