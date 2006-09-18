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
package org.uncommons.watchmaker.framework.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Base-class for selection strategies that use absolute fitness values to
 * determine a candidate's probability of being selected.
 * @author Daniel Dyer
 */
public abstract class FitnessProportionateSelection implements SelectionStrategy
{
    /**
     * {@inheritDoc}
     * <p>For fitness-proportionate selection this method first makes sure the
     * evaluated population is normalised (higher fitness scores for fitter
     * candidates) and then delegates to
     * {@link #fpSelect(java.util.List, int, java.util.Random)}.
     */
    public final <T> List<T> select(List<EvaluatedCandidate<T>> population,
                                    int selectionSize,
                                    Random rng)
    {
        assert !population.isEmpty() : "Cannot select from an empty population.";

        // Make sure we are dealing with a population with normalised (descending)
        // fitnesses.  Since the population is sorted by fitness this is just a
        // matter of comparing the first and last scores to see whether the list
        // is in ascending or descending order.
        boolean normalised = population.get(0).getFitness() >= population.get(population.size() - 1).getFitness();
        List<EvaluatedCandidate<T>> normalisedPopulation = normalised ? population : normaliseFitnesses(population);
        return fpSelect(normalisedPopulation, selectionSize, rng);
    }


    /**
     * Performs fitness-proportionate Selection on a population with normalised fitness
     * scores.
     */
    protected abstract <T> List<T> fpSelect(List<EvaluatedCandidate<T>> normalisedPopulation,
                                            int selectionSize,
                                            Random rng);

    /**
     * <p>Convert a population that is ranked using a lower-fitness-is-better
     * approach into a population in which the best candidate has the highest
     * score and the weakest candidate has the lowest.  In other words, the
     * ordering of the candidates remains the same but their scores are
     * converted so that they can be used for fitness-proportionate selection).</p>
     * <p>WARNING: Do not apply this method to a population that is already
     * normalised.</p>
     * @param population A sorted population in which the fittest individuals
     * have lower fitness scores than the weaker individuals.  That means the
     * first individual in the list both has the lowest fitness score and is
     * the fittest individual.
     */
    private <T> List<EvaluatedCandidate<T>> normaliseFitnesses(List<EvaluatedCandidate<T>> population)
    {
        List<EvaluatedCandidate<T>> normalisedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        double totalInverseFitnesses = 0d;
        for (EvaluatedCandidate<T> candidate : population)
        {
            totalInverseFitnesses += 1d / candidate.getFitness();
        }
        for (EvaluatedCandidate<T> candidate : population)
        {
            double inverseFitness = 1d / candidate.getFitness();
            double normalisedFitness = inverseFitness / totalInverseFitnesses;
            normalisedPopulation.add(new EvaluatedCandidate<T>(candidate.getCandidate(),
                                                               normalisedFitness));
        }
        return normalisedPopulation;
    }
}
