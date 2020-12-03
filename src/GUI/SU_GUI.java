package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class SU_GUI extends TemplateWindow {

    // Attach instances of each sub-window
    static PreSeedingWindow PS_window;
    static ImportWindow IR_window;
    static RankingsWindow Rank_window;

    // Add SwingWorker to wake up htmlunit in background
    static SwingWorker<Void, Void> worker;

    static JButton b1 = new JButton("Seed Bracket");
    static JButton b2 = new JButton("Import Results");
    static JButton b3 = new JButton("View Rankings");
    static JButton b4 = new JButton("Remake Database");
    static JComboBox<String> dbase_selector;

    public SU_GUI() {
        // Construct JComponents
        dbase_selector = new JComboBox<>();

        // Set Window Attributes
        window.setTitle("AutoBracket");
        window.setLayout(null);

        // Set fonts and colors
        dbase_selector.setFont(acumin16);
        dbase_selector.setBackground(Color.WHITE);

        // Set component sizes
        b1.setSize(200, 40);
        b2.setSize(b1.getWidth(), b1.getHeight());
        b3.setSize(b1.getWidth(), b1.getHeight());
        b4.setSize(b1.getWidth(), b1.getHeight());
        dbase_selector.setSize(200, 30);


        // Set component locations
        dbase_selector.setLocation(10, 0);
        b1.setLocation(40,dbase_selector.getY()+dbase_selector.getHeight()+20);
        b2.setLocation(b1.getX(),b1.getY()+b1.getHeight()+20);
        b3.setLocation(b1.getX(),b2.getY()+b2.getHeight()+20);
        b4.setLocation(b1.getX(),b3.getY()+b3.getHeight()+20);

        // Set window size
        window.setSize(300,b4.getHeight()+b4.getY()+(4*edge));

        // Add action listeners
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PS_window = new PreSeedingWindow();
                PS_window.launch();
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IR_window = new ImportWindow();
                IR_window.launch();
            }
        });

        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rank_window = new RankingsWindow();
                Rank_window.launch();
            }
        });

        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                API.remakeDatabase();
                dbase_selector.removeAllItems();
                String [] seasons = API.getSeasons();
                for (String season: seasons) {System.out.println(season); dbase_selector.addItem(season); }
            }
        });

        dbase_selector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected_dbase = (String) dbase_selector.getSelectedItem();
                System.out.println(selected_dbase);
            }
        });

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Create swing worker
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("Waking it up...");
                long startTime = System.nanoTime();
                API.wakeUpHTML();
                long endTime = System.nanoTime();
                System.out.println("Done! " + ((endTime-startTime)/10000000));
                return null;
            }
        };

        // Pack items into window
        window.add(dbase_selector);
        window.add(b1);
        window.add(b2);
        window.add(b3);
        window.add(b4);
    }

    public void startWakeUpHTML() {
        worker.execute();
    }

    public static void closeHTML() {
        API.closeHTML();
    }

    @Override
    public void launch() {
        API.setMetadata();
        String [] seasons = API.getSeasons();
        for (String season: seasons) dbase_selector.addItem(season);
        API.selectSeason(seasons[0]);
        window.setVisible(true);
    }
}
