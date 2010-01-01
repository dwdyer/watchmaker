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
package org.uncommons.watchmaker.framework.selection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Implements selection of <i>n</i> candidates from a population by simply
 * selecting the <i>n</i> candidates with the highest fitness scores (the
 * rest are discarded).  A candidate is never selected more than once.
 * @author Daniel Dyer
 */
public class TruncationSelection implements SelectionStrategy<Object>
{
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.###%");
    private final NumberGenerator<Double> selectionRatio;

    private String description = "Truncation Selection";

    /**
     * Creates a truncation selection strategy that is controlled by the
     * variable selection ratio provided by the specified
     * {@link NumberGenerator}.
     * @param selectionRatio A number generator that produces values in
     * the range {@literal 0 < r < 1}.  These values are used to determine
     * the proportion of the population that is retained in any given selection.
     */
    public TruncationSelection(NumberGenerator<Double> selectionRatio)
    {
        this.selectionRatio = selectionRatio;
    }


    /**
     * @param selectionRatio The proportion of the highest ranked candidates to
     * select from the population.  The value must be positive and less than 1.
     */
    public TruncationSelection(double selectionRatio)
    {
        this(new ConstantGenerator<Double>(selectionRatio));
        if (selectionRatio <= 0 || selectionRatio >= 1)
        {
            throw new IllegalArgumentException("Selection ratio must be greater than 0 and less than 1.");
        }
        this.description = "Truncation Selection (" + PERCENT_FORMAT.format(selectionRatio) + ")";
    }


    /**
     * Selects the fittest candidates.  If the selectionRatio results in
     * fewer selected candidates than required, then these candidates are
     * selected multiple times to make up the shortfall.
     * @param population The population of evolved and evaluated candidates
     * from which to select.
     * @param naturalFitnessScores Whether higher fitness values represent fitter
     * individuals or not.
     * @param selectionSize The number of candidates to select from the
     * evolved population.
     * @param rng A source of randomness (not used by this selection
     * implementation since truncation selection is deterministic).
     * @param <S> The type of evolved entity that is being selected.
     * @return The selected candidates.
     */
    public <S> List<S> select(List<EvaluatedCandidate<S>> population,
                              boolean naturalFitnessScores,
                              int selectionSize,
                              Random rng)
    {
        List<S> selection = new ArrayList<S>(selectionSize);

        double ratio = selectionRatio.nextValue();
        assert ratio < 1 && ratio > 0 : "Selection ratio out-of-range: " + ratio;
        
        int eligibleCount = (int) Math.round(ratio * population.size());
        eligibleCount = eligibleCount > selectionSize ? selectionSize : eligibleCount;

        do
        {
            int count = Math.min(eligibleCount, selectionSize - selection.size());
            for (int i = 0; i < count; i++)
            {
                selection.add(population.get(i).getCandidate());
            }
        } while (selection.size() < selectionSize);
        return selection;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return description;
    }
}
