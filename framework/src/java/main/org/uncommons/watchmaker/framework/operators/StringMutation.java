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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation of individual characters in a string according to some
 * probability.
 * @author Daniel Dyer
 */
public class StringMutation implements EvolutionaryOperator<String>
{
    private final char[] alphabet;
    private final double mutationProbability;

    /**
     * @param alphabet The permitted values for each character in a string.
     * @param mutationProbability The probability that a given character
     * is changed.
     */
    public StringMutation(char[] alphabet, double mutationProbability)
    {
        if (mutationProbability <= 0 || mutationProbability > 1)
        {
            throw new IllegalArgumentException("Mutation probability must be greater than "
                                               + "zero and less than or equal to one.");
        }
        this.alphabet = alphabet.clone();
        this.mutationProbability = mutationProbability;
    }


    public List<String> apply(List<String> selectedCandidates, Random rng)
    {
        List<String> mutatedPopulation = new ArrayList<String>(selectedCandidates.size());
        for (String s : selectedCandidates)
        {
            mutatedPopulation.add(mutateString(s, rng));
        }
        return mutatedPopulation;
    }


    private String mutateString(String s, Random rng)
    {
        StringBuilder buffer = new StringBuilder(s);
        for (int i = 0; i < buffer.length(); i++)
        {
            if (rng.nextDouble() < mutationProbability)
            {
                buffer.setCharAt(i, alphabet[rng.nextInt(alphabet.length)]);
            }
        }
        return buffer.toString();
    }
}
