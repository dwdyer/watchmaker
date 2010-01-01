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

import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>A wrapper that provides caching for {@link FitnessEvaluator} implementations.  The
 * results of fitness evaluations are stored in a cache so that if the same candidate
 * is evaluated twice, the expense of the fitness calculation can be avoided the second
 * time.  The cache uses weak references in order to avoid memory leakage.</p>
 *
 * <p>Caching of fitness values can be a useful optimisation in situations where the
 * fitness evaluation is expensive and there is a possibility that some candidates
 * will survive from generation to generation unmodified.  Programs that use elitism
 * are one example of candidates surviving unmodified.  Another scenario is when the
 * configured evolutionary operator does not always modify every candidate in the
 * population for every generation.</p>
 *
 * <p>Unmodified candidates are identified by reference equality.  This is a valid
 * assumption since evolutionary operators are required to return distinct objects,
 * except when the candidate is unaffected by the evolution, as per the contract of the
 * {@link EvolutionaryOperator} interface.  In other words, the Watchmaker Framework
 * treats candidate representations as immutable even when that is not strictly the case.</p>
 * 
 * <p>Caching of fitness scores is provided as an option rather than as the default
 * Watchmaker Framework behaviour because caching is only valid when fitness evaluations
 * are <em>isolated</em> and repeatable.  An isolated fitness evaluation is one where the
 * result depends only upon the candidate being evaluated.  This is not the case when
 * candidates are evaluated against the other members of the population.  So unless the
 * fitness evaluator ignores the second parameter to the
 * {@link #getFitness(Object, List)} method, caching must not be used.</p>
 * @param <T> The type of evolvable entity that can be evaluated.
 * 
 * @author Daniel Dyer
 */
public class CachingFitnessEvaluator<T> implements FitnessEvaluator<T>
{
    private final FitnessEvaluator<T> delegate;

    // This field is marked as transient, even though the class is not Serializable, because
    // Terracotta will respect the fact it is transient and not try to share it.
    private final transient ConcurrentMap<T, Double> cache = new MapMaker().weakKeys().makeMap();


    /**
     * Creates a caching fitness evaluator that wraps the specified evaluator.
     * @param delegate The fitness evaluator that performs the actual calculations.
     */
    public CachingFitnessEvaluator(FitnessEvaluator<T> delegate)
    {
        this.delegate = delegate;
    }


    /**
     * {@inheritDoc}
     *
     * <p>This implementation performs a cache look-up every time it is invoked.  If the
     * fitness evaluator has already calculated the fitness score for the specified
     * candidate that score is returned without delegating to the wrapped evaluator.</p>
     */
    public double getFitness(T candidate, List<? extends T> population)
    {
        Double fitness = cache.get(candidate);
        if (fitness == null)
        {
            fitness = delegate.getFitness(candidate, population);
            cache.put(candidate, fitness);
        }
        return fitness;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isNatural()
    {
        return delegate.isNatural();
    }
}
