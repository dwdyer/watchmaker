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
 * Unit test for the {@link Constant} node type.
 * @author Daniel Dyer
 */
public class ConstantTest
{
    @Test
    public void testEquality()
    {
        Constant zero = new Constant(0);
        Constant one = new Constant(1);
        Constant anotherOne = new Constant(1);

        assert zero.equals(zero) : "Equality must be reflexive.";
        assert one.equals(anotherOne) : "Same-valued constants must be equal.";
        assert anotherOne.equals(one) : "Equality must be symmetric.";
        assert one.hashCode() == anotherOne.hashCode() : "Equal objects must have equal hash codes.";
        assert !zero.equals(one) : "Different valued constants must be non-equal.";
        assert !zero.equals(null) : "No non-null object should not be considered equal to null.";
        assert !zero.equals(Double.valueOf(0)) : "Objects of different types should not be equal.";
    }


    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testGetInvalidNodeIndex()
    {
        new Constant(1).getNode(1); // Should throw an exception.
    }


    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testReplaceInvalidNodeIndex()
    {
        new Constant(1).replaceNode(1, new Constant(2)); // Should throw an exception.
    }
}
