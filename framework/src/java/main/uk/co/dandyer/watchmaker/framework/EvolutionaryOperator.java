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

import java.util.List;
import java.util.Random;

/**
 * An evolutionary operator is a function that takes a population of
 * candidates as an argument and returns a new population that is the
 * result of applying a transformation to the original population.
 * @author Daniel Dyer
 */
public interface EvolutionaryOperator<T>
{
    /**
     * @param <S> A more spefic type restriction than the one specified
     * for this class.  Allows the operation to be applied to sub-classes
     * of T and still return a list of the appropriate type.
     */
    <S extends T> List<S> apply(List<S> population, Random rng);
}
