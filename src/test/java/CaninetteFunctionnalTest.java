import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.swing.core.matcher.JButtonMatcher.withText;

public class CaninetteFunctionnalTest {
    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        window = new FrameFixture(GuiActionRunner.execute(CaniCrottes::createWindow));
    }

    /**
     * Test display window caninette list
     *
     * @throws InterruptedException InterruptedException
     */
    @Test
    public void displayCaninettesList() throws InterruptedException {
        window.button("btnListeCani").click();
        window.button("btnListeCani").requireText("Liste des caninettes");
        Thread.sleep(2000);
    }

    /**
     * Test display window caninette out of order list
     *
     * @throws InterruptedException InterruptedException
     */
    @Test
    public void displayOooCaninettesList() throws InterruptedException {
        window.button("btnCaninettesHS").click();
        window.button("btnCaninettesHS").requireText("Caninettes hors service");
        Thread.sleep(2000);
    }

    /***
     * Exit button for tse automated tests
     */
    @Test
    public void buttonQuitter() {
        window.button(withText("Quitter")).click();
        window.button(withText("Quitter")).requireText("Quitter");
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}