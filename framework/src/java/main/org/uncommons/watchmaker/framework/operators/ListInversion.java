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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * An evolutionary operator that randomly reverses a subsection of a list.
 * @author Daniel Dyer
 */
public class ListInversion<T> implements EvolutionaryOperator<List<T>>
{
    private final NumberGenerator<Probability> inversionProbability;


    /**
     * @param inversionProbability The probability that an individual list will have some
     * subsection inverted.
     */
    public ListInversion(Probability inversionProbability)
    {
        this(new ConstantGenerator<Probability>(inversionProbability));
    }


    /**
     * @param inversionProbability A variable that controls the probability that an
     * individual list will have some subsection inverted. 
     */
    public ListInversion(NumberGenerator<Probability> inversionProbability)
    {
        this.inversionProbability = inversionProbability;
    }


    public List<List<T>> apply(List<List<T>> selectedCandidates, Random rng)
    {
        List<List<T>> result = new ArrayList<List<T>>(selectedCandidates.size());
        for (List<T> candidate : selectedCandidates)
        {
            if (inversionProbability.nextValue().nextEvent(rng))
            {
                List<T> newCandidate = new ArrayList<T>(candidate);
                int length = newCandidate.size();
                int start = rng.nextInt(length - 2) + 2; // Make sure segment is at least 2 elements.
                int end = (start + rng.nextInt(length)) % length;
                int segmentLength = end - start;
                if (segmentLength < 0)
                {
                    segmentLength += length;
                }
                for (int i = 0; i < segmentLength / 2; i++)
                {
                    Collections.swap(newCandidate, (start + i) % length, (end - i + length) % length);
                }
                result.add(newCandidate);
            }
            else
            {
                result.add(candidate);
            }
        }
        return result;
    }
}
