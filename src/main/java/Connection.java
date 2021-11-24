import javax.swing.*;

public class Connection {
    private boolean connected;
    private JButton btnConnection;

    /**
     * Changes the connection button name when connected
     *
     * @param btnConnection JButton
     */
    public Connection(JButton btnConnection) {
        connected = false;
        this.btnConnection = btnConnection;
    }

    public void setConnection() {
        connected = true;
        btnConnection.setText("Déconnexion");
    }

    public void setDeconnection() {
        connected = false;
        btnConnection.setText("Connexion");
    }

    public boolean isConnected() {
        return connected;
    }
}
