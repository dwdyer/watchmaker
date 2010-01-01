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

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for Sudoku cross-over operator.
 * @author Daniel Dyer
 */
public class SudokuVerticalCrossoverTest
{
    /**
     * Ensures that the simplest configuration (a single cross-over point)
     * works as expected.
     */
    @Test
    public void testSinglePointCrossover()
    {
        EvolutionaryOperator<Sudoku> crossover = new SudokuVerticalCrossover();
        int[] forwards = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] backwards = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[][] parent1 = new int[Sudoku.SIZE][];
        int[][] parent2 = new int[Sudoku.SIZE][];
        for (int i = 0; i < Sudoku.SIZE; i++)
        {
            parent1[i] = forwards;
            parent2[i] = backwards;
        }
        List<Sudoku> population = Arrays.asList(SudokuTestUtils.createSudoku(parent1),
                                                SudokuTestUtils.createSudoku(parent2));
        population = crossover.apply(population, ExamplesTestUtils.getRNG());
        assert population.size() == 2 : "Population size changed after cross-over.";
        Sudoku offspring1 = population.get(0);
        Sudoku offspring2 = population.get(1);
        // Two groups of rows should be different from each other (they come from different parents).
        assert !Arrays.equals(offspring1.getRow(0), offspring1.getRow(8))
            : "Row 0 should not match row 8.";
        assert !Arrays.equals(offspring2.getRow(0), offspring2.getRow(8))
            : "Row 0 should not match row 8.";
        // The two offspring should have inherited different rows.
        assert !Arrays.equals(offspring1.getRow(0), offspring2.getRow(0))
            : "Offspring 1 row 0 should not match offspring 2 row 0.";
        assert !Arrays.equals(offspring1.getRow(8), offspring2.getRow(8))
            : "Offspring 1 row 8 should not match offspring 2 row 8.";
    }
}
