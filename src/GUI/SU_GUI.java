package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Backend.API;

public class SU_GUI extends TemplateWindow {

    // Attach instances of each sub-window
    static PreSeedingWindow preseed_window;
    static ImportWindow import_window;
    static RankingsWindow rankings_window;
    static AddSeasonWindow add_season_window;
    static SeasonSettingsWindow settings_window;

    // Add SwingWorker to wake up htmlunit in background
    static SwingWorker<Void, Void> worker;

    static JButton seed_button = new JButton("Seed Bracket");
    static JButton import_button = new JButton("Import Results");
    static JButton rankings_button = new JButton("View Rankings");
    static JButton remake_button = new JButton();
    static JButton add_button = new JButton("+");
    static JButton settings_button = new JButton("i");
    static JComboBox<String> dbase_selector = new JComboBox<String>();

    public SU_GUI() {
        // Set Window Attributes
        window.setTitle("AutoSeeder");
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        dbase_selector.setFont(acumin16);
        dbase_selector.setBackground(fg_color);

        add_button.setFont(acumin16);
        settings_button.setFont(acumin16);

        add_button.setMargin(new Insets(0,0,0,0));
        settings_button.setMargin(new Insets(0,0,0,0));

        // Map images to buttons
        add_button.setIcon(new ImageIcon(getClass().getResource("/img/plus.png")));
        add_button.setOpaque(false);
        add_button.setContentAreaFilled(false);
        add_button.setBorderPainted(false);

        settings_button.setIcon(new ImageIcon(getClass().getResource("/img/info.png")));
        settings_button.setOpaque(false);
        settings_button.setContentAreaFilled(false);
        settings_button.setBorderPainted(false);

        // Make remake button invisible
        remake_button.setOpaque(false);
        remake_button.setContentAreaFilled(false);
        remake_button.setBorderPainted(false);

        // Set component sizes
        seed_button.setSize(200, 40);
        import_button.setSize(seed_button.getWidth(), seed_button.getHeight());
        rankings_button.setSize(seed_button.getWidth(), seed_button.getHeight());
        add_button.setSize(30,30);
        settings_button.setSize(30, 30);
        remake_button.setSize(settings_button.getWidth(), settings_button.getHeight());
        dbase_selector.setSize(200, 30);


        // Set component locations
        dbase_selector.setLocation(1, 0);
        seed_button.setLocation(40,setBelow(dbase_selector)+20);
        import_button.setLocation(seed_button.getX(), setBelow(seed_button)+20);
        rankings_button.setLocation(seed_button.getX(), setBelow(import_button)+20);
        add_button.setLocation(setRight(dbase_selector)+5, 0);
        settings_button.setLocation(setRight(add_button)+5, 0);
        remake_button.setLocation(settings_button.getX(), settings_button.getY());

        // Set window size
        window.setSize(300,setBelow(rankings_button)+(4*edge));

        // Add action listeners
        seed_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                preseed_window = new PreSeedingWindow();
                preseed_window.addCustomListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!worker.isDone()) {
                            worker.cancel(true);
                            API.cancelWakeUp();
                        }
                        preseed_window.submitAction();
                    }
                });
                preseed_window.launch();
            }
        });

        import_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                import_window = new ImportWindow();
                import_window.addCustomListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!worker.isDone()) {
                            worker.cancel(true);
                            API.cancelWakeUp();
                        }
                        import_window.submitAction();
                    }
                });
                import_window.launch();
            }
        });

        rankings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rankings_window = new RankingsWindow();
                rankings_window.launch();
            }
        });

        remake_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // If clicked 5 times, reset database completely
                if (evt.getClickCount() == 5) {
                    API.remakeDatabase();
                    dbase_selector.removeAllItems();
                    enableButtons(false);
                }
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
                add_season_window = new AddSeasonWindow();
                add_season_window.addCustomListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String new_season = add_season_window.action();
                        if (!new_season.equals("")) {
                            dbase_selector.addItem(new_season);
                            if (!settings_button.isVisible()) enableButtons(true);
                        }
                    }
                });
                add_season_window.launch();
            }
        });

        settings_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings_window = new SeasonSettingsWindow((String) dbase_selector.getSelectedItem());
                settings_window.addCustomDeleteListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String deleted_season = settings_window.delete();
                        dbase_selector.removeItem(deleted_season);
                        if (dbase_selector.getItemCount() == 0) enableButtons(false);
                    }
                });
                settings_window.addCustomUpdateListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String [] names = settings_window.updateName();
                        if (names[0] != null) {
                            dbase_selector.removeItem(names[0]);
                            dbase_selector.addItem(names[1]);
                        }
                    }
                });
                settings_window.launch();
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
                System.out.println("Done waking up! " + ((endTime-startTime)/10000000));
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
        settings_button.setVisible(setting);
        remake_button.setVisible(!setting);
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
