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

import java.util.Random;
import org.uncommons.maths.random.Probability;

/**
 * Operations supported by the different types of nodes that make up
 * genetic program trees.
 * @author Daniel Dyer
 */
public interface Node
{
    /**
     * Recursively evaluates the (sub-)tree represented by this node (including any
     * child nodes) and returns a numeric value.
     * @param programParameters Program parameters (possibly used by this node and/or
     * in the evaluation of child nodes).
     * @return The result of evaluating this node and all of its children.
     */
    double evaluate(double[] programParameters);

    /**
     * Recursively builds a string representation of the tree rooted at this node.
     * @return A string representation of this tree.
     */
    String print();

    /**
     * @return A short String that represents the function or value represented by this node.
     */
    String getLabel();

    /**
     * If this is a function (non-leaf) node, how many arguments does it take?  For
     * leaf nodes the answer is zero.
     * @return The arity of this function, or zero if this node is a leaf node.
     * @see #countNodes()
     */
    int getArity();

    /**
     * @return The number of levels of nodes that make up this tree.
     * @see #getWidth()
     */
    int getDepth();

    /**
     * Work out how wide (in nodes) this tree is.  Used primarily for laying out a
     * visual representation.  A leaf node has a width of 1.  A binary node's width
     * is the sum of the widths of its sub-trees.
     * @return The maximum width of this tree.
     * @see #getDepth()
     * @see #getArity()
     */
    int getWidth();

    /**
     * @return The total number of nodes in this tree (recursively counts the nodes
     * for each sub-node of this node).
     * @see #getArity() 
     */
    int countNodes();

    /**
     * Retrieves a sub-node from this tree. 
     * @param index The index of a node.  Index 0 is the root node.  Nodes are numbered
     * depth-first, right-to-left.
     * @return The node at the specified position.
     */
    Node getNode(int index);

    /**
     * Retrieves a direct sub-node from this tree.
     * @param index The index of a child node.  Index 0 is the first child.  Nodes are numbered
     * right-to-left, grandchild nodes are not included.
     * @return The node at the specified position.
     */
    Node getChild(int index);

    /**
     * Returns a new tree that is identical to this tree except with the specified node
     * replaced. 
     * @param index The index of the node to replace.
     * @param newNode The replacement node.
     * @return A new tree with the node at the specified index replaced by
     * {@code newNode}.
     */
    Node replaceNode(int index, Node newNode);

    /**
     * Helper method for the {@link TreeMutation} evolutionary operator.
     * @param rng A source of randomness.
     * @param mutationProbability The probability that a given node will be mutated.
     * @param treeFactory A factory for creating the new sub-trees needed for mutation.
     * @return The mutated node (or the same node if no mutation occurred).
     */
    Node mutate(Random rng, Probability mutationProbability, TreeFactory treeFactory);

    /**
     * Reduce this program tree to its simplest equivalent form.
     * @return A simplification of this program tree, or this program tree unodified if it
     * cannot be simplified.
     */
    Node simplify();
}
