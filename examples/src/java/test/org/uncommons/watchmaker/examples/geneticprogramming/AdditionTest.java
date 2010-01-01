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
 * Simple unit test for the {@link Addition} node type.
 * @author Daniel Dyer
 */
public class AdditionTest
{
    @Test
    public void testEvaluation()
    {
        Node node = new Addition(new Constant(1), new Parameter(0));
        double value = node.evaluate(new double[]{3}); // 1 + 3
        assert value == 4 : "Wrong result: " + value;
    }

    @Test
    public void testStringRepresentation()
    {
        Node node = new Addition(new Constant(1), new Constant(3));
        assert node.print().equals("(1.0 + 3.0)") : "Wrong string representation: " + node.print();
    }


    /**
     * If the arguments to the add function are both constants then the addition node
     * should be replaced by a constant node containing the evaluation of this sum.
     */
    @Test
    public void testSimplifyConstants()
    {
        Node node = new Addition(new Constant(7), new Constant(5));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
        assert simplified.evaluate(BinaryNode.NO_ARGS) == 12;

    }



    /**
     * Test that simplification doesn't cause any problems when the expression is already as simple
     * as possible.
     */
    @Test
    public void testSimplifySimplest()
    {
        Node node = new Addition(new Parameter(0), new Constant(1));
        Node simplified = node.simplify();
        assert simplified == node : "Expression should not have been changed.";
    }


    /**
     * Make sure that sub-nodes are simplified.
     */
    @Test
    public void testSimplifySubNode()
    {
        Node node = new Addition(new Parameter(0),
                                 new Addition(new Constant(3), new Constant(2)));
        Node simplified = node.simplify();
        assert simplified instanceof Addition
            : "Simplified node should be Addition, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.countNodes() < node.countNodes() : "Should be fewer nodes after simplification.";
    }


    @Test
    public void testSimplifyAddZero()
    {
        Node node = new Addition(new Parameter(0), new Constant(0));
        Node simplified = node.simplify();
        assert simplified instanceof Parameter
            : "Simplified node should be Parameter, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
    }


    @Test
    public void testSimplifyAddToZero()
    {
        Node node = new Addition(new Constant(0), new Parameter(0));
        Node simplified = node.simplify();
        assert simplified instanceof Parameter
            : "Simplified node should be Parameter, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
    }
}
