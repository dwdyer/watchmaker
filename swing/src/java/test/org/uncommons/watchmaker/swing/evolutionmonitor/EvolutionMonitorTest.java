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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.Dialog;
import java.awt.Frame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.DialogMatcher;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Basic unit test for the {@link EvolutionMonitor} component.
 * @author Daniel Dyer
 */
public class EvolutionMonitorTest
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
    public void testShowInFrame()
    {
        EvolutionMonitor<String> monitor = new EvolutionMonitor<String>();
        monitor.showInFrame("MonitorFrame", false);
        robot.waitForIdle();
        // There ought to be a visible frame containing the evolution monitor.
        Frame frame = robot.finder().find(FrameMatcher.withTitle("MonitorFrame").andShowing());
        assert monitor.getGUIComponent().isShowing() : "Evolution monitor should be showing.";
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frameFixture.close();
        robot.waitForIdle();
        assert !monitor.getGUIComponent().isShowing() : "Evolution monitor should not be showing.";
    }


    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testShowInDialog()
    {
        EvolutionMonitor<String> monitor = new EvolutionMonitor<String>();
        monitor.showInDialog(null, "MonitorDialog", false);
        robot.waitForIdle();
        // There ought to be a visible dialog containing the evolution monitor.
        Dialog dialog = robot.finder().find(DialogMatcher.withTitle("MonitorDialog").andShowing());
        assert monitor.getGUIComponent().isShowing() : "Evolution monitor should be showing.";
        DialogFixture dialogFixture = new DialogFixture(robot, dialog);
        dialogFixture.close();
        robot.waitForIdle();
        assert !monitor.getGUIComponent().isShowing() : "Evolution monitor should not be showing.";
    }


    /**
     * If the evolution monitor is already displayed in a window, a subsequent call to one of
     * the show methods should result in that window being replaced.
     */
    @Test(dependsOnMethods = {"testShowInFrame", "testShowInDialog"},
          groups = "display-required") // Will fail if run in a headless environment.
    public void testShowInFrameThenShowInDialog()
    {
        EvolutionMonitor<String> monitor = new EvolutionMonitor<String>(true);
        monitor.showInFrame("MonitorFrame", false);
        robot.waitForIdle();
        // There ought to be a visible frame containing the evolution monitor.
        Frame frame = robot.finder().find(FrameMatcher.withTitle("MonitorFrame").andShowing());

        monitor.showInDialog(null, "MonitorDialog", false);
        robot.waitForIdle();
        // There ought to be a visible dialog containing the evolution monitor.
        robot.finder().find(DialogMatcher.withTitle("MonitorDialog").andShowing());
        assert monitor.getGUIComponent().isShowing() : "Evolution monitor should be showing.";

        assert !frame.isShowing() : "Frame should have been replaced by dialog.";
    }
}
