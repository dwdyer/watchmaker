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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Evolutionary operator that simply returns the selected candidates unaltered.
 * This can be useful when combined with {@link SplitEvolution} so that a
 * proportion of the selected candidates can be copied unaltered into the next
 * generation while the remainder are evolved.
 * @author Daniel Dyer
 */
public class IdentityOperator implements EvolutionaryOperator<Object>
{
    public <S> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        return new ArrayList<S>(selectedCandidates);
    }
}
