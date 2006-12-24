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
package org.uncommons.watchmaker.framework.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.types.BitString;

/**
 * Unit test for bit string candidate factory.
 * @author Daniel Dyer
 */
public class BitStringFactoryTest
{
    private final Random rng = new MersenneTwisterRNG();
    private final int candidateLength = 10;
    private final int populationSize = 5;
    
    @Test
    public void testUnseededPopulation()
    {
        CandidateFactory<BitString> factory = new BitStringFactory(candidateLength);
        List<BitString> population = factory.generateInitialPopulation(populationSize, rng);
        validatePopulation(population);
    }


    @Test
    public void testSeededPopulation()
    {
        CandidateFactory<BitString> factory = new BitStringFactory(candidateLength);
        BitString seed1 = new BitString("1111100000");
        BitString seed2 = new BitString("1010101010");
        List<BitString> seeds = new ArrayList<BitString>(2);
        seeds.add(seed1);
        seeds.add(seed2);
        List<BitString> population = factory.generateInitialPopulation(populationSize,
                                                                       seeds,
                                                                       rng);

        // Check that the seed candidates appear in the generated population.
        assert population.contains(seed1) : "Population does not contain seed candidate 1.";
        assert population.contains(seed2) : "Population does not contain seed candidate 2.";
        validatePopulation(population);
    }


    private void validatePopulation(List<BitString> population)
    {
        // Make sure the correct number of candidates were generated.
        assert population.size() == populationSize : "Wrong size population generated: " + population.size();
        // Make sure that each individual is the right length.
        for (BitString bitString : population)
        {
            assert bitString.getLength() == candidateLength : "Bit string is wrong length: " + bitString.getLength();
        }
    }
}
