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
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Unit test for sudoku fitness evaluator.
 * @author Daniel Dyer
 */
public class SudokuEvaluatorTest
{
    /**
     * Ensure that the evaluator returns zero for a correct solution.
     */
    @Test
    public void testCorrectSolution()
    {
        Sudoku sudoku = SudokuTestUtils.createSudoku(new int[][]
        {
            {1, 2, 8, 5, 4, 3, 9, 6, 7},
            {7, 6, 4, 9, 2, 8, 5, 1, 3},
            {3, 9, 5, 7, 6, 1, 2, 4, 8},
            {6, 1, 9, 4, 8, 5, 7, 3, 2},
            {5, 8, 3, 6, 7, 2, 1, 9, 4},
            {4, 7, 2, 3, 1, 9, 8, 5, 6},
            {8, 5, 1, 2, 3, 6, 4, 7, 9},
            {9, 4, 6, 8, 5, 7, 3, 2, 1},
            {2, 3, 7, 1, 9, 4, 6, 8, 5}
        });
        FitnessEvaluator<Sudoku> evaluator = new SudokuEvaluator();
        int fitness = (int) evaluator.getFitness(sudoku, null);
        assert fitness == 0 : "Fitness should be zero for correct solution, is " + fitness; 
    }


    /**
     * Ensure that the evaluator returns zero for a correct solution.
     */
    @Test
    public void testDuplicates()
    {
        // This sudoku as 4 invalid columns (0, 1, 3 and 5) and 2 invalid
        // sub-grids (bottom-left and bottom-center).
        Sudoku sudoku = SudokuTestUtils.createSudoku(new int[][]
        {
            {2, 1, 8, 5, 4, 3, 9, 6, 7},
            {7, 6, 4, 9, 2, 8, 5, 1, 3},
            {3, 9, 5, 7, 6, 1, 2, 4, 8},
            {6, 1, 9, 4, 8, 5, 7, 3, 2},
            {5, 8, 3, 6, 7, 2, 1, 9, 4},
            {4, 7, 2, 3, 1, 9, 8, 5, 6},
            {8, 5, 2, 1, 3, 6, 4, 7, 9},
            {9, 4, 6, 8, 5, 7, 3, 2, 1},
            {2, 3, 7, 1, 9, 4, 6, 8, 5}
        });
        FitnessEvaluator<Sudoku> evaluator = new SudokuEvaluator();
        int fitness = (int) evaluator.getFitness(sudoku, null);
        assert fitness == 6 : "Fitness should be 6, is " + fitness;
    }


}
