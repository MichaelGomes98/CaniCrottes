
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;

public class CaninetteOooUnitTest {

    /**
     * Test that the Caninette's to String is correct
     */
    @Test
    public void checkDisplayOooCaninette() {
        Caninette caninette = new Caninette(602, "Av. Curé-Baud 14", "2_99", "HS", "Très belle", 2599865.69, 1112555.36);
        String expectedValue = "Caninette n°: 602" + "\n" +
                "Adresse: Av. Curé-Baud 14" + "\n" +
                "Numero: 2_99" + "\n" +
                "Etat: HS" + "\n" +
                "Remarque: Très belle" + "\n" +
                "======================" + "\n";
        assertEquals(expectedValue, caninette.toString());
    }

    /**
     * Test displayOooCaninettes() method, creates correctly an object Caninette
     *
     * @throws SQLException SQLException
     */
    @Test
    public void checkCaninetteOooObjectCreation() throws SQLException {
        DaoCaninette daoCaninette = new DaoCaninette(CaniCrottes.getSqliteConnection(true));
        String expectedValue = "Caninette n°: 474" + "\n" +
                "Adresse: Rue Charles-GALLAND" + "\n" +
                "Numero: 4_14" + "\n" +
                "Etat: En travaux" + "\n" +
                "Remarque: Angle Promenade de Saint-Antoine" + "\n" +
                "======================" + "\n";
        assertEquals(expectedValue, daoCaninette.displayOooCaninettes().get(0).toString());
    }

}
