// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.swing;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.uncommons.maths.Maths;
import org.uncommons.maths.number.AdjustableNumberGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * A GUI control that allows the user to set/update the value of a
 * {@link Probability} parameter.
 * @author Daniel Dyer
 */
public class ProbabilityParameterControl implements EvolutionControl
{
    private final Probability defaultValue;
    private final int range;
    private final JSlider control;
    private final AdjustableNumberGenerator<Probability> numberGenerator;


    /**
     * Creates a control with a default range of 0..1 and a default granularity
     * of 2 decimal places.
     * @param defaultValue The default probability value.
     */
    public ProbabilityParameterControl(Probability defaultValue)
    {
        this(Probability.ZERO, Probability.ONE, 2, defaultValue);
    }


    /**
     * @param minimum The minimum probability that this control will permit.
     * @param maximum The maximum probability that this control will permit.
     * @param decimalPlaces The granularity of the control.
     * @param initialValue The default probability.
     */
    public ProbabilityParameterControl(Probability minimum,
                                       Probability maximum,
                                       int decimalPlaces,
                                       Probability initialValue)
    {
        if (initialValue.compareTo(minimum) < 0 || initialValue.compareTo(maximum) > 0)
        {
            throw new IllegalArgumentException("Initial value must respect minimum and maximum.");
        }
        this.defaultValue = initialValue;
        this.numberGenerator = new AdjustableNumberGenerator<Probability>(this.defaultValue);
        this.range = (int) Maths.raiseToPower(10, decimalPlaces);
        this.control = createSlider(initialValue, minimum, maximum);
    }

    
    private JSlider createSlider(Probability initialValue,
                                 Probability minimum,
                                 Probability maximum)
    {
        int value = (int) Math.round(range * initialValue.doubleValue());
        int min = (int) Math.round(range * minimum.doubleValue());
        int max = (int) Math.round(range * maximum.doubleValue());
        final JSlider slider = new JSlider(min, max, value);
        slider.setToolTipText(defaultValue.toString());
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent changeEvent)
            {
                Probability probability = new Probability((double) slider.getValue() / range);
                numberGenerator.setValue(probability);
                slider.setToolTipText(probability.toString());
            }
        });
        Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        labels.put(min, new JLabel(minimum.toString()));
        labels.put(max, new JLabel(maximum.toString()));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(range / 10);
        slider.setPaintTicks(true);
        return slider;
    }


    /**
     * {@inheritDoc}
     */
    public JComponent getControl()
    {
        return control;
    }


    /**
     * {@inheritDoc}
     */
    public void reset()
    {
        int value = (int) Math.round(range * defaultValue.doubleValue());
        control.setValue(value);
        control.setToolTipText(defaultValue.toString());
        numberGenerator.setValue(defaultValue);
    }


    /**
     * Returns a number generator that simply returns the current probability value
     * represented by the position of the slider control.
     * @return A number generator that can be used to control an evolutionary program.
     */
    public NumberGenerator<Probability> getNumberGenerator()
    {
        return numberGenerator;
    }
}
