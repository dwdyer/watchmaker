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

import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link CachingFitnessEvaluator} wrapper.
 * @author Daniel Dyer
 */
public class CachingFitnessEvaluatorTest
{
    @Test
    public void testCacheMiss()
    {
        FitnessEvaluator<String> evaluator = new CachingFitnessEvaluator<String>(new IncrementingEvaluator(true));
        double fitness = evaluator.getFitness("Test1", Collections.<String>emptyList());
        assert fitness == 1 : "Wrong fitness: " + fitness;
        // Different candidate so shouldn't return a cached value.
        fitness = evaluator.getFitness("Test2", Collections.<String>emptyList());
        assert fitness == 2 : "Wrong fitness: " + fitness;

    }


    @Test
    public void testCacheHit()
    {
        FitnessEvaluator<String> evaluator = new CachingFitnessEvaluator<String>(new IncrementingEvaluator(true));
        double fitness = evaluator.getFitness("Test", Collections.<String>emptyList());
        assert fitness == 1 : "Wrong fitness: " + fitness;
        fitness = evaluator.getFitness("Test", Collections.<String>emptyList());
        // If the value is found in the cache it won't have changed.  If it is recalculated, it will have.
        assert fitness == 1 : "Expected cached value (1), got " + fitness;
    }


    @Test
    public void testNatural()
    {
        FitnessEvaluator<String> evaluator = new CachingFitnessEvaluator<String>(new IncrementingEvaluator(true));
        assert evaluator.isNatural() : "Wrapper for natural scores should also be natural.";
    }


    @Test
    public void testNonNatural()
    {
        FitnessEvaluator<String> evaluator = new CachingFitnessEvaluator<String>(new IncrementingEvaluator(false));
        assert !evaluator.isNatural() : "Wrapper for non-natural scores should also be non-natural.";
    }


    /**
     * This breaks the rules for the caching evaluator in that it is not repeatable
     * (it returns different values when invoked multiple times for the same candidate),
     * but it allows us to see whether we are getting a cached value or a new value.
     */
    private static final class IncrementingEvaluator implements FitnessEvaluator<String>
    {
        private final boolean natural;
        private int count = 0;

        IncrementingEvaluator(boolean natural)
        {
            this.natural = natural;
        }

        public double getFitness(String candidate, List<? extends String> population)
        {
            return ++count;
        }

        public boolean isNatural()
        {
            return natural;
        }
    }
}
