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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.uncommons.maths.random.DiscreteUniformGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.SwingEvolutionObserver;
import org.uncommons.watchmaker.swing.evolutionmonitor.StatusBar;

/**
 * An evolutionary Sudoku solver.
 * @author Daniel Dyer
 */
public class SudokuApplet extends AbstractExampleApplet
{
    private static final String[] BLANK_PUZZLE = {".........",
                                                  ".........",
                                                  ".........",
                                                  ".........",
                                                  ".........",
                                                  ".........",
                                                  ".........",
                                                  ".........",
                                                  "........."};

    private static final String[] EASY_PUZZLE = {"4.5...9.7",
                                                 ".2..9..6.",
                                                 "39.6.7.28",
                                                 "9..3.2..6",
                                                 "7..9.6..3",
                                                 "5..4.8..1",
                                                 "28.1.5.49",
                                                 ".7..3..8.",
                                                 "6.4...3.2"};

    private static final String[] MEDIUM_PUZZLE = {"....3....",
                                                   ".....6293",
                                                   ".2.9.48..",
                                                   ".754...38",
                                                   "..46.71..",
                                                   "91...547.",
                                                   "..38.9.1.",
                                                   "1567.....",
                                                   "....1...."};

    private static final String[] HARD_PUZZLE = {"...891...",
                                                 "....5.8..",
                                                 ".....6.2.",
                                                 "5....4..8",
                                                 "49....67.",
                                                 "8.13....5",
                                                 ".6..8..9.",
                                                 "..5.4.2.7",
                                                 "...1.3.8."};

    private static final String[][] PUZZLES = {EASY_PUZZLE,
                                               MEDIUM_PUZZLE,
                                               HARD_PUZZLE,
                                               BLANK_PUZZLE};

    private SelectionStrategy<Object> selectionStrategy;

    private SudokuView sudokuView;
    private JButton solveButton;
    private JComboBox puzzleCombo;
    private JSpinner populationSizeSpinner;
    private AbortControl abortControl;
    private StatusBar statusBar;


    /**
     * Initialise and layout the GUI.
     * @param container The Swing component that will contain the GUI controls.
     */
    @Override
    protected void prepareGUI(Container container)
    {
        sudokuView = new SudokuView();
        container.add(createControls(), BorderLayout.NORTH);
        container.add(sudokuView, BorderLayout.CENTER);
        statusBar = new StatusBar();
        container.add(statusBar, BorderLayout.SOUTH);
        sudokuView.setPuzzle(EASY_PUZZLE);
    }


    private JComponent createControls()
    {
        JPanel controls = new JPanel(new BorderLayout());
        JPanel innerPanel = new JPanel(new SpringLayout());
        innerPanel.add(new JLabel("Puzzle: "));
        puzzleCombo = new JComboBox(new String[]{"Easy Demo (38 givens)",
                                                 "Medium Demo (32 givens)",
                                                 "Hard Demo (28 givens)",
                                                 "Custom"});
        innerPanel.add(puzzleCombo);
        puzzleCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                sudokuView.setPuzzle(PUZZLES[puzzleCombo.getSelectedIndex()]);
            }
        });
        innerPanel.add(new JLabel("Selection Pressure: "));
        ProbabilityParameterControl selectionPressure = new ProbabilityParameterControl(Probability.EVENS,
                                                                                        Probability.ONE,
                                                                                        2,
                                                                                        new Probability(0.85d));

        selectionStrategy = new TournamentSelection(selectionPressure.getNumberGenerator());
        innerPanel.add(selectionPressure.getControl());
        innerPanel.add(new JLabel("Population Size: "));
        populationSizeSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 50000, 1));
        innerPanel.add(populationSizeSpinner);
        SpringUtilities.makeCompactGrid(innerPanel, 3, 2, 0, 6, 6, 6);
        innerPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
        controls.add(innerPanel, BorderLayout.CENTER);
        controls.add(createButtonPanel(), BorderLayout.SOUTH);
        return controls;
    }


    private JComponent createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                int populationSize = (Integer) populationSizeSpinner.getValue();
                puzzleCombo.setEnabled(false);
                populationSizeSpinner.setEnabled(false);
                solveButton.setEnabled(false);
                abortControl.reset();
                createTask(sudokuView.getPuzzle(),
                           populationSize,
                           (int) Math.round(populationSize * 0.05)).execute(); // Elite count is 5%.
            }
        });

        buttonPanel.add(solveButton);
        abortControl = new AbortControl();
        buttonPanel.add(abortControl.getControl());
        abortControl.getControl().setEnabled(false);
        return buttonPanel;
    }


    /**
     * Helper method to create a background task for running the interactive evolutionary
     * algorithm.
     * @return A Swing task that will execute on a background thread and update
     * the GUI when it is done.
     */
    private SwingBackgroundTask<Sudoku> createTask(final String[] puzzle,
                                                   final int populationSize,
                                                   final int eliteCount)
    {
        return new SwingBackgroundTask<Sudoku>()
        {
            @Override
            protected Sudoku performTask()
            {
                Random rng = new MersenneTwisterRNG();
                List<EvolutionaryOperator<Sudoku>> operators = new ArrayList<EvolutionaryOperator<Sudoku>>(2);
                // Cross-over rows between parents (so offspring is x rows from parent1 and
                // y rows from parent2).
                operators.add(new SudokuVerticalCrossover());
                // Mutate the order of cells within individual rows.
                operators.add(new SudokuRowMutation(new PoissonGenerator(2, rng),
                                                    new DiscreteUniformGenerator(1, 8, rng)));

                EvolutionaryOperator<Sudoku> pipeline = new EvolutionPipeline<Sudoku>(operators);

                EvolutionEngine<Sudoku> engine = new GenerationalEvolutionEngine<Sudoku>(new SudokuFactory(puzzle),
                                                                                         pipeline,
                                                                                         new SudokuEvaluator(),
                                                                                         selectionStrategy,
                                                                                         rng);
                engine.addEvolutionObserver(new SwingEvolutionObserver<Sudoku>(new GridViewUpdater(),
                                                                               100,
                                                                               TimeUnit.MILLISECONDS));
                engine.addEvolutionObserver(statusBar);
                return engine.evolve(populationSize,
                                     eliteCount,
                                     new TargetFitness(0, false), // Continue until a perfect solution is found...
                                     abortControl.getTerminationCondition()); // ...or the user aborts.
            }


            @Override
            protected void postProcessing(Sudoku result)
            {
                puzzleCombo.setEnabled(true);
                populationSizeSpinner.setEnabled(true);
                solveButton.setEnabled(true);
                abortControl.getControl().setEnabled(false);
            }
        };
    }



    /**
     * Evolution observer for displaying information at the end of
     * each generation.
     */
    private class GridViewUpdater implements EvolutionObserver<Sudoku>
    {
        public void populationUpdate(final PopulationData<? extends Sudoku> data)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    sudokuView.setSolution(data.getBestCandidate());
                }
            });
        }
    }


    /**
     * Entry point for running this example as an application rather than an applet.
     * @param args Program arguments (ignored).
     */
    public static void main(String[] args)
    {
        new SudokuApplet().displayInFrame("Watchmaker Framework - Sudoku Example");
    }

}
