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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Unit test for sudoku candidate solution factory.
 * @author Daniel Dyer
 */
public class SudokuFactoryTest
{
    /**
     * Checks to make sure that the givens are correctly placed and that each row
     * contains each value exactly once.
     */
    @Test
    public void testValidity()
    {
        CandidateFactory<Sudoku> factory = new SudokuFactory(".9.......",
                                                             ".........",
                                                             "........5",
                                                             "....2....",
                                                             ".........",
                                                             ".........",
                                                             ".........",
                                                             "...1.....",
                                                             "........9");

        List<Sudoku> population = factory.generateInitialPopulation(20, ExamplesTestUtils.getRNG());
        for (Sudoku sudoku : population)
        {
            // Check givens are correctly placed.
            assert sudoku.isFixed(2, 8) : "Cell (2, 8) should be fixed.";
            assert sudoku.getValue(2, 8) == 5 : "Cell (2, 8) should contain 5.";
            assert sudoku.isFixed(7, 3) : "Cell (7, 3) should be fixed.";
            assert sudoku.getValue(7, 3) == 1 : "Cell (7, 3) should contain 1.";
            assert sudoku.isFixed(3, 4) : "Cell (3, 4) should be fixed.";
            assert sudoku.getValue(3, 4) == 2 : "Cell (3, 4) should contain 2.";
            assert sudoku.isFixed(0, 1) : "Cell (0, 1) should be fixed.";
            assert sudoku.getValue(0, 1) == 9 : "Cell (0, 1) should contain 9.";
            assert sudoku.isFixed(8, 8) : "Cell (8, 8) should be fixed.";
            assert sudoku.getValue(8, 8) == 9 : "Cell (8, 8) should contain 9.";

            // Check that each row has no duplicates.
            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < 9; i++)
            {
                Sudoku.Cell[] row = sudoku.getRow(i);
                for (Sudoku.Cell cell : row)
                {
                    set.add(cell.getValue());
                }
                if (set.size() < 9)
                {
                    System.out.println(sudoku);
                    assert false : "Row " + i + " contains duplicates.";
                }
            }
        }
    }


    /**
     * If the pattern used to create a Sudoku factory contains any characters
     * other than the values 1 - 9 or dots (which represent empty cells), then
     * an appropriate exception should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidPatternChars()
    {
        // This pattern is the right size but contains an invalid character.
        String[] pattern = {"....9....",
                            "2..3.....",
                            "........1",
                            "....a....", // Invalid character on this line.
                            "....4....",
                            ".........",
                            ".........",
                            ".........",
                            "........."};
        new SudokuFactory(pattern);
    }


    /**
     * If the pattern used to create a Sudoku factory contains the wrong number
     * of rows then an appropriate exception should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongNumberOfRows()
    {
        // This pattern contains only valid characters and has the right number
        // of columns but doesn't have enough rows.
        String[] pattern = {"....9....",
                            "2..3.....",
                            "........1",
                            ".........",
                            "........."};
        new SudokuFactory(pattern);
    }


    /**
     * If the pattern used to create a Sudoku factory contains the wrong number
     * of columns then an appropriate exception should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongNumberOfColumns()
    {
        // This pattern contains only valid characters and has the right number
        // of rows but has too many columns in some
        String[] pattern = {"....9....",
                            "2..3.....",
                            "........1",
                            ".........",
                            ".........7",
                            ".........",
                            ".4.......6",
                            "..1.3....",
                            "........8"};
        new SudokuFactory(pattern);
    }
}
