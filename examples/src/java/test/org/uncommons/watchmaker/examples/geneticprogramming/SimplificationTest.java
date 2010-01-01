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
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;

/**
 * Unit test for the {@link Simplification} evolutionary operator.
 * @author Daniel Dyer
 */
public class SimplificationTest
{
    /**
     * When the probability is 1, all candidates should be processed.
     */
    @Test
    public void testProbabilityOne()
    {
        Node node1 = new Addition(new Constant(1), new Constant(1));
        Node node2 = new Subtraction(new Constant(5), new Constant(4));
        Node node3 = new Multiplication(new Constant(3), new Constant(3));
        List<Node> population = Arrays.asList(node1, node2, node3); 

        Simplification simplification = new Simplification();
        List<Node> evolved = simplification.apply(population, ExamplesTestUtils.getRNG());
        assert evolved.size() == population.size() : "Output should be same size as input.";
        for (Node node : evolved)
        {
            assert node instanceof Constant : "Node was not simplified.";
        }
    }


    /**
     * When the probability is 0, no candidates should be processed.
     */
    @Test
    public void testProbabilityZero()
    {
        Node node1 = new Addition(new Constant(1), new Constant(1));
        Node node2 = new Subtraction(new Constant(5), new Constant(4));
        Node node3 = new Multiplication(new Constant(3), new Constant(3));
        List<Node> population = Arrays.asList(node1, node2, node3);

        Simplification simplification = new Simplification(Probability.ZERO);
        List<Node> evolved = simplification.apply(population, ExamplesTestUtils.getRNG());
        assert evolved.size() == population.size() : "Output should be same size as input.";
        for (Node node : evolved)
        {
            assert !(node instanceof Constant) : "Node should not have been simplified.";
        }
    }
}
