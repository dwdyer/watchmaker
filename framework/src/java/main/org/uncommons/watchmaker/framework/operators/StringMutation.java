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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation of individual characters in a string according to some
 * probability.
 * @author Daniel Dyer
 */
public class StringMutation implements EvolutionaryOperator<String>
{
    private final char[] alphabet;
    private final NumberGenerator<Probability> mutationProbability;

    /**
     * Creates a mutation operator that is applied with the given
     * probability and draws its characters from the specified alphabet.
     * @param alphabet The permitted values for each character in a string.
     * @param mutationProbability The probability that a given character
     * is changed.
     */
    public StringMutation(char[] alphabet, Probability mutationProbability)
    {
        this(alphabet, new ConstantGenerator<Probability>(mutationProbability));
    }


    /**
     * Creates a mutation operator that is applied with the given
     * probability and draws its characters from the specified alphabet.
     * @param alphabet The permitted values for each character in a string.
     * @param mutationProbability The (possibly variable) probability that a
     * given character is changed.
     */
    public StringMutation(char[] alphabet,
                          NumberGenerator<Probability> mutationProbability)
    {
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


    /**
     * Mutate a single string.  Zero or more characters may be modified.  The
     * probability of any given character being modified is governed by the
     * probability generator configured for this mutation operator.
     * @param s The string to mutate.
     * @param rng A source of randomness.
     * @return The mutated string.
     */
    private String mutateString(String s, Random rng)
    {
        StringBuilder buffer = new StringBuilder(s);
        for (int i = 0; i < buffer.length(); i++)
        {
            if (mutationProbability.nextValue().nextEvent(rng))
            {
                buffer.setCharAt(i, alphabet[rng.nextInt(alphabet.length)]);
            }
        }
        return buffer.toString();
    }
}
