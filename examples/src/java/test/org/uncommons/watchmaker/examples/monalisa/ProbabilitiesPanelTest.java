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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for the {@link ProbabilitiesPanel} class.
 * @author Daniel Dyer
 */
public class ProbabilitiesPanelTest
{
    private Robot robot;

    @BeforeMethod(groups = "display-required")
    public void prepare()
    {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }


    @AfterMethod(groups = "display-required")
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }
    
    @Test(groups = "display-required")
    public void testSetAllProbabilitiesToZero()
    {
        ProbabilitiesPanel panel = new ProbabilitiesPanel();
        JFrame frame = new JFrame();
        frame.add(panel, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(700, 300);
        frame.validate();

        frameFixture.show();
        frameFixture.panel("AddPolygon").slider().slideToMinimum();
        frameFixture.panel("RemovePolygon").slider().slideToMinimum();
        frameFixture.panel("MovePolygon").slider().slideToMinimum();
        frameFixture.panel("Cross-over").slider().slideToMinimum();
        frameFixture.panel("AddVertex").slider().slideToMinimum();
        frameFixture.panel("RemoveVertex").slider().slideToMinimum();
        frameFixture.panel("MoveVertex").slider().slideToMinimum();
        frameFixture.panel("ChangeColour").slider().slideToMinimum();

        Dimension canvasSize = new Dimension(100, 100);
        PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
        EvolutionaryOperator<List<ColouredPolygon>> operator = panel.createEvolutionPipeline(factory,
                                                                                             canvasSize,
                                                                                             ExamplesTestUtils.getRNG());
        List<ColouredPolygon> candidate1 = Arrays.asList(new ColouredPolygon(Color.WHITE,
                                                                             Arrays.asList(new Point(1, 1))));
        List<ColouredPolygon> candidate2 = Arrays.asList(new ColouredPolygon(Color.BLACK,
                                                                             Arrays.asList(new Point(2, 2))));
        List<List<ColouredPolygon>> population = new ArrayList<List<ColouredPolygon>>(2);
        population.add(candidate1);
        population.add(candidate2);
        
        List<List<ColouredPolygon>> evolved = operator.apply(population,
                                                             ExamplesTestUtils.getRNG());
        // Candidate order may have changed, but individual candidates should remain unaltered.
        assert (checkEquals(evolved.get(0), candidate1) && checkEquals(evolved.get(1), candidate2))
               || (checkEquals(evolved.get(0), candidate2) && checkEquals(evolved.get(1), candidate1))
            : "Candidates should be unaltered when all probabilities are zero.";
    }


    private boolean checkEquals(List<ColouredPolygon> candidate1,
                                List<ColouredPolygon> candidate2)
    {
        if (candidate1.size() != candidate2.size())
        {
            return false;
        }
        for (int i = 0; i < candidate1.size(); i++)
        {
            ColouredPolygon polygon1 = candidate1.get(i);
            ColouredPolygon polygon2 = candidate2.get(i);
            if (!polygon1.getColour().equals(polygon2.getColour()))
            {
                return false;
            }
            List<Point> vertices1 = polygon1.getVertices();
            List<Point> vertices2 = polygon2.getVertices();
            if (vertices1.size() != vertices2.size())
            {
                return false;
            }
            for (int j = 0; j < vertices1.size(); j++)
            {
                Point point1 = vertices1.get(j);
                Point point2 = vertices2.get(j);
                if (point1.x != point2.x || point1.y != point2.y)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
