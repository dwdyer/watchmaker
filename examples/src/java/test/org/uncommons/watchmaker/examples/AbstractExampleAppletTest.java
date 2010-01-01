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
package org.uncommons.watchmaker.examples;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link AbstractExampleAppletTest} class.
 * @author Daniel Dyer
 */
public class AbstractExampleAppletTest
{
    private Robot robot;

    @BeforeMethod
    public void prepare()
    {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }


    @AfterMethod
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }

    
    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testPreparationOnEDT()
    {
        final boolean[] onEDT = new boolean[1];
        AbstractExampleApplet applet = new AbstractExampleApplet()
        {
            @Override
            protected void prepareGUI(Container container)
            {
                onEDT[0] = SwingUtilities.isEventDispatchThread();
            }
        };
        applet.init();
        assert onEDT[0] : "Prepare method was not called on Event Dispatch Thread.";
    }


    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testDisplayInFrame()
    {
        AbstractExampleApplet applet = new AbstractExampleApplet()
        {
            @Override
            protected void prepareGUI(Container container)
            {
                JLabel label = new JLabel("Test");
                label.setName("Test");
                container.add(label, BorderLayout.CENTER);
            }
        };
        applet.displayInFrame("ExampleFrame");
        robot.waitForIdle();
        // There ought to be a visible frame containing the example GUI.
        JFrame frame = (JFrame) robot.finder().find(FrameMatcher.withTitle("ExampleFrame").andShowing());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        FrameFixture frameFixture = new FrameFixture(robot, frame);
        assert frameFixture.label("Test").component().isShowing() : "GUI not displayed correctly.";        
        frameFixture.close();
    }
}
