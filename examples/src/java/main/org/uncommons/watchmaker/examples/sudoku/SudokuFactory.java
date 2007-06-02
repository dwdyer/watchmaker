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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    private static final List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final Set<Given> givens;

    public SudokuFactory(Set<Given> givens)
    {
        this.givens = givens;
    }


    protected Sudoku generateRandomCandidate(Random rng)
    {
        List<List<Integer>> nonFixedValues = new ArrayList<List<Integer>>(9);
        for (int i = 0; i < 9; i++)
        {
            nonFixedValues.add(new ArrayList<Integer>(values));
        }

        Sudoku.Cell[][] rows = new Sudoku.Cell[9][9];
        for (Given given : givens)
        {
            rows[given.getRow()][given.getColumn()] = new Sudoku.Cell(given.getValue(), true);
            List<Integer> rowValues = nonFixedValues.get(given.getRow());
            int index = Collections.binarySearch(rowValues, given.getValue());
            rowValues.remove(index);
        }

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


    /**
     * Encapsulates data about a single "given" cell.  A given is one of the
     * numbers provided at the start from which the solution is derived.
     */
    public static final class Given
    {
        private final int row;
        private final int column;
        private final int value;

        public Given(int row, int column, int value)
        {
            if (row < 0 || row > 8)
            {
                throw new IllegalArgumentException("Row index must be between 0 and 8.");
            }
            else if (column < 0 || column > 8)
            {
                throw new IllegalArgumentException("Column index must be between 0 and 8.");
            }
            else if (value < 1 || value > 9)
            {
                throw new IllegalArgumentException("Value must be between 1 and 9.");
            }
            this.row = row;
            this.column = column;
            this.value = value;
        }

        public int getRow()
        {
            return row;
        }

        public int getColumn()
        {
            return column;
        }

        public int getValue()
        {
            return value;
        }


        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            Given given = (Given) o;
            return column == given.column && row == given.row && value == given.value;
        }


        @Override
        public int hashCode()
        {
            int result = row;
            result = 31 * result + column;
            result = 31 * result + value;
            return result;
        }
    }
}
