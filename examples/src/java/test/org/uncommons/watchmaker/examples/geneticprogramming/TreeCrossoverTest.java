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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;

/**
 * Unit test for the {@link TreeCrossover} evolutionary operator used by
 * the genetic programming example application.
 * @author Daniel Dyer
 */
public class TreeCrossoverTest
{
    /**
     * This is just a simple sanity check.  Cross-over should result in the same
     * number of individuals, all constructed from the same set of nodes that existed
     * in the parent generation (but perhaps connected differently).
     */
    @Test
    public void testCrossover()
    {
        TreeCrossover crossover = new TreeCrossover();
        Node tree1 = new Multiplication(new Constant(1), new Constant(2));
        Node tree2 = new Subtraction(new Parameter(0), new Parameter(1));

        List<Node> offspring = crossover.apply(Arrays.asList(tree1, tree2), ExamplesTestUtils.getRNG());
        assert offspring.size() == 2 : "Should be 2 offspring after cross-over.";
        int totalNodeCount = offspring.get(0).countNodes() + offspring.get(1).countNodes();
        assert totalNodeCount == 6 : "Should be exactly 6 nodes in total, is " + totalNodeCount; 
    }
}
