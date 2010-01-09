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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * General purpose engine for implementing Evolution Strategies.  Both (?+?) and (?,?)
 * strategies are supported (choose which to use by setting the boolean constructor parameter).
 * @author Daniel Dyer
 */
public class EvolutionStrategyEngine<T> extends AbstractEvolutionEngine<T>
{
    private final EvolutionaryOperator<T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final boolean plusSelection;
    private final int offspringMultiplier;


    /**
     * Creates a new engine for an evolution strategy.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param plusSelection If true this object implements a (?+?) evolution strategy rather
     * than (?,?).  With plus-selection the parents are eligible for survival.  With
     * comma-selection only the offspring survive.
     * @param offspringMultiplier How many offspring to create for each member of the parent
     * population.  This parameter effectively defines a multiplier for ? that gives ?.
     * We define ? in this indirect way because we don't know the value of ? until
     * it is passed as an argument to one of the evolve methods. 
     * For a 1+1 ES this parameter would be set to one.  For other evolution strategies
     * a higher value might be better. Eiben & Smith suggest 7 as a good value. 
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public EvolutionStrategyEngine(CandidateFactory<T> candidateFactory,
                                   EvolutionaryOperator<T> evolutionScheme,
                                   FitnessEvaluator<? super T> fitnessEvaluator,
                                   boolean plusSelection,
                                   int offspringMultiplier,
                                   Random rng)
    {
        super(candidateFactory, fitnessEvaluator, rng);
        this.evolutionScheme = evolutionScheme;
        this.fitnessEvaluator = fitnessEvaluator;
        this.plusSelection = plusSelection;
        this.offspringMultiplier = offspringMultiplier;
    }


    @Override
    protected List<EvaluatedCandidate<T>> nextEvolutionStep(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                            int eliteCount,
                                                            Random rng)
    {
        // Select candidates that will be operated on to create the offspring.
        int offspringCount = offspringMultiplier * evaluatedPopulation.size();
        List<T> parents = new ArrayList<T>(offspringCount);
        for (int i = 0; i < offspringCount; i++)
        {
            parents.add(evaluatedPopulation.get(rng.nextInt(evaluatedPopulation.size())).getCandidate());
        }

        // Then evolve the parents.
        List<T> offspring = evolutionScheme.apply(parents, rng);

        List<EvaluatedCandidate<T>> evaluatedOffspring = evaluatePopulation(offspring);
        if (plusSelection) // Plus-selection means parents are considered for survival as well as offspring.
        {
            evaluatedOffspring.addAll(evaluatedPopulation);
        }
        EvolutionUtils.sortEvaluatedPopulation(evaluatedOffspring, fitnessEvaluator.isNatural());
        // Retain the fittest of the candidates that are eligible for survival.
        return evaluatedOffspring.subList(0, evaluatedPopulation.size());
    }
}
