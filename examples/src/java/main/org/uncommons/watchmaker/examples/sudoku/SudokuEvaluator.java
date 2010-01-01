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
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * {@link org.uncommons.watchmaker.framework.FitnessEvaluator} for potential Sudoku
 * solutions.  Counts the number of duplicate values in rows, columns and sub-grids.
 * The fitness score is the total number of duplicate values.  Therefore, a fitness
 * score of zero indicates a perfect solution. 
 * @author Daniel Dyer
 */
public class SudokuEvaluator implements FitnessEvaluator<Sudoku>
{
    /**
     * The fitness score for a potential Sudoku solution is the number of
     * cells that conflict with other cells in the grid (i.e. if there are
     * two 7s in the same column, both of these cells are conflicting).  A
     * lower score indicates a fitter individual.
     * @param candidate The Sudoku grid to evaluate.
     * @param population {@inheritDoc}
     * @return The fitness score for the specified individual.
     */
    public double getFitness(Sudoku candidate,
                             List<? extends Sudoku> population)
    {
        // We can assume that there are no duplicates in any rows because
        // the candidate factory and evolutionary operators that we use do
        // not permit rows to contain duplicates.

        int fitness = 0;

        // Check columns for duplicates.
        Set<Integer> values = new HashSet<Integer>(Sudoku.SIZE * 2); // Big enough to avoid re-hashing.
        for (int column = 0; column < Sudoku.SIZE; column++)
        {
            for (int row = 0; row < Sudoku.SIZE; row++)
            {
                values.add(candidate.getValue(row, column));
            }
            fitness += (Sudoku.SIZE - values.size());
            values.clear();
        }

        // Check sub-grids for duplicates.
        for (int band = 0; band < Sudoku.SIZE; band += 3)
        {
            for (int stack = 0; stack < Sudoku.SIZE; stack += 3)
            {
                for (int row = band; row < band + 3; row++)
                {
                    for (int column = stack; column < stack + 3; column++)
                    {
                        values.add(candidate.getValue(row, column));
                    }
                }
                fitness += (Sudoku.SIZE - values.size());
                values.clear();
            }
        }
        return fitness;
    }


    /**
     * @return false
     */
    public boolean isNatural()
    {
        return false;
    }
}
