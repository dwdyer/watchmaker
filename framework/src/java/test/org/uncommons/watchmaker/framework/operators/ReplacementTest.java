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
package org.uncommons.watchmaker.framework.operators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Unit test for the {@link Replacement} evolutionary operator.
 * @author Daniel Dyer
 */
public class ReplacementTest
{
    @Test
    public void testReplacement()
    {
        IntegerFactory factory = new IntegerFactory();
        List<Integer> candidates = Arrays.asList(10, 11, 12);
        Replacement<Integer> replacement = new Replacement<Integer>(factory,
                                                                    Probability.ONE);
        // Numbers will be replaced with lower ones.
        List<Integer> output = replacement.apply(candidates, FrameworkTestUtils.getRNG());
        assert output.size() == candidates.size() : "Candidate list should be same size.";
        assert !output.contains(10) : "Candidate should have been replaced.";
        assert !output.contains(11) : "Candidate should have been replaced.";
        assert !output.contains(12) : "Candidate should have been replaced.";
    }


    @Test
    public void testZeroProbability()
    {
        IntegerFactory factory = new IntegerFactory();
        List<Integer> candidates = Arrays.asList(10, 11, 12);
        Replacement<Integer> replacement = new Replacement<Integer>(factory,
                                                                    Probability.ZERO);
        // Numbers will be replaced with lower ones.
        List<Integer> output = replacement.apply(candidates, FrameworkTestUtils.getRNG());
        assert output.size() == candidates.size() : "Candidate list should be same size.";
        assert output.contains(10) : "Candidate should not have been replaced.";
        assert output.contains(11) : "Candidate should not have been replaced.";
        assert output.contains(12) : "Candidate should not have been replaced.";
    }


    /**
     *  Non-random factory, for test purposes.
     */
    protected static final class IntegerFactory extends AbstractCandidateFactory<Integer>
    {
        private int count = 0;

        public Integer generateRandomCandidate(Random rng)
        {
            return ++count;
        }
    }

}
