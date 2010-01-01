//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.swing;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link SwingConsole} class.
 * @author Daniel Dyer
 */
public class SwingConsoleTest
{
    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testUserSelection() throws InterruptedException
    {
        Robot robot = BasicRobot.robotWithNewAwtHierarchy();
        final SwingConsole swingConsole = new SwingConsole();
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
                // This method blocks so we can't run it on the test thread.
                selection[0] = swingConsole.select(labels);
            }
        }).start();
        Thread.sleep(250);  // TO DO: Come up with a proper solution to this race condition.
        frameFixture.button("Selection-1").click();

        assert selection[0] == 1
                : "Second item (index 1) should have been selected, selection index was " + selection[0];

        robot.cleanUp();
    }
}
