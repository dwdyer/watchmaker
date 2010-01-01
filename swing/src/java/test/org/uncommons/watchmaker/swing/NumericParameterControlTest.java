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

import org.testng.annotations.Test;
import org.uncommons.maths.number.NumberGenerator;

/**
 * Unit test for the numeric parameter GUI control.
 * @author Daniel Dyer
 */
public class NumericParameterControlTest
{
    /**
     * Make sure that the control works with integers.
     */
    @Test
    public void testIntegerValues()
    {
        NumericParameterControl<Integer> control = new NumericParameterControl<Integer>(50, 100, 1, 60);
        NumberGenerator<Integer> generator = control.getNumberGenerator();

        // Check the initial output of the generator.
        int initialValue = generator.nextValue();
        assert initialValue == 60 : "Initial value should be 60, is " + initialValue;

        // Modify the position of the slider and check that the generator value changes.
        control.getControl().setValue(87);
        int adjustedValue = generator.nextValue();
        assert adjustedValue == 87 : "Adjusted value should be 87, is " + adjustedValue;

        // Reset the control and check that the output is reverted to the default.
        control.reset();
        int resetValue = generator.nextValue();
        assert resetValue == 60 : "Reset value should be 60, is " + resetValue;
    }


    /**
     * Make sure that the control works with doubles.
     */
    @Test
    public void testRealValues()
    {
        NumericParameterControl<Double> control = new NumericParameterControl<Double>(0d, 1d, 0.01d, 0.4d);
        NumberGenerator<Double> generator = control.getNumberGenerator();

        // Check the initial output of the generator.
        double initialValue = generator.nextValue();
        assert initialValue == 0.4d : "Initial value should be 0.4, is " + initialValue;

        // Modify the position of the slider and check that the generator value changes.
        control.getControl().setValue(0.73d);
        double adjustedValue = generator.nextValue();
        assert adjustedValue == 0.73d : "Adjusted value should be 0.73, is " + adjustedValue;

        // Reset the control and check that the output is reverted to the default.
        control.reset();
        double resetValue = generator.nextValue();
        assert resetValue == 0.4d : "Reset value should be 0.4, is " + resetValue;
    }
}
