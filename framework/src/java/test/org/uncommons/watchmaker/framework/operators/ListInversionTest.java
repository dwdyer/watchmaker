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
import java.util.HashSet;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for the {@link ListInversion} evolutionary operator.
 * @author Daniel Dyer
 */
public class ListInversionTest
{
    @Test
    public void testZeroProbability()
    {
        EvolutionaryOperator<List<Integer>> inversion = new ListInversion<Integer>(Probability.ZERO);
        @SuppressWarnings("unchecked")
        List<List<Integer>> selection = Arrays.asList(Arrays.asList(1, 2, 3));
        List<List<Integer>> evolvedSelection = inversion.apply(selection, FrameworkTestUtils.getRNG());
        assert evolvedSelection.size() == 1 : "Wrong number of individuals after evolution: " + evolvedSelection.size();
        assert evolvedSelection.get(0) == selection.get(0) : "Candidate should not have been modified.";
    }


    @Test
    public void testInversion()
    {
        EvolutionaryOperator<List<Integer>> inversion = new ListInversion<Integer>(Probability.ONE);
        @SuppressWarnings("unchecked")
        List<List<Integer>> selection = Arrays.asList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        for (int i = 0; i < 50; i++) // Try several times so that different random numbers are generated.
        {
            List<List<Integer>> evolvedSelection = inversion.apply(selection, FrameworkTestUtils.getRNG());

            // After inversion, candidate should have same elements but not in the same order.
            assert evolvedSelection.size() == 1 : "Wrong number of individuals after evolution: " + evolvedSelection.size();
            assert evolvedSelection.get(0).size() == selection.get(0).size() : "Candidate length should be unchanged.";
            assert !Arrays.deepEquals(evolvedSelection.get(0).toArray(), selection.get(0).toArray())
                : "Candidate should have been modified.";
            assert new HashSet<Integer>(evolvedSelection.get(0)).size() == 8
                : "Evolved candidate should contain each element once.";
        }
    }

}
