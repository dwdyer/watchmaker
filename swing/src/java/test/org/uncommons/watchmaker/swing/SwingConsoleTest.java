package org.uncommons.watchmaker.swing;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.fest.swing.core.RobotFixture;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link SwingConsole} class.
 * @author Daniel Dyer
 */
public class SwingConsoleTest
{
    @Test(groups = "display-required") // Will fail if run in a headless environment (such as hudson.uncommons.org).
    public void testUserSelection() throws InterruptedException
    {
        RobotFixture robot = RobotFixture.robotWithNewAwtHierarchy();
        final SwingConsole swingConsole = new SwingConsole();
        swingConsole.setName("SwingConsole");
        JFrame frame = new JFrame();
        frame.add(swingConsole, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(300, 100);
        frame.validate();
        frame.setVisible(true);

        final List<JLabel> labels = Arrays.asList(new JLabel("Zero"),
                                                  new JLabel("One"),
                                                  new JLabel("Two"));

        final int[] selection = new int[1];
        new Thread(new Runnable()
        {
            public void run()
            {
                selection[0] = swingConsole.select(labels);
            }
        }).start();
        // Is this a race condition?  Probably.
        frameFixture.panel("SwingConsole").button("Selection-1").click();

        assert selection[0] == 1
                : "Second item (index 1) should have been selected, selection index was " + selection[0];

        robot.cleanUp();
    }
}
