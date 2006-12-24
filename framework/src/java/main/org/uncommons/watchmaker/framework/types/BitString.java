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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Implementation of a fixed-length bit-string, useful for implementing
 * genetic algorithms.  This implementation makes more efficient use of
 * space than the alternative approach of using an array of booleans.
 * @author Daniel Dyer
 */
public class BitString implements Cloneable, Serializable
{
    private static final int WORD_LENGTH = 32;

    private final int length;

    /**
     * Store the bits packed in an array of 32-bit ints.  This field cannot
     * be declared final because it must be cloneable.
     */
    private int[] data;

    
    /**
     * Creates a bit string of the specified length with all bits
     * initially set to zero (off).
     * @param length The number of bits.
     */
    public BitString(int length)
    {
        this.length = length;
        this.data = new int[(length + WORD_LENGTH - 1) / WORD_LENGTH];
    }


    /**
     * Initialisies the bit string from a character string of 1s and 0s
     * in little-endian order.
     * @param value A character string of ones and zeros.
     */    
    public BitString(String value)
    {
        this(value.length());
        for (int i = 0; i < value.length(); i++)
        {
            if (value.charAt(i) == '1')
            {
                setBit(value.length() - (i + 1), true);
            }
            else if (value.charAt(i) != '0')
            {
                throw new IllegalArgumentException("Illegal character at position " + i);
            }
        }
    }


    /**
     * @return The length of this bit string.
     */
    public int getLength()
    {
        return length;
    }


    /**
     * Returns the bit at the specified index.
     * @param index The index of the bit to look-up.
     * @return A boolean indicating whether the bit is set or not.
     */
    public boolean getBit(int index)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        return (data[word] & (1 << offset)) != 0;
    }


    /**
     * Sets the bit at the specified index.
     * @param index The index of the bit to set.
     * @param set A boolean indicating whether the bit should be set or not.
     */
    public void setBit(int index, boolean set)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        if (set)
        {
            data[word] = (data[word] | (1 << offset));
        }
        else // Unset the bit.
        {
            data[word] = (data[word] & ~(1 << offset));
        }
    }


    /**
     * Inverts the value of the bit at the specified index.
     * @param index The bit to flip.
     */
    public void flipBit(int index)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        data[word] = (data[word] ^ (1 << offset));        
    }


    /**
     * Helper method to check whether a bit index is valid or not.
     * @param index The index to check.
     * @throws IndexOutOfBoundsException If the index is not valid.
     */
    private void assertValidIndex(int index)
    {
        if (index >= length || index < 0)
        {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " (length: " + length + ")");
        }
    }


    /**
     * @return The number of bits that are 1s rather than 0s.
     */
    public int countSetBits()
    {
        int count = 0;
        for (int i = 0; i < length; i++)
        {
            if (getBit(i))
            {
                ++count;
            }
        }
        return count;
    }


    /**
     * @return The number of bits that are 1s rather than 0s.
     */
    public int countUnsetBits()
    {
        int count = 0;
        for (int i = 0; i < length; i++)
        {
            if (!getBit(i))
            {
                ++count;
            }
        }
        return count;
    }


    /**
     * Interprets this bit string as being a binary numeric value and returns
     * the integer that it represents.
     * @return A {@link BigInteger} that contains the numeric value represented
     * by this bit string.
     */
    public BigInteger toNumber()
    {
        return (new BigInteger(toString(), 2));
    }


    /**
     * Creates a textual representation of this bit string in little-endian
     * order (index 0 is the left-most bit).
     * @return This bit string rendered as a String of 1s and 0s.
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = length - 1; i >= 0; i--)
        {
            buffer.append(getBit(i) ? '1' : '0');
        }
        return buffer.toString();
    }


    @Override
    public BitString clone()
    {
        try
        {
            BitString clone = (BitString) super.clone();
            clone.data = data.clone();
            return clone;
        }
        catch (CloneNotSupportedException ex)
        {
            // Not possible.
            throw new InternalError("Cloning failed.");
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        BitString bitString = (BitString) o;

        return length == bitString.length && Arrays.equals(data, bitString.data);
    }


    @Override
    public int hashCode()
    {
        int result = length;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
