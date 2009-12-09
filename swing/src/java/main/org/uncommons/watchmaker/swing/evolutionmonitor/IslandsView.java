// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * An evolution monitor view that gives an insight into how the evolution is progressing on
 * individual islands.
 * @author Daniel Dyer
 */
class IslandsView extends JPanel implements IslandEvolutionObserver<Object>
{
    private final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

    private final Map<Integer, Double> values = Collections.synchronizedMap(new HashMap<Integer, Double>());
    private final JFreeChart chart;

    private double max = 0;

    IslandsView()
    {
        super(new BorderLayout());
        chart = ChartFactory.createBarChart("Fittest Candidate by Island",
                                                       "Island No.",
                                                       "Best Candidate Fitness",
                                                       dataSet,
                                                       PlotOrientation.VERTICAL,
                                                       false,
                                                       false,
                                                       false);
        ((CategoryPlot) chart.getPlot()).getRangeAxis().setAutoRange(false);
        ChartPanel chartPanel = new ChartPanel(chart,
                                               ChartPanel.DEFAULT_WIDTH,
                                               ChartPanel.DEFAULT_HEIGHT,
                                               ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
                                               ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
                                               ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
                                               ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
                                               false, // Buffered
                                               false, // Properties
                                               true, // Save
                                               true, // Print
                                               false, // Zoom
                                               false); // Tooltips
        add(chartPanel, BorderLayout.CENTER);
    }


    public void islandPopulationUpdate(final int islandIndex, final PopulationData<? extends Object> populationData)
    {
        // Make sure the bars are added to the chart in order of island index, regardless of which island
        // reports its results first.
        if (islandIndex >= values.size())
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        synchronized (values)
                        {
                            for (Integer i = values.size(); i <= islandIndex; i++)
                            {
                                values.put(i, 0.0d);
                                dataSet.addValue(0, "Fittest", i);
                            }
                        }
                    }
                });
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (InvocationTargetException ex)
            {
                throw new IllegalStateException(ex.getCause());
            }
        }

        // Only queue a GUI update if it is actually necessary.
        Double oldValue = values.put(islandIndex, populationData.getBestCandidateFitness());
        if (oldValue == null || oldValue != populationData.getBestCandidateFitness())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    chart.setNotify(false);
                    dataSet.setValue(populationData.getBestCandidateFitness(), "Fittest", (Integer) islandIndex);
                    ValueAxis rangeAxis = ((CategoryPlot) chart.getPlot()).getRangeAxis();
                    // If the range is not sufficient to display all values, enlarge it.
                    max = Math.max(max, populationData.getBestCandidateFitness());
                    while (max > rangeAxis.getUpperBound())
                    {
                        rangeAxis.setUpperBound(rangeAxis.getUpperBound() * 2);
                    }
                    chart.setNotify(true);
                }
            });
        }
    }


    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        // Do nothing.
    }
}
