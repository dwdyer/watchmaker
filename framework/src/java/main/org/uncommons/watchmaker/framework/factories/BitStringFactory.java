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

import java.util.Random;
import org.uncommons.maths.binary.BitString;

/**
 * General purpose candidate factory for generating bit strings for
 * genetic algorithms.
 * @see BitString
 * @author Daniel Dyer
 */
public class BitStringFactory extends AbstractCandidateFactory<BitString>
{
    private final int length;


    /**
     * @param length The length of all bit strings created by this
     * factory.
     */
    public BitStringFactory(int length)
    {
        this.length = length;
    }


    /**
     * Generates a random bit string, with a uniform distribution of
     * ones and zeroes.
     * @param rng The source of randomness for setting the bits.
     * @return A random bit string of the length configured for this
     * factory.
     */
    public BitString generateRandomCandidate(Random rng)
    {
        return new BitString(length, rng);
    }
}
