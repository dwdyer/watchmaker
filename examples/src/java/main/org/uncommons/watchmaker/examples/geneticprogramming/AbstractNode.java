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

import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Dyer
 */
public abstract class AbstractNode implements Node
{
    private final int childCount;
    protected List<? extends Node> children = Collections.emptyList();

    protected AbstractNode(int childCount)
    {
        this.childCount = childCount;
    }


    public final int getChildCount()
    {
        return childCount;
    }


    public final void setChildren(List<? extends Node> children)
    {
        if (children.size() != getChildCount())
        {
            throw new IllegalArgumentException("Wrong number of parameters: " + children.size()
                                               + "(expected " + getChildCount() + ")");
        }
        this.children = children;
    }


    public int getDepth()
    {
        int childDepth = 0;
        for (Node node : children)
        {
            childDepth = Math.max(childDepth, node.getDepth());
        }
        return 1 + childDepth;
    }
}
