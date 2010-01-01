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
package org.uncommons.watchmaker.framework.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates random candidates from a set of elements.  Each candidate is a random
 * permutation of the full set of elements.
 * @param <T> The component type of the lists created by this factory.
 * @author Daniel Dyer
 */
public class ListPermutationFactory<T>  extends AbstractCandidateFactory<List<T>>
{
    private final List<T> elements;

    /**
     * Creates a factory that creates lists that contain each of the specified
     * elements exactly once.  The ordering of those elements within generated
     * lists is random.
     * @param elements The elements to permute.
     */
    public ListPermutationFactory(List<T> elements)
    {
        this.elements = elements;
    }


    /**
     * Generates a random permutation from the configured elements.
     * @param rng A source of randomness used to generate the random
     * permutation.
     * @return A random permutation.
     */
    public List<T> generateRandomCandidate(Random rng)
    {
        List<T> candidate = new ArrayList<T>(elements);
        Collections.shuffle(candidate, rng);
        return candidate;
    }
}
