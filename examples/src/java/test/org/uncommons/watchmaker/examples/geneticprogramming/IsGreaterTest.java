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

/**
 * Simple unit test for the {@link IsGreater} node type.
 * @author Daniel Dyer
 */
public class IsGreaterTest
{
    /**
     * If two nodes are equal then neither is greater than the other.
     */
    @Test
    public void testBothNodesEqual()
    {
        Node node = new IsGreater(new Constant(1), new Constant(1));
        assert node.evaluate(new double[0]) == 0 : "First node should not be greater than second.";
    }


    @Test
    public void testFirstNodeGreater()
    {
        Node node = new IsGreater(new Constant(2), new Constant(1));
        assert node.evaluate(new double[0]) > 0 : "First node should be greater than second.";
    }


    @Test
    public void testSecondNodeGreater()
    {
        Node node = new IsGreater(new Constant(-1), new Constant(1));
        assert node.evaluate(new double[0]) == 0 : "First node should be less than second.";
    }


    /**
     * If the arguments to the IsGreater function are both constants then the node
     * should be replaced by a constant node containing the evaluation of this expression.
     */
    @Test
    public void testSimplifyConstants()
    {
        Node node = new IsGreater(new Constant(7), new Constant(5));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
        assert simplified.evaluate(BinaryNode.NO_ARGS) == 1;

    }


    @Test
    public void testSimplifyIdenticalArguments()
    {
        Node node = new IsGreater(new Parameter(0), new Parameter(0));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
    }

    
    /**
     * Test that simplification doesn't cause any problems when the expression is already as simple
     * as possible.
     */
    @Test
    public void testSimplifySimplest()
    {
        Node node = new IsGreater(new Parameter(0), new Constant(1));
        Node simplified = node.simplify();
        assert simplified == node : "Expression should not have been changed.";
    }


    /**
     * Make sure that sub-nodes are simplified.
     */
    @Test
    public void testSimplifySubNode()
    {
        Node node = new IsGreater(new Parameter(0),
                                  new IsGreater(new Constant(3), new Constant(2)));
        Node simplified = node.simplify();
        assert simplified instanceof IsGreater
            : "Simplified node should be IsGreater, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.countNodes() < node.countNodes() : "Should be fewer nodes after simplification.";
    }
}
