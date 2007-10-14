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

import java.math.BigInteger;
import org.testng.annotations.Test;

/**
 * Unit test for mathematical utility methods.
 * @author Daniel Dyer
 */
public class MathsTest
{
    private static final long SIX_FACTORIAL = 720;
    private static final long TWENTY_FACTORIAL = 2432902008176640000L;
    private static final BigInteger TWENTY_FIVE_FACTORIAL
        = BigInteger.valueOf(TWENTY_FACTORIAL).multiply(BigInteger.valueOf(6375600));

    @Test
    public void testFactorial()
    {
        // Make sure that the correct value (1) is returned for zero
        // factorial.
        assert Maths.factorial(0) == 1 : "0! should be 1." ;
        // Make sure that the correct value (1) is returned for one
        // factorial.
        assert Maths.factorial(1) == 1 : "1! should be 1." ;
        // Make sure that the correct results are returned for other values.
        assert Maths.factorial(6) == SIX_FACTORIAL : "6! should be " + SIX_FACTORIAL ;
        assert Maths.factorial(20) == TWENTY_FACTORIAL : "20! should be " + TWENTY_FACTORIAL ;
    }


    @Test
    public void testBigFactorial()
    {
        // Make sure that the correct value (1) is returned for zero
        // factorial.
        assert Maths.bigFactorial(0).equals(BigInteger.ONE) : "0! should be 1." ;
        // Make sure that the correct value (1) is returned for one
        // factorial.
        assert Maths.bigFactorial(1).equals(BigInteger.ONE) : "1! should be 1." ;
        // Make sure that the correct results are returned for other values
        assert Maths.bigFactorial(6).longValue() == SIX_FACTORIAL : "6! should be " + SIX_FACTORIAL ;
        assert Maths.bigFactorial(20).longValue() == TWENTY_FACTORIAL : "20! should be " + TWENTY_FACTORIAL ;
        // Make sure that the correct value is returned for factorials
        // outside of the range of longs.
        assert Maths.bigFactorial(25).equals(TWENTY_FIVE_FACTORIAL) : "25! should be " + TWENTY_FIVE_FACTORIAL;
    }


    /**
     * Factorials of negative integers are not supported.  This method
     * checks that an appropriate exception is thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeFactorial()
    {
        Maths.factorial(-1); // Should throw an exception.
    }


    /**
     * Factorials of negative integers are not supported.  This method
     * checks that an appropriate exception is thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeBigFactorial()
    {
        Maths.bigFactorial(-1); // Should throw an exception.
    }


    @Test
    public void testRaiseToPower()
    {
        assert Maths.raiseToPower(0, 0) == 1 : "Any value raised to the power of zero should equal 1.";
        assert Maths.raiseToPower(5, 0) == 1 : "Any value raised to the power of zero should equal 1.";
        assert Maths.raiseToPower(123, 1) == 123 : "Any value raised to the power of one should be unchanged.";
        assert Maths.raiseToPower(250, 2) == 62500 : "250^2 incorrectly calculated,";
        assert Maths.raiseToPower(2, 10) == 1024 : "2^10 incorrectly calculated.";
        // Check values that generate a result outside of the range of an int.
        assert Maths.raiseToPower(2, 34) == 17179869184L : "2^34 incorrectly calculated.";
    }
}
