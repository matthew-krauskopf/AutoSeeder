package GUI;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class TemplateWindow {

    JFrame window = new JFrame();

    static Color bg_color = new Color(46, 52, 61);
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static int screen_height = screenSize.height*24/25;

    static int edge = 17;
    static int offset = 10;

    public int getTextWidth(JLabel l) {
        return l.getFontMetrics(l.getFont()).stringWidth(l.getText());
    }

    public void dispose() {
        window.dispose();
    }

    public void launch() {
        window.setVisible(true);
    }
}