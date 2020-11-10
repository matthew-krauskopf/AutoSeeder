package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class ConflictsWindow extends TemplateWindow {

    JLabel notice;
    JList conflict_list;
    JScrollPane sc_pane;

    BracketData br_data;
    String title = "Unresolved Conflicts";

    public ConflictsWindow(BracketData fed_br_data) {
        // Attach passed in arguments
        br_data = fed_br_data;

        // Create JComponents
        notice = new JLabel("Unable to resolve the following conflicts...");
        makeScrollPane();

        // Set Window Attributes
        window.setTitle(title);
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        conflict_list.setFont(acumin16);
        notice.setFont(helveticaB24);
        notice.setForeground(Color.WHITE);

        // Set component sizes
        notice.setSize(getTextWidth(notice), notice.getFont().getSize()+10);
        sc_pane.setSize(notice.getWidth(), (br_data.conflicts.length)*(conflict_list.getFont().getSize()+7)+3);

        // Set component locations
        notice.setLocation(edge, 10);
        sc_pane.setLocation(edge, notice.getY()+notice.getHeight()+10);

        // Set window size
        window.setSize(notice.getWidth()+(edge*3),sc_pane.getY()+sc_pane.getHeight()+(edge*3));

        // Pack items into window
        window.add(notice);
        window.add(sc_pane);
    }

    private void makeScrollPane() {
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < br_data.conflicts.length; i++) {
            l1.addElement(String.format("(%d) %s vs (%d) %s",
                                        (br_data.conflicts[i][0]+1), br_data.entrants[br_data.conflicts[i][0]],
                                        (br_data.conflicts[i][1]+1), br_data.entrants[br_data.conflicts[i][1]]
                                        ));
        }
        conflict_list = new JList<>(l1);
        // Center text in list
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) conflict_list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Make scroll pane
        sc_pane = new JScrollPane(conflict_list);
    }
}