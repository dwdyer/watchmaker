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
 * Unit test for the {@link IfThenElse} node type.
 * @author Daniel Dyer
 */
public class IfThenElseTest
{
    @Test
    public void testIfBranch()
    {
        Node node = new IfThenElse();
        node.setChildren(Arrays.asList(new Constant(1),
                                       new Constant(5),
                                       new Constant(10)));
        // Condition (1) is true, so result should be 5.
        double value = node.evaluate(new double[0]);
        assert value == 5 : "Wrong answer: " + value; 
    }


    @Test
    public void testElseBranch()
    {
        Node node = new IfThenElse();
        node.setChildren(Arrays.asList(new Constant(0),
                                       new Constant(5),
                                       new Constant(10)));
        // Condition (0) is true, so result should be 10.
        double value = node.evaluate(new double[0]);
        assert value == 10 : "Wrong answer: " + value; 
    }


    /**
     * An IfThenElse node requires exactly 3 child nodes.  Adding
     * more should result in an exception.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTooManyChildren()
    {
        Node node = new IfThenElse();
        node.setChildren(Arrays.asList(new Constant(0),
                                       new Constant(5),
                                       new Constant(10),
                                       new Constant(15))); // 4 children should cause an exception.
    }
}
