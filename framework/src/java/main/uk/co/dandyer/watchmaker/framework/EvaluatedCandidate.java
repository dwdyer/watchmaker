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
package uk.co.dandyer.watchmaker.framework;

/**
 * Wrapper class for associating a candidate solution with its
 * fitness score.
 * @author Daniel Dyer.
 * @param <T> The candidate type.
 */
public final class EvaluatedCandidate<T>
{
    private final T candidate;
    private final double fitness;
    
    public EvaluatedCandidate(T candidate, double fitness)
    {
        this.candidate = candidate;
        this.fitness = fitness;
    }
    
    public T getCandidate()
    {
        return candidate;
    }
    
    public double getFitness()
    {
        return fitness;
    }
    

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
        if (Double.compare(that.fitness, fitness) != 0)
        {
            return false;
        }
        return candidate.equals(that.candidate);
    }


    @Override
    public int hashCode()
    {
        int result = candidate.hashCode();
        long temp = fitness != +0.0d ? Double.doubleToLongBits(fitness) : 0L;
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}