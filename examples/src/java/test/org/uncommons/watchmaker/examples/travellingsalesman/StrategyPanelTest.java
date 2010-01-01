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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
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
        robot.waitForIdle();

        // Evolution controls should be enabled by default.
        frameFixture.panel("EvolutionPanel").requireEnabled();

        frameFixture.radioButton("BruteForceOption").click();

        // Evolution controls should be disabled when brute force option is selected.
        frameFixture.panel("EvolutionPanel").requireDisabled();

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
        robot.waitForIdle();

        frameFixture.radioButton("EvolutionOption").click();
        TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
        assert strategy instanceof EvolutionaryTravellingSalesman : "Wrong strategy class: " + strategy.getClass();
    }


    @Test(groups = "display-required")
    public void testDisablePanel()
    {
        StrategyPanel strategyPanel = new StrategyPanel(CITIES);
        JFrame frame = new JFrame();
        frame.add(strategyPanel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(500, 300);
        frame.validate();
        frame.setVisible(true);
        robot.waitForIdle();

        // Components should be enabled initially.
        frameFixture.radioButton("EvolutionOption").requireEnabled();
        frameFixture.panel("EvolutionPanel").requireEnabled();
        frameFixture.radioButton("BruteForceOption").requireEnabled();

        strategyPanel.setEnabled(false);
        frameFixture.radioButton("EvolutionOption").requireDisabled();
        frameFixture.panel("EvolutionPanel").requireDisabled();
        frameFixture.radioButton("BruteForceOption").requireDisabled();        
    }
}
