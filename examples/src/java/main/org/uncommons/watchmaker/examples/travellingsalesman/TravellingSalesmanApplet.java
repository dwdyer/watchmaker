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
 * @author Daniel Dyer
 */
public class TravellingSalesmanApplet extends JApplet
{
    private final ItineraryPanel itineraryPanel;
    private final StrategyPanel strategyPanel;
    private final ExecutionPanel executionPanel;

    public TravellingSalesmanApplet()
    {
        itineraryPanel = new ItineraryPanel(Europe.getInstance().getCities());
        strategyPanel = new StrategyPanel();
        executionPanel = new ExecutionPanel();
        add(itineraryPanel, BorderLayout.WEST);
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(strategyPanel, BorderLayout.NORTH);
        innerPanel.add(executionPanel, BorderLayout.CENTER);
        add(innerPanel, BorderLayout.CENTER);
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
        private JButton startButton;
        private JScrollPane scroller;

        public ExecutionPanel()
        {
            super(new BorderLayout());
            JPanel controlPanel = new JPanel(new BorderLayout());
            startButton = new JButton("Start");
            controlPanel.add(startButton, BorderLayout.WEST);
            final JProgressBar progressBar = new JProgressBar(0, 100);
            controlPanel.add(progressBar, BorderLayout.CENTER);
            add(controlPanel, BorderLayout.NORTH);
            final JTextArea output = new JTextArea();
            output.setEditable(false);
            output.setLineWrap(true);
            output.setWrapStyleWord(true);
            output.setFont(new Font("Monospaced", Font.PLAIN, 10));
            scroller = new JScrollPane(output);
            scroller.setBorder(BorderFactory.createTitledBorder("Results"));
            add(scroller, BorderLayout.CENTER);

            startButton.addActionListener(new ActionListener()
            {
                private final ProgressBarUpdater progressBarUpdater = new ProgressBarUpdater(progressBar);
                private final FitnessEvaluator<List<String>> evaluator = new RouteEvaluator();

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
                        TravellingSalesmanApplet.this.setEnabled(false);
                        final TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
                        SwingBackgroundTask<List<String>> task = new SwingBackgroundTask<List<String>>()
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
                                output.append("[");
                                output.append(strategy.getDescription());
                                output.append("]\n");
                                output.append("ROUTE: ");
                                for (String s : result)
                                {
                                    output.append(s);
                                    output.append(" -> ");
                                }
                                output.append(result.get(0));
                                output.append("\n");
                                output.append("TOTAL DISTANCE: ");
                                output.append(String.valueOf(evaluator.getFitness(result)));
                                output.append("km\n");
                                output.append("(Search Time: ");
                                output.append(String.valueOf(elapsedTime));
                                output.append("ms)\n\n");
                                TravellingSalesmanApplet.this.setEnabled(true);
                            }
                        };
                        task.execute();
                    }
                }
            });
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
    private class ProgressBarUpdater implements ProgressListener, Runnable
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
