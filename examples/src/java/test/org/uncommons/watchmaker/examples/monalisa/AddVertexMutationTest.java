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
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for the {@link AddVertexMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class AddVertexMutationTest
{
    private final Dimension canvasSize = new Dimension(200, 200);
    private final PolygonImageFactory factory = new PolygonImageFactory(canvasSize);

    @Test
    public void testAddVertex()
    {
        ColouredPolygon polygon = factory.createRandomPolygon(ExamplesTestUtils.getRNG());
        List<ColouredPolygon> image = new ArrayList<ColouredPolygon>(1);
        image.add(polygon);

        EvolutionaryOperator<ColouredPolygon> mutation = new AddVertexMutation(canvasSize,
                                                                               Probability.ONE);

        List<ColouredPolygon> evolved = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Polygon count should not be altered by mutation.";
        List<Point> vertices = evolved.get(0).getVertices();
        assert vertices.size() == polygon.getVertices().size() + 1
            : "Polygon should have 1 extra point after mutation.";
    }


    @Test
    public void testZeroProbability()
    {
        ColouredPolygon polygon = factory.createRandomPolygon(ExamplesTestUtils.getRNG());
        List<ColouredPolygon> image = new ArrayList<ColouredPolygon>(1);
        image.add(polygon);

        EvolutionaryOperator<ColouredPolygon> mutation = new AddVertexMutation(canvasSize,
                                                                               Probability.ZERO);

        List<ColouredPolygon> evolved = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Polygon count should not be altered by mutation.";
        ColouredPolygon evolvedPolygon = evolved.get(0);
        List<Point> vertices = evolvedPolygon.getVertices();
        assert vertices.size() == polygon.getVertices().size() : "Polygon should have no extra points after mutation.";
        assert evolvedPolygon == polygon : "Polygon should not have been changed at all.";
    }


    /**
     * If the image already has the maximum permitted number of points, extra points
     * should not be added by mutation.
     */
    @Test
    public void testAddMaxPoints()
    {
        List<Point> points = new ArrayList<Point>(AddVertexMutation.MAX_VERTEX_COUNT);
        for (int i = 0; i < AddVertexMutation.MAX_VERTEX_COUNT; i++)
        {
            points.add(new Point(i, i));
        }
        ColouredPolygon polygon = new ColouredPolygon(Color.RED, points);
        List<ColouredPolygon> image = new ArrayList<ColouredPolygon>(1);
        image.add(polygon);

        EvolutionaryOperator<ColouredPolygon> mutation = new AddVertexMutation(canvasSize,
                                                                               Probability.ONE);

        List<ColouredPolygon> evolved = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Polygon count should not be altered by mutation.";
        ColouredPolygon evolvedPolygon = evolved.get(0);
        List<Point> vertices = evolvedPolygon.getVertices();
        assert vertices.size() == polygon.getVertices().size() : "Polygon should have no extra points after mutation.";
        assert evolvedPolygon == polygon : "Polygon should not have been changed at all.";
    }
}
