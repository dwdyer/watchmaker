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
import java.util.Map;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Fitness function for the genetic programming example application.
 * An evolved program tree is tested against a table of inputs and associated
 * expected outputs.  If the evolved program correctly calculates the right answer
 * for all sets of inputs then it has a fitness of zero.  Otherwise, its fitness
 * is an error value that indicates how accurate it was (the larger the combined
 * error value, the less accurate the function is). 
 * @author Daniel Dyer
 */
public class TreeEvaluator implements FitnessEvaluator<Node>
{
    private final Map<double[], Double> data;


    /**
     * @param data Each row is consists of a set of inputs and an expected output (the
     * last item in the row is the output).
     */
    public TreeEvaluator(Map<double[], Double> data)
    {
        this.data = data;
    }


    /**
     * If the evolved program correctly calculates the right answer
     * for all sets of inputs then it has a fitness of zero.  Otherwise, its fitness
     * is an error value that indicates how accurate it was (the larger the combined
     * error value, the less accurate the function is).
     * The combined error value is calculated by summing the squares of each individual
     * error (the difference between the expected output and the actual output).
     * @param candidate The program tree to evaluate.
     * @param population Ignored by this implementation.
     * @return The fitness score for the specified candidate.
     */
    public double getFitness(Node candidate, List<? extends Node> population)
    {
        double error = 0;
        for (Map.Entry<double[], Double> entry : data.entrySet())
        {
            double actualValue = candidate.evaluate(entry.getKey());
            double diff = actualValue - entry.getValue();
            error += (diff * diff);
        }
        return error;
    }


    /**
     * This fitness evaluator is a minimising function.  A fitness of zero
     * indicates a perfect solution.
     * @return false
     */
    public boolean isNatural()
    {
        return false;
    }
}
