// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
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
    private final XYSeries bestSeries = new XYSeries("Fittest Individual");
    private final XYSeries meanSeries = new XYSeries("Mean Fitness");
    private final JFreeChart chart;
    private final XYSeriesCollection dataSet = new XYSeriesCollection();


    PopulationFitnessView()
    {
        super(new BorderLayout());
        dataSet.addSeries(bestSeries);
        dataSet.addSeries(meanSeries);
        chart = ChartFactory.createXYLineChart("Population Fitness",
                                               "Generations",
                                               "Fitness",
                                               dataSet,
                                               PlotOrientation.VERTICAL,
                                               true, // Legend.
                                               false, // Tooltips.
                                               false); // URLs.

        chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        add(createControls(), BorderLayout.SOUTH);
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }


    /**
     * Creates the GUI controls for toggling graph display options.
     * @return A component that can be added to the main panel.
     */
    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JCheckBox deviationCheckBox = new JCheckBox("Show Mean", true);
        deviationCheckBox.addItemListener(new ItemListener()
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
        controls.add(deviationCheckBox);

        final JCheckBox invertCheckBox = new JCheckBox("Invert Range Axis", false);
        invertCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                chart.getXYPlot().getRangeAxis().setInverted(invertCheckBox.isSelected());
            }
        });
        controls.add(invertCheckBox);

        return controls;
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(final PopulationData<? extends Object> populationData)
    {
        int generation = populationData.getGenerationNumber();
        double mean = populationData.getMeanFitness();
        meanSeries.add(generation, mean);
        double best = populationData.getBestCandidateFitness();
        bestSeries.add(generation, best);
    }
}
