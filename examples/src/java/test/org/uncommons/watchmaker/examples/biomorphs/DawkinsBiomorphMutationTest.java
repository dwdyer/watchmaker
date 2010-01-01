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
package org.uncommons.watchmaker.examples.biomorphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for non-random mutation used by Biomorph example application.
 * @author Daniel Dyer
 */
public class DawkinsBiomorphMutationTest
{
    /**
     * Ensure that each possible mutation occurs exactly once.
     */
    @Test
    public void testMutations()
    {
        Biomorph source = new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 1, 2, 8});
        List<Biomorph> originalPopulation = new ArrayList<Biomorph>(18);
        for (int i = 0; i < 18; i++)
        {
            originalPopulation.add(source);
        }
        EvolutionaryOperator<Biomorph> mutation = new DawkinsBiomorphMutation();
        List<Biomorph> mutatedPopulation = mutation.apply(originalPopulation,
                                                          ExamplesTestUtils.getRNG()); // RNG should be ignored.
        assert mutatedPopulation.size() == originalPopulation.size() : "Mutated population is wrong size.";
        // Lazy way of checking for duplicates.  Add all mutations to a set.  If there are any
        // duplicates, the size of the set will be shorter than the list.
        Set<Biomorph> distinctBiomorphs = new HashSet<Biomorph>(mutatedPopulation);
        assert distinctBiomorphs.size() == mutatedPopulation.size() : "Mutated population contains duplicates.";
        // Check for each of the expected mutations (mutations should differ from the original in only one gene).
        assert distinctBiomorphs.contains(new Biomorph(new int[]{5, -4, -3, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-4, -4, -3, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -5, -3, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -3, -3, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -4, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -2, -2, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -3, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -1, -1, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -2, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, 0, 0, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, -1, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 1, 1, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 0, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 2, 2, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 1, 1, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 1, 3, 8})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 1, 2, 7})) : "Missing mutation.";
        assert distinctBiomorphs.contains(new Biomorph(new int[]{-5, -4, -3, -2, -1, 0, 1, 2, 1})) : "Missing mutation.";
    }
}
