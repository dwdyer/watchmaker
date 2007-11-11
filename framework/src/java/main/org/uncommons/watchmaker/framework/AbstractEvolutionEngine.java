// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.stats.PopulationDataSet;

/**
 * Base class for {@link EvolutionEngine} implementations.
 * @param <T> The type of entity evolved by the evolution engine.
 * @author Daniel Dyer
 * @see CandidateFactory
 * @see FitnessEvaluator
 * @see SelectionStrategy
 * @see EvolutionaryOperator 
 */
public abstract class AbstractEvolutionEngine<T> implements EvolutionEngine<T>
{
    private final List<EvolutionObserver<T>> observers = new LinkedList<EvolutionObserver<T>>();
    private final Random rng;
    private final CandidateFactory<T> candidateFactory;
    private final EvolutionaryOperator<? super T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;

    private long startTime;
    private int currentGenerationIndex;


    /**
     * Creates a new evolution engine by specifying the various components required by
     * an evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param selectionStrategy A strategy for selecting which candidates survive to
     * be evolved.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    protected AbstractEvolutionEngine(CandidateFactory<T> candidateFactory,
                                      EvolutionaryOperator<? super T> evolutionScheme,
                                      FitnessEvaluator<? super T> fitnessEvaluator,
                                      SelectionStrategy<? super T> selectionStrategy,
                                      Random rng)
    {
        this.candidateFactory = candidateFactory;
        this.evolutionScheme = evolutionScheme;
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.rng = rng;
    }


    /**
     * Provides sub-classes with access to the fitness evaluator.
     * @return A reference to the fitness evaluator configured for this engine.
     */
    protected final FitnessEvaluator<? super T> getFitnessEvaluator()
    {
        return fitnessEvaluator;
    }


    /**
     * {@inheritDoc}
     *
     * <em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em>
     */
    public T evolve(int populationSize,
                    int eliteCount,
                    TerminationCondition... conditions)
    {
        return evolve(populationSize,
                      eliteCount,
                      Collections.<T>emptySet(),
                      conditions);
    }


    /**
     * {@inheritDoc}
     *
     * <em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em>
     */
    public T evolve(int populationSize,
                    int eliteCount,
                    Collection<T> seedCandidates,
                    TerminationCondition... conditions)
    {
        if (eliteCount < 0 || eliteCount >= populationSize)
        {
            throw new IllegalArgumentException("Elite count must be non-zero and less than population size.");
        }

        currentGenerationIndex = 0;
        startTime = System.currentTimeMillis();

        // Don't use the list returned by the factory, because the type might be too specific.
        // Instead copy the contents into a list of the desired type.
        List<T> population = new ArrayList<T>(candidateFactory.generateInitialPopulation(populationSize,
                                                                                         seedCandidates,
                                                                                         rng));
        // Calculate the fitness scores for each member of the initial population.
        List<EvaluatedCandidate<T>> evaluatedPopulation = evaluatePopulation(population);
        PopulationData<T> data = getPopulationData(evaluatedPopulation);
        // Notify observers of the state of the population.
        notifyPopulationChange(data);

        while (shouldContinue(data, conditions))
        {
            ++currentGenerationIndex;
            population = createNextGeneration(evaluatedPopulation, eliteCount);
            evaluatedPopulation = evaluatePopulation(population);
            data = getPopulationData(evaluatedPopulation);
            // Notify observers of the state of the population.
            notifyPopulationChange(data);
        }
        // Once we have completed the final generation, we need to pick one of
        // the individuals in the population to return as the result of the
        // algorithm.  Usually we would just need to pick the fittest individual
        // and return that.  However, this doesn't work very well with interactive
        // evolutionary algorithms because all individuals have a nominal fitness
        // of zero and the population has been evolved one final time since the
        // user last expressed a selection prefence.
        // 
        // The solution is to always use the selection strategy to pick the candidate
        // to return.  We only let it select from the set of candidates with the
        // highest fitness score.  In the case that there is a clear fittest
        // individual, there will be only one member of this set and the "winner" is
        // clear.
        //
        // In other situations, there may be multiple "best" individuals with equal
        // fitness scores.  In this case, any other individuals (those with lesser
        // fitness scores) are discarded and selection is applied to the remainder.
        // In the case of non-interactive selection, it doesn't really matter which
        // of these fittest individuals is returned since they are all equivalent
        // from a fitness perspective.
        //
        // This approach works well for interactive evolutionary algorithms.  Because
        // all fitness scores are equal, no individuals are discared before selection
        // and the user gets to have the final say over which individual is chosen as
        // the "best" from the final evolved generation.

        // The evaluated population is sorted in order of fitness, so we can just scan
        // the list and retain all individuals with a fitness equal to the first
        // individual.
        List<EvaluatedCandidate<T>> fittest = new ArrayList<EvaluatedCandidate<T>>(evaluatedPopulation.size());
        double bestFitness = evaluatedPopulation.get(0).getFitness();
        for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
        {
            if ((fitnessEvaluator.isNatural() && candidate.getFitness() >= bestFitness)
                || (!fitnessEvaluator.isNatural() && candidate.getFitness() <= bestFitness))
            {
                fittest.add(candidate);
            }
            else
            {
                break;
            }
        }
        return selectionStrategy.select(fittest, fitnessEvaluator.isNatural(), 1, rng).get(0);
    }


    private boolean shouldContinue(PopulationData<T> data,
                                   TerminationCondition... conditions)
    {
        // If the thread has been interrupted, we should abort and return whatever
        // result we currently have.
        if (Thread.currentThread().isInterrupted())
        {
            return false;
        }
        // Otherwise check the termination conditions for the evolution.
        for (TerminationCondition condition : conditions)
        {
            if (condition.shouldTerminate(data))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     * @param population The population of evolved candidate to be evaluated.
     * @return A list containing each of the candidates with an attached fitness
     * score.
     */
    protected abstract List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population);


    /**
     * Evolve the specified evaluated population (the current generation).
     * and return the resultant population (the next generation).
     * @param evaluatedPopulation The population of evolved candidates, with
     * fitness scores attached.
     * @param eliteCount The number of the most fit candidates that will be
     * preserved unchanged in the next generation.
     * @return The next generation of evolved individuals.
     */
    private List<T> createNextGeneration(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                         int eliteCount)
    {
        List<T> population = new ArrayList<T>(evaluatedPopulation.size());

        // First perform any elitist selection.
        List<T> elite = new ArrayList<T>(eliteCount);
        Iterator<EvaluatedCandidate<T>> iterator = evaluatedPopulation.iterator();
        while (elite.size() < eliteCount)
        {
            elite.add(iterator.next().getCandidate());
        }
        // Then select candidates that will be operated on to create the evolved
        // portion of the next generation.
        population.addAll(selectionStrategy.select(evaluatedPopulation,
                                                   fitnessEvaluator.isNatural(),
                                                   evaluatedPopulation.size() - eliteCount,
                                                   rng));
        // Then evolve the population.
        population = evolutionScheme.apply(population, rng);
        // When the evolution is finished, add the elite to the population.
        population.addAll(elite);
        assert population.size() == evaluatedPopulation.size() : "Population size is not consistent.";
        return population;
    }


    /**
     * {@inheritDoc}
     */
    public void addEvolutionObserver(EvolutionObserver<T> observer)
    {
        observers.add(observer);
    }


    /**
     * {@inheritDoc}
     */
    public void removeEvolutionObserver(EvolutionObserver<T> observer)
    {
        observers.remove(observer);
    }


    /**
     * Send the population data to all registered observers.
     * @param data Information about the current state of the population.
     */
    private void notifyPopulationChange(PopulationData<T> data)
    {
        for (EvolutionObserver<T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }


    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     * @return Statistics about the current generation of evolved individuals.
     */
    private PopulationData<T> getPopulationData(List<EvaluatedCandidate<T>> evaluatedPopulation)
    {
        double[] fitnesses = new double[evaluatedPopulation.size()];
        int index = -1;
        for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
        {
            fitnesses[++index] = candidate.getFitness();
        }
        PopulationDataSet stats = new PopulationDataSet(fitnesses);
        return new PopulationData<T>(evaluatedPopulation.get(0).getCandidate(),
                                     evaluatedPopulation.get(0).getFitness(),
                                     stats.getArithmeticMean(),
                                     stats.getStandardDeviation(),
                                     stats.getSize(),
                                     currentGenerationIndex,
                                     System.currentTimeMillis() - startTime);
    }
}
