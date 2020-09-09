package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class my_gui {

    static Font font = new Font("Acumin", 0, 16);
    static int set_gap = 75;
    static int x_edge = 10;

    static DBManager db;

    public my_gui() {
        db = new DBManager();
    }

    public static void seed_bracket() {
        JFrame popup = new JFrame("Seed Bracket");

        JLabel label = new JLabel("Enter Challonge link");
        label.setBounds(0,0,200,30);

        JTextArea area = new JTextArea();
        area.setBounds(0, 30, 300, 40);

        JLabel example = new JLabel("Format: https://challonge.com/tournament_name");
        example.setBounds(0, 70, 500, 20);

        JLabel error = new JLabel("Entered url is invalid. Please try again");
        error.setBounds(0, 95, 250, 20);

        JButton submit = new JButton("Submit");
        submit.setBounds(25, 115, 200, 40);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = area.getText().trim();
                String [] entrants = WebData.grab_entrants(url);
                if (entrants.length==0) {
                    error.setVisible(true);
                    return;
                }
                popup.setVisible(false);
                int [] scores = db.grab_scores(entrants);
                Bracket.seed_bracket(entrants, scores);
                // Show initial assignments
                Bracket.show_bracket(entrants);
                Set[] sets = Bracket.grab_sets(entrants);
                seeding_window(entrants, sets);
            }
        });

        popup.add(label); popup.add(area); popup.add(submit); popup.add(example); popup.add(error);
        popup.setSize(500, 250);
        popup.setLayout(null);
        error.setVisible(false);
        popup.setVisible(true);
    }

    public static void seeding_window(String[] entrants, Set[] sets) {
        // TODO REFACTOR!!!!!
        JFrame window = new JFrame("Suggested Seeding");
        window.setSize(1500,750);

        int y_offset = 0;
        // Create Entrants List
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < entrants.length; i++) {
            l1.addElement((i+1) + ": " + entrants[i]);
        }
        JList<String> list = new JList<>(l1);

        // Set font
        list.setFont(font);

        JPanel match_panel = new JPanel(null);
        //match_panel.setLayout(null);
        match_panel.setBounds(0, 0, (int)(window.getWidth()*.125), window.getHeight()-40);
        match_panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Pack winner matches
        int sq_entrants = (sets.length+3)/2;
        int round = 1;
        int tot = 0;
        JLabel [] w_round_labels = new JLabel[(int) (Math.log10(sq_entrants)/Math.log10(2))];
        JSplitPane [] set_panes = new JSplitPane[sets.length];
        int set_count = 0;
        int max_win_rs = 0;
        while (tot < sq_entrants-1) {
            //System.out.println("\n" + sq_entrants + " WR " + round);
            w_round_labels[round-1] = new JLabel("Winner's Round " + round);
            w_round_labels[round-1].setBounds(200*(round-1)+x_edge,0,200,30);
            w_round_labels[round-1].setAlignmentX(Component.LEFT_ALIGNMENT);
            match_panel.add(w_round_labels[round-1]);
            int end = (sq_entrants-tot)/2;
            int skipped = 0;
            for (int cur = 0; cur < end ; cur++) {
                if (!sets[tot].l_player.equals("Bye")) {
                    JLabel h_seed = new JLabel(String.format("%3d:   %s",sets[tot].h_seed, sets[tot].h_player));
                    JLabel l_seed = new JLabel(String.format("%3d:   %s",sets[tot].l_seed, sets[tot].l_player));
                    set_panes[set_count] = new JSplitPane(JSplitPane.VERTICAL_SPLIT, h_seed, l_seed);
                    set_panes[set_count].setBounds(200*(round-1)+x_edge,set_gap*(cur+1-skipped)-50, 150, 50);
                    match_panel.add(set_panes[set_count]);
                    set_count++;
                }
                else {
                    skipped++;
                }
                tot++;
            }
            max_win_rs = (end-skipped > max_win_rs ? end-skipped : max_win_rs);
            round++;
        }

        // Pack loser matches
        round = 1;
        // Just accept it. It works
        JLabel [] l_round_labels = new JLabel[2 * ( (int)(Math.log10(sq_entrants)/Math.log10(2)) - 1)];
        int end = sq_entrants/4;
        while(tot < sets.length) {
            l_round_labels[round-1] = new JLabel("Loser's Round " + round);
            l_round_labels[round-1].setBounds(200*(round-1)+x_edge,set_gap*(max_win_rs)+50,200,30);
            l_round_labels[round-1].setAlignmentX(Component.LEFT_ALIGNMENT);
            match_panel.add(l_round_labels[round-1]);
            int skipped = 0;
            for (int cur = 0; cur < end ; cur++) {
                if (!sets[tot].l_player.equals("Bye")) {
                    JLabel h_seed = new JLabel(String.format("%3d:   %s",sets[tot].h_seed, sets[tot].h_player));
                    JLabel l_seed = new JLabel(String.format("%3d:   %s",sets[tot].l_seed, sets[tot].l_player));
                    set_panes[set_count] = new JSplitPane(JSplitPane.VERTICAL_SPLIT, h_seed, l_seed);
                    int x_pos = 200*(round-1)+x_edge;
                    int y_pos = set_gap*(max_win_rs+cur+1-skipped);
                    // Used to know how large to size window
                    y_offset = (y_pos > y_offset ? y_pos : y_offset);
                    set_panes[set_count].setBounds(x_pos, y_pos, 150, 50);
                    match_panel.add(set_panes[set_count]);
                    set_count++;
                }
                else {
                    skipped++;
                }
                tot++;
            }
            round++;
            if (round % 2 == 1) end /= 2;
        }

        match_panel.setMinimumSize(new Dimension(window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40));
        match_panel.setPreferredSize(new Dimension(200*(round-1), y_offset+60));

        JScrollPane matchup_view = new JScrollPane(match_panel);
        matchup_view.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        matchup_view.getVerticalScrollBar().setUnitIncrement(16);
        matchup_view.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        matchup_view.setBounds( (int)(window.getWidth()*.125), 0, window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40);

        // Attach list of entrants to scroll pane
        JScrollPane seeded_entrants = new JScrollPane(list);
        // Set scroll properties
        seeded_entrants.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setBounds(0, 0, (int)(window.getWidth()*.125), window.getHeight()-40);

        window.getContentPane().add(seeded_entrants); window.getContentPane().add(matchup_view);
        window.setLayout(null);
        window.setVisible(true);
        ReadFile.clean_tmp_files();
    }

    public static void import_results() {
        JFrame popup = new JFrame("Import Results");

        JLabel label = new JLabel("Enter Challonge link");
        label.setBounds(0,0,200,30);

        JTextArea area = new JTextArea();
        area.setBounds(0, 30, 300, 40);

        JLabel example = new JLabel("Format: https://challonge.com/tournament_name");
        example.setBounds(0, 70, 500, 20);

        JLabel error = new JLabel("Entered url is invalid. Please try again");
        error.setBounds(0, 95, 250, 20);

        JButton submit = new JButton("Submit");
        submit.setBounds(25, 115, 200, 40);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = area.getText().trim();
                // Checks if valid URL: if yes, also grabs entrants
                String [] entrants = WebData.grab_entrants(url);
                if (entrants.length==0) {
                    error.setVisible(true);
                    return;
                }
                popup.setVisible(false);
                show_progress_menu(entrants, url);
            }
        });

        popup.add(label); popup.add(area); popup.add(submit); popup.add(example); popup.add(error);
        popup.setSize(500, 250);
        popup.setLayout(null);
        error.setVisible(false);
        popup.setVisible(true);
    }

    public static void show_progress_menu(String[] entrants, String url) {
        JFrame popup = new JFrame("Import Progress");
        JLabel message = new JLabel("Checking if data is new...", SwingConstants.CENTER);
        JButton ok_button = new JButton("OK");

        message.setBounds(0, 10, 300, 20);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok_button.setBounds(70, 50, 160, 50);
        ok_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                ReadFile.clean_tmp_files();
            }
        });

        popup.setLayout(null);
        popup.add(message); popup.add(ok_button);
        popup.setSize(320,150);
        popup.setVisible(true);

        int id = WebData.grab_tourney_id(url);
        // Make sure imported bracket is new
        int status = db.tourneyID_table.check_bracket_data_new(id);
        if (status == 1) {
            message.setText("Adding new players to database...");
            db.add_players(entrants);

            message.setText("Adding matchup results to database...");
            Match [] results = WebData.grab_results(url);
            db.add_history(results);

            message.setText("Done!");
        }
        else if (status == 0) {
            message.setText("Tourney results already exist in database!");
        }
        else {
            message.setText("Unknown Error");
        }
    }

    public static void show_rankings() {
        String [][] rankings = db.players_table.get_rankings();
        String columns[] = {"Rank", "Player", "Wins", "Losses", "ELO"};
        JTable jt = new JTable(rankings, columns);
        jt.setEnabled(false);
        JScrollPane sc_pane = new JScrollPane(jt);
        JFrame f = new JFrame();
        f.add(sc_pane);
        f.setSize(600, 800);
        f.setVisible(true);
    }

    public static void main_menu() {
        // TODO Remove later
        ReadFile.clean_tmp_files();
        JFrame f = new JFrame("AutoBracket");

        // Create Buttons
        JButton b1 = new JButton("Seed Bracket");
        b1.setBounds(40,30,200, 40);//x axis, y axis, width, height
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seed_bracket();
            }
        });
        JButton b2 = new JButton("Import Results");
        b2.setBounds(40,90,200, 40);//x axis, y axis, width, height
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                import_results();
            }
        });

        JButton b3 = new JButton("View Rankings");
        b3.setBounds(40, 150, 200, 40);
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                show_rankings();
            }
        });

        f.add(b1);//adding button in JFrame
        f.add(b2);
        f.add(b3);

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.setSize(300,250);
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
    }
}
