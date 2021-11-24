import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginForm extends JFrame implements ActionListener {

    private JPasswordField txtPassword;
    private JTextField txtLogin;
    private JButton btnSignIn;
    private Connection connectionStatus;

    /**
     * Login form
     *
     * @param connectionStatus Connection : object responsible for maintaining the login status for the admin user
     */
    LoginForm(Connection connectionStatus) {

        this.connectionStatus = connectionStatus;

        this.setResizable(false);
        setTitle("Connection");
        JPanel up = new JPanel();
        add(up, "North");

        JPanel middle = new JPanel();
        add(middle, "Center");

        JPanel down = new JPanel();
        add(down, "South");

        JLabel lblLogin = new JLabel("Login :        ");
        lblLogin.setBounds(10, 10, 80, 25);
        up.add(lblLogin);

        txtLogin = new JTextField(20);
        txtLogin.setName("txtLogin");
        txtLogin.setBounds(100, 10, 160, 25);
        up.add(txtLogin);

        JLabel lblPassword = new JLabel("Password :");
        lblLogin.setBounds(10, 10, 80, 25);
        middle.add(lblPassword);

        txtPassword = new JPasswordField(20);
        txtPassword.setName("txtPassword");
        txtPassword.setBounds(100, 10, 160, 25);
        middle.add(txtPassword);

        btnSignIn = new JButton("Connection");
        btnSignIn.setName("btnSignIn");
        btnSignIn.addActionListener(this);
        down.add(btnSignIn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSignIn)) {
            try {
                System.out.println(txtLogin.getText());
                DaoAdmin log = new DaoAdmin(CaniCrottes.getSqliteConnection(false));
                if (log.loginAdmin(txtLogin.getText(), String.valueOf(txtPassword.getPassword()))) {
                    dispose();
                    connectionStatus.setConnection();
                } else {
                    connectionStatus.setDeconnection();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}


