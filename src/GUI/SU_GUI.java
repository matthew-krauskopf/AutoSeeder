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

    static JButton seed_button = new JButton("Seed Bracket");
    static JButton import_button = new JButton("Import Results");
    static JButton rankings_button = new JButton("View Rankings");
    static JButton remake_button = new JButton("Remake Database");
    static JButton add_button = new JButton("+");
    static JButton settings_button = new JButton("i");
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

        add_button.setFont(acumin16);
        settings_button.setFont(acumin16);

        // Set component sizes
        seed_button.setSize(200, 40);
        import_button.setSize(seed_button.getWidth(), seed_button.getHeight());
        rankings_button.setSize(seed_button.getWidth(), seed_button.getHeight());
        remake_button.setSize(seed_button.getWidth(), seed_button.getHeight());
        add_button.setSize(30,30);
        settings_button.setSize(30, 30);
        dbase_selector.setSize(200, 30);


        // Set component locations
        dbase_selector.setLocation(10, 0);
        seed_button.setLocation(40,dbase_selector.getY()+dbase_selector.getHeight()+20);
        import_button.setLocation(seed_button.getX(),seed_button.getY()+seed_button.getHeight()+20);
        rankings_button.setLocation(seed_button.getX(),import_button.getY()+import_button.getHeight()+20);
        remake_button.setLocation(seed_button.getX(),rankings_button.getY()+rankings_button.getHeight()+20);
        add_button.setLocation(dbase_selector.getX()+dbase_selector.getWidth()+5, 0);
        settings_button.setLocation(add_button.getX()+add_button.getWidth()+1, 0);

        // Set window size
        window.setSize(300,remake_button.getHeight()+remake_button.getY()+(4*edge));

        // Add action listeners
        seed_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PS_window = new PreSeedingWindow();
                PS_window.launch();
            }
        });

        import_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IR_window = new ImportWindow();
                IR_window.launch();
            }
        });

        rankings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rank_window = new RankingsWindow();
                Rank_window.launch();
            }
        });

        remake_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                API.remakeDatabase();
                dbase_selector.removeAllItems();
                String [] seasons = API.getSeasons();
                for (String season: seasons) {dbase_selector.addItem(season);}
            }
        });

        dbase_selector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected_season = (String) dbase_selector.getSelectedItem();
                API.selectSeason(selected_season);
            }
        });

        add_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Create window to add season name
                String season_name = String.format("bracketresults%d", dbase_selector.getItemCount());
                API.createSeason(season_name);
                dbase_selector.addItem(season_name);
                if (!settings_button.isEnabled()) enableButtons(true);
            }
        });

        settings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Create window to show settings, change name, and delete database
                // For now, just delete season
                API.deleteSeason((String) dbase_selector.getSelectedItem());
                // Remove deleted season from season list
                dbase_selector.removeItemAt(dbase_selector.getSelectedIndex());
                if (dbase_selector.getItemCount() == 0) enableButtons(false);
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
        window.add(seed_button);
        window.add(import_button);
        window.add(rankings_button);
        window.add(remake_button);
        window.add(add_button);
        window.add(settings_button);
    }

    public void startWakeUpHTML() {
        worker.execute();
    }

    public static void closeHTML() {
        API.closeHTML();
    }

    public static void enableButtons(Boolean setting) {
        seed_button.setEnabled(setting);
        import_button.setEnabled(setting);
        rankings_button.setEnabled(setting);
        settings_button.setEnabled(setting);
    }

    @Override
    public void launch() {
        API.setMetadata();
        String [] seasons = API.getSeasons();
        for (String season: seasons) dbase_selector.addItem(season);
        if (dbase_selector.getItemCount() > 0) {
            API.selectSeason(seasons[0]);
            enableButtons(true);
        }
        else {
            enableButtons(false);
        }
        window.setVisible(true);
    }
}
