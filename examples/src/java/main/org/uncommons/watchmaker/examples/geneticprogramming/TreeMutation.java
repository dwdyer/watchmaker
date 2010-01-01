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
 * Mutation operator for the trees of {@link Node}s used in the genetic
 * programming example application.
 * @author Daniel Dyer
 */
public class TreeMutation implements EvolutionaryOperator<Node>
{
    private final TreeFactory treeFactory;

    private final Probability mutationProbability;

    /**
     * The tree mutation operator requires a {@link TreeFactory} because
     * the process of mutation involves creating new sub-trees.  The same
     * TreeFactory that is used to create the initial population should be
     * used.
     * @param treeFactory Used to generate the new sub-trees required for mutation.
     * @param mutationProbability The probability that any given node in a tree is
     * mutated by this operator.
     */
    public TreeMutation(TreeFactory treeFactory,
                        Probability mutationProbability)
    {
        this.treeFactory = treeFactory;
        this.mutationProbability = mutationProbability;
    }


    public List<Node> apply(List<Node> selectedCandidates, Random rng)
    {
        List<Node> mutatedPopulation = new ArrayList<Node>(selectedCandidates.size());
        for (Node tree : selectedCandidates)
        {
            mutatedPopulation.add(tree.mutate(rng, mutationProbability, treeFactory));
        }
        return mutatedPopulation;
    }
}
