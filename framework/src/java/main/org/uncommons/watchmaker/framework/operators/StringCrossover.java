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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.NumberGenerator;

/**
 * Variable-point (fixed or random) cross-over for String candidates.
 * @author Daniel Dyer
 */
public class StringCrossover extends AbstractCrossover<String>
{
    /**
     * Default is single-point cross-over.
     */
    public StringCrossover()
    {
        this(1);
    }

    
    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public StringCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     */
    public StringCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<String> mate(String parent1,
                                String parent2,
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
        return result;
    }
}
