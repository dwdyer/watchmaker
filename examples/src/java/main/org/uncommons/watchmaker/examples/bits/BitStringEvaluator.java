// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.bits;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.types.BitString;

/**
 * A fitness evaluator that simply counts the number of ones in a bit
 * string.
 * @author Daniel Dyer
 */
public class BitStringEvaluator implements FitnessEvaluator<BitString>
{
    public double getFitness(BitString candidate)
    {
        return candidate.countSetBits();
    }

    
    public boolean isNatural()
    {
        return true;
    }
}
