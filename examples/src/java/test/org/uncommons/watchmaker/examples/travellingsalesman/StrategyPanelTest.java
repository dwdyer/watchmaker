// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.fest.swing.core.RobotFixture;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Basic unit test for the {@link StrategyPanel} used by the Travelling
 * Salesman example applet.  Makes sure that it returns the correct type
 * of solver strategy depending on the radio button settings.
 * @author Daniel Dyer
 */
public class StrategyPanelTest
{
    private static final TestDistances CITIES = new TestDistances();
    
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

    
    @Test(groups = "display-required")
    public void testBruteForceOption()
    {
        StrategyPanel strategyPanel = new StrategyPanel(CITIES);
        JFrame frame = new JFrame();
        frame.add(strategyPanel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(500, 300);
        frame.validate();
        frame.setVisible(true);
        frameFixture.radioButton("BruteForce").click();
        TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
        assert strategy instanceof BruteForceTravellingSalesman : "Wrong strategy class: " + strategy.getClass();
    }


    @Test(groups = "display-required")
    public void testEvolutionOption()
    {
        StrategyPanel strategyPanel = new StrategyPanel(CITIES);
        JFrame frame = new JFrame();
        frame.add(strategyPanel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(500, 300);
        frame.validate();
        frame.setVisible(true);
        frameFixture.radioButton("Evolution").click();
        TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
        assert strategy instanceof EvolutionaryTravellingSalesman : "Wrong strategy class: " + strategy.getClass();
    }
}
