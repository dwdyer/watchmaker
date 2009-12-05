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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An implementation of steady-state evolution, which is a type of evolutionary algorithm
 * where a population is changed incrementally, with one individual evolved at a time.  This
 * differs from {@link GenerationalEvolution} in which the entire population is evolved in
 * parallel.
 * @see GenerationalEvolution
 * @author Daniel Dyer
 */
public class SteadyStateEvolution<T> implements PopulationEvolution<T>
{
    private final EvolutionaryOperator<T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;
    private final int selectionSize;


    /**
     * Create a steady-state evolution strategy in which one or more evolved offspring
     * replace randomly-chosen individuals.
     * @param evolutionScheme The evolutionary operator that modifies the population.  The
     * number of candidates used as input is controlled by the {@code selectionSize} parameter.
     * The number of candidates that will be outputted depends on the implementation.  Typically
     * it will be the same as the input size, but this is not necessary.  In fact, for steady-state
     * evolution, it is typical that the output size is always 1, regardless of the input size, so
     * that only one member of the population is replaced at a time.  To acheive this using cross-over
     * requires a cross-over implementation that returns only one offspring, rather than the normal
     * two.
     * @param fitnessEvaluator The fitness function.
     * @param selectionStrategy The strategy for selecting which candidate(s) will be
     * the parent(s) when evolving individuals.
     * @param selectionSize How many parent candidates are required by the evolution scheme.
     * This controls how many individuals will be provided to the evolutionary operator at
     * each iteration. If you are just using mutation, this will typically be 1.  For
     * cross-over, two separate parents are required, so this must be set to 2.
     */
    public SteadyStateEvolution(EvolutionaryOperator<T> evolutionScheme,
                                FitnessEvaluator<? super T> fitnessEvaluator,                                
                                SelectionStrategy<? super T> selectionStrategy,
                                int selectionSize)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.evolutionScheme = evolutionScheme;
        this.selectionStrategy = selectionStrategy;
        this.selectionSize = selectionSize;
    }


    /**
     * {@inheritDoc}
     */
    public List<EvaluatedCandidate<T>> evolvePopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                        int eliteCount,
                                                        Random rng)
    {
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
        List<T> selectedCandidates = selectionStrategy.select(evaluatedPopulation,
                                                              fitnessEvaluator.isNatural(),
                                                              selectionSize,
                                                              rng);
        List<EvaluatedCandidate<T>> offspring = evaluatePopulation(evolutionScheme.apply(selectedCandidates, rng));

        doReplacement(evaluatedPopulation, offspring, eliteCount, rng);

        return evaluatedPopulation;
    }


    /**
     * Add the offspring to the population, removing the same number of existing individuals to make
     * space for them.
     * This method randomly chooses which individuals should be replaced, but it can be over-ridden
     * in sub-classes if alternative behaviour is required.
     * @param existingPopulation The full popultation, sorted in descending order of fitness.
     * @param newCandidates The (unsorted) newly-created individual(s) that should replace existing members
     * of the population.
     * @param eliteCount The number of the fittest individuals that should be exempt from being replaced.
     * @param rng A source of randomness.
     */
    protected void doReplacement(List<EvaluatedCandidate<T>> existingPopulation,
                                 List<EvaluatedCandidate<T>> newCandidates,
                                 int eliteCount,
                                 Random rng)
    {
        assert newCandidates.size() < existingPopulation.size() - eliteCount : "Too many new candidates for replacement.";
        for (EvaluatedCandidate<T> candidate : newCandidates)
        {
            // Replace a randomly selected individual, but not one of the "elite" individuals at the
            // beginning of the sorted population.
            existingPopulation.set(rng.nextInt(existingPopulation.size() - eliteCount) + eliteCount, candidate);
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate, population)));
        }
        return evaluatedPopulation;
    }
}
