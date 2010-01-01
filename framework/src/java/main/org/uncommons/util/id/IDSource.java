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

import java.io.Serializable;

/**
 * Defines operations for classes that generate unique identifiers.  Generated IDs must
 * be of a {@link java.io.Serializable} type.  The strategy used will vary between
 * implementations.  It may be a straightforward sequence or a more complex, less predictable
 * algorithm.
 * @param <T> The type of ID returned by this source.
 * @author Daniel Dyer
 */
public interface IDSource<T extends Serializable>
{
    /**
     * Implementing classes are responsible for synchronization if concurrent invocations
     * of this method are required.
     * @return The next ID.
     * @throws IDSourceExhaustedException If this ID source cannot generate any more
     * unique IDs.
     */
    T nextID();
}
