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

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Unit test for {@link PolygonImageFactory}.
 * @author Daniel Dyer
 */
public class PolygonImageFactoryTest
{
    /**
     * Make sure that the generated images have the correct number of polygons,
     * that each polygon has the correct number of points and that all of the
     * points fall within the bounds of the specified canvas.
     */
    @Test
    public void testConstraints()
    {
        final int width = 100;
        final int height = 50;
        CandidateFactory<List<ColouredPolygon>> factory = new PolygonImageFactory(new Dimension(width, height));
        List<List<ColouredPolygon>> candidates = factory.generateInitialPopulation(20, ExamplesTestUtils.getRNG());
        for (List<ColouredPolygon> image : candidates)
        {
            assert image.size() == PolygonImageFactory.MINIMUM_POLYGON_COUNT
                : "Wrong number of polygons: " + image.size();
            for (ColouredPolygon polygon : image)
            {
                assert polygon.getVertices().size() == PolygonImageFactory.MINIMUM_VERTEX_COUNT
                    : "Wrong number of vertices: " + polygon.getVertices().size();
                for (Point point : polygon.getVertices())
                {
                    assert point.x >= 0 && point.x < width : "X out-of-range: " + point.x;
                    assert point.y >= 0 && point.y < height : "Y out-of-range: " + point.y;
                }
            }
        }
    }
}
