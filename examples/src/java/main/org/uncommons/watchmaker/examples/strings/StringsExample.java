// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.strings;

import java.util.ArrayList;
import java.util.List;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.operators.StringCrossover;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;

/**
 * Simple evolutionary algorithm that evolves a population of randomly-generated
 * strings until at least one matches a specified target string.
 * @author Daniel Dyer
 */
public class StringsExample
{
    private static final char[] ALPHABET = new char[26];
    static
    {
        for (int i = 0; i < ALPHABET.length; i++)
        {
            ALPHABET[i] = (char) (i + 'a');
        }
    }

    private StringsExample()
    {
        // Prevents instantiation.
    }

    public static void main(String args[])
    {
        String target = args[0].toLowerCase();
        List<EvolutionaryOperator<? super String>> pipeline = new ArrayList<EvolutionaryOperator<? super String>>(2);
        pipeline.add(new StringMutation(ALPHABET, 0.02d));
        pipeline.add(new StringCrossover());
        EvolutionEngine<String> engine = new StandaloneEvolutionEngine<String>(new StringFactory(ALPHABET, target.length()),
                                                                               pipeline,
                                                                               new StringEvaluator(target),
                                                                               new RouletteWheelSelection(),
                                                                               new MersenneTwisterRNG());
        engine.addEvolutionObserver(new EvolutionLogger());
        String result = engine.evolve(100, // 100 individuals in the population.
                                      target.length(), // Perfect fitness score.
                                      120000); // Two minute timeout.
    }


    private static class EvolutionLogger implements EvolutionObserver<String>
    {
        public void populationUpdate(PopulationData<? extends String> data)
        {
            System.out.println("Generation " + data.getGenerationNumber() + ": " + data.getBestCandidate());
        }
    }
}
