import java.sql.SQLException;

/**
 * Entry point of the program
 */
public class CaniCrottes {
    private static String SqliteConnection = "jdbc:sqlite:mydatabase.db";
    private static String SqliteConnectionTest = "jdbc:sqlite:mydatabaseTest.db";

    public static void main(String[] args) throws SQLException {
        createWindow();
    }

    public static MainWindow createWindow() throws SQLException {
        MainWindow f = new MainWindow("CaniCrottes");

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        return f;
    }

    /**
     * Returns the SQLite connection string
     *
     * @param isTest boolean
     * @return String
     */
    public static String getSqliteConnection(boolean isTest) {
        if (isTest) {
            return SqliteConnectionTest;
        } else {
            return SqliteConnection;
        }
    }
}
