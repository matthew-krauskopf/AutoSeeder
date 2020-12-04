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
    static AddSeasonWindow AS_window;
    static SeasonSettingsWindow SS_window;

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

        add_button.setMargin(new Insets(0,0,0,0));
        settings_button.setMargin(new Insets(0,0,0,0));

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
                enableButtons(false);
            }
        });

        dbase_selector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dbase_selector.getItemCount() > 1) {
                    String selected_season = (String) dbase_selector.getSelectedItem();
                    API.selectSeason(selected_season);
                }
            }
        });

        add_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AS_window = new AddSeasonWindow();
                AS_window.addCustomListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String new_season = AS_window.action();
                        if (!new_season.equals("")) {
                            dbase_selector.addItem(new_season);
                            if (!settings_button.isEnabled()) enableButtons(true);
                        }
                    }
                });
                AS_window.launch();
            }
        });

        settings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SS_window = new SeasonSettingsWindow((String) dbase_selector.getSelectedItem());
                SS_window.addCustomDeleteListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String deleted_season = SS_window.delete();
                        dbase_selector.removeItem(deleted_season);
                        if (dbase_selector.getItemCount() == 0) enableButtons(false);
                    }
                });
                SS_window.addCustomUpdateListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String [] names = SS_window.updateName();
                        if (names[0] != null) {
                            dbase_selector.removeItem(names[0]);
                            dbase_selector.addItem(names[1]);
                        }
                    }
                });
                SS_window.launch();
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
