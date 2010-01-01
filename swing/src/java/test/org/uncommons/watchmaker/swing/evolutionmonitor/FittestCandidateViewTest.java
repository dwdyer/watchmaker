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
import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;

/**
 * Unit test for the {@link FittestCandidateView} evolution monitor panel.
 * @author Daniel Dyer
 */
public class FittestCandidateViewTest
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

    
    @Test(groups = "display-required")
    public void testUpdate()
    {
        Renderer<Object, JComponent> renderer = new ObjectSwingRenderer();
        FittestCandidateView<BigDecimal> view = new FittestCandidateView<BigDecimal>(renderer);
        JFrame frame = new JFrame();
        frame.add(view, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(300, 300);
        frame.validate();
        frame.setVisible(true);

        view.populationUpdate(new PopulationData<BigDecimal>(BigDecimal.TEN, 10, 5, 2, true, 5, 0, 1, 100));
        robot.waitForIdle();

        // Check displayed fitness.
        String fitnessText = frameFixture.label("FitnessLabel").text();
        assert fitnessText.equals("10.0") : "Wrong fitness score displayed: " + fitnessText;

        // Check rendered candidate.
        frameFixture.textBox().requireNotEditable();
        String text = frameFixture.textBox().component().getText();
        assert text.equals("10") : "Candidate rendered incorrectly.";
    }


    /**
     * If the view is updated with the same candidate it is already displaying, it should
     * avoid the expense of re-rendering it.
     */
    @Test(groups = "display-required",
          dependsOnMethods = "testUpdate")
    public void testUpdateSameCandidate()
    {
        Renderer<Object, JComponent> renderer = new ObjectSwingRenderer();
        FittestCandidateView<BigDecimal> view = new FittestCandidateView<BigDecimal>(renderer);
        JFrame frame = new JFrame();
        frame.add(view, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(300, 300);
        frame.validate();
        frame.setVisible(true);

        PopulationData<BigDecimal> data1 = new PopulationData<BigDecimal>(BigDecimal.TEN, 10, 5, 2, true, 5, 0, 1, 100);
        // Render the first time.
        view.populationUpdate(data1);
        robot.waitForIdle();
        JTextComponent component1 = frameFixture.textBox().component();

        // Render the same candidate for the second generation.
        PopulationData<BigDecimal> data2 = new PopulationData<BigDecimal>(BigDecimal.TEN, 10, 5, 2, true, 5, 0, 2, 100);
        view.populationUpdate(data2);
        robot.waitForIdle();
        JTextComponent component2 = frameFixture.textBox().component();

        assert component1 == component2 : "Rendered component should be the same.";
    }
}
