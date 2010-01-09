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
package org.uncommons.watchmaker.framework;

import java.util.List;
import java.util.Random;

/**
 * An implementation of steady-state evolution, which is a type of evolutionary algorithm
 * where a population is changed incrementally, with one individual evolved at a time.  This
 * differs from {@link GenerationalEvolutionEngine} in which the entire population is evolved in
 * parallel.
 *
 * @param <T> The type of entity that is to be evolved.
 * @see GenerationalEvolutionEngine
 * @see EvolutionStrategyEngine
 * @author Daniel Dyer
 */
public class SteadyStateEvolutionEngine<T> extends AbstractEvolutionEngine<T>
{
    private final EvolutionaryOperator<T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;
    private final int selectionSize;
    private final boolean forceSingleCandidateUpdate;

    /**
     * Create a steady-state evolution strategy in which one or more (usually just one) evolved
     * offspring replace randomly-chosen individuals.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
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
     * @param forceSingleCandidateUpdate Some evolutionary operators, specifically cross-over
     * operators, generate more than one evolved individual.  A true steady-state algorithm will
     * only replace one individual at a time.  Setting this parameter to true forces the evolution
     * to discard any additional generated offspring so that for each iteration of the algorithm
     * there is only one updated individual.  This allows cross-over operators that were designed
     * for generational evolutionary algorithms to be reused for steady-state evolution.  A more
     * efficient, but less straightforward, alternative would be to implement a steady-state-specific
     * cross-over operator that returns only a single evolved individual.  Setting this parameter to
     * false permits multiple candidates to be replaced per iteration, depending on the specifics of
     * the evolutionary operator(s).
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public SteadyStateEvolutionEngine(CandidateFactory<T> candidateFactory,
                                      EvolutionaryOperator<T> evolutionScheme,
                                      FitnessEvaluator<? super T> fitnessEvaluator,
                                      SelectionStrategy<? super T> selectionStrategy,
                                      int selectionSize,
                                      boolean forceSingleCandidateUpdate,
                                      Random rng)
    {
        super(candidateFactory, fitnessEvaluator, rng);
        this.fitnessEvaluator = fitnessEvaluator;
        this.evolutionScheme = evolutionScheme;
        this.selectionStrategy = selectionStrategy;
        this.selectionSize = selectionSize;
        this.forceSingleCandidateUpdate = forceSingleCandidateUpdate;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<EvaluatedCandidate<T>> nextEvolutionStep(List<EvaluatedCandidate<T>> evaluatedPopulation,
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
        // If this is strictly steady-state (only one updated individual per iteration), then we can't keep multiple
        // evolved individuals, so just pick one at random and use that.
        if (newCandidates.size() > 1 && forceSingleCandidateUpdate)
        {
            // Replace a randomly selected individual, but not one of the "elite" individuals at the
            // beginning of the sorted population.
            existingPopulation.set(rng.nextInt(existingPopulation.size() - eliteCount) + eliteCount,
                                   newCandidates.get(rng.nextInt(newCandidates.size())));
        }
        else
        {
            for (EvaluatedCandidate<T> candidate : newCandidates)
            {
                // Replace a randomly selected individual, but not one of the "elite" individuals at the
                // beginning of the sorted population.
                existingPopulation.set(rng.nextInt(existingPopulation.size() - eliteCount) + eliteCount, candidate);
            }
        }
    }
}
