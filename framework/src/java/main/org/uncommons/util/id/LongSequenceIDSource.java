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
package org.uncommons.util.id;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe source for unique IDs.  This particular implementation restricts
 * values to those positive integer values that can be represented by the long data type.
 * Provides sequenced 64-bit IDs.
 * @author Daniel Dyer
 */
public final class LongSequenceIDSource implements IDSource<Long>
{
    private static final int SECONDS_IN_DAY = 86400;

    private final Lock lock = new ReentrantLock();
    private final long startTime;
    private long lastID = -1;


    /**
     * @param firstValue The value at which to start the sequence (must
     * be non-negative).
     */
    public LongSequenceIDSource(long firstValue)
    {
        if (firstValue < 0)
        {
            throw new IllegalArgumentException("Initial value must be non-negative.");
        }
        lastID = firstValue - 1;
        startTime = System.currentTimeMillis();
    }


    /**
     * Creates a sequence that starts at zero.
     */
    public LongSequenceIDSource()
    {
        this(0);
    }


    /**
     * {@inheritDoc}
     */
    public Long nextID()
    {
        lock.lock();
        try
        {
            if (lastID == Long.MAX_VALUE)
            {
                long days = (System.currentTimeMillis() - startTime) / SECONDS_IN_DAY;
                throw new IDSourceExhaustedException("64-bit ID source exhausted after " + days + " days.");
            }
            ++lastID;
            return lastID;
        }
        finally
        {
            lock.unlock();
        }
    }
}
