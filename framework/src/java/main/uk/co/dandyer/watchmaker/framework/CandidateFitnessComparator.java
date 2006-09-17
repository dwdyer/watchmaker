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

import java.util.Comparator;

/**
 * A comparator for ranking arbitrary canidate types by fitness.
 * @author Daniel Dyer
 */
public class CandidateFitnessComparator implements Comparator<EvaluatedCandidate<?>>
{
    private final boolean descending;

    /**
     * Creates a comparator that sorts the candidates in descending order
     * of fitness.  Equivalent to <code>new CandidateFitnessComparator(true)</code>.
     */
    public CandidateFitnessComparator()
    {
        this(true);
    }

    
    /**
     * Creates a comparator that sorts the candidates in either ascending or descending
     * order of fitness depending on the argument (false for ascending, true for
     * descending).  Ascending order is used for evolutionary algorithms that attempt
     * to minimise fitness scores and descending is used for algorithms that attempt
     * to maximise fitness scores.
     */
    public CandidateFitnessComparator(boolean descending)
    {
        this.descending = descending;
    }


    public int compare(EvaluatedCandidate<?> item1, EvaluatedCandidate<?> item2)
    {
        if (descending)
        {
            return Double.compare(item2.getFitness(), item1.getFitness());
        }
        else
        {
            return Double.compare(item1.getFitness(), item2.getFitness());
        }
    }
}
