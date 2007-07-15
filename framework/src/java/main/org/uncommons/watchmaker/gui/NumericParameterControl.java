// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.gui;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.uncommons.maths.AdjustableNumberGenerator;
import org.uncommons.maths.NumberGenerator;

/**
 * A GUI control that allows the user to set/update the value of a
 * numeric parameter.
 * @author Daniel Dyer
 */
public class NumericParameterControl<T extends Number> implements EvolutionControl
{
    private final T initialValue;
    private final JSpinner control;
    private final AdjustableNumberGenerator<T> numberGenerator;

    public NumericParameterControl(T minimum,
                                   T maximum,
                                   T stepSize,
                                   T initialValue)
    {
        this.initialValue = initialValue;
        this.numberGenerator = new AdjustableNumberGenerator<T>(this.initialValue);
        control = new JSpinner(new SpinnerNumberModel(initialValue,
                                                      (Comparable) minimum,
                                                      (Comparable) maximum,
                                                      stepSize));
        control.addChangeListener(new ChangeListener()
        {
            @SuppressWarnings({"unchecked"})
            public void stateChanged(ChangeEvent changeEvent)
            {
                numberGenerator.setValue((T) control.getValue());
            }
        });
    }


    public JSpinner getControl()
    {
        return control;
    }


    /**
     * Resets the spinner to its initial state.
     */
    public void reset()
    {
        control.setValue(initialValue);
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
}
