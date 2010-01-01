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
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Unit test for the {@link FitnessEvaluator} used
 * in the genetic programming example applicaiton.
 * @author Daniel Dyer
 */
public class TreeEvaluatorTest
{
    /**
     * A function that perfectly generates the correct output for all inputs
     * should have a fitness of zero.
     */
    @Test
    public void testPerfectFunction()
    {
        Map<double[], Double> data = new HashMap<double[], Double>();
        // Data for multiplication program.
        data.put(new double[]{5d, 3d}, 15d);
        data.put(new double[]{3d, 8d}, 24d);
        data.put(new double[]{7d, 2d}, 14d);

        FitnessEvaluator<Node> evaluator = new TreeEvaluator(data);

        // Program that multiplies its two inputs together.
        Node program = new Multiplication(new Parameter(0), new Parameter(1));

        double fitness = evaluator.getFitness(program, Arrays.asList(program));
        assert fitness == 0 : "Correct program should have zero fitness.";
    }


    /**
     * A function that doesn't generate the correct output for all inputs
     * should have a non-zero fitness.
     */
    @Test
    public void testIncorrectFunction()
    {
        Map<double[], Double> data = new HashMap<double[], Double>();
        // Data for multiplication program.
        data.put(new double[]{5d, 3d}, 15d);
        data.put(new double[]{3d, 8d}, 24d);
        data.put(new double[]{7d, 2d}, 14d);

        FitnessEvaluator<Node> evaluator = new TreeEvaluator(data);

        // Program that multiplies its first input by 3 (will give the correct answer
        // for the first set of inputs but the wrong answer for the other two).
        Node program = new Multiplication(new Parameter(0), new Constant(3d));

        double fitness = evaluator.getFitness(program, Arrays.asList(program));
        // Error on second example is 15, error on third is 7.
        // 15^2 + 7^2 = 225 + 49 = 274
        assert fitness == 274d : "Wrong fitness for incorrect program.";
    }
}
