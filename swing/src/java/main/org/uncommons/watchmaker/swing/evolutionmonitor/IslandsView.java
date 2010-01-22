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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * An evolution monitor view that gives an insight into how the evolution is progressing on
 * individual islands.
 * @author Daniel Dyer
 */
class IslandsView extends JPanel implements IslandEvolutionObserver<Object>
{
    private static final String FITTEST_INDIVIDUAL_LABEL = "Fittest Individual";
    private static final String MEAN_FITNESS_LABEL = "Mean Fitness/Standard Deviation";

    private final DefaultCategoryDataset bestDataSet = new DefaultCategoryDataset();
    private final DefaultStatisticalCategoryDataset meanDataSet = new DefaultStatisticalCategoryDataset();
    private final StatisticalLineAndShapeRenderer meanRenderer = new StatisticalLineAndShapeRenderer();
    private final JFreeChart chart;

    private final AtomicInteger islandCount = new AtomicInteger(0);
    private final Object maxLock = new Object();
    private double max = 0;



    IslandsView()
    {
        super(new BorderLayout());
        chart = ChartFactory.createBarChart("Island Population Fitness",
                                            "Island No.",
                                            "Candidate Fitness",
                                            bestDataSet,
                                            PlotOrientation.VERTICAL,
                                            true, // Legend
                                            false, // Tooltips
                                            false); // URLs
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getDomainAxis().setLowerMargin(0.02);
        plot.getDomainAxis().setUpperMargin(0.02);
        ((BarRenderer) plot.getRenderer()).setShadowVisible(false);
        plot.getRangeAxis().setAutoRange(false);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        meanRenderer.setBaseLinesVisible(false);
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
        add(createControls(), BorderLayout.SOUTH);
    }


    /**
     * Creates the GUI controls for toggling graph display options.
     * @return A component that can be added to the main panel.
     */
    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JCheckBox meanCheckBox = new JCheckBox("Show Mean and Standard Deviation", false);
        meanCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                chart.setNotify(false);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    plot.setDataset(1, meanDataSet);
                    plot.setRenderer(1, meanRenderer);
                }
                else
                {
                    plot.setDataset(1, null);
                    plot.setRenderer(1, null);
                }
                chart.setNotify(true);
            }
        });
        controls.add(meanCheckBox);

        return controls;
    }



    public void islandPopulationUpdate(final int islandIndex, final PopulationData<? extends Object> populationData)
    {
        // Make sure the bars are added to the chart in order of island index, regardless of which island
        // reports its results first.
        if (islandIndex >= islandCount.get())
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        // Don't need synchronisation here because SwingUtilities queues these updates
                        // and if a second update gets queued, the loop will be a no-op so it's not a problem.
                        for (Integer i = islandCount.get(); i <= islandIndex; i++)
                        {
                            bestDataSet.addValue(0, FITTEST_INDIVIDUAL_LABEL, i);
                            meanDataSet.add(0, 0, MEAN_FITNESS_LABEL, i);
                            islandCount.incrementAndGet();
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

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                chart.setNotify(false);
                bestDataSet.setValue(populationData.getBestCandidateFitness(), FITTEST_INDIVIDUAL_LABEL, (Integer) islandIndex);
                meanDataSet.remove(MEAN_FITNESS_LABEL, (Integer) islandIndex);
                meanDataSet.add(populationData.getMeanFitness(),
                                populationData.getFitnessStandardDeviation(),
                                MEAN_FITNESS_LABEL,
                                (Integer) islandIndex);
                ValueAxis rangeAxis = ((CategoryPlot) chart.getPlot()).getRangeAxis();
                // If the range is not sufficient to display all values, enlarge it.
                synchronized (maxLock)
                {
                    max = Math.max(max, populationData.getBestCandidateFitness());
                    max = Math.max(max, populationData.getMeanFitness() + populationData.getFitnessStandardDeviation());
                    while (max > rangeAxis.getUpperBound())
                    {
                        rangeAxis.setUpperBound(rangeAxis.getUpperBound() * 2);
                    }
                    // If the range is much bigger than it needs to be, reduce it.
                    while (max < rangeAxis.getUpperBound() / 4)
                    {
                        rangeAxis.setUpperBound(rangeAxis.getUpperBound() / 4);
                    }
                }
                chart.setNotify(true);
            }
        });
    }


    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        synchronized (maxLock)
        {
            max = 0;
        }
    }
}
