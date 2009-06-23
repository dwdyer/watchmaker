// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework;

import java.util.Random;

/**
 * Unit-test for the multi-threaded evolution engine.
 * @author Daniel Dyer
 */
public class ConcurrentEvolutionEngineTest extends EvolutionEngineTestBase
{
    // All test cases are provided by the super-class.
    
    protected <T> EvolutionEngine<T> createEvolutionEngine(CandidateFactory<T> candidateFactory,
                                                           EvolutionaryOperator<T> evolutionScheme,
                                                           FitnessEvaluator<? super T> fitnessEvaluator,
                                                           SelectionStrategy<? super T> selectionStrategy,
                                                           Random rng)
    {
        return new ConcurrentEvolutionEngine<T>(candidateFactory,
                                                evolutionScheme,
                                                fitnessEvaluator,
                                                selectionStrategy,
                                                FrameworkTestUtils.getRNG());
    }
}
