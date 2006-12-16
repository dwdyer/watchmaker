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
 * <p>A compound evolutionary operator that applies multiple operators (of the
 * same type) in series.</p>
 *
 * <p>By combining EvolutionPipeline operators with {@link SplitEvolution} operators,
 * elaborate evolutionary schemes can be constructed.</p>
 *
 * @author Daniel Dyer
 */
public class EvolutionPipeline<T> implements EvolutionaryOperator<T>
{
    private final List<EvolutionaryOperator<? super T>> pipeline;


    public EvolutionPipeline(List<EvolutionaryOperator<? super T>> pipeline)
    {
        this.pipeline = new ArrayList<EvolutionaryOperator<? super T>>(pipeline);
    }


    public <S extends T> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> population = selectedCandidates;
        for (EvolutionaryOperator<? super T> operator : pipeline)
        {
            population = operator.apply(population, rng);
        }
        return population;
    }
}
