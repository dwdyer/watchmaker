// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation operator for the trees of {@link Node}s used in the genetic
 * programming example application.
 * @author Daniel Dyer
 */
public class TreeMutation implements EvolutionaryOperator<Node>
{
    private final TreeFactory treeFactory;

    private final double mutationProbability;

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
                        double mutationProbability)
    {
        this.treeFactory = treeFactory;
        this.mutationProbability = mutationProbability;
    }


    @SuppressWarnings("unchecked")
    public <S extends Node> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> mutatedPopulation = new ArrayList<S>(selectedCandidates.size());
        for (Node tree : selectedCandidates)
        {
            mutatedPopulation.add((S) mutateTree(tree, rng));
        }
        return mutatedPopulation;
    }


    private Node mutateTree(Node tree, Random rng)
    {
        if (rng.nextDouble() < mutationProbability)
        {
            return treeFactory.generateRandomCandidate(rng);
        }
        else
        {
            ListIterator<Node> iterator = tree.getChildren().listIterator();
            while (iterator.hasNext())
            {
                Node child = iterator.next();
                iterator.set(mutateTree(child, rng));
            }
            return tree;
        }
    }
}
