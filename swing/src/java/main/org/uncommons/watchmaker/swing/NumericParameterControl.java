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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.uncommons.maths.number.AdjustableNumberGenerator;
import org.uncommons.maths.number.NumberGenerator;

/**
 * A GUI control that allows the user to set/update the value of a
 * numeric parameter.
 * @param <T> The numeric type of this control (e.g. Integer, Double).
 * @author Daniel Dyer
 */
public class NumericParameterControl<T extends Number & Comparable<T>> implements EvolutionControl
{
    private final T defaultValue;
    private final JSpinner control;
    private final AdjustableNumberGenerator<T> numberGenerator;

    public NumericParameterControl(T minimum,
                                   T maximum,
                                   T stepSize,
                                   T initialValue)
    {
        this.defaultValue = initialValue;
        this.numberGenerator = new AdjustableNumberGenerator<T>(this.defaultValue);
        control = new JSpinner(new SpinnerNumberModel(initialValue,
                                                      minimum,
                                                      maximum,
                                                      stepSize));
        control.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent changeEvent)
            {
                @SuppressWarnings("unchecked")
                T value = (T) control.getValue();
                numberGenerator.setValue(value);
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    public JSpinner getControl()
    {
        return control;
    }


    /**
     * {@inheritDoc}
     */
    public void reset()
    {
        control.setValue(defaultValue);
        control.setEnabled(true);
    }


    /**
     * Returns a number generator that simply returns the current value contained
     * in the spinner field.
     * @return A number generator that can be used to control an evolutionary program.
     */
    public NumberGenerator<T> getNumberGenerator()
    {
        return numberGenerator;
    }


    /**
     * {@inheritDoc}
     */
    public void setDescription(String description)
    {
        control.setToolTipText(description);
    }
}
