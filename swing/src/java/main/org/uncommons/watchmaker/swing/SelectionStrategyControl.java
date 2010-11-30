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
package org.uncommons.watchmaker.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.JComboBox;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;

/**
 * An evolution control for selecting between different {@link SelectionStrategy} implementations.
 * This control provides a proxy selection strategy that delegates to the currently selected
 * strategy.  Using this proxy strategy with an {@link org.uncommons.watchmaker.framework.EvolutionEngine}
 * means that any change to the combo-box selection is immediately reflected in the selection used
 * by the running evolution engine.
 * @param <T> A generic type that matches the type associated with the selection strategies.
 * @author Daniel Dyer
 */
public class SelectionStrategyControl<T> implements EvolutionControl
{
    private final JComboBox control;
    private final ProxySelectionStrategy selectionStrategy;


    /**
     * Creates a control for choosing between a specified set of selection strategies.
     * @param options The selection strategies to choose from.
     */
    public SelectionStrategyControl(List<SelectionStrategy<? super T>> options)
    {
        this.control = new JComboBox(new Vector<SelectionStrategy<? super T>>(options));
        this.selectionStrategy = new ProxySelectionStrategy(options.get(0));
        this.control.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                if (ev.getStateChange() == ItemEvent.SELECTED)
                {
                    @SuppressWarnings("unchecked")
                    SelectionStrategy<? super T> delegate = (SelectionStrategy<? super T>) control.getSelectedItem();
                    selectionStrategy.setDelegate(delegate);
                }
            }
        });
    }


    /**
     * Creates a list containing one instance of each of the standard selection strategies.
     * These strategies are {@link RankSelection}, {@link RouletteWheelSelection},
     * {@link StochasticUniversalSampling}, {@link TournamentSelection} and {@link TruncationSelection}.
     * @param tournamentProbability The probability parameter for {@link TournamentSelection}.
     * @param truncationRatio The ratio parameter for {@link TruncationSelection}.
     * @return A list of selection strategies.
     */
    public static <T> List<SelectionStrategy<? super T>> createDefaultOptions(Probability tournamentProbability,
                                                                              double truncationRatio)
    {
        List<SelectionStrategy<? super T>> options = new LinkedList<SelectionStrategy<? super T>>();
        options.add(new RankSelection());
        options.add(new RouletteWheelSelection());
        options.add(new SigmaScaling());
        options.add(new StochasticUniversalSampling());
        options.add(new TournamentSelection(tournamentProbability));
        options.add(new TruncationSelection(truncationRatio));
        return options;
    }


    /**
     * {@inheritDoc}
     */
    public JComboBox getControl()
    {
        return control;
    }


    /**
     * {@inheritDoc}
     */
    public void reset()
    {
        control.setSelectedIndex(0);
    }


    /**
     * {@inheritDoc}
     */
    public void setDescription(String description)
    {
        control.setToolTipText(description);
    }    


    /**
     * @return A proxied {@link SelectionStrategy} that delegates to whichever
     * concrete selection strategy is currently selected.
     */
    public SelectionStrategy<T> getSelectionStrategy()
    {
        return selectionStrategy;
    }


    /**
     * A {@link SelectionStrategy} implementation that simply delegates to the selection strategy
     * currently selected by the combobox control.
     */
    private class ProxySelectionStrategy implements SelectionStrategy<T>
    {
        private volatile SelectionStrategy<? super T> delegate;

        ProxySelectionStrategy(SelectionStrategy<? super T> delegate)
        {
            this.delegate = delegate;
        }


        public void setDelegate(SelectionStrategy<? super T> delegate)
        {
            this.delegate = delegate;
        }


        /**
         * {@inheritDoc}
         */
        public <S extends T> List<S> select(List<EvaluatedCandidate<S>> population,
                                            boolean naturalFitnessScores,
                                            int selectionSize,
                                            Random rng)
        {
            return delegate.select(population, naturalFitnessScores, selectionSize, rng);
        }


        @Override
        public String toString()
        {
            return delegate.toString();
        }
    }
}
