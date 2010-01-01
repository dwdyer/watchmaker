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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Evolutionary operator for GP trees that reduces them to their simplest form.
 * @author Daniel Dyer
 */
public class Simplification implements EvolutionaryOperator<Node>
{
    private final Probability probability;


    /**
     * Creates a simplification operator with a probability of 1 (i.e. all
     * canidates will be simplified).
     */
    public Simplification()
    {
        this(Probability.ONE);
    }


    /**
     * Creates a simplfication operator that has the specified probability of being
     * applied to any individual candidate. 
     * @param probability The probability that this operator will attempt to simplify
     * any single expression.
     */
    public Simplification(Probability probability)
    {
        this.probability = probability;
    }


    /**
     * Simplify the expressions represented by the candidates.  Each expression
     * is simplified according to the configured probability.
     * @param selectedCandidates The individuals to evolve.
     * @param rng A source of randomness.
     * @return The (possibly) simplified candidates.
     */
    public List<Node> apply(List<Node> selectedCandidates, Random rng)
    {
        List<Node> evolved = new ArrayList<Node>(selectedCandidates.size());
        for (Node node : selectedCandidates)
        {
            evolved.add(probability.nextEvent(rng) ? node.simplify() : node);
        }
        return evolved;
    }
}
