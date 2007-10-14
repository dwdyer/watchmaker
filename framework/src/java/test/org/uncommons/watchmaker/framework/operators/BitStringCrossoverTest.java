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
package org.uncommons.watchmaker.framework.operators;

import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.util.binary.BitString;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.factories.BitStringFactory;

/**
 * Unit test for cross-over applied to bit strings.
 * @author Daniel Dyer
 */
public class BitStringCrossoverTest
{
    @Test
    public void testCrossover()
    {
        EvolutionaryOperator<BitString> operator = new BitStringCrossover();
        CandidateFactory<BitString> factory = new BitStringFactory(50);
        MersenneTwisterRNG rng = new MersenneTwisterRNG();
        List<BitString> population = factory.generateInitialPopulation(2, rng);
        // Test to make sure that cross-over correctly preserves all genetic material
        // originally present in the population and does not introduce anything new.
        int totalSetBits = population.get(0).countSetBits() + population.get(1).countSetBits();
        for (int i = 0; i < 50; i++) // Test several generations.
        {
            population = operator.apply(population, rng);
            int setBits = population.get(0).countSetBits() + population.get(1).countSetBits();
            assert setBits == totalSetBits : "Total number of set bits in population changed during cross-over.";
        }
    }
}
