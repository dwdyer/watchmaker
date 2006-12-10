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
package org.uncommons.maths;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Combination generator for generating all combinations for all sets up to
 * 20 elements in size.  The reason for the size restriction is in order to
 * provide the best possible performance for small sets by avoiding the need
 * to perform the internal arithmetic using {@link java.math.BigInteger} (which
 * would be necessary to calculate any factorial greater than 20!).
 * @param <T> The type of element that the combinations are made from.
 * @author Daniel Dyer (modified from the original version written by Michael
 * Gilleland of Merriam Park Software -
 * <a href="http://www.merriampark.com/perm.htm">http://www.merriampark.com/comb.htm</a>).
 */
public class CombinationGenerator<T>
{
    // Maximum number of elements that can be combined by this class.
    private static final int MAX_SET_LENGTH = 20;

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
        if (elements.length < 1 || elements.length > MAX_SET_LENGTH)
        {
            throw new IllegalArgumentException("Combination length must be between 1 and 20.");
        }

        this.elements = elements.clone();
        this.combinationIndices = new int[combinationLength];

        long sizeFactorial = Maths.factorial(elements.length);
        long lengthFactorial = Maths.factorial(combinationLength);
        long differenceFactorial = Maths.factorial(elements.length - combinationLength);
        totalCombinations = sizeFactorial / (lengthFactorial * differenceFactorial);
        reset();
    }


    /**
     * Create a combination generator that generates all combinations of
     * a specified length from the given set.
     * @param elements The set from which to generate combinations.
     * @param combinationLength The length of the combinations to be generated.
     */
    @SuppressWarnings("unchecked")
    public CombinationGenerator(Collection<T> elements,
                                int combinationLength)
    {
        this(elements.toArray((T[]) new Object[elements.size()]),
             combinationLength);
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
     * @return The number of combinations not yet generated.
     */
    public long getRemainingCombinations()
    {
        return remainingCombinations;
    }


    /**
     * Are there more combinations?
     * @return true if there are more combinations available, false otherwise.
     */
    public boolean hasMore()
    {
        return remainingCombinations > 0;
    }


    /**
     * @return The total number of combinations.
     */
    public long getTotalCombinations()
    {
        return totalCombinations;
    }


    /**
     * Generate the next combination and return an array containing
     * the appropriate elements.
     * @see #nextCombinationAsArray(Object[])
     * @see #nextCombinationAsList()
     * @return An array containing the elements that make up the next combination.
     */
    @SuppressWarnings("unchecked")
    public T[] nextCombinationAsArray()
    {
        generateNextCombinationIndices();
        // Generate actual combination.
        T[] combination = (T[]) Array.newInstance(elements.getClass().getComponentType(),
                                                  combinationIndices.length);
        for (int i = 0; i < combinationIndices.length; i++)
        {
            combination[i] = elements[combinationIndices[i]];
        }
        return combination;
    }


    /**
     * Generate the next combination and return an array containing
     * the appropriate elements.  This overloaded method allows the caller
     * to provide an array that will be used and returned.
     * The purpose of this is to improve performance when iterating over
     * combinations.  If the {@link #nextCombinationAsArray()} method is
     * used it will create a new array every time.  When iterating over
     * combinations this will result in lots of short-lived objects that
     * have to be garbage collected.  This method allows a single array
     * instance to be reused in such circumstances.
     * @param destination Provides an array to use to create the
     * combination.  The specified array must be the same length as a
     * combination.
     */
    public void nextCombinationAsArray(T[] destination)
    {
        if (destination.length != elements.length)
        {
            throw new IllegalArgumentException("Destination array must be the same length as combinations.");
        }
        generateNextCombinationIndices();
        for (int i = 0; i < combinationIndices.length; i++)
        {
            destination[i] = elements[combinationIndices[i]];
        }
    }


    /**
     * Generate the next combination and return a list containing the
     * appropriate elements.
     * @see #nextCombinationAsList(List)
     * @see #nextCombinationAsArray()
     * @return A list containing the elements that make up the next combination.
     */
    public List<T> nextCombinationAsList()
    {
        generateNextCombinationIndices();
        // Generate actual combination.
        List<T> combination = new ArrayList<T>(elements.length);
        for (int i : combinationIndices)
        {
            combination.add(elements[i]);
        }
        return combination;
    }


    /**
     * Generate the next combination and return a list containing
     * the appropriate elements.  This overloaded method allows the caller
     * to provide a list that will be used and returned.
     * The purpose of this is to improve performance when iterating over
     * combinations.  If the {@link #nextCombinationAsList()} method is
     * used it will create a new list every time.  When iterating over
     * combinations this will result in lots of short-lived objects that
     * have to be garbage collected.  This method allows a single list
     * instance to be reused in such circumstances.
     * @param destination Provides a list to use to create the
     * combination.
     */
    public void nextCombinationAsList(List<T> destination)
    {
        generateNextCombinationIndices();
        // Generate actual combination.
        destination.clear();
        for (int i : combinationIndices)
        {
            destination.add(elements[i]);
        }
    }



    /**
     * Generate the indices into the elements array for the next combination. The
     * algorithm is from Kenneth H. Rosen, Discrete Mathematics and Its Applications,
     * 2nd edition (NY: McGraw-Hill, 1991), p. 286.
     */
    private void generateNextCombinationIndices()
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
    }
}
