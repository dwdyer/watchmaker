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
package org.uncommons.maths;

import java.util.List;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link PermutationGenerator} class.
 * @author Daniel Dyer
 */
public class PermutationGeneratorTest
{
    @Test
    public void testPermutationGenerator()
    {
        String[] elements = new String[]{"1", "2", "3"};
        PermutationGenerator<String> generator = new PermutationGenerator<String>(elements);
        assert generator.getTotalPermutations() == 6 : "Possible permutations should be 6.";
        assert generator.hasMore() : "Generator should have more permutations available.";
        assert generator.getRemainingPermutations() == 6 : "Remaining permutations should be 6.";

        String[] permutation1 = generator.nextPermutationAsArray();
        assert permutation1.length == 3 : "Permutation length should be 3.";
        assert generator.hasMore() : "Generator should have more permutations available.";
        assert generator.getRemainingPermutations() == 5 : "Remaining permutations should be 5.";
        assert !permutation1[0].equals(permutation1[1]) : "Permutation elements should be different.";
        assert !permutation1[0].equals(permutation1[2]) : "Permutation elements should be different.";
        assert !permutation1[1].equals(permutation1[2]) : "Permutation elements should be different.";

        List<String> permutation2 = generator.nextPermutationAsList();
        assert permutation2.size() == 3 : "Permutation length should be 3.";
        assert generator.hasMore() : "Generator should have more permutations available.";
        assert generator.getRemainingPermutations() == 4: "Remaining permutations should be 4.";
        // Make sure this combination is different from the previous one.
        assert !permutation2.get(0).equals(permutation2.get(1)) : "Permutation elements should be different.";
        assert !permutation2.get(0).equals(permutation2.get(2)) : "Permutation elements should be different.";
        assert !permutation2.get(1).equals(permutation2.get(2)) : "Permutation elements should be different.";

        String perm1String = permutation1[0] + permutation1[1] + permutation1[2];
        String perm2String = permutation2.get(0) + permutation2.get(1) + permutation2.get(2);
        assert !(perm1String).equals(perm2String) : "Permutation should be different from previous one.";
    }
}
