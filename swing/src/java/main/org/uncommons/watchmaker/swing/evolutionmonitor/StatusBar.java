package org.uncommons.watchmaker.swing.evolutionmonitor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * @author Daniel Dyer
 */
class StatusBar extends Box implements EvolutionObserver<Object>
{
    private final JLabel timeLabel = new JLabel("00:00:00", JLabel.RIGHT);

    public StatusBar()
    {
        super(BoxLayout.X_AXIS);
        add(new JLabel("Elapsed Time: "));
        add(timeLabel);
    }


    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        long time = populationData.getElapsedTime();
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
        timeLabel.setText(buffer.toString());
    }
}
