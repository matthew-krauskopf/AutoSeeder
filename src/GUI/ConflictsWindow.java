package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import MyUtils.API;
import MyUtils.BracketData;

public class ConflictsWindow extends TemplateWindow {

    JLabel notice;
    JList<String> conflict_list;
    JScrollPane sc_pane;

    BracketData br_data;
    String title = "Unresolved Conflicts";

    public ConflictsWindow(BracketData fed_br_data, int shake_rounds) {
        // Attach passed in arguments
        br_data = fed_br_data;

        // Create JComponents
        notice = new JLabel((shake_rounds > 0
                             ? "Unable to resolve the following conflicts..."
                             : "Existing conflicts in first 3 rounds"
                             ));
        makeScrollPane();

        // Set Window Attributes
        window.setTitle(title);
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);
        conflict_list.setFont(acumin16);
        notice.setFont(helveticaB24);
        notice.setForeground(fg_color);

        // Set component sizes
        notice.setSize(getTextWidth(notice), notice.getFont().getSize()+10);
        sc_pane.setSize(notice.getWidth(), min((br_data.conflicts.length)*(conflict_list.getFont().getSize()+7)+3, SCREEN_HEIGHT*3/4));

        // Set window width
        window.setSize(notice.getWidth()+(edge*3), 0);

        // Set component locations
        notice.setLocation(setCenter(notice), edge);
        sc_pane.setLocation(edge, setBelow(notice)+edge);

        // Set window height
        window.setSize(window.getWidth(),setBelow(sc_pane)+(edge*3));

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