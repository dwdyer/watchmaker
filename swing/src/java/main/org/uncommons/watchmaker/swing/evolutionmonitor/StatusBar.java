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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * Status bar component for the evolution monitor.
 * @author Daniel Dyer
 */
class StatusBar extends Box implements EvolutionObserver<Object>
{
    private final JLabel generationsLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel timeLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel populationLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel elitismLabel = new JLabel("N/A", JLabel.RIGHT);

    public StatusBar()
    {
        super(BoxLayout.X_AXIS);
        add(new JLabel("Population Size: "));
        add(populationLabel);
        add(createHorizontalStrut(20));
        add(new JLabel("Elitism: "));
        add(elitismLabel);
        add(createHorizontalStrut(20));
        add(new JLabel("Generations: "));
        add(generationsLabel);
        add(createHorizontalStrut(20));
        add(new JLabel("Elapsed Time: "));
        add(timeLabel);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    }


    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        populationLabel.setText(String.valueOf(populationData.getPopulationSize()));
        elitismLabel.setText(String.valueOf(populationData.getEliteCount()));
        generationsLabel.setText(String.valueOf(populationData.getGenerationNumber() + 1));
        timeLabel.setText(formatTime(populationData.getElapsedTime()));
    }


    private String formatTime(long time)
    {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        StringBuilder buffer = new StringBuilder();
        if (hours < 10)
        {
            buffer.append('0');
        }
        buffer.append(hours);
        buffer.append(':');
        if (minutes < 10)
        {
            buffer.append('0');
        }
        buffer.append(minutes);
        buffer.append(':');
        if (seconds < 10)
        {
            buffer.append('0');
        }
        buffer.append(seconds);
        return buffer.toString();
    }
}
