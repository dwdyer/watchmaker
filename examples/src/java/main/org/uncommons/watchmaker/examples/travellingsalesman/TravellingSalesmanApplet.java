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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Applet for comparing evolutionary and brute force approaches to the
 * Travelling Salesman problem.
 * @author Daniel Dyer
 */
public final class TravellingSalesmanApplet extends AbstractExampleApplet
{
    private final DistanceLookup distances = new EuropeanDistanceLookup();
    private final FitnessEvaluator<List<String>> evaluator = new RouteEvaluator(distances);

    private ItineraryPanel itineraryPanel;
    private StrategyPanel strategyPanel;
    private ExecutionPanel executionPanel;


    /**
     * Initialise and layout the GUI.
     * @param container The Swing component that will contain the GUI controls.
     */
    @Override
    protected void prepareGUI(Container container)
    {
        itineraryPanel = new ItineraryPanel(distances.getKnownCities());
        strategyPanel = new StrategyPanel(distances);
        executionPanel = new ExecutionPanel();

        container.add(itineraryPanel, BorderLayout.WEST);
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(strategyPanel, BorderLayout.NORTH);
        innerPanel.add(executionPanel, BorderLayout.CENTER);
        container.add(innerPanel, BorderLayout.CENTER);

        executionPanel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                Collection<String> cities = itineraryPanel.getSelectedCities();
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
                        setEnabled(false);
                        createTask(cities).execute();
                    }
                    catch (IllegalArgumentException ex)
                    {
                        JOptionPane.showMessageDialog(TravellingSalesmanApplet.this,
                                                      ex.getMessage(),
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                        setEnabled(true);
                    }
                }
            }
        });
        container.validate();
    }


    /**
     * Helper method to create a background task for running the travelling
     * salesman algorithm.
     * @param cities The set of cities to generate a route for.
     * @return A Swing task that will execute on a background thread and update
     * the GUI when it is done.
     */
    private SwingBackgroundTask<List<String>> createTask(final Collection<String> cities)
    {
        final TravellingSalesmanStrategy strategy = strategyPanel.getStrategy();
        return new SwingBackgroundTask<List<String>>()
        {
            private long elapsedTime = 0;

            @Override
            protected List<String> performTask()
            {
                long startTime = System.currentTimeMillis();
                List<String> result = strategy.calculateShortestRoute(cities, executionPanel);
                elapsedTime = System.currentTimeMillis() - startTime;
                return result;
            }

            @Override
            protected void postProcessing(List<String> result)
            {
                executionPanel.appendOutput(createResultString(strategy.getDescription(),
                                                               result,
                                                               evaluator.getFitness(result, null),
                                                               elapsedTime));
                setEnabled(true);
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


    /**
     * Toggles whether the controls are enabled for input or not.
     * @param b Enables the controls if this flag is true, disables them otherwise.
     */
    @Override
    public void setEnabled(boolean b)
    {
        itineraryPanel.setEnabled(b);
        strategyPanel.setEnabled(b);
        executionPanel.setEnabled(b);
        super.setEnabled(b);
    }


    /**
     * Entry point for running this example as an application rather than an applet.
     * @param args Program arguments (ignored).
     */
    public static void main(String[] args)
    {
        new TravellingSalesmanApplet().displayInFrame("Watchmaker Framework - Travelling Salesman Example");
    }
}
