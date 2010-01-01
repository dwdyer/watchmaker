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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Creates random polygon-based images.
 * @author Daniel Dyer
 */
public class PolygonImageFactory extends AbstractCandidateFactory<List<ColouredPolygon>>
{
    /**
     * Each image must have at least 2 polygons.
     */
    static final int MINIMUM_POLYGON_COUNT = 2;

    /**
     * Each polygon must have at least 3 points.
     */
    static final int MINIMUM_VERTEX_COUNT = 3;

    private final Dimension canvasSize;

    /**
     * @param canvasSize The size of the canvas on which the image will be rendered.
     * All polygons must fit within its bounds. 
     */
    public PolygonImageFactory(Dimension canvasSize)
    {
        this.canvasSize = canvasSize;
    }

    
    public List<ColouredPolygon> generateRandomCandidate(Random rng)
    {
        List<ColouredPolygon> polygons = new ArrayList<ColouredPolygon>(MINIMUM_POLYGON_COUNT);
        for (int i = 0; i < MINIMUM_POLYGON_COUNT; i++)
        {
            polygons.add(createRandomPolygon(rng));
        }
        return polygons;
    }


    ColouredPolygon createRandomPolygon(Random rng)
    {
        List<Point> vertices = new ArrayList<Point>(MINIMUM_VERTEX_COUNT);
        for (int j = 0; j < MINIMUM_VERTEX_COUNT; j++)
        {
            vertices.add(new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
        }
        Color colour = new Color(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        return new ColouredPolygon(colour, vertices);
    }
}
