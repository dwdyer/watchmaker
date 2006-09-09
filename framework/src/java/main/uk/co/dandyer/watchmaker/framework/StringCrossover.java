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
package uk.co.dandyer.watchmaker.framework;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * Variable-point (fixed or random) cross-over for String candidates.
 * @author Daniel Dyer
 */
public class StringCrossover extends AbstractCrossover<String>
{
    @SuppressWarnings("unchecked")
    protected <S extends String> List<S> reproduce(S parent1,
                                                   S parent2,
                                                   int numberOfCrossoverPoints,
                                                   Random rng)
    {
        if (parent1.length() != parent2.length())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        StringBuilder offspring1 = new StringBuilder(parent1);
        StringBuilder offspring2 = new StringBuilder(parent2);
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length());
            for (int j = 0; j < crossoverIndex; j++)
            {
                char temp = offspring1.charAt(j);
                offspring1.setCharAt(j, offspring2.charAt(j));
                offspring2.setCharAt(j, temp);
            }
        }
        List<String> result = new ArrayList<String>(2);
        result.add(offspring1.toString());
        result.add(offspring2.toString());
        // Since there are no sub-classes of String, this is a perfectly safe cast.
        return (List<S>) result;
    }
}
