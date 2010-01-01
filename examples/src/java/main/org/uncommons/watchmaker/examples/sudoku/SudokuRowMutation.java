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
package org.uncommons.watchmaker.examples.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
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

    // These look-up tables keep track of which values are fixed in which columns
    // and sub-grids.  Because the values are fixed, they are the same for all
    // potential solutions, so we cache the information here to minimise the amount
    // of processing that needs to be done for each mutation.  There is no need to
    // worry about rows since the mutation ensures that rows are always valid.
    private final boolean[][] columnFixedValues = new boolean[Sudoku.SIZE][Sudoku.SIZE];
    private final boolean[][] subGridFixedValues = new boolean[Sudoku.SIZE][Sudoku.SIZE];
    private boolean cached = false;

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
        this(new ConstantGenerator<Integer>(mutationCount),
             new ConstantGenerator<Integer>(mutationAmount));
        if (mutationCount < 1)
        {
            throw new IllegalArgumentException("Mutation count must be at least 1.");
        }
        else if (mutationAmount < 1)
        {
            throw new IllegalArgumentException("Mutation amount must be at least 1.");
        }
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


    public List<Sudoku> apply(List<Sudoku> selectedCandidates, Random rng)
    {
        if (!cached)
        {
            buildCache(selectedCandidates.get(0));
        }

        List<Sudoku> mutatedCandidates = new ArrayList<Sudoku>(selectedCandidates.size());
        for (Sudoku sudoku : selectedCandidates)
        {
            mutatedCandidates.add(mutate(sudoku, rng));
        }
        return mutatedCandidates;
    }

    
    private Sudoku mutate(Sudoku sudoku, Random rng)
    {
        Sudoku.Cell[][] newRows = new Sudoku.Cell[Sudoku.SIZE][];
        // Mutate each row in turn.
        for (int i = 0; i < Sudoku.SIZE; i++)
        {
            newRows[i] = new Sudoku.Cell[Sudoku.SIZE];
            System.arraycopy(sudoku.getRow(i), 0, newRows[i], 0, Sudoku.SIZE);
        }

        int mutationCount = Math.abs(mutationCountVariable.nextValue());
        while (mutationCount > 0)
        {
            int row = rng.nextInt(Sudoku.SIZE);
            int fromIndex = rng.nextInt(Sudoku.SIZE);
            int mutationAmount = mutationAmountVariable.nextValue();
            int toIndex = (fromIndex + mutationAmount) % Sudoku.SIZE;

            // Make sure we're not trying to mutate a 'given'.
            if (!newRows[row][fromIndex].isFixed()
                && !newRows[row][toIndex].isFixed()
                // ...or trying to introduce a duplicate of a given value.
                && (!isIntroducingFixedConflict(sudoku, row, fromIndex, toIndex)
                    || isRemovingFixedConflict(sudoku, row, fromIndex, toIndex)))
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


    private void buildCache(Sudoku sudoku)
    {
        for (int row = 0; row < Sudoku.SIZE; row++)
        {
            for (int column = 0; column < Sudoku.SIZE; column++)
            {
                if (sudoku.isFixed(row, column))
                {
                    columnFixedValues[column][sudoku.getValue(row, column) - 1] = true;
                    subGridFixedValues[convertToSubGrid(row, column)][sudoku.getValue(row, column) - 1] = true;
                }
            }
        }
        cached = true;
    }


    /**
     * Checks whether the proposed mutation would introduce a duplicate of a fixed value
     * into a column or sub-grid.
     */
    private boolean isIntroducingFixedConflict(Sudoku sudoku,
                                               int row,
                                               int fromIndex,
                                               int toIndex)
    {
        return columnFixedValues[fromIndex][sudoku.getValue(row, toIndex) - 1]
               || columnFixedValues[toIndex][sudoku.getValue(row, fromIndex) - 1]
               || subGridFixedValues[convertToSubGrid(row, fromIndex)][sudoku.getValue(row, toIndex) - 1]
               || subGridFixedValues[convertToSubGrid(row, toIndex)][sudoku.getValue(row, fromIndex) - 1];
    }


    /**
     * Checks whether the proposed mutation would remove a duplicate of a fixed value
     * from a column or sub-grid.
     */
    private boolean isRemovingFixedConflict(Sudoku sudoku,
                                            int row,
                                            int fromIndex,
                                            int toIndex)
    {
        return columnFixedValues[fromIndex][sudoku.getValue(row, fromIndex) - 1]
               || columnFixedValues[toIndex][sudoku.getValue(row, toIndex) - 1]
               || subGridFixedValues[convertToSubGrid(row, fromIndex)][sudoku.getValue(row, fromIndex) - 1]
               || subGridFixedValues[convertToSubGrid(row, toIndex)][sudoku.getValue(row, toIndex) - 1];
    }



    /**
     * Returns the index of the sub-grid that the specified cells belongs to.
     * @return A number between 0 (top left) and 8 (bottom right).
     */
    private int convertToSubGrid(int row, int column)
    {
        int band = row / 3;
        int stack = column / 3;
        return band * 3 + stack;
    }
}
