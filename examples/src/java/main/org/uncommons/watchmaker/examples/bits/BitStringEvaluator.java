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
package org.uncommons.watchmaker.examples.bits;

import java.util.List;
import org.uncommons.maths.binary.BitString;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * A fitness evaluator that simply counts the number of ones in a bit
 * string.
 * @see BitString
 * @author Daniel Dyer
 */
public class BitStringEvaluator implements FitnessEvaluator<BitString>
{
    /**
     * Calculates a fitness score for the candidate bit string.
     * @param candidate The evolved bit string to evaluate.
     * @param population {@inheritDoc}
     * @return How many bits in the string are set to 1.
     */
    public double getFitness(BitString candidate,
                             List<? extends BitString> population)
    {
        return candidate.countSetBits();
    }


    /**
     * Always returns true.  A higher score indicates a fitter individual.
     * @return True.
     */
    public boolean isNatural()
    {
        return true;
    }
}
