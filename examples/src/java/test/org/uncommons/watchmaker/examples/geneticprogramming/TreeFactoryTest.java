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

import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Unit test for the {@link TreeFactory} used by the gentic programming
 * example.
 * @author Daniel Dyer
 */
public class TreeFactoryTest
{
    @Test
    public void testMaxDepth()
    {
        final int maxDepth = 3;
        CandidateFactory<Node> factory = new TreeFactory(2,
                                                         maxDepth,
                                                         new Probability(0.6),
                                                         Probability.EVENS);
        List<Node> trees = factory.generateInitialPopulation(20, ExamplesTestUtils.getRNG());
        for (Node tree : trees)
        {
            // Make sure that each tree is no bigger than the maximum permitted.
            assert tree.getDepth() <= maxDepth : "Generated tree is too deep: " + tree.getDepth();
        }
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidParameterCount()
    {
        new TreeFactory(-1,
                        1,
                        Probability.EVENS,
                        Probability.EVENS); // Should throw an exception, parameter count can't be negative.
    }

    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxDepth()
    {
        new TreeFactory(1,
                        0,
                        Probability.EVENS,
                        Probability.EVENS); // Should throw an exception, depth must be at least one.
    }
}
