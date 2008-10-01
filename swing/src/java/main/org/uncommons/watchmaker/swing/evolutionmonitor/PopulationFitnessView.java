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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * {@link EvolutionMonitor} view for displaying a graph of population fitness data
 * over the lifetime of the evolutionary algorithm.
 * @param <T> The type of entity that exists in the evolving population
 * that is being observed.  This type can be bound to a super-type of the
 * actual population type so as to allow a non-specific observer that can
 * be re-used for different population types.
 * @author Daniel Dyer
 */
class PopulationFitnessView<T> extends JPanel implements EvolutionObserver<T>
{
    private final XYIntervalSeries meanSeries = new XYIntervalSeries("Mean Fitness");
    private final XYIntervalSeries bestSeries = new XYIntervalSeries("Fittest Individual");
    private final DeviationRenderer renderer = new DeviationRenderer(true, false);
    private final JFreeChart chart;

    PopulationFitnessView()
    {
        super(new BorderLayout());
        XYIntervalSeriesCollection dataSet = new XYIntervalSeriesCollection();
        dataSet.addSeries(meanSeries);
        dataSet.addSeries(bestSeries);
        chart = ChartFactory.createXYLineChart("Population Fitness",
                                               "Generations",
                                               "Fitness",
                                               dataSet,
                                               PlotOrientation.VERTICAL,
                                               true, // Legend.
                                               false, // Tooltips.
                                               false); // URLs.
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesFillPaint(0, Color.BLUE.brighter());
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setAlpha(0.1f);
        chart.getXYPlot().setRenderer(renderer);
        add(createControls(), BorderLayout.SOUTH);
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }


    /**
     * Creates the GUI controls for toggling graph display options.
     * @return A component that can be added to the main panel.
     */
    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JCheckBox deviationCheckBox = new JCheckBox("Standard Deviation", true);
        deviationCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                Paint paint = deviationCheckBox.isSelected() ? Color.BLUE.brighter() : null;
                renderer.setSeriesFillPaint(0, paint);
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

        controls.setBorder(BorderFactory.createTitledBorder("Display Options"));
        return controls;
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(PopulationData<T> populationData)
    {
        int generation = populationData.getGenerationNumber();
        double mean = populationData.getMeanFitness();
        double meanMin = mean - populationData.getFitnessStandardDeviation();
        double meanMax = mean + populationData.getFitnessStandardDeviation();
        meanSeries.add(generation, generation, generation, mean, meanMin, meanMax);
        double best = populationData.getBestCandidateFitness();
        bestSeries.add(generation, generation, generation, best, best, best);
    }
}
