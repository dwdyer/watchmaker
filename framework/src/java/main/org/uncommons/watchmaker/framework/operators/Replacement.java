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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * An evolutionary operator that replaces individuals with randomly-generated
 * new individuals, according to some specified probability.  The new individuals
 * are not derived from the selected individuals, they are completely random.  This
 * operator provides a way to prevent stagnation by occassionally introducing
 * new genetic material into the population.
 * @param <T> The type of evolvable entity that this operator applies to. 
 * @author Daniel Dyer
 */
public class Replacement<T> implements EvolutionaryOperator<T>
{
    private final CandidateFactory<T> factory;
    private final NumberGenerator<Probability> replacementProbability;


    /**
     * Creates a replacement operator that replaces individuals according to
     * the specified probability.  New individuals are obtained from the factory
     * provided.
     * @param factory A source of new individuals.
     * @param replacementProbability The probability that any given individual will
     * be replaced by a new individual.  This should typically be quite low.  If it is
     * too high, it will undermine the evolutionary progress. 
     */
    public Replacement(CandidateFactory<T> factory,
                       Probability replacementProbability)
    {
        this(factory, new ConstantGenerator<Probability>(replacementProbability));
    }


    /**
     * Creates a replacement operator that replaces individuals according to
     * a variable probability.  New individuals are obtained from the factory
     * provided.
     * @param factory A source of new individuals.
     * @param replacementProbability A {@link NumberGenerator} that provides
     * a probability of replacement.  The probablity may be constant, or it may change
     * over time.  The probability should typically be quite low.  If it is too high,
     * it will undermine the evolutionary progress.
     */
    public Replacement(CandidateFactory<T> factory,
                       NumberGenerator<Probability> replacementProbability)
    {
        this.factory = factory;
        this.replacementProbability = replacementProbability;
    }


    /**
     * Randomly replace zero or more of the selected candidates with new,
     * independent individuals that are randomly created.
     * @param selectedCandidates The selected candidates, some of these may be
     * discarded and replaced with new individuals.
     * @param rng A source of randomness.
     * @return The remaining candidates after some (or none) have been replaced
     * with new individuals.
     */
    public List<T> apply(List<T> selectedCandidates, Random rng)
    {
        List<T> output = new ArrayList<T>(selectedCandidates.size());
        for (T candidate : selectedCandidates)
        {
            output.add(replacementProbability.nextValue().nextEvent(rng)
                       ? factory.generateRandomCandidate(rng)
                       : candidate);
        }
        return output;
    }
}
