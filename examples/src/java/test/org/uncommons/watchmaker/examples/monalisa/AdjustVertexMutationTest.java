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
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for the {@link AdjustVertexMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class AdjustVertexMutationTest
{
    private final Dimension canvasSize = new Dimension(200, 200);

    @Test
    public void testAdjustVertex()
    {
        final Point point1 = new Point(1, 1);
        final Point point2 = new Point(2, 2);
        final Point point3 = new Point(3, 3);
        List<Point> points = Arrays.asList(point1, point2, point3);
        ColouredPolygon polygon = new ColouredPolygon(Color.RED, points);
        List<ColouredPolygon> image = new ArrayList<ColouredPolygon>(1);
        image.add(polygon);

        final int amount = 5;        
        EvolutionaryOperator<ColouredPolygon> mutation = new AdjustVertexMutation(canvasSize,
                                                                                  Probability.ONE,
                                                                                  new ConstantGenerator<Integer>(amount));

        List<ColouredPolygon> evolved = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Polygon count should not be altered by mutation.";
        ColouredPolygon evolvedPolygon = evolved.get(0);
        List<Point> vertices = evolvedPolygon.getVertices();
        assert vertices.size() == polygon.getVertices().size() : "Polygon should have same number of points after mutation.";

        if (vertices.get(0) != point1)
        {
            Point newPoint = vertices.get(0);
            assert newPoint.x == point1.x + 5 : "X-coordinate not mutated properly.";
            assert newPoint.y == point1.y + 5 : "Y-coordinate not mutated properly.";
            // If the first point is different the other two should be the same.
            assert vertices.get(1) == point2 : "Second point should be unchanged.";
            assert vertices.get(2) == point3 : "Third point should be unchanged.";
        }
        else if (vertices.get(1) != point2)
        {
            Point newPoint = vertices.get(1);
            assert newPoint.x == point2.x + 5 : "X-coordinate not mutated properly.";
            assert newPoint.y == point2.y + 5 : "Y-coordinate not mutated properly.";
            // If the second point is different the other two should be the same.
            assert vertices.get(0) == point1 : "First point should be unchanged.";
            assert vertices.get(2) == point3 : "Third point should be unchanged.";
        }
        else if (vertices.get(2) != point3)
        {
            Point newPoint = vertices.get(2);
            assert newPoint.x == point3.x + 5 : "X-coordinate not mutated properly.";
            assert newPoint.y == point3.y + 5 : "Y-coordinate not mutated properly.";
            // If the third point is different the other two should be the same.
            assert vertices.get(0) == point1 : "Third point should be unchanged.";
            assert vertices.get(1) == point2 : "Second point should be unchanged.";
        }
    }


    @Test
    public void testZeroProbability()
    {
        Point point1 = new Point(1, 1);
        Point point2 = new Point(2, 2);
        Point point3 = new Point(3, 3);
        List<Point> points = Arrays.asList(point1, point2, point3);
        ColouredPolygon polygon = new ColouredPolygon(Color.RED, points);
        List<ColouredPolygon> image = new ArrayList<ColouredPolygon>(1);
        image.add(polygon);

        EvolutionaryOperator<ColouredPolygon> mutation = new AdjustVertexMutation(canvasSize,
                                                                                  Probability.ZERO,
                                                                                  new ConstantGenerator<Integer>(1));

        List<ColouredPolygon> evolved = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Polygon count should not be altered by mutation.";
        ColouredPolygon evolvedPolygon = evolved.get(0);
        List<Point> vertices = evolvedPolygon.getVertices();
        assert vertices.size() == polygon.getVertices().size() : "Polygon should have same number of points after mutation.";
        assert evolvedPolygon == polygon : "Polygon should not have been changed at all.";
        assert vertices.get(0) == point1 : "First point should be unchanged.";
        assert vertices.get(1) == point2 : "Second point should be unchanged.";
        assert vertices.get(2) == point3 : "Third point should be unchanged.";
    }
}
