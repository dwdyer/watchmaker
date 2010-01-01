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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates random candidates from a set of elements.  Each candidate is a random
 * permutation of the full set of elements.
 * @author Daniel Dyer
 * @param <T> The element type of the arrays created.
 */
public class ObjectArrayPermutationFactory<T>  extends AbstractCandidateFactory<T[]>
{
    private final T[] elements;

    /**
     * Creates a factory that creates arrays that contain each of the specified
     * elements exactly once.  The ordering of those elements within generated
     * arrays is random.
     * @param elements The elements to permute.
     */
    public ObjectArrayPermutationFactory(T[] elements)
    {
        this.elements = elements.clone();
    }


    /**
     * Generates a random permutation from the configured elements.
     * @param rng A source of randomness used to generate the random
     * permutation.
     * @return A random permutation.
     */
    public T[] generateRandomCandidate(Random rng)
    {
        T[] candidate = elements.clone();
        List<T> list = Arrays.asList(candidate);
        Collections.shuffle(list, rng);
        return list.toArray(candidate);
    }
}
