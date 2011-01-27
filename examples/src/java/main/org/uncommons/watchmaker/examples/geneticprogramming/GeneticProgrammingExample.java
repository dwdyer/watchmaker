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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.EvolutionLogger;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

/**
 * Simple tree-based genetic programming application based on the first example
 * in Chapter 11 of Toby Segaran's Programming Collective Intelligence.
 * @author Daniel Dyer
 */
public class GeneticProgrammingExample
{
    // This data describes the problem.  For each pair of inputs, the generated program
    // should return the associated output.  The goal of this appliction is to generalise
    // the examples into an equation.
    private static final Map<double[], Double> TEST_DATA = new HashMap<double[], Double>();
    static
    {
        TEST_DATA.put(new double[]{26, 35}, 829.0d);
        TEST_DATA.put(new double[]{8, 24}, 141.0d);
        TEST_DATA.put(new double[]{20, 1}, 467.0d);
        TEST_DATA.put(new double[]{33, 11}, 1215.0d);
        TEST_DATA.put(new double[]{37, 16}, 1517.0d);
    }


    public static void main(String[] args)
    {
        Node program = evolveProgram(TEST_DATA);
        System.out.println(program.print());
    }


    /**
     * Evolve a function to fit the specified data.
     * @param data A map from input values to expected output values.
     * @return A program that generates the correct outputs for all specified
     * sets of input.
     */
    public static Node evolveProgram(Map<double[], Double> data)
    {
        TreeFactory factory = new TreeFactory(2, // Number of parameters passed into each program.
                                              4, // Maximum depth of generated trees.
                                              Probability.EVENS, // Probability that a node is a function node.
                                              new Probability(0.6d)); // Probability that other nodes are params rather than constants.
        List<EvolutionaryOperator<Node>> operators = new ArrayList<EvolutionaryOperator<Node>>(3);
        operators.add(new TreeMutation(factory, new Probability(0.4d)));
        operators.add(new TreeCrossover());
        operators.add(new Simplification());
        TreeEvaluator evaluator = new TreeEvaluator(data);
        EvolutionEngine<Node> engine = new GenerationalEvolutionEngine<Node>(factory,
                                                                             new EvolutionPipeline<Node>(operators),
                                                                             evaluator,
                                                                             new RouletteWheelSelection(),
                                                                             new MersenneTwisterRNG());
        engine.addEvolutionObserver(new EvolutionLogger<Node>());
        return engine.evolve(1000, 5, new TargetFitness(0d, evaluator.isNatural()));
    }
}
