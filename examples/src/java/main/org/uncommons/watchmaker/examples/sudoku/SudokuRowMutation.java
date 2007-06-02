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
package org.uncommons.watchmaker.examples.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.ConstantGenerator;
import org.uncommons.maths.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutates rows in a potential Sudoku solution by manipulating the order
 * of non-fixed cells in much the same way as the
 * {@link org.uncommons.watchmaker.framework.operators.ListOrderMutation}
 * operator does in the Travelling Salesman example.
 * @author Daniel Dyer
 */
public class SudokuRowMutation implements EvolutionaryOperator<Sudoku>
{
    private final NumberGenerator<Integer> mutationCountVariable;
    private final NumberGenerator<Integer> mutationAmountVariable;

    /**
     * Default is one mutation per candidate.
     */
    public SudokuRowMutation()
    {
        this(1, 1);
    }

    /**
     * @param mutationCount The constant number of mutations
     * to apply to each row in a Sudoku solution.
     * @param mutationAmount The constant number of positions by
     * which a list element will be displaced as a result of mutation.
     */
    public SudokuRowMutation(int mutationCount, int mutationAmount)
    {
        this.mutationCountVariable =  new ConstantGenerator<Integer>(mutationCount);
        this.mutationAmountVariable = new ConstantGenerator<Integer>(mutationAmount);
    }


    /**
     * Typically the mutation count will be from a Poisson distribution.
     * The mutation amount can be from any discrete probability distribution
     * and can include negative values.
     * @param mutationCount A random variable that provides a number
     * of mutations that will be applied to each row in an individual.
     * @param mutationAmount A random variable that provides a number
     * of positions by which to displace an element when mutating.
     */
    public SudokuRowMutation(NumberGenerator<Integer> mutationCount,
                             NumberGenerator<Integer> mutationAmount)
    {
        this.mutationCountVariable = mutationCount;
        this.mutationAmountVariable = mutationAmount;
    }


    @SuppressWarnings({"unchecked"})
    public <S extends Sudoku> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> mutatedCandidates = new ArrayList<S>(selectedCandidates.size());
        for (Sudoku sudoku : selectedCandidates)
        {
            mutatedCandidates.add((S) mutate(sudoku, rng));
        }
        return mutatedCandidates;
    }

    private Sudoku mutate(Sudoku sudoku, Random rng)
    {
        Sudoku.Cell[][] newRows = new Sudoku.Cell[9][];
        // Mutate each row in turn.
        for (int i = 0; i < 9; i++)
        {
            newRows[i] = new Sudoku.Cell[9];
            System.arraycopy(sudoku.getRow(i), 0, newRows[i], 0, 9);
        }

        int mutationCount = Math.abs(mutationCountVariable.nextValue());
        while (mutationCount > 0)
        {
            int row = rng.nextInt(9);
            int fromIndex = rng.nextInt(9);
            int mutationAmount = mutationAmountVariable.nextValue();
            int toIndex = (fromIndex + mutationAmount) % 9;
            if (toIndex < 0)
            {
                toIndex += 9;
            }

            // Make sure we're not trying to mutate a 'given'.
            if (!newRows[row][fromIndex].isFixed() && !newRows[row][toIndex].isFixed())
            {
                // Swap the randomly selected element with the one that is the
                // specified displacement distance away.
                Sudoku.Cell temp = newRows[row][fromIndex];
                newRows[row][fromIndex] = newRows[row][toIndex];
                newRows[row][toIndex] = temp;
                --mutationCount;
            }
        }

        return new Sudoku(newRows);
    }
}
