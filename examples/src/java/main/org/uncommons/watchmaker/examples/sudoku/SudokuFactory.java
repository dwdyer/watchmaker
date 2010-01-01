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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Factory that generates potential Sudoku solutions from a list of "givens".
 * The rows of the generated solutions will all be valid (i.e. no duplicate values)
 * but there are no constraints on the columns or sub-grids (these will be refined
 * by the evolutionary algorithm).
 * @author Daniel Dyer
 */
public class SudokuFactory extends AbstractCandidateFactory<Sudoku>
{
    private static final List<Integer> VALUES = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final Sudoku.Cell[][] template;
    private final List<List<Integer>> nonFixedValues = new ArrayList<List<Integer>>(Sudoku.SIZE);


    /**
     * Creates a factory for generating random candidate solutions
     * for a specified Sudoku puzzle.
     * @param pattern An array of Strings.  Each element represents
     * one row in the puzzle.  Each character represents a single
     * cell.  Permitted characters are the digits '1' to '9' (each
     * of which represents a fixed cell in the pattern) or the '.'
     * character, which represents an empty cell.
     * @throws IllegalArgumentException If {@literal pattern} does not
     * consist of nine Strings with nine characters ('0' to '9', or '.')
     * in each.
     */
    public SudokuFactory(String... pattern)
    {
        if (pattern.length != Sudoku.SIZE)
        {
            throw new IllegalArgumentException("Sudoku layout must have " + Sudoku.SIZE + " rows.");
        }

        this.template = new Sudoku.Cell[Sudoku.SIZE][Sudoku.SIZE];

        // Keep track of which values in each row are not 'givens'.
        for (int i = 0; i < Sudoku.SIZE; i++)
        {
            nonFixedValues.add(new ArrayList<Integer>(VALUES));
        }

        for (int i = 0; i < pattern.length; i++)
        {
            char[] rowPattern = pattern[i].toCharArray();
            if (rowPattern.length != Sudoku.SIZE)
            {
                throw new IllegalArgumentException("Sudoku layout must have " + Sudoku.SIZE + " cells in each row.");
            }
            for (int j = 0; j < rowPattern.length; j++)
            {
                char c = rowPattern[j];
                if (c >= '1' && c <= '9') // Cell is a 'given'.
                {
                    int value = c - '0'; // Convert char to in that it represents..
                    template[i][j] = new Sudoku.Cell(value, true);
                    List<Integer> rowValues = nonFixedValues.get(i);
                    int index = Collections.binarySearch(rowValues, value);
                    rowValues.remove(index);
                }
                else if (c != '.')
                {
                    throw new IllegalArgumentException("Unexpected character at (" + i + ", " + j + "): " + c);
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     * The generated potential solution is guaranteed to have no
     * duplicates in any row but could have duplicates in a column or sub-grid.
     */
    public Sudoku generateRandomCandidate(Random rng)
    {
        // Clone the template as the basis for this grid.
        Sudoku.Cell[][] rows = template.clone();
        for (int i = 0; i < rows.length; i++)
        {
            rows[i] = template[i].clone();
        }

        // Fill-in the non-fixed cells.
        for (int i = 0; i < rows.length; i++)
        {
            List<Integer> rowValues = nonFixedValues.get(i);
            Collections.shuffle(rowValues);
            int index = 0;
            for (int j = 0; j < rows[i].length; j++)
            {
                if (rows[i][j] == null)
                {
                    rows[i][j] = new Sudoku.Cell(rowValues.get(index), false);
                    ++index;
                }
            }
        }
        return new Sudoku(rows);
    }
}
