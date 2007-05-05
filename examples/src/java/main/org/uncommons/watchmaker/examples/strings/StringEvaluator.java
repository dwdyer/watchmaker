// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Evaluates strings and assigns a fitness score based on how many characters
 * match the equivalent positions in a given target string.
 * @author Daniel Dyer
 */
public class StringEvaluator implements FitnessEvaluator<String>
{
    private final String targetString;


    /**
     * Creates a {@link FitnessEvaluator} that calculates scores
     * for Strings based on how close they are to a target String.
     * @param targetString The target of the evolution.
     */
    public StringEvaluator(String targetString)
    {
        this.targetString = targetString;
    }


    /**
     * Assigns one "fitness point" for every character in the candidate
     * string that matches the corresponding position in the target string.
     * @param candidate The evolved string to evaluate.
     * @return The fitness score (how many characters are correct) of the
     * specified string.
     */
    public double getFitness(String candidate)
    {
        int matches = 0;
        for (int i = 0; i < candidate.length(); i++)
        {
            if (candidate.charAt(i) == targetString.charAt(i))
            {
                ++matches;
            }
        }
        return matches;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isNatural()
    {
        return true;
    }
}
