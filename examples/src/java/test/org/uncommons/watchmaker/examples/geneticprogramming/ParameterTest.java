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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.Arrays;
import org.testng.annotations.Test;

/**
 * Simple unit test for the {@link Parameter} node type.
 * @author Daniel Dyer
 */
public class ParameterTest
{
    @Test
    public void testParameterSelection()
    {
        Parameter parameter = new Parameter(2);
        double value = parameter.evaluate(new double[]{0, 1, 2, 3});
        assert value == 2 : "Incorect argument selected: " + value;
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidParameterSelection()
    {
        Parameter parameter = new Parameter(2);
        // There is only one argument, so trying to select the 3rd one should
        // result in an IllegalArgumentException.
        parameter.evaluate(new double[]{0});
    }


    /**
     * A parameter node should not accept any child nodes.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetChildren()
    {
        Parameter parameter = new Parameter(2);
        parameter.setChildren(Arrays.asList(new Constant(1))); // Should throw an exception.
    }
}
