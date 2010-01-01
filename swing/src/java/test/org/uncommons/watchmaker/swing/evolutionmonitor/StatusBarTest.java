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

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * Unit test for the {@link StatusBar} class.
 * @author Daniel Dyer
 */
public class StatusBarTest
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
    public void testFieldUpdates()
    {
        StatusBar statusBar = new StatusBar();
        JFrame frame = new JFrame();
        frame.add(statusBar, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(400, 30);
        frame.validate();
        frameFixture.show();

        assert frameFixture.label("Population").text().equals("N/A") : "Wrong initial text for population label.";
        assert frameFixture.label("Elitism").text().equals("N/A") : "Wrong initial text for elitism label.";
        assert frameFixture.label("Generations").text().equals("N/A") : "Wrong initial text for generations label.";
        assert frameFixture.label("Time").text().equals("N/A") : "Wrong initial text for elapsed time label.";

        statusBar.populationUpdate(new PopulationData<Object>(new Object(), 10, 8, 2, true, 10, 1, 0, 36610000));
        assert frameFixture.label("Population").text().equals("10") : "Wrong value for popluation label.";
        assert frameFixture.label("Elitism").text().equals("1") : "Wrong value for elitism label.";
        // Generation count is number + 1 (because generations start at zero).
        assert frameFixture.label("Generations").text().equals("1") : "Wrong value for generations label.";
        assert frameFixture.label("Time").text().equals("10:10:10") : "Wrong value for elapsed time label.";
    }


    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testFieldUpdatesForIslandMode()
    {
        StatusBar statusBar = new StatusBar(true);
        JFrame frame = new JFrame();
        frame.add(statusBar, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(400, 30);
        frame.validate();
        frameFixture.show();

        assert frameFixture.label("Population").text().equals("N/A") : "Wrong initial text for population label.";
        assert frameFixture.label("Elitism").text().equals("N/A") : "Wrong initial text for elitism label.";
        assert frameFixture.label("Generations").text().equals("N/A") : "Wrong initial text for generations label.";
        assert frameFixture.label("Time").text().equals("N/A") : "Wrong initial text for elapsed time label.";

        statusBar.islandPopulationUpdate(0, new PopulationData<Object>(new Object(), 10, 8, 2, true, 10, 1, 0, 36610000));
        statusBar.populationUpdate(new PopulationData<Object>(new Object(), 10, 8, 2, true, 50, 1, 0, 36610000));
        assert frameFixture.label("Population").text().equals("5x10") : "Wrong value for popluation label.";
        assert frameFixture.label("Elitism").text().equals("5x1") : "Wrong value for elitism label.";
        // Generation count is number + 1 (because generations start at zero).
        assert frameFixture.label("Generations").text().equals("1") : "Wrong value for generations label.";
        assert frameFixture.label("Time").text().equals("10:10:10") : "Wrong value for elapsed time label.";
    }

    
    @Test(groups = "display-required") // Will fail if run in a headless environment.
    public void testTimeFormat()
    {
        StatusBar statusBar = new StatusBar();
        JFrame frame = new JFrame();
        frame.add(statusBar, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(400, 30);
        frame.validate();
        frameFixture.show();

        // Previous test checks two-digit field values, this test checks that single-digit
        // values and zeros are correctly padded.
        statusBar.populationUpdate(new PopulationData<Object>(new Object(), 10, 8, 2, true, 10, 1, 0, 1000));
        assert frameFixture.label("Time").text().equals("00:00:01");
    }

}
