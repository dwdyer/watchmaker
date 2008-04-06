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
 * A program {@link Node} that simply returns the value of one of the
 * program's parameters.
 * @author Daniel Dyer
 */
public class Parameter extends AbstractNode
{
    private final int parameterIndex;

    /**
     * @param parameterIndex Which of the program's (zero-indexed) parameter
     * values should be returned upon evaluation of this node.
     */
    public Parameter(int parameterIndex)
    {
        super(0);
        this.parameterIndex = parameterIndex;
    }

    
    public double evaluate(double[] programParameters)
    {
        if (parameterIndex >= programParameters.length)
        {
            throw new IllegalArgumentException("Invalid parameter index: " + parameterIndex);
        }
        return programParameters[parameterIndex];
    }


    public String print()
    {
        return "arg" + parameterIndex;
    }
}
