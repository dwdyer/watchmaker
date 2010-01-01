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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * Evolution monitor panel that displays information about the current
 * state of the Java Virtual machine that is running the program.
 * @author Daniel Dyer
 */
class JVMView extends JPanel
{
    private static final int MEGABYTE = 1048576;

    private final TimeSeries memoryUsageSeries = new TimeSeries("Memory Usage");
    private final TimeSeries heapSizeSeries = new TimeSeries("Heap Size");

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    JVMView()
    {
        super(new BorderLayout());
        double maxMemory = (double) memoryBean.getHeapMemoryUsage().getMax() / MEGABYTE;

        ChartPanel heapPanel = new ChartPanel(createHeapChart(maxMemory),
                                              false, // Properties
                                              true, // Save
                                              true, // Print
                                              false, // Zoom
                                              true); // Tooltips
        heapPanel.setMouseZoomable(false);
        add(heapPanel, BorderLayout.CENTER);
        add(createControls(), BorderLayout.SOUTH);

        Timer timer = new Timer(5000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addMemoryDataPoint();
            }
        });

        // Plot start values.
        addMemoryDataPoint();

        timer.start();
    }

    
    private JFreeChart createHeapChart(double maxMemory)
    {
        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        dataSet.addSeries(memoryUsageSeries);
        dataSet.addSeries(heapSizeSeries);
        JFreeChart chart = ChartFactory.createXYAreaChart("JVM Heap",
                                                          "Time",
                                                          "Megabytes",
                                                           dataSet,
                                                           PlotOrientation.VERTICAL,
                                                           true, // Legend.
                                                           false, // Tooltips.
                                                           false);
        DateAxis timeAxis = new DateAxis("Time");
        timeAxis.setLowerMargin(0);
        timeAxis.setUpperMargin(0);
        chart.getXYPlot().setDomainAxis(timeAxis);
        chart.getXYPlot().getRangeAxis().setLowerBound(0);
        chart.getXYPlot().getRangeAxis().setUpperBound(maxMemory * 1.1); // Add 10% to leave room for marker.

        // Add a horizontal marker to indicate the heap growth limit.
        ValueMarker marker = new ValueMarker(maxMemory, Color.BLACK, new BasicStroke(1));
        marker.setLabel("Maximum Permitted Heap Size (adjust with -Xmx)");
        marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        marker.setLabelAnchor(RectangleAnchor.RIGHT);
        chart.getXYPlot().addRangeMarker(marker);

        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.RED);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, new Color(0, 128, 0, 128));

        return chart;
    }


    /**
     * Creates the GUI controls for toggling graph display options.
     * @return A component that can be added to the main panel.
     */
    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton gcButton = new JButton("Request GC");
        gcButton.setToolTipText("Perform garbage collection (the JVM may ignore this request).");
        gcButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                memoryBean.gc();
            }
        });
        controls.add(gcButton);
        return controls;
    }



    private void addMemoryDataPoint()
    {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        double usedMegabytes = (double) heapUsage.getUsed() / MEGABYTE;
        Second second = new Second();
        memoryUsageSeries.add(second, usedMegabytes);
        heapSizeSeries.add(second, (double) heapUsage.getCommitted() / MEGABYTE);
    }
}
