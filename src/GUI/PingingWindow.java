package GUI;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class PingingWindow extends TemplateWindow {

    JLabel ping_label = new JLabel("Fetching ", SwingConstants.CENTER);
    JProgressBar progress_bar;

    SwingWorker<Boolean, Integer> worker;

    String url;

    public PingingWindow(String fed_url, int pages) {
        // Attach fed in arguments
        url = fed_url;

        // Construct JComponents
        ping_label.setText(ping_label.getText()+url+"...");
        progress_bar = new JProgressBar(0,pages);
        progress_bar.setIndeterminate(pages==1);

        // Set Window Attributes
        window.setTitle("Fetching info");
        window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        ping_label.setFont(helvetica24);
        ping_label.setForeground(Color.WHITE);

        // Set component sizes
        ping_label.setSize(getTextWidth(ping_label), 50);
        progress_bar.setSize(ping_label.getWidth()+50, 30);

        window.setSize(progress_bar.getWidth() + 50, ping_label.getHeight()+progress_bar.getHeight()+75);
        
        // Set component locations
        ping_label.setLocation(getCenter(ping_label)-(2*offset),10);
        progress_bar.setLocation((window.getWidth()/2)-(progress_bar.getWidth()/2)-offset,ping_label.getY()+ping_label.getHeight());

        // Add action listeners
 
        // Add background workers
        worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (pages >= 1) {API.makeStandingsFile(url); publish(1);}
                if (pages >= 2) {API.makeResultsFile(url); publish(2);}
                if (pages >= 3) {API.makeLogFile(url); publish(3);}
                return true;
            }

            @Override
            protected void done() {
                window.dispose();
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int cur_val = chunks.get(0);
                progress_bar.setValue(cur_val);
            }
        };

        // Pack items into window
        window.add(ping_label);
        window.add(progress_bar);
    }

    public void addCustomListener(WindowAdapter wa) {
        window.addWindowListener(wa);
    }

    @Override
    public void launch() {
        window.setVisible(true);
        worker.execute();
    }
}
