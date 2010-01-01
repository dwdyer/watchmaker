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
import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * An alternative to straightforward fitness-proportionate selection such as that offered
 * by {@link RouletteWheelSelection} and {@link StochasticUniversalSampling}.  Uses the
 * mean population fitness and fitness standard deviation to adjust individual fitness
 * scores.  Early on in an evolutionary algorithm this helps to avoid premature convergence
 * caused by the dominance of one or two relatively fit candidates in a population of mostly
 * unfit individuals.  It also helps to amplify minor fitness differences in a more mature
 * population where the rate of improvement has slowed.
 * @author Daniel Dyer
 */
public class SigmaScaling implements SelectionStrategy<Object>
{
    private final SelectionStrategy<Object> delegate;

    /**
     * Creates a default sigma-scaled selection strategy.
     */
    public SigmaScaling()
    {
        this(new StochasticUniversalSampling());
    }


    /**
     * Creates a sigma-scaled selection strategy that delegates to the specified selection
     * strategy after adjusting individual fitness scores using sigma-scaling.
     * @param delegate The proportionate selector that will be delegated
     * to after fitness scores have been adjusted using sigma-scaling.
     */
    public SigmaScaling(SelectionStrategy<Object> delegate)
    {
        this.delegate = delegate;
    }


    /**
     * {@inheritDoc}
     */
    public <S> List<S> select(List<EvaluatedCandidate<S>> population,
                              boolean naturalFitnessScores,
                              int selectionSize,
                              Random rng)
    {
        DataSet statistics = new DataSet(population.size());
        for (EvaluatedCandidate<S> candidate : population)
        {
            statistics.addValue(candidate.getFitness());
        }

        List<EvaluatedCandidate<S>> scaledPopulation = new ArrayList<EvaluatedCandidate<S>>(population.size());
        for (EvaluatedCandidate<S> candidate : population)
        {
            double scaledFitness = getSigmaScaledFitness(candidate.getFitness(),
                                                         statistics.getArithmeticMean(),
                                                         statistics.getStandardDeviation());
            scaledPopulation.add(new EvaluatedCandidate<S>(candidate.getCandidate(),
                                                           scaledFitness));
        }
        return delegate.select(scaledPopulation, naturalFitnessScores, selectionSize, rng);
    }


    private double getSigmaScaledFitness(double candidateFitness,
                                         double populationMeanFitness,
                                         double fitnessStandardDeviation)
    {
        if (fitnessStandardDeviation == 0)
        {
            return 1;
        }
        else
        {
            double scaledFitness = 1 + (candidateFitness - populationMeanFitness) / (2 * fitnessStandardDeviation);
            // Don't allow negative expected frequencies, use an arbitrary low but still positive
            // frequency of 1 time in 10 for extremely unfit individuals (relative to the remainder
            // of the population).
            return scaledFitness > 0 ? scaledFitness : 0.1;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Sigma Scaling";
    }
}
