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
package uk.co.dandyer.maths;

import java.lang.reflect.Array;

/**
 * Combination generator for generating all permutations for all sets up to
 * 20 elements in size.  The reason for the size restriction is in order to
 * provide the best possible performance for small sets by avoiding the need
 * to perform the internal arithmetic using {@link java.math.BigInteger} (which
 * would be necessary to calculate any factorial greater than 20!).
 * @author Daniel Dyer (modified from the original version written by Michael
 * Gilleland of Merriam Park Software -
 * <a href="http://www.merriampark.com/perm.htm">http://www.merriampark.com/comb.htm</a>).
 */
public class CombinationGenerator<T>
{
    private final T[] elements;
    private final int[] combinationIndices;
    private long remainingCombinations;
    private long totalCombinations;

    /**
     * Create a combination generator that generates all combinations of
     * a specified length from the given set.
     * @param elements The set from which to generate combinations.
     * @param combinationLength The length of the combinations to be generated.
     */
    public CombinationGenerator(T[] elements,
                                int combinationLength)
    {
        if (combinationLength > elements.length)
        {
            throw new IllegalArgumentException("Combination length cannot be greater than set size.");
        }
        if (elements.length < 1 || elements.length > 20)
        {
            throw new IllegalArgumentException("Combination length must be between 1 and 20.");
        }

        this.elements = elements;
        this.combinationIndices = new int[combinationLength];

        long sizeFactorial = Maths.factorial(elements.length);
        long lengthFactorial = Maths.factorial(combinationLength);
        long differenceFactorial = Maths.factorial(elements.length - combinationLength);
        totalCombinations = sizeFactorial / (lengthFactorial * differenceFactorial);
        reset();
    }


    /**
     * Reset the combination generator.
     */
    public final void reset()
    {
        for (int i = 0; i < combinationIndices.length; i++)
        {
            combinationIndices[i] = i;
        }
        remainingCombinations = totalCombinations;
    }


    /**
     * Returns the number of combinations not yet generated.
     */
    public long getRemainingCombinations ()
    {
        return remainingCombinations;
    }


    /**
     * Are there more combinations?
     */
    public boolean hasMore()
    {
        return remainingCombinations > 0;
    }


    /**
     * Returns the total number of combinations.
     */
    public long getTotalCombinations ()
    {
        return totalCombinations;
    }



    /**
     * Generate the next combination. The algorithm is from Kenneth H. Rosen, Discrete
     * Mathematics and Its Applications, 2nd edition (NY: McGraw-Hill, 1991), p. 286.
     */
    @SuppressWarnings("unchecked")
    public T[] next()
    {
        if (remainingCombinations < totalCombinations)
        {
            int i = combinationIndices.length - 1;
            while (combinationIndices[i] == elements.length - combinationIndices.length + i)
            {
                i--;
            }
            ++combinationIndices[i];
            for (int j = i + 1; j < combinationIndices.length; j++)
            {
                combinationIndices[j] = combinationIndices[i] + j - i;
            }
        }
        --remainingCombinations;

        // Generate actual combination.
        T[] combination = (T[]) Array.newInstance(elements.getClass().getComponentType(),
                                                  combinationIndices.length);
        for (int i = 0; i < combinationIndices.length; i++)
        {
            combination[i] = elements[combinationIndices[i]];
        }
        return combination;
    }
}
