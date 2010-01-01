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

/**
 * Immutable wrapper class for associating a candidate solution with its
 * fitness score.
 * @author Daniel Dyer.
 * @param <T> The candidate type.
 */
public final class EvaluatedCandidate<T> implements Comparable<EvaluatedCandidate<T>>
{
    private final T candidate;
    private final double fitness;


    /**
     * @param candidate The evolved candidate.
     * @param fitness The candidates fitness score.
     */
    public EvaluatedCandidate(T candidate, double fitness)
    {
        if (fitness < 0)
        {
            throw new IllegalArgumentException("Fitness score must be greater than or equal to zero.");
        }
        this.candidate = candidate;
        this.fitness = fitness;
    }


    /**
     * @return The evolved candidate solution.
     */
    public T getCandidate()
    {
        return candidate;
    }


    /**
     * @return The fitness score for the associated candidate.
     */
    public double getFitness()
    {
        return fitness;
    }


    /**
     * Compares this candidate's fitness score with that of the specified
     * candidate.
     * @param evaluatedCandidate The candidate to compare scores with.
     * @return -1, 0 or 1 if this candidate's score is less than, equal to,
     * or greater than that of the specified candidate.  The comparison applies
     * to the raw numerical score and does not consider whether that score is
     * a natural fitness score or not.
     */
    public int compareTo(EvaluatedCandidate<T> evaluatedCandidate)
    {
        return Double.compare(fitness, evaluatedCandidate.getFitness());
    }


    /**
     * Over-ridden to be consistent with {@link #compareTo(EvaluatedCandidate)}.
     * @param o The object to check for equality.
     * @return true If this object is logically equivalent to {code o}.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final EvaluatedCandidate<?> that = (EvaluatedCandidate<?>) o;
        return Double.compare(that.getFitness(), fitness) == 0;
    }


    /**
     * Over-ridden to be consistent with {@link #equals(Object)}.
     * @return This object's hash code.
     */
    @Override
    public int hashCode()
    {
        final long temp = fitness == 0.0d ? 0L : Double.doubleToLongBits(fitness);
        return (int) (temp ^ (temp >>> 32));
    }
}
