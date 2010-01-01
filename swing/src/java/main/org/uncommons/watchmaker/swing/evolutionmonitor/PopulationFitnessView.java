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
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * {@link EvolutionMonitor} view for displaying a graph of population fitness data
 * over the lifetime of the evolutionary algorithm.
 * @author Daniel Dyer
 */
class PopulationFitnessView extends JPanel implements IslandEvolutionObserver<Object>
{
    private static final int SHOW_FIXED_GENERATIONS = 200;

    private final XYSeries bestSeries = new XYSeries("Fittest Individual");
    private final XYSeries meanSeries;
    private final XYSeriesCollection dataSet = new XYSeriesCollection();
    private final ValueAxis domainAxis;
    private final ValueAxis rangeAxis;

    private final JRadioButton allDataButton = new JRadioButton("All Data", false);
    private final JCheckBox invertCheckBox = new JCheckBox("Invert Range Axis", false);
    private final JFreeChart chart;

    private double maxY = 1;
    private double minY = 0;


    PopulationFitnessView(boolean islands)
    {
        super(new BorderLayout());
        meanSeries = new XYSeries(islands ? "Global Mean Fitness" : "Population Mean Fitness");
        dataSet.addSeries(bestSeries);
        dataSet.addSeries(meanSeries);
        chart = ChartFactory.createXYLineChart(islands ? "Global Population Fitness" : "Population Fitness",
                                               islands ? "Epochs" : "Generations",
                                               "Fitness",
                                               dataSet,
                                               PlotOrientation.VERTICAL,
                                               true, // Legend.
                                               false, // Tooltips.
                                               false);
        this.domainAxis = chart.getXYPlot().getDomainAxis();
        this.rangeAxis = chart.getXYPlot().getRangeAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setLowerMargin(0);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setRangeWithMargins(0, SHOW_FIXED_GENERATIONS);
        rangeAxis.setRange(minY, maxY);
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
        add(createControls(islands), BorderLayout.SOUTH);
    }


    /**
     * Creates the GUI controls for toggling graph display options.
     * @return A component that can be added to the main panel.
     */
    private JComponent createControls(boolean islands)
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        allDataButton.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                updateDomainAxisRange();
            }
        });
        String text = "Last " + SHOW_FIXED_GENERATIONS + (islands ? " Epochs" : " Generations");
        JRadioButton recentDataButton = new JRadioButton(text, true);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(allDataButton);
        buttonGroup.add(recentDataButton);

        controls.add(allDataButton);
        controls.add(recentDataButton);

        final JCheckBox meanCheckBox = new JCheckBox("Show Mean", true);
        meanCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    dataSet.addSeries(meanSeries);
                }
                else
                {
                    dataSet.removeSeries(meanSeries);
                }
            }
        });
        controls.add(meanCheckBox);

        invertCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                rangeAxis.setInverted(invertCheckBox.isSelected());
            }
        });
        controls.add(invertCheckBox);

        return controls;
    }


    /**
     * If "all data" is selected, set the range of the domain axis to include all
     * values.  Otherwise set it to show the most recent 200 generations.
     */
    private void updateDomainAxisRange()
    {
        int count = dataSet.getSeries(0).getItemCount();
        if (count < SHOW_FIXED_GENERATIONS)
        {
            domainAxis.setRangeWithMargins(0, SHOW_FIXED_GENERATIONS);
        }
        else if (allDataButton.isSelected())
        {
            domainAxis.setRangeWithMargins(0, Math.max(SHOW_FIXED_GENERATIONS, count));
        }
        else
        {
            domainAxis.setRangeWithMargins(count - SHOW_FIXED_GENERATIONS, count);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(final PopulationData<?> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                chart.setNotify(false); // Avoid triggering a redraw for every change we make in this method.
                if (populationData.getGenerationNumber() == 0)
                {
                    if (!populationData.isNaturalFitness())
                    {
                        invertCheckBox.setSelected(true);
                    }
                    // The graph might be showing data from a previous run, so clear it.
                    meanSeries.clear();
                    bestSeries.clear();
                }                
                meanSeries.add(populationData.getGenerationNumber(), populationData.getMeanFitness());
                double best = populationData.getBestCandidateFitness();
                bestSeries.add(populationData.getGenerationNumber(), best);

                // We don't use JFreeChart's auto-range for the axes because it is inefficient
                // (it degrades linearly with the number of items in the data set).  Instead we track
                // the minimum and maximum ourselves.
                double high = Math.max(populationData.getMeanFitness(), populationData.getBestCandidateFitness());
                double low = Math.min(populationData.getMeanFitness(), populationData.getBestCandidateFitness());
                if (high > maxY)
                {
                    maxY = high;
                    rangeAxis.setRange(minY, maxY);
                }
                if (low < minY)
                {
                    minY = low;
                    rangeAxis.setRange(minY, maxY);
                }

                updateDomainAxisRange();
                chart.setNotify(true); // Redraw all at once now.
            }
        });
    }


    public void islandPopulationUpdate(int islandIndex, PopulationData<? extends Object> populationData)
    {
        // Do nothing.
    }
}
