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

import org.testng.annotations.Test;

/**
 * Unit test for the {@link Sudoku} data structure.
 * @author Daniel Dyer
 */
public class SudokuTest
{
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongNumberOfRows()
    {
        // This grid has one row and nine columns.  Constructing it should throw
        // an IllegalArgumentException.
        new Sudoku(new Sudoku.Cell[][]{{new Sudoku.Cell(1, false),
                                        new Sudoku.Cell(2, false),
                                        new Sudoku.Cell(3, false),
                                        new Sudoku.Cell(4, false),
                                        new Sudoku.Cell(5, false),
                                        new Sudoku.Cell(6, false),
                                        new Sudoku.Cell(7, false),
                                        new Sudoku.Cell(8, false),
                                        new Sudoku.Cell(9, false)}});
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongNumberOfColumns()
    {
        // This grid has nine rows and one column.  Constructing it should throw
        // an IllegalArgumentException.
        new Sudoku(new Sudoku.Cell[][]{{new Sudoku.Cell(1, false)},
                                       {new Sudoku.Cell(2, false)},
                                       {new Sudoku.Cell(3, false)},
                                       {new Sudoku.Cell(4, false)},
                                       {new Sudoku.Cell(5, false)},
                                       {new Sudoku.Cell(6, false)},
                                       {new Sudoku.Cell(7, false)},
                                       {new Sudoku.Cell(8, false)},
                                       {new Sudoku.Cell(9, false)}});
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCellValueTooLow()
    {
        new Sudoku.Cell(0, false);
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCellValueTooHigh()
    {
        new Sudoku.Cell(10, false);
    }


    @Test
    public void testToString()
    {
        Sudoku sudoku = SudokuTestUtils.createSudoku(new int[][]{{1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                                                                 {1, 2, 3, 4, 5, 6, 7, 8, 9}});
        String text = sudoku.toString();
        assert text.equals(" 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n" +
                           " 1 2 3 4 5 6 7 8 9\n") : "Wrong string representation:\n" + text;
    }
}
