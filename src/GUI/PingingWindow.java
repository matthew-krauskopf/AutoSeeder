package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

import Backend.API;

public class PingingWindow extends TemplateWindow {

    JLabel ping_label = new JLabel("Fetching ", SwingConstants.CENTER);
    JButton cancel_button = new JButton("Cancel");
    JProgressBar progress_bar;

    SwingWorker<Boolean, Integer> worker;

    Boolean finished = false;
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
        ping_label.setForeground(fg_color);

        // Set component sizes
        ping_label.setSize(getTextWidth(ping_label), 50);
        progress_bar.setSize(ping_label.getWidth()+50, 30);
        cancel_button.setSize(progress_bar.getWidth()/2, 30);

        // Set window width
        window.setSize(progress_bar.getWidth() + 50, 0);
        
        // Set component locations
        ping_label.setLocation(setCenter(ping_label),edge);
        progress_bar.setLocation(setCenter(progress_bar), setBelow(ping_label));
        cancel_button.setLocation(setCenter(cancel_button), setBelow(progress_bar)+edge); 

        // Set window height
        window.setSize(window.getWidth(), setBelow(cancel_button)+50);

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
                finished = true;
                window.setVisible(false);
            }

            @Override
            protected void process(List<Integer> chunks) {
                int cur_val = chunks.get(0);
                progress_bar.setValue(cur_val);
            }
        };

        // Pack items into window
        window.add(ping_label);
        window.add(progress_bar);
        window.add(cancel_button);
    }

    public void addClosingListener(WindowAdapter wa) {
        window.addWindowListener(wa);
    }

    public void addVisibleListener(ComponentListener e) {
        window.addComponentListener(e);
    }

    public void addCancelListener(ActionListener al) {
        cancel_button.addActionListener(al);
    }

    public void cancelPing() {
        worker.cancel(true);
    }

    public Boolean isFinished() {
        return finished;
    }

    @Override
    public void launch() {
        window.setVisible(true);
        worker.execute();
    }

    public void dispose() {
        window.dispose();
    }
}