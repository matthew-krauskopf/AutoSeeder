package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class my_gui {

    static Font font = new Font("Acumin", 0, 16);

    public static void main(String args[]) {
        main_menu();
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
                int [] rankings = DBManager.grab_scores(entrants);
                Bracket.seed_bracket(entrants, rankings);
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
        JFrame window = new JFrame("Suggested Seeding");
        window.setSize(1500,750);

        // Create Entrants List
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < entrants.length; i++) {
            l1.addElement((i+1) + ": " + entrants[i]);
        }
        JList<String> list = new JList<>(l1);

        // Set font
        list.setFont(font);

        // Attach list of entrants to scroll pane
        JScrollPane seeded_entrants = new JScrollPane(list);
        // Set scroll properties
        seeded_entrants.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setBounds(0, 0, (int)(window.getWidth()*.125), window.getHeight()-40);

        window.getContentPane().add(seeded_entrants);
        window.setLayout(null);
        window.setVisible(true);
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
                String [] entrants = WebData.grab_entrants(url);
                if (entrants.length==0) {
                    error.setVisible(true);
                    return;
                }
                popup.setVisible(false);
                DBManager.add_players(entrants);
                Match [] results = WebData.grab_results(url);
                DBManager.add_history(results);
            }
        });

        popup.add(label); popup.add(area); popup.add(submit); popup.add(example); popup.add(error);
        popup.setSize(500, 250);
        popup.setLayout(null);
        error.setVisible(false);
        popup.setVisible(true);
    }

    public static void main_menu() {
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
                seeding_window(new String[0], new Set[0]);
            }
        });

        f.add(b1);//adding button in JFrame
        f.add(b2);
        f.add(b3);

        f.setSize(300,250);
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
    }
}
