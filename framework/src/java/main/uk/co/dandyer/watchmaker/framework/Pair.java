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
package uk.co.dandyer.watchmaker.framework;

/**
 * Generic implementation of an immutable 2-tuple or ordered pair.
 * @author Daniel Dyer.
 * @param <T1> The type of the first item in the pair.
 * @param <T2> The type of the second item in the pair.
 */
public final class Pair<T1, T2>
{
    private final T1 first;
    private final T2 second;
    
    public Pair(T1 first, T2 second)
    {
        this.first = first;
        this.second = second;
    }
    
    public T1 getFirst()
    {
        return first;
    }
    
    public T2 getSecond()
    {
        return second;
    }
    

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if (obj instanceof Pair)
        {
            Pair<T1, T2> pair = (Pair<T1, T2>) obj;
            return this.getFirst().equals(pair.getFirst()) && this.getSecond().equals(pair.getSecond());
        }
        return false;
    }
    
    
    /**
     * Over-ride hashCode because equals has also been over-ridden, to satisfy general contract
     * of equals.
     * Algorithm from Effective Java by Joshua Bloch.
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + first.hashCode();
        result = 37 * result + second.hashCode();
        return result;
    }
}