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
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * {@link EvolutionMonitor} view for displaying a graph of population fitness data
 * over the lifetime of the evolutionary algorithm.
 * @author Daniel Dyer
 */
class PopulationFitnessView extends JPanel implements EvolutionObserver<Object>
{
    private static final int SHOW_FIXED_GENERATIONS = 200;

    private final XYSeries bestSeries = new XYSeries("Fittest Individual");
    private final XYSeries meanSeries = new XYSeries("Population Mean Fitness");
    private final XYSeriesCollection dataSet = new XYSeriesCollection();
    private final ValueAxis domainAxis;
    private final ValueAxis rangeAxis;

    private final JRadioButton allDataButton = new JRadioButton("All Data", false);
    private final JCheckBox invertCheckBox = new JCheckBox("Invert Range Axis", false);

    private double maxY = 1;
    private double minY = 0;

    PopulationFitnessView()
    {
        super(new BorderLayout());
        dataSet.addSeries(bestSeries);
        dataSet.addSeries(meanSeries);
        JFreeChart chart = ChartFactory.createXYLineChart("Population Fitness",
                                                          "Generations",
                                                          "Fitness",
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Legend.
                                                          false, // Tooltips.
                                                          false); // URLs.
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
                                               true, // Buffered
                                               false, // Properties
                                               true, // Save
                                               true, // Print
                                               false, // Zoom
                                               true); // Tooltips
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

        allDataButton.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                updateDomainAxisRange();
            }
        });
        JRadioButton recentDataButton = new JRadioButton("Last " + SHOW_FIXED_GENERATIONS + " Generations", true);
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
        if (allDataButton.isSelected())
        {
            domainAxis.setRangeWithMargins(0, Math.max(SHOW_FIXED_GENERATIONS, count));
        }
        else
        {
            if (count >= SHOW_FIXED_GENERATIONS)
            {
                domainAxis.setRangeWithMargins(count - SHOW_FIXED_GENERATIONS, count);
            }
            else
            {
                domainAxis.setRangeWithMargins(0, SHOW_FIXED_GENERATIONS);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(final PopulationData<? extends Object> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (populationData.getGenerationNumber() == 0 && !populationData.isNaturalFitness())
                {
                    invertCheckBox.setSelected(true);
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
            }
        });
    }
}
