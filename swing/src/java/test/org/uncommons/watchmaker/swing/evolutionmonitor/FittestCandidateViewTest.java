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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.fest.swing.core.RobotFixture;
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

        // View should initially display nothing.
        assert frameFixture.panel("ViewPanel").component().getComponentCount() == 0 : "View should be empty.";

        PopulationData<BigDecimal> data = new PopulationData<BigDecimal>(BigDecimal.TEN,
                                                                         10,
                                                                         5,
                                                                         2,
                                                                         5,
                                                                         0,
                                                                         1,
                                                                         100);
        view.populationUpdate(data);
        frameFixture.panel("ViewPanel").textBox().requireNotEditable();
        String text = frameFixture.panel("ViewPanel").textBox().component().getText();
        assert text.equals("10") : "Candidate rendered incorrectly.";
    }
}
