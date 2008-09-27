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
 * Simple unit test for the {@link IsGreater} node type.
 * @author Daniel Dyer
 */
public class IsGreaterTest
{
    /**
     * If two nodes are equal then neither is greater than the other.
     */
    @Test
    public void testBothNodesEqual()
    {
        Node node = new IsGreater();
        node.setChildren(Arrays.<Node>asList(new Constant(1), new Constant(1)));
        assert node.evaluate(new double[0]) == 0 : "First node should not be greater than second.";
    }


    @Test
    public void testFirstNodeGreater()
    {
        Node node = new IsGreater();
        node.setChildren(Arrays.<Node>asList(new Constant(2), new Constant(1)));
        assert node.evaluate(new double[0]) > 0 : "First node should be greater than second.";
    }


    @Test
    public void testSecondNodeGreater()
    {
        Node node = new IsGreater();
        node.setChildren(Arrays.<Node>asList(new Constant(-1), new Constant(1)));
        assert node.evaluate(new double[0]) == 0 : "First node should be less than second.";
    }
}
