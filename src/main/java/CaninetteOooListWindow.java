import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Out of order Caninettes List window
 */
public class CaninetteOooListWindow extends JFrame implements ActionListener {

    //Bouton
    JButton btnQuitter;
    JTextArea listCaniHs;

    public CaninetteOooListWindow(String aTitle) throws SQLException {
        setTitle(aTitle);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DaoCaninette daoCaninette = new DaoCaninette(CaniCrottes.getSqliteConnection(false));

        JPanel jpHaut = new JPanel(new BorderLayout());
        add(jpHaut, "North");
        JPanel jpWest = new JPanel();
        jpHaut.add(jpWest, "West");
        JPanel jpBas = new JPanel(new BorderLayout());
        add(jpBas, "South");

        btnQuitter = new JButton("Fermer");
        jpWest.add(btnQuitter);
        btnQuitter.addActionListener(this);

        listCaniHs = new JTextArea();
        JScrollPane jsp = new JScrollPane(listCaniHs, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp = new JScrollPane(listCaniHs);
        listCaniHs.append(daoCaninette.displayOooCaninettes().toString());
        add(jsp, "Center");

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(btnQuitter)) {
            this.dispose();
        }
    }
}
