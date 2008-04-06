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

/**
 * @author Daniel Dyer
 */
public class Addition extends AbstractNode
{
    public Addition()
    {
        super(2);
    }


    public double evaluate(double[] programParameters)
    {
        return children.get(0).evaluate(programParameters) + children.get(1).evaluate(programParameters);
    }

    
    public String print()
    {
        return "(" + children.get(0).print() + " + " + children.get(1).print() + ")";
    }
}
