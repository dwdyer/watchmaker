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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;

/**
 * A general purpose evolution implementation that can be customised by plugging
 * in different {@link PopulationEvolution} implementations.
 *
 * @param <T> The type of entity that is to be evolved.
 * @author Daniel Dyer
 * @see CandidateFactory
 * @see PopulationEvolution
 * @see FitnessEvaluator
 */
public class EvolutionEngine<T>
{
    private final List<EvolutionObserver<? super T>> observers = new LinkedList<EvolutionObserver<? super T>>();

    private final Random rng;
    private final CandidateFactory<T> candidateFactory;
    private final PopulationEvolution<T> evolution;
    private final boolean naturalFitness;

    private List<TerminationCondition> satisfiedTerminationConditions;


    /**
     * Creates a new evolution engine by specifying the various components required by
     * an evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolution A preconfigured strategy for evolving a population.
     * @param naturalFitness If true, indicates that higher fitness values mean fitter
     * individuals.  If false, indicates that fitter individuals will have lower scores.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     * @see #createGenerationalEvolutionEngine(CandidateFactory, EvolutionaryOperator, FitnessEvaluator, SelectionStrategy, Random, boolean)
     * @see #createInteractiveEvolutionEngine(CandidateFactory, EvolutionaryOperator, InteractiveSelection, Random)
     */
    public EvolutionEngine(CandidateFactory<T> candidateFactory,
                           PopulationEvolution<T> evolution,
                           boolean naturalFitness,
                           Random rng)
    {
        this.candidateFactory = candidateFactory;
        this.evolution = evolution;
        this.naturalFitness = naturalFitness;
        this.rng = rng;
    }


    /**
     * <p>Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return the fittest candidate from the final generation.  To return the
     * entire population rather than just the fittest candidate, use the
     * {@link #evolvePopulation(int, int, TerminationCondition[])} method instead.</p>
     *
     * <p><em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em></p>
     *
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int, int, Collection, TerminationCondition[])
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
     * <p>Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return the fittest candidate from the final generation.  To return the
     * entire population rather than just the fittest candidate, use the
     * {@link #evolvePopulation(int, int, Collection, TerminationCondition[])}
     * method instead.</p>
     *
     * <p><em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the best individual found so far).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em></p>
     *
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int,int,TerminationCondition[])
     */
    public T evolve(int populationSize,
                    int eliteCount,
                    Collection<T> seedCandidates,
                    TerminationCondition... conditions)
    {
        return evolvePopulation(populationSize,
                                eliteCount,
                                seedCandidates,
                                conditions).get(0).getCandidate();
    }


    /**
     * <p>Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return all of the candidates from the final generation.  To return just the
     * fittest candidate rather than the entire population, use the
     * {@link #evolve(int, int, TerminationCondition[])} method instead.</p>
     *
     * <p><em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the members of the most recent
     * generation).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em></p>

     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The final population of individuals and their fitness scores, sorted in
     * descending order of fitness.
     * @see #evolve(int, int, Collection, TerminationCondition[])
     * @see #evolvePopulation(int, int, Collection, TerminationCondition[])
     */
    public List<EvaluatedCandidate<T>> evolvePopulation(int populationSize,
                                                        int eliteCount,
                                                        TerminationCondition... conditions)
    {
        return evolvePopulation(populationSize,
                                eliteCount,
                                Collections.<T>emptySet(),
                                conditions);
    }


    /**
     * <p>Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return all of the candidates from the final generation.  To return just the
     * fittest candidate rather than the entire population, use the
     * {@link #evolve(int, int, Collection, TerminationCondition[])} method instead.</p>
     *
     * <p><em>If you interrupt the request thread before this method returns, the
     * method will return prematurely (with the members of the most recent
     * generation).
     * After returning in this way, the current thread's interrupted flag
     * will be set.  It is preferable to use an appropritate
     * {@link TerminationCondition} rather than interrupting the evolution in
     * this way.</em></p>
     *
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The final population of individuals and their fitness scores, sorted in
     * descending order of fitness.
     * @see #evolve(int, int, Collection, TerminationCondition[])
     * @see #evolvePopulation(int, int, Collection, TerminationCondition[])
     */
    public List<EvaluatedCandidate<T>> evolvePopulation(int populationSize,
                                                        int eliteCount,
                                                        Collection<T> seedCandidates,
                                                        TerminationCondition... conditions)
    {
        if (eliteCount < 0 || eliteCount >= populationSize)
        {
            throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
        }
        if (conditions.length == 0)
        {
            throw new IllegalArgumentException("At least one TerminationCondition must be specified.");
        }

        satisfiedTerminationConditions = null;
        int currentGenerationIndex = 0;
        long startTime = System.currentTimeMillis();

        List<T> population = candidateFactory.generateInitialPopulation(populationSize,
                                                                        seedCandidates,
                                                                        rng);

        // Calculate the fitness scores for each member of the initial population.
        List<EvaluatedCandidate<T>> evaluatedPopulation = evolution.evaluatePopulation(population);
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, naturalFitness);
        PopulationData<T> data = EvolutionUtils.getPopulationData(evaluatedPopulation,
                                                                  naturalFitness,
                                                                  eliteCount,
                                                                  currentGenerationIndex,
                                                                  startTime);
        // Notify observers of the state of the population.
        notifyPopulationChange(data);

        List<TerminationCondition> satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
        while (satisfiedConditions == null)
        {
            ++currentGenerationIndex;
            evaluatedPopulation = evolution.evolvePopulation(evaluatedPopulation, eliteCount, rng);
            EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, naturalFitness);
            data = EvolutionUtils.getPopulationData(evaluatedPopulation,
                                                    naturalFitness,
                                                    eliteCount,
                                                    currentGenerationIndex,
                                                    startTime);
            // Notify observers of the state of the population.
            notifyPopulationChange(data);
            satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
        }
        this.satisfiedTerminationConditions = satisfiedConditions;
        return evaluatedPopulation;
    }


    /**
     * <p>Returns a list of all {@link TerminationCondition}s that are satisfied by the current
     * state of the evolution engine.  Usually this list will contain only one item, but it
     * is possible that mutliple termination conditions will become satisfied at the same
     * time.  In this case the condition objects in the list will be in the same order that
     * they were specified when passed to the engine.</p>
     *
     * <p>If the evolution has not yet terminated (either because it is still in progress or
     * because it hasn't even been started) then an IllegalStateException will be thrown.</p>
     *
     * <p>If the evolution terminated because the request thread was interrupted before any
     * termination conditions were satisfied then this method will return an empty list.</p>
     *
     * @throws IllegalStateException If this method is invoked on an evolution engine before
     * evolution is started or while it is still in progress.
     *
     * @return A list of statisfied conditions.  The list is guaranteed to be non-null.  The
     * list may be empty because it is possible for evolution to terminate without any conditions
     * being matched.  The only situation in which this occurs is when the request thread is
     * interrupted.
     */
    public List<TerminationCondition> getSatisfiedTerminationConditions()
    {
        if (satisfiedTerminationConditions == null)
        {
            throw new IllegalStateException("EvolutionEngine has not terminated.");
        }
        else
        {
            return Collections.unmodifiableList(satisfiedTerminationConditions);
        }
    }


    /**
     * Adds a listener to receive status updates on the evolution progress.
     * Updates are dispatched synchronously on the request thread.  Observers should
     * complete their processing and return in a timely manner to avoid holding up
     * the evolution.
     * @param observer An evolution observer call-back.
     * @see #removeEvolutionObserver(EvolutionObserver) 
     */
    public void addEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.add(observer);
    }


    /**
     * Removes an evolution progress listener.
     * @param observer An evolution observer call-back.
     * @see #addEvolutionObserver(EvolutionObserver)
     */
    public void removeEvolutionObserver(EvolutionObserver<? super T> observer)
    {
        observers.remove(observer);
    }


    /**
     * Send the population data to all registered observers.
     * @param data Information about the current state of the population.
     */
    private void notifyPopulationChange(PopulationData<T> data)
    {
        for (EvolutionObserver<? super T> observer : observers)
        {
            observer.populationUpdate(data);
        }
    }



    /**
     * Creates a new evolution engine by specifying the various components required by
     * a generational evolutionary algorithm.
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
    public static <T> EvolutionEngine<T> createGenerationalEvolutionEngine(CandidateFactory<T> candidateFactory,
                                                                           EvolutionaryOperator<T> evolutionScheme,
                                                                           FitnessEvaluator<? super T> fitnessEvaluator,
                                                                           SelectionStrategy<? super T> selectionStrategy,
                                                                           Random rng,
                                                                           boolean multiThreaded)
    {
        PopulationEvolution<T> evolution = new GenerationalEvolution<T>(evolutionScheme,
                                                                       fitnessEvaluator,
                                                                       selectionStrategy,
                                                                       multiThreaded);
        return new EvolutionEngine<T>(candidateFactory,
                                      evolution,
                                      fitnessEvaluator.isNatural(),
                                      rng);
    }



    /**
     * Creates a new generational evolution engine for an interactive evolutionary algorithm.
     * It is not necessary to specify a fitness evaluator for interactive evolution.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param selectionStrategy Interactive selection strategy configured with appropriate
     * console.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public static <T> EvolutionEngine<T> createInteractiveEvolutionEngine(CandidateFactory<T> candidateFactory,
                                                                          EvolutionaryOperator<T> evolutionScheme,
                                                                          final InteractiveSelection<T> selectionStrategy,
                                                                          final Random rng)
    {
        FitnessEvaluator<? super T> fitnessEvaluator = new NullFitnessEvaluator();
        PopulationEvolution<T> evolution = new GenerationalEvolution<T>(evolutionScheme,
                                                                       fitnessEvaluator,
                                                                       selectionStrategy,
                                                                       false);
        return new EvolutionEngine<T>(candidateFactory,
                                      evolution,
                                      fitnessEvaluator.isNatural(),
                                      rng)
        {
            @Override
            public T evolve(int populationSize,
                            int eliteCount,
                            Collection<T> seedCandidates,
                            TerminationCondition... conditions)
            {
                List<EvaluatedCandidate<T>> evaluatedPopulation = evolvePopulation(populationSize,
                                                                                   eliteCount,
                                                                                   seedCandidates,
                                                                                   conditions);
                // Once we have completed the final generation, we need to pick one of
                // the individuals in the population to return as the result of the
                // algorithm.  Usually we would just need to pick the fittest individual
                // and return that.  However, this doesn't work very well with interactive
                // evolutionary algorithms because all individuals have a nominal fitness
                // of zero and the population has been evolved one final time since the
                // user last expressed a selection preference.
                //
                // The solution is to always use the selection strategy to pick the candidate
                // to return. This allows the user gets to have the final say over which
                // individual is chosen as the "best" from the final evolved generation.
                return selectionStrategy.select(evaluatedPopulation, true, 1, rng).get(0);
            }
        };
    }



    /**
     * Creates a new evolution engine by specifying the various components required by
     * a steady-state evolutionary algorithm.
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
    public static <T> EvolutionEngine<T> createSteadyStateEvolutionEngine(CandidateFactory<T> candidateFactory,
                                                                          EvolutionaryOperator<T> evolutionScheme,
                                                                          FitnessEvaluator<? super T> fitnessEvaluator,
                                                                          SelectionStrategy<? super T> selectionStrategy,
                                                                          int selectionSize,
                                                                          Random rng)
    {
        PopulationEvolution<T> evolution = new SteadyStateEvolution<T>(evolutionScheme,
                                                                       fitnessEvaluator,
                                                                       selectionStrategy,
                                                                       selectionSize);
        return new EvolutionEngine<T>(candidateFactory,
                                      evolution,
                                      fitnessEvaluator.isNatural(),
                                      rng);
    }

}
