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
package org.uncommons.watchmaker.examples.strings;

import java.util.ArrayList;
import java.util.List;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.StringCrossover;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

/**
 * Simple evolutionary algorithm that evolves a population of randomly-generated
 * strings until at least one matches a specified target string.
 * @author Daniel Dyer
 */
public final class StringsExample
{
    private static final char[] ALPHABET = new char[27];
    static
    {
        for (char c = 'A'; c <= 'Z'; c++)
        {
            ALPHABET[c - 'A'] = c;
        }
        ALPHABET[26] = ' ';
    }


    /**
     * Entry point for the sample application.  Any data specified on the
     * command line is considered to be the target String.  If no target is
     * specified, a default of "HELLOW WORLD" is used instead.
     * @param args The target String (as an array of words).
     */
    public static void main(String[] args)
    {
        String target = args.length == 0 ? "HELLO WORLD" : convertArgs(args);
        String result = evolveString(target);
        System.out.println("Evolution result: " + result);
    }


    public static String evolveString(String target)
    {
        StringFactory factory = new StringFactory(ALPHABET, target.length());
        List<EvolutionaryOperator<String>> operators = new ArrayList<EvolutionaryOperator<String>>(2);
        operators.add(new StringMutation(ALPHABET, new Probability(0.02d)));
        operators.add(new StringCrossover());
        EvolutionaryOperator<String> pipeline = new EvolutionPipeline<String>(operators);
        EvolutionEngine<String> engine = new GenerationalEvolutionEngine<String>(factory,
                                                                                 pipeline,
                                                                                 new StringEvaluator(target),
                                                                                 new RouletteWheelSelection(),
                                                                                 new MersenneTwisterRNG());
        engine.addEvolutionObserver(new EvolutionLogger());
        return engine.evolve(100, // 100 individuals in the population.
                             5, // 5% elitism.
                             new TargetFitness(0, false));
    }


    /**
     * Converts an arguments array into a single String of words
     * separated by spaces.
     * @param args The command-line arguments.
     * @return A single String made from the command line data.
     */
    private static String convertArgs(String[] args)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            result.append(args[i]);
            if (i < args.length - 1)
            {
                result.append(' ');
            }
        }
        return result.toString().toUpperCase();
    }


    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private static class EvolutionLogger implements EvolutionObserver<String>
    {
        public void populationUpdate(PopulationData<? extends String> data)
        {
            System.out.printf("Generation %d: %s\n",
                              data.getGenerationNumber(),
                              data.getBestCandidate());
        }
    }
}
