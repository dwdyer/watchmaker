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
import javax.swing.JFrame;
import javax.swing.JSlider;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;

/**
 * Unit test for the {@link ProbabilityParameterControl} component.
 * @author Daniel Dyer
 */
public class ProbabilityParameterControlTest
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

    
    @Test
    public void testDefaultValue()
    {
        Probability defaultValue = new Probability(0.75d);
        ProbabilityParameterControl control = new ProbabilityParameterControl(defaultValue);
        assert control.getNumberGenerator().nextValue().equals(defaultValue) : "Wrong initial value.";
    }


    /**
     * Initial value must not be less than the minimum.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDefaultValueTooLow()
    {
        new ProbabilityParameterControl(Probability.EVENS,
                                        Probability.ONE,
                                        2,
                                        new Probability(0.45d)); // Should throw an IllegalArgumentException.
    }


    /**
     * Initial value must not be less than the minimum.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDefaultValueTooHigh()
    {
        new ProbabilityParameterControl(Probability.ZERO,
                                        Probability.EVENS,
                                        2,
                                        new Probability(0.55d)); // Should throw an IllegalArgumentException.
    }


    /**
     * Minimum must be less than maximum.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMinimumHigherThanMaximum()
    {
        new ProbabilityParameterControl(new Probability(0.7d),
                                        new Probability(0.6d),
                                        2,
                                        new Probability(0.7d)); // Should throw an IllegalArgumentException.
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidDecimalPlaces()
    {
        new ProbabilityParameterControl(Probability.ZERO,
                                        Probability.ONE,
                                        0, // Invalid, should trigger IllegalArgumentException.
                                        Probability.EVENS);
    }


    @Test(dependsOnMethods = "testDefaultValue",
          groups = "display-required")
    public void testSlider()
    {
        Probability defaultValue = new Probability(0.75d);
        ProbabilityParameterControl control = new ProbabilityParameterControl(defaultValue);

        JFrame frame = new JFrame();
        frame.add(control.getControl(), BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(300, 50);
        frame.validate();
        frame.setVisible(true);

        JSlider slider = frameFixture.slider().component();
        assert slider.getValue() == 75 : "Wrong slider position: " + slider.getValue();
        String displayedValue = frameFixture.label().text();
        assert displayedValue.equals("0.75") : "Wrong value displayed: " + displayedValue;


        frameFixture.slider().slideTo(80); // 80 ticks is a probability of 0.8.
        robot.waitForIdle();
        double probability = control.getNumberGenerator().nextValue().doubleValue();
        assert probability == 0.8 : "Wrong probability: " + probability;
        displayedValue = frameFixture.label().text();
        assert displayedValue.equals("0.80") : "Wrong value displayed: " + displayedValue;
    }


    @Test(dependsOnMethods = "testSlider",
          groups = "display-required")
    public void testReset()
    {
        Probability defaultValue = new Probability(0.75d);
        ProbabilityParameterControl control = new ProbabilityParameterControl(defaultValue);

        JFrame frame = new JFrame();
        frame.add(control.getControl(), BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(300, 50);
        frame.validate();
        frame.setVisible(true);

        JSlider slider = frameFixture.slider().component();
        frameFixture.slider().slideTo(80); // 80 ticks is a probability of 0.8.

        control.reset();
        assert control.getNumberGenerator().nextValue().equals(defaultValue) : "NumberGenerator reset failed.";
        assert slider.getValue() == 75 : "JSlider reset failed.";

        String displayedValue = frameFixture.label().text();
        assert displayedValue.equals("0.75") : "Wrong value displayed: " + displayedValue;
    }
}
