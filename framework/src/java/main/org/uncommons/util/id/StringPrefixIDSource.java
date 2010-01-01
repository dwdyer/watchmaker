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
 * Thread-safe ID source that wraps another source of IDs and adds a fixed String
 * prefix to each ID generated.
 * @author Daniel Dyer
 */
public class StringPrefixIDSource implements IDSource<String>
{
    private final Lock lock = new ReentrantLock();
    private final String prefix;
    private final IDSource<?> source;

    /**
     * @param prefix A fixed String that is attached to the front of each ID.
     * @param source The source of IDs to which the prefix is added.
     */
    public StringPrefixIDSource(String prefix, IDSource<?> source)
    {
        this.prefix = prefix;
        this.source = source;
    }


    /**
     * {@inheritDoc}
     */
    public String nextID()
    {
        lock.lock();
        try
        {
            StringBuilder output = new StringBuilder(prefix);
            output.append(source.nextID());
            return output.toString();
        }
        finally
        {
            lock.unlock();
        }
    }
}
