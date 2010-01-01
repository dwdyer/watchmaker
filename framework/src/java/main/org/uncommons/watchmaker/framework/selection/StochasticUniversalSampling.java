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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * An alternative to {@link RouletteWheelSelection}
 * as a fitness-proportionate selection strategy.  Ensures that the frequency of selection for
 * each candidate is consistent with its expected frequency of selection.
 * @author Daniel Dyer
 */
public class StochasticUniversalSampling implements SelectionStrategy<Object>
{
    public <S> List<S> select(List<EvaluatedCandidate<S>> population,
                              boolean naturalFitnessScores,
                              int selectionSize,
                              Random rng)
    {
        // Calculate the sum of all fitness values.
        double aggregateFitness = 0;
        for (EvaluatedCandidate<S> candidate : population)
        {
            aggregateFitness += getAdjustedFitness(candidate.getFitness(),
                                                   naturalFitnessScores);
        }

        List<S> selection = new ArrayList<S>(selectionSize);
        // Pick a random offset between 0 and 1 as the starting point for selection.
        double startOffset = rng.nextDouble();
        double cumulativeExpectation = 0;
        int index = 0;
        for (EvaluatedCandidate<S> candidate : population)
        {
            // Calculate the number of times this candidate is expected to
            // be selected on average and add it to the cumulative total
            // of expected frequencies.
            cumulativeExpectation += getAdjustedFitness(candidate.getFitness(),
                                                        naturalFitnessScores) / aggregateFitness * selectionSize;

            // If f is the expected frequency, the candidate will be selected at
            // least as often as floor(f) and at most as often as ceil(f). The
            // actual count depends on the random starting offset.
            while (cumulativeExpectation > startOffset + index)
            {
                selection.add(candidate.getCandidate());
                index++;
            }
        }
        return selection;
    }


    private double getAdjustedFitness(double rawFitness, boolean naturalFitness)
    {
        if (naturalFitness)
        {
            return rawFitness;
        }
        else
        {
            // If standardised fitness is zero we have found the best possible
            // solution.  The evolutionary algorithm should not be continuing
            // after finding it.
            return rawFitness == 0 ? Double.POSITIVE_INFINITY : 1 / rawFitness;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Stochastic Universal Sampling";
    }
}
