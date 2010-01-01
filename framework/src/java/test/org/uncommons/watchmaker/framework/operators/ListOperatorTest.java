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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for the {@link ListOperator} high-order evolutionary operator.
 * @author Daniel Dyer
 */
public class ListOperatorTest
{
    /**
     * Make sure that the delegate operator is applied to each list in the
     * population.
     */
    @Test
    public void testApplication()
    {
        ListOperator<Integer> operator = new ListOperator<Integer>(new IntegerAdjuster(1));
        List<List<Integer>> selection = new ArrayList<List<Integer>>(3);
        selection.add(Arrays.asList(1, 2, 3));
        selection.add(Arrays.asList(4, 5, 6));
        selection.add(Arrays.asList(7, 8, 9));

        List<List<Integer>> mutations = operator.apply(selection, FrameworkTestUtils.getRNG());
        assert mutations.size() == 3 : "Wrong number of candidates after list operation: " + selection.size();

        // Each element in each candidate list should have been incremented by the delegate operator.
        for (int i = 0; i < selection.size(); i++)
        {
            List<Integer> original = selection.get(i);
            List<Integer> mutation = mutations.get(i);
            assert original.size() == mutation.size() : "Mutation size mismatch: " + mutation.size();
            for (int j = 0; j < original.size(); j++)
            {
                assert mutation.get(j) == original.get(j) + 1 : "List value not mutated correctly."; 
            }
        }
    }
}
