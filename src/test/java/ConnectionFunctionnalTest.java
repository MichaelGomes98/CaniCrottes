import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.ComponentLookupScope;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.core.matcher.JButtonMatcher.withText;
import static org.assertj.swing.finder.WindowFinder.findFrame;

public class ConnectionFunctionnalTest {
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
     * Test successful login
     *
     * @throws InterruptedException InterruptedException
     */
    @Test
    public void Connexion() throws InterruptedException {
        window.button("btnConnexion").click();
        window.button("btnConnexion").requireText("Connection");

        FrameFixture loginFrame = findFrame(LoginForm.class).using(window.robot());

        loginFrame.textBox("txtLogin").enterText("AdminCani");
        loginFrame.textBox("txtPassword").enterText("adminMDP");
        loginFrame.button("btnSignIn").click();
        window.button("btnConnexion").requireText("Déconnexion");
        Thread.sleep(2000);
    }

    /**
     * Test failed login
     *
     * @throws InterruptedException InterruptedException
     */
    @Test
    public void FailConnexion() throws InterruptedException {
        window.button("btnConnexion").click();
        window.button("btnConnexion").requireText("Connection");

        //Change frame
        FrameFixture loginFrame = findFrame(LoginForm.class).using(window.robot());

        loginFrame.textBox("txtLogin").enterText("Fail");
        loginFrame.textBox("txtPassword").enterText("adminMDP");
        loginFrame.button("btnSignIn").click();
        loginFrame.button("btnSignIn").requireText("Connection");
        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}