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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
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
 * @author Daniel Dyer
 */
class IslandsView extends JPanel implements IslandEvolutionObserver<Object>
{
    private final Map<Integer, XYSeries> islandSeries = new HashMap<Integer, XYSeries>();
    private final XYSeriesCollection dataSet = new XYSeriesCollection();
    private final ValueAxis domainAxis;

    IslandsView()
    {
        super(new BorderLayout());
        JFreeChart chart = ChartFactory.createXYLineChart("Island Populations",
                                                          "Generations",
                                                          "Fitness",
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Legend.
                                                          false, // Tooltips.
                                                          false); // URLs.
        this.domainAxis = chart.getXYPlot().getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setLowerMargin(0);
        domainAxis.setUpperMargin(0.05);
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
    }


    public void islandPopulationUpdate(final int islandIndex, final PopulationData<? extends Object> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                XYSeries islandData = islandSeries.get(islandIndex);
                if (islandData == null)
                {
                    islandData = new XYSeries("Island " + islandIndex);
                    islandSeries.put(islandIndex, islandData);
                    dataSet.addSeries(islandData);
                }
                islandData.add(islandData.getItemCount(), populationData.getBestCandidateFitness());
            }
        });
    }


    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        // Do nothing.
    }
}
