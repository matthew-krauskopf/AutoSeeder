package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import MyUtils.*;

public class PlayerWindow {

    JFrame window = new JFrame("Player Info");
    JLabel player_label;

    Color bg_color = new Color(46, 52, 61);
    Font font = new Font("Helvetica", Font.BOLD, 24);

    String player;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public PlayerWindow(String fed_player) {
        // Attach passed in arguments
        player = fed_player;

        // Construct JComponents
        player_label = new JLabel(String.format("%s", player));

        // Set Window Attributes
        window.setLayout(null);
        //window.setResizeable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        player_label.setFont(font);
        player_label.setForeground(Color.WHITE);

        // Set component sizes
        window.setSize(screenSize.width/2, screenSize.height/2);
        player_label.setSize(getTextWidth(player_label), 32);

        // Set component locations
        player_label.setLocation((window.getWidth()/2)-(player_label.getWidth()/2), 10);

        // Add action listeners

        // Pack items into window
        window.add(player_label);
    }

    public int getTextWidth(JLabel l) {
        return l.getFontMetrics(l.getFont()).stringWidth(l.getText());
    }

    public void launch() {
        window.setVisible(true);
    }
}
