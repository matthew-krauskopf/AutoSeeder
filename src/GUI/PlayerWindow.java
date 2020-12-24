package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import Backend.API;

public class PlayerWindow extends TemplateWindow {

    AliasTab alias_tab;
    ExceptionsTab exceptions_tab;

    JLabel player_label;
    JLabel rank_label;
    JLabel set_count_label;
    JTabbedPane tab_pane;
    JScrollPane tourney_pane;
    JScrollPane h2h_pane;
    JTable tourney_table;
    JTable h2h_table;

    final int TABLE_WIDTH = 35 * 16;

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
        tab_pane = new JTabbedPane();

        makeTourneyTable();
        tourney_pane = new JScrollPane(tourney_table);

        makeH2HTable();
        h2h_pane = new JScrollPane(h2h_table);

        alias_tab = new AliasTab();
        alias_tab.config();

        exceptions_tab = new ExceptionsTab();
        exceptions_tab.config();

        tab_pane.addTab("Tournies", tourney_pane);
        tab_pane.addTab("H2H", h2h_pane);
        tab_pane.addTab("Tags", alias_tab.getPanel());
        tab_pane.addTab("Exceptions", exceptions_tab.getPanel());

        // Set Window Attributes
        window.setTitle("Player Info");
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        player_label.setFont(helveticaB50);
        player_label.setForeground(fg_color);

        rank_label.setFont(helveticaB40);
        rank_label.setForeground(fg_color);

        set_count_label.setFont(helvetica30);
        set_count_label.setForeground(fg_color);

        // Set component sizes
        player_label.setSize(getTextWidth(player_label), 60);
        rank_label.setSize(getTextWidth(rank_label), 42);
        set_count_label.setSize(getTextWidth(set_count_label), 42);
        window.setSize(TABLE_WIDTH+28, SCREEN_SIZE.height/2);
        tab_pane.setSize(TABLE_WIDTH+12, window.getHeight()*3/5);
        alias_tab.setSizes();
        exceptions_tab.setSizes();

        // Set component locations
        tab_pane.setLocation(0, setAboveOf(window, tab_pane)-40);
        set_count_label.setLocation(setCenter(set_count_label), setAbove(tab_pane, set_count_label));
        player_label.setLocation(setCenter(player_label), setAbove(set_count_label, player_label));
        rank_label.setLocation(setLeft(window, rank_label)-25, edge);
        alias_tab.setLocations();
        exceptions_tab.setLocations();

        // Set misc attributes
        tourney_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        h2h_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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
        // Add sorting by column when clicking column header
        addSortToHeader(tourney_table, data);
        int [] column_sizes = {15, 10, 5, 5};
        resizeTable(tourney_table, column_sizes);
    }

    private void makeH2HTable() {
        String [] columns = {"Opponent","Wins","Loses", "Last Played"};
        String [][] data = API.getMatchupHistory(player);
        h2h_table = new JTable(data, columns);
        configureTable(h2h_table);
        // Add sorting by column when clicking column header
        addSortToHeader(h2h_table, data);
        int [] column_sizes = {15, 5, 5, 10};
        resizeTable(h2h_table, column_sizes);
    }

    private void addSortToHeader(JTable jt, String [][] data) {
        jt.getTableHeader().addMouseListener(new MouseAdapter() {
            int selected_col = 0;
            @Override
            public void mouseClicked(MouseEvent evt) {
                int column = jt.columnAtPoint(evt.getPoint());
                selected_col = (column == selected_col ? -1 : column);
                Utils.quickSort(data, column, (selected_col >= 0 ? 1 : -1), 0, data.length-1);
                window.repaint();
            }
        });
    }

    private void configureTable(JTable jt) {
        jt.setFont(acumin16);
        jt.setEnabled(false);
        // Disable column draggin
        jt.getTableHeader().setReorderingAllowed(false);
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

    public void addCustomListener(ActionListener al) {
        alias_tab.addCustomListener(al);
    }

    public void updateAliasTab() {
        alias_tab.updateInfo();
    }

    public class TabWindow {
        JScrollPane sc_pane;
        JPanel main_panel;
        JList<String> player_list;
        JButton add_button = new JButton();
        JButton apply_button = new JButton();
        JTextField text_field;
        DefaultListModel<String> lm;

        int min_list_amt = 1;

        public JPanel getPanel() {
            return main_panel;
        }

        public void setSizes() {
            text_field.setSize(tab_pane.getWidth()/4, 30);
            apply_button.setSize(((tab_pane.getWidth()-text_field.getWidth())/2)-edge, 30);
            add_button.setSize(apply_button.getWidth(), 30);
            sc_pane.setSize(tab_pane.getWidth()-5, tab_pane.getHeight()-apply_button.getHeight()-apply_button.getY()-28);
        }

        public void setLocations() {
            text_field.setLocation(1, 0);
            add_button.setLocation(setRight(text_field)+9, 0);
            apply_button.setLocation(setRight(add_button)+5, 0);
            sc_pane.setLocation(0, setBelow(apply_button));
        }

        public TabWindow() {
            main_panel = new JPanel();
            main_panel.setLayout(null);
            lm = new DefaultListModel<>();
            player_list = new JList<>(lm);
            sc_pane = new JScrollPane(player_list);
            text_field = new JTextField();

            text_field.setFont(acumin16);
            player_list.setFont(acumin16);

            sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            apply_button.setEnabled(false);

            player_list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        apply_button.setEnabled(player_list.getSelectedIndex() >= min_list_amt);
                    }
                }
            });

            main_panel.add(text_field);
            main_panel.add(add_button);
            main_panel.add(apply_button);
            main_panel.add(sc_pane);
        }
    }

    public class AliasTab extends TabWindow {

        JButton delete_button = new JButton("Delete Tag");

        public void config () {
            add_button.setText("Add Tag");
            apply_button.setText("Make Main Tag");

            String [] aliases = API.getAliases(player);
            lm.addElement(player);
            for (int i = 0; i < aliases.length; i++) {
                if (!aliases[i].equals(player)) lm.addElement(aliases[i]);
            }

            // Add action listeners
            add_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String new_alias = text_field.getText().trim();
                    if (!new_alias.equals("")) {
                        API.addAlias(new_alias, player);
                        lm.addElement(new_alias);
                        text_field.setText("");
                    }
                }
            });

            delete_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    delete_button.setEnabled(false);
                    String alias = player_list.getSelectedValue();
                    API.deleteAlias(alias, player);
                    lm.remove(player_list.getSelectedIndex());
                    player_list.clearSelection();
                }
            });

            player_list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        delete_button.setEnabled(player_list.getSelectedIndex() >= min_list_amt);
                    }
                }
            });

            delete_button.setEnabled(false);
            main_panel.add(delete_button);
        }

        @Override
        public void setSizes() {
            text_field.setSize(tab_pane.getWidth()/4, 30);
            sc_pane.setSize(tab_pane.getWidth()-5, tab_pane.getHeight()-apply_button.getHeight()-apply_button.getY()-28);
            add_button.setSize(((tab_pane.getWidth()-text_field.getWidth())-25)/3, 30);
            delete_button.setSize(add_button.getWidth(), add_button.getHeight());
            apply_button.setSize(add_button.getWidth(), add_button.getHeight());
        }

        @Override
        public void setLocations() {
            text_field.setLocation(1, 0);
            sc_pane.setLocation(0, setBelow(apply_button));
            add_button.setLocation(setRight(text_field)+9, 0);
            delete_button.setLocation(setRight(add_button)+5, add_button.getY());
            apply_button.setLocation(setRight(delete_button)+5, delete_button.getY());
        }

        public void addCustomListener(ActionListener al) {
            apply_button.addActionListener(al);
        }

        public void updateInfo() {
            String new_name = player_list.getSelectedValue();
            String old_name = player;
            // Reconfigure player label
            player_label.setText((new_name.length() > 18 ? new_name.substring(0, 15)+"..." : new_name));
            player_label.setSize(getTextWidth(player_label), 60);
            player_label.setLocation(setCenter(player_label), setAbove(set_count_label, player_label));
            // Update list of aliases to have main name on top
            lm.setElementAt(new_name, 0);
            lm.setElementAt(old_name, player_list.getSelectedIndex());
            // Clear selection and disable change button
            player_list.clearSelection();
            apply_button.setEnabled(false);
            // Update database tables to reflect change
            API.updateName(old_name, new_name);
            player = new_name;
        }
    }

    public class ExceptionsTab extends TabWindow {

        public void config () {
            min_list_amt = 0;

            add_button.setText("Add Exception");
            apply_button.setText("Delete Exception");

            String [] exceptions = API.getExceptions(player);
            for (int i = 0; i < exceptions.length; i++) {
                lm.addElement(exceptions[i]);
            }

            add_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String new_exception = text_field.getText().trim();
                    if (!new_exception.equals("")) {
                        API.addException(player, new_exception);
                        lm.addElement(new_exception);
                        text_field.setText("");
                    }
                }
            });

            apply_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    apply_button.setEnabled(false);
                    String exception = player_list.getSelectedValue();
                    API.deleteException(player, exception);
                    lm.remove(player_list.getSelectedIndex());
                    player_list.clearSelection();
                }
            });
        }
    }
}
