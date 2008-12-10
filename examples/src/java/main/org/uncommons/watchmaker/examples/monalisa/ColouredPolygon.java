// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

/**
 * A coloured polygon consists of a set of vertices and a colour that is used to
 * fill the polygon when it is rendered.
 * @author Daniel Dyer
 */
public class ColouredPolygon
{
    private final Color colour;
    private final List<Point> vertices;
    private final Polygon polygon;

    
    public ColouredPolygon(Color colour, List<Point> vertices)
    {
        this.colour = colour;
        this.vertices = vertices;
        this.polygon = new Polygon();
        for (Point point : vertices)
        {
            polygon.addPoint(point.x, point.y);
        }
    }


    public Color getColour()
    {
        return colour;
    }


    public List<Point> getVertices()
    {
        return vertices;
    }


    public Polygon getPolygon()
    {
        return polygon;
    }
}
