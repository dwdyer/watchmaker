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
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.DiscreteUniformGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

/**
 * An evolutionary Sudoku solver.
 * @author Daniel Dyer
 */
public class SudokuExample
{
    public static void main(String[] args)
    {
        Random rng = new MersenneTwisterRNG();
        List<EvolutionaryOperator<? super Sudoku>> operators
            = new ArrayList<EvolutionaryOperator<? super Sudoku>>(2);
        // Cross-over rows between parents (so offspring is x rows from parent1 and
        // y rows from parent2).
        operators.add(new SudokuVerticalCrossover());
        // Mutate the order of cells within individual rows.
        operators.add(new SudokuRowMutation(new PoissonGenerator(2, rng),
                                            new DiscreteUniformGenerator(1, 8, rng)));

        EvolutionaryOperator<Sudoku> pipeline = new EvolutionPipeline<Sudoku>(operators);

        // An 'easy' puzzle for the program to solve.
        String[] pattern = new String[]{"4.5...9.7",
                                        ".2..9..6.",
                                        "39.6.7.28",
                                        "9..3.2..6",
                                        "7..9.6..3",
                                        "5..4.8..1",
                                        "28.1.5.49",
                                        ".7..3..8.",
                                        "6.4...3.2"};

        EvolutionEngine<Sudoku> engine = new StandaloneEvolutionEngine<Sudoku>(new SudokuFactory(pattern),
                                                                               pipeline,
                                                                               new SudokuEvaluator(),
                                                                               new TournamentSelection(0.75d),
                                                                               rng);
        engine.addEvolutionObserver(new EvolutionLogger());
        Sudoku solution = engine.evolve(100, // 100 individuals in each generation.
                                        5, // Some elitism.
                                        new TargetFitness(0, false)); // Continue until a perfect solution is found.
        System.out.println("Solution found:\n" + solution);
    }


    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private static class EvolutionLogger implements EvolutionObserver<Sudoku>
    {
        public void populationUpdate(PopulationData<Sudoku> data)
        {
            // Print out the best candidate so far after every 100 generations.
            if (data.getGenerationNumber() % 100 == 0)
            {
                System.out.println("Generation " + data.getGenerationNumber()
                                   + ", fittest candidate: " + data.getBestCandidateFitness());
                System.out.print(data.getBestCandidate());
            }
        }
    }
}
