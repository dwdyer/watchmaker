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

/**
 * Utility methods that help in unit tests.
 * @author Daniel Dyer
 */
class SudokuTestUtils
{
    private SudokuTestUtils()
    {
        // Prevents instantiation.
    }


    /**
     * Convenience method for creating a Sudoku object from a 2D array of ints.
     * Ignores which cells are fixed and which are not.
     * @param values A 9x9 2-dimensional array that contains values (1-9) for
     * each of the cells in a Sudoku grid.
     */
    public static Sudoku createSudoku(int[][] values)
    {
        Sudoku.Cell[][] cells = new Sudoku.Cell[Sudoku.SIZE][Sudoku.SIZE];
        for (int i = 0; i < Sudoku.SIZE; i++)
        {
            for (int j = 0; j < Sudoku.SIZE; j++)
            {
                cells[i][j] = new Sudoku.Cell(values[i][j], false);
            }
        }
        return new Sudoku(cells);
    }
}
