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
package org.uncommons.maths.random;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Random;
import javax.crypto.Cipher;
import org.uncommons.maths.Maths;

/**
 * Non-linear random number generator based on the AES block cipher in counter mode.
 * Uses the 128-bit seed as a key to encrypt a 128-bit counter using AES(Rijndael).
 * We only use a 128-bit key for the cipher because any larger key requires
 * the inconvenience of installing the unlimited strength cryptography policy
 * files for the Java platform.
 * @author Daniel Dyer
 */
public class AESCounterRNG extends Random implements RepeatableRNG
{
    private static final int SEED_SIZE_BYTES = 16;

    private final byte[] seed;
    private final Cipher cipher; // TO DO: This field is not Serializable.
    private final byte[] counter = new byte[16]; // 128-bit counter.

    private byte[] currentBlock = null;
    private int index = 0;


    public AESCounterRNG() throws GeneralSecurityException
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the provided seed generation strategy.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     */
    public AESCounterRNG(SeedGenerator seedGenerator) throws SeedException, GeneralSecurityException
    {
        this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Creates the RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     */
    public AESCounterRNG(byte[] seed) throws GeneralSecurityException
    {
        if (seed == null || seed.length != SEED_SIZE_BYTES)
        {
            throw new IllegalArgumentException("AES RNG requires a 128-bit (16-byte) seed.");
        }
        this.seed = seed.clone();
        cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new AESKey(this.seed));
    }


    public byte[] getSeed()
    {
        return seed.clone();
    }


    private void incrementCounter()
    {
        for (int i = 0; i < counter.length; i++)
        {
            ++counter[i];
            if (counter[i] != 0) // Check whether we need to loop again to carry the one.
            {
                break;
            }
        }
    }


    /**
     * Generates a single 128-bit block (16 bytes).
     * @throws GeneralSecurityException If there is a problem with the cipher
     * that generates the random data.
     * @return A 16-byte block of random data.
     */
    private byte[] nextBlock() throws GeneralSecurityException
    {
        incrementCounter();
        return cipher.doFinal(counter);
    }


    @Override
    protected final synchronized int next(int bits)
    {
        if (currentBlock == null || currentBlock.length - index < 4)
        {
            try
            {
                currentBlock = nextBlock();
                index = 0;
            }
            catch (GeneralSecurityException ex)
            {
                // Should never happen.  If initialisation succeeds without exceptions
                // we should be able to proceed indefinitely without exceptions.
                throw new IllegalStateException("Failed creating next random block.", ex);
            }
        }
        int result = Maths.convertBytesToInt(currentBlock, index);
        index += 4;
        return result >>> (32 - bits);
    }



    /**
     * Trivial key implementation for use with AES cipher.
     */
    private static final class AESKey implements Key
    {
        private final byte[] keyData;

        public AESKey(byte[] keyData)
        {
            this.keyData = keyData;
        }

        public String getAlgorithm()
        {
            return "AES";
        }

        public String getFormat()
        {
            return "RAW";
        }

        public byte[] getEncoded()
        {
            return keyData;
        }
    }
}
