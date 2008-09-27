// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.Dialog;
import java.awt.Frame;
import org.fest.swing.core.RobotFixture;
import org.fest.swing.core.matcher.DialogByTitleMatcher;
import org.fest.swing.core.matcher.FrameByTitleMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Daniel Dyer
 */
public class EvolutionMonitorTest
{
    private RobotFixture robot;

    @BeforeMethod
    public void prepare()
    {
        robot = RobotFixture.robotWithNewAwtHierarchy();
    }


    @AfterMethod
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }


    @Test(groups = "display-required") // Will fail if run in a headless environment (such as hudson.uncommons.org).
    public void testShowInFrame()
    {
        EvolutionMonitor<String> monitor = new EvolutionMonitor<String>();
        monitor.showInFrame("MonitorFrame");
        robot.waitForIdle();
        // There ought to be a visible frame containing the evolution monitor.
        Frame frame = robot.finder().find(FrameByTitleMatcher.withTitleAndShowing("MonitorFrame"));
        assert monitor.getGUIComponent().isShowing() : "Evolution monitor should be showing.";
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frameFixture.close();
        robot.waitForIdle();
        assert !monitor.getGUIComponent().isShowing() : "Evolution monitor should not be showing.";
    }


    @Test(groups = "display-required") // Will fail if run in a headless environment (such as hudson.uncommons.org).
    public void testShowInDialog()
    {
        EvolutionMonitor<String> monitor = new EvolutionMonitor<String>();
        monitor.showInDialog(null, "MonitorDialog", false);
        robot.waitForIdle();
        // There ought to be a visible frame containing the evolution monitor.
        Dialog dialog = robot.finder().find(DialogByTitleMatcher.withTitleAndShowing("MonitorDialog"));
        assert monitor.getGUIComponent().isShowing() : "Evolution monitor should be showing.";
        DialogFixture dialogFixture = new DialogFixture(robot, dialog);
        dialogFixture.close();
        robot.waitForIdle();
        assert !monitor.getGUIComponent().isShowing() : "Evolution monitor should not be showing.";
    }
}
