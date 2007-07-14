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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.uncommons.gui.SpringUtilities;
import org.uncommons.gui.SwingBackgroundTask;
import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.DiscreteUniformGenerator;
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.framework.termination.UserAbort;

/**
 * An evolutionary Sudoku solver.
 * @author Daniel Dyer
 */
public class SudokuApplet extends JApplet
{
    private static final String[] EASY_PUZZLE = new String[]{"4.5...9.7",
                                                             ".2..9..6.",
                                                             "39.6.7.28",
                                                             "9..3.2..6",
                                                             "7..9.6..3",
                                                             "5..4.8..1",
                                                             "28.1.5.49",
                                                             ".7..3..8.",
                                                             "6.4...3.2"};

    private static final String[] MEDIUM_PUZZLE = new String[]{"....3....",
                                                               ".....6293",
                                                               ".2.9.48..",
                                                               ".754...38",
                                                               "..46.71..",
                                                               "91...547.",
                                                               "..38.9.1.",
                                                               "1567.....",
                                                               "....1...."};

    private static final String[] HARD_PUZZLE = new String[]{"...891...",
                                                             "....5.8..",
                                                             ".....6.2.",
                                                             "5....4..8",
                                                             "49....67.",
                                                             "8.13....5",
                                                             ".6..8..9.",
                                                             "..5.4.2.7",
                                                             "...1.3.8."};

    private static final String[][] PUZZLES = new String[][]{EASY_PUZZLE,
                                                             MEDIUM_PUZZLE,
                                                             HARD_PUZZLE};

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#.###s");
    private final SudokuTableModel sudokuTableModel = new SudokuTableModel();
    private final JButton solveButton = new JButton("Solve");
    private final JButton abortButton = new JButton("Abort");
    private final JLabel generationsLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JComboBox puzzleCombo = new JComboBox(new String[]{"Easy Puzzle (38 givens)",
                                                                     "Medium Puzzle (32 givens)",
                                                                     "Hard Puzzle (28 givens)"});
    private final JSpinner populationSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 1));
    private final JSlider selectionPressureSlider = new JSlider(51, 99, 85);
    private UserAbort abortCondtion;

    public SudokuApplet()
    {
        Box mainArea = new Box(BoxLayout.X_AXIS);
        mainArea.add(createControls());
        mainArea.add(createSudokuView());
        add(mainArea, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }


    private JComponent createControls()
    {
        JPanel controls = new JPanel(new BorderLayout());
        JPanel innerPanel = new JPanel(new SpringLayout());
        innerPanel.add(new JLabel("Puzzle: "));
        innerPanel.add(puzzleCombo);
        innerPanel.add(new JLabel("Selection Pressure: "));
        innerPanel.add(selectionPressureSlider);
        innerPanel.add(new JLabel("Population Size: "));
        innerPanel.add(populationSizeSpinner);
        SpringUtilities.makeCompactGrid(innerPanel, 3, 2, 0, 6, 6, 6);
        innerPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
        controls.add(innerPanel, BorderLayout.CENTER);
        controls.add(createButtonPanel(), BorderLayout.SOUTH);
        return controls;
    }


    private JComponent createSudokuView()
    {
        JTable sudokuTable = new JTable(sudokuTableModel);
        TableColumnModel columnModel = sudokuTable.getColumnModel();
        TableCellRenderer renderer = new SudokuCellRenderer();
        for (int i = 0; i < columnModel.getColumnCount(); i++)
        {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(sudokuTable, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createTitledBorder("Puzzle/Solution"));
        return wrapper;
    }


    private JComponent createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        solveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                String[] puzzle = PUZZLES[puzzleCombo.getSelectedIndex()];
                int populationSize = (Integer) populationSizeSpinner.getValue();
                double selectionPressure = selectionPressureSlider.getValue() / 100d;
                createTask(puzzle,
                           populationSize,
                           (int) Math.round(populationSize * 0.05), // Elite count is 5%.
                           selectionPressure).execute();
                solveButton.setEnabled(false);
                abortButton.setEnabled(true);
            }
        });

        abortButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                abortCondtion.abort();
            }
        });
        buttonPanel.add(solveButton);
        buttonPanel.add(abortButton);
        abortButton.setEnabled(false);
        return buttonPanel;
    }


    private JComponent createStatusBar()
    {
        JPanel statusBar = new JPanel(new GridLayout(1, 4));
        statusBar.add(new JLabel("Generations: "));
        statusBar.add(generationsLabel);
        statusBar.add(new JLabel("Time: ", JLabel.RIGHT));
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusBar.add(timeLabel);
        return statusBar;
    }


    /**
     * Helper method to create a background task for running the interactive evolutionary
     * algorithm.
     * @return A Swing task that will execute on a background thread and update
     * the GUI when it is done.
     */
    private SwingBackgroundTask<Sudoku> createTask(final String[] puzzle,
                                                   final int populationSize,
                                                   final int eliteCount,
                                                   final double selectionPressure)
    {
        return new SwingBackgroundTask<Sudoku>()
        {
            protected Sudoku performTask()
            {
                Random rng = new CellularAutomatonRNG();
                List<EvolutionaryOperator<? super Sudoku>> operators
                    = new ArrayList<EvolutionaryOperator<? super Sudoku>>(2);
                // Cross-over rows between parents (so offspring is x rows from parent1 and
                // y rows from parent2).
                operators.add(new SudokuVerticalCrossover());
                // Mutate the order of cells within individual rows.
                operators.add(new SudokuRowMutation(new PoissonGenerator(2, rng),
                                                    new DiscreteUniformGenerator(1, 8, rng)));

                EvolutionaryOperator<Sudoku> pipeline = new EvolutionPipeline<Sudoku>(operators);

                EvolutionEngine<Sudoku> engine = new StandaloneEvolutionEngine<Sudoku>(new SudokuFactory(puzzle),
                                                                                       pipeline,
                                                                                       new SudokuEvaluator(),
                                                                                       new TournamentSelection(selectionPressure),
                                                                                       rng);
                engine.addEvolutionObserver(new EvolutionLogger());
                abortCondtion = new UserAbort();
                return engine.evolve(populationSize,
                                     eliteCount,
                                     new TargetFitness(0, false), // Continue until a perfect solution is found...
                                     abortCondtion); // ...or the user aborts. 
            }

            
            protected void postProcessing(Sudoku result)
            {
                solveButton.setEnabled(true);
                abortButton.setEnabled(false);
            }
        };
    }



    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private class EvolutionLogger implements EvolutionObserver<Sudoku>
    {
        public void populationUpdate(PopulationData<Sudoku> data)
        {
            sudokuTableModel.setSudoku(data.getBestCandidate());
            generationsLabel.setText(String.valueOf(data.getGenerationNumber() + 1));
            timeLabel.setText(TIME_FORMAT.format(((double) data.getElapsedTime()) / 1000));
        }
    }
}
