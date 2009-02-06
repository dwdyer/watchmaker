// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.bits;

import java.util.ArrayList;
import java.util.List;
import org.uncommons.maths.binary.BitString;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.ConcurrentEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.factories.BitStringFactory;
import org.uncommons.watchmaker.framework.operators.BitStringCrossover;
import org.uncommons.watchmaker.framework.operators.BitStringMutation;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

/**
 * An implementation of the first exercise (page 31) from the book An Introduction to
 * Genetic Algorithms, by Melanie Mitchell.  The algorithm evolves bit strings and the
 * fitness function simply counts the number of ones in the bit string.  The evolution
 * should therefore converge on strings that consist only of ones.
 * @author Daniel Dyer
 */
public class BitsExample
{
    private static final int BITS = 20;

    public static void main(String[] args)
    {
        evolveBits(BITS);
    }


    public static BitString evolveBits(int length)
    {
        List<EvolutionaryOperator<BitString>> operators = new ArrayList<EvolutionaryOperator<BitString>>(2);
        operators.add(new BitStringCrossover(1, new Probability(0.7d)));
        operators.add(new BitStringMutation(new Probability(0.001d)));
        EvolutionaryOperator<BitString> pipeline = new EvolutionPipeline<BitString>(operators);
        EvolutionEngine<BitString> engine = new ConcurrentEvolutionEngine<BitString>(new BitStringFactory(length),
                                                                                     pipeline,
                                                                                     new BitStringEvaluator(),
                                                                                     new RouletteWheelSelection(),
                                                                                     new MersenneTwisterRNG());
        engine.addEvolutionObserver(new EvolutionLogger());
        return engine.evolve(100, // 100 individuals in each generation.
                             0, // Don't use elitism.
                             new TargetFitness(length, true)); // Continue until a perfect match is found.        
    }


    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private static class EvolutionLogger implements EvolutionObserver<BitString>
    {
        public void populationUpdate(PopulationData<? extends BitString> data)
        {
            System.out.println("Generation " + data.getGenerationNumber() + ": " + data.getBestCandidate());
        }
    }
}
