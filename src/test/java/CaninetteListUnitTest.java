import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class CaninetteListUnitTest {

    /**
     * Test the database connection and the number of stored records on a test database
     */
    @Test
    public void validateNumberOfRecords() throws SQLException {
        DaoCaninette daoCanninettes = new DaoCaninette(CaniCrottes.getSqliteConnection(true));
        int numberOfRecords = daoCanninettes.displayCaninettes().size();

        // Checks that there are 601 caninettes in the db
        assertEquals(601, numberOfRecords);
    }

    /**
     * Test the method displayCaninettes() create an entire Caninette list without NULL records
     */
    @Test
    public void checkCaninetteList() throws SQLException {
        DaoCaninette daoCanninettes = new DaoCaninette(CaniCrottes.getSqliteConnection(true));
        ArrayList expectedCaninetteList = daoCanninettes.displayCaninettes();
        for (int i = 0; i < expectedCaninetteList.size(); i++) {
            Assert.assertNotNull(expectedCaninetteList.get(i));
        }
    }
}
