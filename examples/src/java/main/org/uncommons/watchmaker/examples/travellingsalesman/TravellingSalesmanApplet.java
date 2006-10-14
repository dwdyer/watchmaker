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
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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


    private static class ExecutionPanel extends JPanel
    {
        public ExecutionPanel()
        {
            super(new BorderLayout());
            JPanel controlPanel = new JPanel(new BorderLayout());
            JButton startButton = new JButton("Start");
            controlPanel.add(startButton, BorderLayout.WEST);
            JProgressBar progressBar = new JProgressBar(0, 100);
            controlPanel.add(progressBar, BorderLayout.CENTER);
            add(controlPanel, BorderLayout.NORTH);
            JTextArea output = new JTextArea();
            JScrollPane scroller = new JScrollPane(output);
            scroller.setBorder(BorderFactory.createTitledBorder("Results"));
            add(scroller, BorderLayout.CENTER);
        }
    }
}
