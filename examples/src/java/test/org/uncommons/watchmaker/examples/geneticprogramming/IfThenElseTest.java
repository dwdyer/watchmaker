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

import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;

/**
 * Unit test for the {@link IfThenElse} node type.
 * @author Daniel Dyer
 */
public class IfThenElseTest
{
    @Test
    public void testIfBranch()
    {
        Node node = new IfThenElse(new Constant(1),
                                   new Constant(5),
                                   new Constant(10));
        // Condition (1) is true, so result should be 5.
        double value = node.evaluate(new double[0]);
        assert value == 5 : "Wrong answer: " + value; 
    }


    @Test
    public void testElseBranch()
    {
        Node node = new IfThenElse(new Constant(0),
                                   new Constant(5),
                                   new Constant(10));
        // Condition (0) is true, so result should be 10.
        double value = node.evaluate(new double[0]);
        assert value == 10 : "Wrong answer: " + value; 
    }


    /**
     * Count nodes when the sub-trees each consist of only a single leaf node.
     */
    @Test
    public void testCountNodesSimple()
    {
        Node node = new IfThenElse(new Constant(1), new Constant(2), new Constant(3));
        int nodeCount = node.countNodes();
        assert nodeCount == 4 : "Tree has 4 nodes (root + 3 children), not " + nodeCount;
    }


    /**
     * Count nodes when the sub-trees consist of non-leaf nodes.
     */
    @Test
    public void testCountNodesComplex()
    {
        Node node = new IfThenElse(new Addition(new Constant(1), new Constant(2)),
                                   new Subtraction(new Constant(3), new Constant(4)),
                                   new Multiplication(new Constant(5), new Constant(6)));
        int nodeCount = node.countNodes();
        assert nodeCount == 10 : "Tree has 10 nodes (root + 3 children and 6 grand-children), not " + nodeCount;
    }


    @Test
    public void testGetNode()
    {
        Node condition = new Constant(1);
        Node then = new Constant(2);
        Constant sub1 = new Constant(3);
        Constant sub2 = new Constant(4);
        Node otherwise = new Addition(sub1, sub2);
        Node node = new IfThenElse(condition, then, otherwise);

        assert node.getNode(0) == node : "Node zero should be root node.";
        assert node.getNode(1) == condition : "Node one should be condition node.";
        assert node.getNode(2) == then : "Node one should be then node.";
        assert node.getNode(3) == otherwise : "Node one should be else node.";
        assert node.getNode(4) == sub1 : "Node 4 should be first sub-node of else node.";
        assert node.getNode(5) == sub2 : "Node 5 should be second sub-node of else node.";
    }


    @Test
    public void testReplaceRootNode()
    {
        Node node = new IfThenElse(new Constant(1), new Constant(2), new Constant(3));
        Constant constant = new Constant(0);
        Node newNode = node.replaceNode(0, constant);
        assert newNode == constant : "Root node should be replaced by new node.";
    }


    @Test(dependsOnMethods = "testIfBranch")
    public void testReplaceConditionNode()
    {
        Node node = new IfThenElse(new Constant(1), new Constant(2), new Constant(3));
        Node newNode = node.replaceNode(1, new Constant(0));
        assert newNode instanceof IfThenElse : "Replacing condition node should not change type of root node.";
        assert newNode.evaluate(BinaryNode.NO_ARGS) == 3d : "Changing condition node should change evaluation.";
    }


    @Test(dependsOnMethods = "testIfBranch")
    public void testReplaceThenNode()
    {
        Node node = new IfThenElse(new Constant(1), new Constant(2), new Constant(3));
        Node newNode = node.replaceNode(2, new Constant(4));
        assert newNode instanceof IfThenElse : "Replacing then node should not change type of root node.";
        assert newNode.evaluate(BinaryNode.NO_ARGS) == 4d : "Changing then node should change evaluation.";
    }


    @Test(dependsOnMethods = "testElseBranch")
    public void testReplaceElseNode()
    {
        Node node = new IfThenElse(new Constant(0), new Constant(2), new Constant(3));
        Node newNode = node.replaceNode(3, new Constant(5));
        assert newNode instanceof IfThenElse : "Replacing else node should not change type of root node.";
        assert newNode.evaluate(BinaryNode.NO_ARGS) == 5d : "Changing then node should change evaluation.";
    }


    /**
     * Test replacing of nodes which are not direct sub-nodes of this node but are further down
     * the tree.
     */
    @Test(dependsOnMethods = "testReplaceElseNode")
    public void testReplaceSubNodes()
    {
        Node node = new IfThenElse(new Constant(0), new Constant(2), new Addition(new Constant(3), new Constant(4)));
        // Replace both of the constants summed by the addition node on the else branch.
        Node newNode = node.replaceNode(4, new Constant(5));
        newNode = newNode.replaceNode(5, new Constant(6));
        assert newNode instanceof IfThenElse : "Replacing sub-nodes should not change type of root node.";
        assert newNode.evaluate(BinaryNode.NO_ARGS) == 11d : "Changing sub-nodes should change evaluation.";
    }


    @Test
    public void testZeroProbabilityMutation()
    {
        Node node = new IfThenElse(new Constant(0), new Constant(2), new Addition(new Constant(3), new Constant(4)));
        double value = node.evaluate(BinaryNode.NO_ARGS);
        String string = node.print();

        Node mutated = node.mutate(ExamplesTestUtils.getRNG(), Probability.ZERO, null);
        assert mutated == node : "Node should not have changed.";
        assert value == mutated.evaluate(BinaryNode.NO_ARGS) : "Node should not have been altered.";
        assert string.equals(mutated.print()) : "Node should not have been altered.";
    }


    @Test
    public void testSimplify()
    {
        Node node = new IfThenElse(new Constant(0), new Constant(1), new Constant(2));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
    }


    /**
     * Make sure that sub-nodes are simplified.
     */
    @Test
    public void testSimplifySubNode()
    {
        Node node = new IfThenElse(new Constant(4),
                                   new IfThenElse(new Constant(1), new Constant(2), new Constant(3)),
                                   new Constant(5));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
        assert simplified.evaluate(BinaryNode.NO_ARGS) == 2;
    }


    /**
     * Make sure that sub-nodes are simplified and that these simplfications are taken into
     * account when optimising the parent node (in other words the child nodes should be
     * simplified first).
     */
    @Test
    public void testSimplifyComplex()
    {
        Node node = new IfThenElse(new IfThenElse(new Constant(1), new Constant(2), new Constant(3)),
                                   new Constant(4),
                                   new Constant(5));
        Node simplified = node.simplify();
        // It is not sufficient to simplify this to an IfThenElse with a constant for each node,
        // the fact that the condition node can be replaced by a Constant should result in the whole
        // tree being reduced to a single constant (with a value of 4).
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
        assert simplified.evaluate(BinaryNode.NO_ARGS) == 4;
    }


    /**
     * Test that simplification doesn't cause any problems when the expression is already as simple
     * as possible.
     */
    @Test
    public void testSimplifySimplest()
    {
        Node node = new IfThenElse(new Parameter(0), new Constant(1), new Constant(2));
        Node simplified = node.simplify();
        assert simplified == node : "Expression should not have been changed.";
    }


    /**
     * If the two branches (then and else) are identical, it doesn't matter what the condition is,
     * the tree can be replaced by either one of the branches.
     */
    @Test
    public void testSimplifyIdenticalBranches()
    {
        Node node = new IfThenElse(new Parameter(0),
                                   new Constant(2),
                                   new Constant(2));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{1}; // Need one argument for the parameter node to use.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.evaluate(args) == 2;

    }
}
