import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Caninettes List window
 */
public class CaninetteListWindow extends JFrame implements ActionListener {

    // Buttons
    JButton btnFermer;
    JTextArea listCani;

    public CaninetteListWindow(String aTitle) throws SQLException {
        setTitle(aTitle);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DaoCaninette daoCaninette = new DaoCaninette(CaniCrottes.getSqliteConnection(false));

        JPanel jpHaut = new JPanel(new BorderLayout());
        add(jpHaut, "North");
        JPanel jpWest = new JPanel();
        jpHaut.add(jpWest, "West");
        JPanel jpBas = new JPanel(new BorderLayout());
        add(jpBas, "South");

        btnFermer = new JButton("Fermer");
        jpWest.add(btnFermer);
        btnFermer.addActionListener(this);

        listCani = new JTextArea();
        JScrollPane jsp = new JScrollPane(listCani, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp = new JScrollPane(listCani);

        // Returns a list of caninettes
        listCani.append(daoCaninette.displayCaninettes().toString());
        add(jsp, "Center");

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(btnFermer)) {
            this.dispose();
        }
    }
}
