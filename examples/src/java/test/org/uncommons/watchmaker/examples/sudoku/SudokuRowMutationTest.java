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

import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * Unit test for Sudoku mutation operator.
 * @author Daniel Dyer
 */
public class SudokuRowMutationTest
{
    /**
     * Tests to ensure that rows are still valid after mutation.  Each row
     * should contain each value 1-9 exactly once.
     */
    @Test
    public void testValidity()
    {
        Random rng = new MersenneTwisterRNG();
        EvolutionaryOperator<Sudoku> mutation = new SudokuRowMutation(8, 1);
        List<Sudoku> population = Arrays.asList(SudokuTestUtils.createSudoku(new int[][]
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
        }));
        final Set<Integer> counts = new HashSet<Integer>(Sudoku.SIZE);
        for (int i = 0; i < 20; i++)
        {
            population = mutation.apply(population, rng);
            assert population.size() == 1 : "Population size changed after mutation(s).";
            Sudoku mutatedSudoku = population.get(0);
            for (int j = 0; j < Sudoku.SIZE; j++)
            {
                Sudoku.Cell[] row = mutatedSudoku.getRow(j);
                assert row.length == Sudoku.SIZE : "Row length is invalid: " + row.length;
                for (Sudoku.Cell cell : row)
                {
                    int value = cell.getValue();
                    assert value > 0 && value <= Sudoku.SIZE : "Cell value out of range: " + value;
                    counts.add(value);
                }
                assert counts.size() == Sudoku.SIZE : "Row contains duplicates.";
                counts.clear();
            }
        }
    }
}
