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
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.binary.BitString;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;
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
        List<BitString> population = factory.generateInitialPopulation(2, FrameworkTestUtils.getRNG());
        // Test to make sure that cross-over correctly preserves all genetic material
        // originally present in the population and does not introduce anything new.
        int totalSetBits = population.get(0).countSetBits() + population.get(1).countSetBits();
        for (int i = 0; i < 50; i++) // Test several generations.
        {
            population = operator.apply(population, FrameworkTestUtils.getRNG());
            int setBits = population.get(0).countSetBits() + population.get(1).countSetBits();
            assert setBits == totalSetBits : "Total number of set bits in population changed during cross-over.";
        }
    }


    /**
     * The {@link BitStringCrossover} operator is only defined to work on populations
     * containing Strings of equal lengths.  Any attempt to apply the operation to
     * populations that contain different length Strings should throw an exception.
     * Not throwing an exception should be considered a bug since it could lead to
     * hard to trace bugs elsewhere.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDifferentLengthParents()
    {
        EvolutionaryOperator<BitString> crossover = new BitStringCrossover(new ConstantGenerator<Integer>(1));
        List<BitString> population = new ArrayList<BitString>(2);
        population.add(new BitString(32, FrameworkTestUtils.getRNG()));
        population.add(new BitString(33, FrameworkTestUtils.getRNG()));
        // This should cause an exception since the parents are different lengths.
        crossover.apply(population, FrameworkTestUtils.getRNG());
    }

}
