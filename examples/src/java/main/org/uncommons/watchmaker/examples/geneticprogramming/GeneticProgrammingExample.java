// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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

import java.util.HashMap;
import java.util.Map;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.Probability;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

/**
 * Simple tree-based genetic programming application based on the first example
 * in Chapter 11 of Toby Segaran's Progamming Collective Intelligence.
 * @author Daniel Dyer
 */
public class GeneticProgrammingExample
{
    private static final Map<double[], Double> TEST_DATA = new HashMap<double[], Double>();
    static
    {
        TEST_DATA.put(new double[]{26, 35}, 829d);
        TEST_DATA.put(new double[]{8, 24}, 141d);
        TEST_DATA.put(new double[]{20, 1}, 467d);
        TEST_DATA.put(new double[]{33, 11}, 1215d);
        TEST_DATA.put(new double[]{37, 16}, 1517d);
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
        TreeFactory factory = new TreeFactory(2, 4, 0.5, 0.6);
        TreeMutation mutation = new TreeMutation(factory, new Probability(0.4d));
        TreeEvaluator evaluator = new TreeEvaluator(data);
        EvolutionEngine<Node> engine = new StandaloneEvolutionEngine<Node>(factory,
                                                                           mutation,
                                                                           evaluator,
                                                                           new RouletteWheelSelection(),
                                                                           new MersenneTwisterRNG());
        engine.addEvolutionObserver(new EvolutionLogger());
        EvolutionMonitor<Node> evolutionMonitor = new EvolutionMonitor<Node>();
        engine.addEvolutionObserver(evolutionMonitor);
        evolutionMonitor.showInFrame("GP Example");
        return engine.evolve(1000, 5, new TargetFitness(0d, evaluator.isNatural()));
    }


    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private static class EvolutionLogger implements EvolutionObserver<Node>
    {
        public void populationUpdate(PopulationData<Node> data)
        {
            System.out.println("Generation " + data.getGenerationNumber() + ": " + data.getBestCandidateFitness());
        }
    }
}
