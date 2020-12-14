package GUI;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import MyUtils.*;

public class ProgressWindow extends TemplateWindow {

    JLabel im_player_label = new JLabel("Adding entrants data...", SwingConstants.CENTER);
    JLabel im_match_data = new JLabel("Adding match data.......", SwingConstants.CENTER);
    JLabel im_placings_label = new JLabel("Adding placings data...", SwingConstants.CENTER);
    JButton ok_button = new JButton("OK");

    SwingWorker<Boolean, Integer> worker;

    String [] entrants;
    Match [] results;
    int tourney_id;
    String tourney_name;

    public ProgressWindow(String[] fed_entrants, Match [] fed_results, int fed_tourney_id, String fed_tourney_name) {

        // Attach fed in arguments
        entrants = fed_entrants;
        results = fed_results;
        tourney_id = fed_tourney_id;
        tourney_name = fed_tourney_name;

        // Set Window Attributes
        window.setTitle("Import Progress");
        window.setLayout(null);

        // Set fonts and colors
        im_player_label.setFont(helveticaB16);
        im_match_data.setFont(helveticaB16);
        im_placings_label.setFont(helveticaB16);
        ok_button.setFont(helveticaB24);

        im_player_label.setForeground(Color.WHITE);
        im_match_data.setForeground(Color.WHITE);
        im_placings_label.setForeground(Color.WHITE);

        window.getContentPane().setBackground(bg_color);

        // Set component sizes
        im_player_label.setSize(getTextWidth(im_player_label), 20);
        im_match_data.setSize(getTextWidth(im_match_data), im_player_label.getHeight());
        im_placings_label.setSize(getTextWidth(im_placings_label), im_player_label.getHeight());
        ok_button.setSize((im_player_label.getWidth() + 20), 50);

        // Set component locations
        im_player_label.setLocation(20, 20);
        im_match_data.setLocation(im_player_label.getX(), im_player_label.getY() + im_player_label.getHeight() + 10);
        im_placings_label.setLocation(im_player_label.getX(), im_match_data.getY() + im_match_data.getHeight() + 10);
        ok_button.setLocation(((getTextWidth(im_player_label) + 115)/2) - (ok_button.getWidth()/2)-10,
                               im_placings_label.getY() + im_placings_label.getHeight()+10);

        window.setSize(getTextWidth(im_player_label) + 115, ok_button.getY() + ok_button.getHeight() + 50);

        // Add action listeners
        ok_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                window.dispose();
            }
        });

        // Add background workers
        worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                window.toFront();
                API.addPlayerData(entrants);
                publish(1);
                API.addHistoryData(results);
                API.addBracketData(entrants.length, results[0].date, tourney_id, tourney_name);
                publish(2);
                API.addPlacingsData(entrants, tourney_id);
                return true;
            }

            @Override
            protected void done() {
                im_placings_label.setText(im_placings_label.getText()+" DONE!");
                im_placings_label.setSize(getTextWidth(im_placings_label), 20);
                ok_button.setEnabled(true);
            }

            @Override
            protected void process(List<Integer> chunks) {
                int cur_val = chunks.get(0);
                if (cur_val == 1) {
                    im_player_label.setText(im_player_label.getText()+" DONE!");
                    im_player_label.setSize(getTextWidth(im_player_label), 20);
                }
                if (cur_val == 2) {
                    im_match_data.setText(im_match_data.getText()+" DONE!");
                    im_match_data.setSize(getTextWidth(im_match_data), 20);
                }
            }
        };

        // Pack items into window
        window.add(ok_button);
        window.add(im_player_label);
        window.add(im_match_data);
        window.add(im_placings_label);

        // Set final window attributes
        ok_button.setEnabled(false);
    }

    @Override
    public void launch() {
        window.setVisible(true);
        worker.execute();
    }
}
