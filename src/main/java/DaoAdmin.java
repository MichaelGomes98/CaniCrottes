import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DaoAdmin extends DataBaseConnection {

    DaoAdmin(String urlDb) throws SQLException {
        super(urlDb);
    }

    /**
     * Admin login process that compare the user and pwd with the db
     *
     * @param login    String
     * @param password String
     * @return boolean
     */
    boolean loginAdmin(String login, String password) {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rs = statement.executeQuery("select Adm_id, Adm_Nom, Adm_Prenom, Adm_Login, Adm_Motdepasse from Admin ");

            while (rs.next()) {
                // read the result set
                Admin admin = new Admin(rs.getInt("Adm_id"), rs.getString("Adm_Nom"), rs.getString("Adm_Prenom"), rs.getString("Adm_Login"), rs.getString("Adm_Motdepasse"));
                // A faire champ.getText().equalsadmin.getLogin() etc.
                if (admin.getLogin().equals(login) && admin.getMdp().equals(password)) {
                    System.out.println("Vous êtes connecté");
                    ToastMessage toastMessage = new ToastMessage("Vous etes connecté", 3000);
                    toastMessage.setVisible(true);
                    return true;

                } else {
                    System.out.println("Login érroné");
                    ToastMessage toastMessage = new ToastMessage("Identifiant ou mot de passe erroné ", 3000);
                    toastMessage.setVisible(true);
                    return false;
                }
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        return false;
    }
}