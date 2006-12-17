// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.uncommons.gui.SwingBackgroundTask;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Applet for comparing evolutionary and brute force approaches to the
 * Travelling Salesman problem.
 * @author Daniel Dyer
 */
public final class TravellingSalesmanApplet extends JApplet
{
    private final ItineraryPanel itineraryPanel;
    private final StrategyPanel strategyPanel;
    private final ExecutionPanel executionPanel;

    public TravellingSalesmanApplet()
    {
        DistanceLookup distances = new EuropeanDistanceLookup();
        itineraryPanel = new ItineraryPanel(distances.getKnownCities());
        strategyPanel = new StrategyPanel(distances);
        executionPanel = new ExecutionPanel(distances);
    }


    @Override
    public void init()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                add(itineraryPanel, BorderLayout.WEST);
                JPanel innerPanel = new JPanel(new BorderLayout());
                innerPanel.add(strategyPanel, BorderLayout.NORTH);
                innerPanel.add(executionPanel, BorderLayout.CENTER);
                add(innerPanel, BorderLayout.CENTER);
            }
        });
    }


    @Override
    public void setEnabled(boolean b)
    {
        itineraryPanel.setEnabled(b);
        strategyPanel.setEnabled(b);
        executionPanel.setEnabled(b);
        super.setEnabled(b);
    }


    private final class ExecutionPanel extends JPanel
    {
        private final JButton startButton;
        private final JTextArea output;
        private final JScrollPane scroller;

        private final FitnessEvaluator<List<String>> evaluator;

        public ExecutionPanel(DistanceLookup distances)
        {
            super(new BorderLayout());
            evaluator = new RouteEvaluator(distances);
            JPanel controlPanel = new JPanel(new BorderLayout());
            startButton = new JButton("Start");
            controlPanel.add(startButton, BorderLayout.WEST);
            JProgressBar progressBar = new JProgressBar(0, 100);
            controlPanel.add(progressBar, BorderLayout.CENTER);
            final ProgressBarUpdater progressBarUpdater = new ProgressBarUpdater(progressBar);
            add(controlPanel, BorderLayout.NORTH);
            output = new JTextArea();
            output.setEditable(false);
            output.setLineWrap(true);
            output.setWrapStyleWord(true);
            output.setFont(new Font("Monospaced", Font.PLAIN, 12));
            scroller = new JScrollPane(output);
            scroller.setBorder(BorderFactory.createTitledBorder("Results"));
            add(scroller, BorderLayout.CENTER);

            startButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    final Collection<String> cities = itineraryPanel.getSelectedCities();
                    if (cities.size() < 4)
                    {
                        JOptionPane.showMessageDialog(TravellingSalesmanApplet.this,
                                                      "Itinerary must include at least 4 cities.",
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        try
                        {
                            TravellingSalesmanApplet.this.setEnabled(false);
                            createTask(cities, progressBarUpdater).execute();
                        }
                        catch (IllegalArgumentException ex)
                        {
                            JOptionPane.showMessageDialog(TravellingSalesmanApplet.this,
                                                          ex.getMessage(),
                                                          "Error",
                                                          JOptionPane.ERROR_MESSAGE);
                            TravellingSalesmanApplet.this.setEnabled(true);
                        }
                    }
                }
            });
        }


        /**
         * Helper method to create a background task for running the travelling
         * salesman algorithm.
         */
        private SwingBackgroundTask<List<String>> createTask(final Collection<String> cities,
                                                             final ProgressBarUpdater progressBarUpdater)
        {
            final TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
            return new SwingBackgroundTask<List<String>>()
            {
                private long elapsedTime = 0;

                protected List<String> performTask()
                {
                    long startTime = System.currentTimeMillis();
                    List<String> result = strategy.calculateShortestRoute(cities,
                                                                          progressBarUpdater);
                    elapsedTime = System.currentTimeMillis() - startTime;
                    return result;
                }

                protected void postProcessing(List<String> result)
                {
                    output.append(createResultString(strategy.getDescription(),
                                                     result,
                                                     evaluator.getFitness(result),
                                                     elapsedTime));
                    TravellingSalesmanApplet.this.setEnabled(true);
                }
            };
        }


        /**
         * Helper method for formatting a result as a string for display.
         */
        private String createResultString(String strategyDescription,
                                          List<String> shortestRoute,
                                          double distance,
                                          long elapsedTime)
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append('[');
            buffer.append(strategyDescription);
            buffer.append("]\n");
            buffer.append("ROUTE: ");
            for (String s : shortestRoute)
            {
                buffer.append(s);
                buffer.append(" -> ");
            }
            buffer.append(shortestRoute.get(0));
            buffer.append('\n');
            buffer.append("TOTAL DISTANCE: ");
            buffer.append(String.valueOf(distance));
            buffer.append("km\n");
            buffer.append("(Search Time: ");
            double seconds = (double) elapsedTime / 1000;
            buffer.append(String.valueOf(seconds));
            buffer.append(" seconds)\n\n");
            return buffer.toString();
        }


        @Override
        public void setEnabled(boolean b)
        {
            startButton.setEnabled(b);
            scroller.setEnabled(b);
            super.setEnabled(b);
        }
    }


    /**
     * Class for updating the progress bar.
     */
    private static class ProgressBarUpdater implements ProgressListener, Runnable
    {
        private final JProgressBar progressBar;
        private int newValue = 0;

        public ProgressBarUpdater(JProgressBar progressBar)
        {
            this.progressBar = progressBar;
        }

        public synchronized void updateProgress(double percentComplete)
        {
            this.newValue = (int) percentComplete;
            SwingUtilities.invokeLater(this);
        }

        public synchronized void run()
        {
            progressBar.setValue(newValue);
        }
    }
}
