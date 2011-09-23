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

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * Status bar component for the evolution monitor.  Can also be used separately to
 * provide basic status information without having to use the full evolution monitor.
 * @author Daniel Dyer
 */
public class StatusBar extends Box implements IslandEvolutionObserver<Object>
{
    private final JLabel generationsLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel timeLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel populationLabel = new JLabel("N/A", JLabel.RIGHT);
    private final JLabel elitismLabel = new JLabel("N/A", JLabel.RIGHT);

    private final AtomicInteger islandPopulationSize = new AtomicInteger(-1);
    private long elapsedTime;
    private long epochTime;


    /**
     * Creates a status bar configured for non-island evolution.
     */
    public StatusBar()
    {
        this(false);
    }


    /**
     * @param islands Whether the status bar should be configured for updates from
     * {@link org.uncommons.watchmaker.framework.islands.IslandEvolution}.  Set this
     * parameter to false when using a standard {@link org.uncommons.watchmaker.framework.EvolutionEngine}
     */
    public StatusBar(boolean islands)
    {
        super(BoxLayout.X_AXIS);
        add(new JLabel("Population: "));
        add(populationLabel);
        add(createHorizontalStrut(15));
        add(new JLabel("Elitism: "));
        add(elitismLabel);
        add(createHorizontalStrut(15));
        add(new JLabel(islands ? "Epochs: " : "Generations: "));
        add(generationsLabel);
        add(createHorizontalStrut(15));
        add(new JLabel("Elapsed Time: "));
        add(timeLabel);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // Set component names for easy look-up from tests.
        populationLabel.setName("Population");
        elitismLabel.setName("Elitism");
        generationsLabel.setName("Generations");
        timeLabel.setName("Time");
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
                if (populationData.getGenerationNumber() == 0)
                {
                    int islandSize = islandPopulationSize.get();
                    if (islandSize > 0)
                    {
                        int islandCount = populationData.getPopulationSize() / islandSize;
                        populationLabel.setText(islandCount + "x" + islandSize);
                        elitismLabel.setText(islandCount + "x" + populationData.getEliteCount());
                    }
                    else
                    {
                        populationLabel.setText(String.valueOf(populationData.getPopulationSize()));
                        elitismLabel.setText(String.valueOf(populationData.getEliteCount()));
                    }
                }
                generationsLabel.setText(String.valueOf(populationData.getGenerationNumber() + 1));
                elapsedTime = populationData.getElapsedTime();
                epochTime = 0;
                timeLabel.setText(formatTime(elapsedTime));
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    public void islandPopulationUpdate(int islandIndex,
                                       final PopulationData<? extends Object> populationData)
    {
        islandPopulationSize.compareAndSet(-1, populationData.getPopulationSize());
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // Only update the label if the time has advanced.  Sometimes, due to threading
                // variations, later updates have shorter elapsed times.
                if (populationData.getElapsedTime() > epochTime)
                {
                    epochTime = populationData.getElapsedTime();
                    timeLabel.setText(formatTime(elapsedTime + epochTime));
                }
            }
        });
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
