import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection to the SQLlite db
 */
public class DataBaseConnection {
    public Connection connection;

    public DataBaseConnection(String urlDb) throws SQLException {
        connection = DriverManager.getConnection(urlDb);
    }
}



