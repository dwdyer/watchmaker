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
package org.uncommons.watchmaker.framework.types;

import java.math.BigInteger;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link BitString} type.
 * @author Daniel Dyer
 */
public class BitStringTest
{
    /**
     * Check that a bit string is constructed correctly, with
     * the correct length and all bits initially set to zero.
     */
    @Test
    public void testCreateBitString()
    {
        BitString bitString = new BitString(100);
        assert bitString.getLength() == 100 : "BitString created with wrong length: " + bitString.getLength();
        for (int i = 0; i < bitString.getLength(); i++)
        {
            assert !bitString.getBit(i) : "Bit " + i + " should not be set."; 
        }
    }


    /**
     * Make sure that bits are set correctly.
     */
    @Test(dependsOnMethods = "testCreateBitString")
    public void testSetBits()
    {
        BitString bitString = new BitString(5);
        bitString.setBit(1, true);
        bitString.setBit(4, true);
        // Testing with non-symmetrical string to ensure that there are no endian
        // problems.
        assert !bitString.getBit(0) : "Bit 0 should not be set.";
        assert bitString.getBit(1) : "Bit 1 should be set.";
        assert !bitString.getBit(2) : "Bit 2 should not be set.";
        assert !bitString.getBit(3) : "Bit 3 should not be set.";
        assert bitString.getBit(4) : "Bit 4 should be set.";
        // Test unsetting a bit.
        bitString.setBit(4, false);
        assert !bitString.getBit(4) : "Bit 4 should be unset.";
    }


    /**
     * Make sure bit-flipping works as expected.
     */
    @Test(dependsOnMethods = "testCreateBitString")
    public void testFlipBits()
    {
        BitString bitString = new BitString(5);
        bitString.flipBit(2);
        assert bitString.getBit(2) : "Flipping unset bit failed.";
        bitString.flipBit(2);
        assert !bitString.getBit(2) : "Flipping set bit failed.";
    }


    /**
     * Checks that string representations are correctly generated.
     */
    @Test(dependsOnMethods = "testSetBits")
    public void testToString()
    {
        BitString bitString = new BitString(10);
        bitString.setBit(3, true);
        bitString.setBit(7, true);
        bitString.setBit(8, true);
        String string = bitString.toString();
        // Testing with leading zero to make sure it isn't omitted.
        assert string.equals("0110001000") : "Incorrect string representation: " + string;
    }


    /**
     * Checks that integer conversion is correct.
     */
    @Test(dependsOnMethods = "testSetBits")
    public void testToNumber()
    {
        BitString bitString = new BitString(10);
        bitString.setBit(0, true);
        bitString.setBit(9, true);
        BigInteger number = bitString.toNumber();
        assert number.intValue() == 513 : "Incorrect number conversion: " + number.intValue();
    }


    /**
     * Checks that the bit string can correctly count its number of set bits.
     */
    @Test(dependsOnMethods = "testSetBits")
    public void testCountSetBits()
    {
        BitString bitString = new BitString(64);
        assert bitString.countSetBits() == 0 : "Initial string should have no 1s.";
        // The bits to set have been chosen because they deal with boundary cases.
        bitString.setBit(0, true);
        bitString.setBit(31, true);
        bitString.setBit(32, true);
        bitString.setBit(33, true);
        bitString.setBit(63, true);
        int setBits = bitString.countSetBits();
        assert setBits == 5 : "No. set bits should be 5, is " + setBits;
    }


    @Test(dependsOnMethods = {"testSetBits", "testFlipBits"})
    public void testClone()
    {
        BitString bitString = new BitString(10);
        bitString.setBit(3, true);
        bitString.setBit(7, true);
        bitString.setBit(8, true);
        BitString clone = bitString.clone();
        // Check the clone is a bit-for-bit duplicate.
        for (int i = 0; i < bitString.getLength(); i++)
        {
            assert bitString.getBit(i) == clone.getBit(i) : "Cloned bit string does not match in position " + i;
        }
        // Check that clone is distinct from original (i.e. it does not change
        // if the original is modified).
        assert clone != bitString : "Clone is same object.";
        bitString.flipBit(2);
        assert !clone.getBit(2) : "Clone is not independent from original.";
    }


    @Test(dependsOnMethods = "testClone")
    public void testEquals()
    {
        BitString bitString = new BitString(10);
        bitString.setBit(2, true);
        bitString.setBit(5, true);
        bitString.setBit(9, true);
        BitString clone = bitString.clone();
        assert clone.equals(bitString) : "Equals comparison failed on equivalent bit strings.";
        clone.flipBit(0);
        assert !clone.equals(bitString) : "Equals comparison failed on different bit strings.";
    }
}
