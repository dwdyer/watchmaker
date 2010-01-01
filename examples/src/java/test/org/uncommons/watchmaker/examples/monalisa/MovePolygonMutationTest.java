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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for the {@link MovePolygonMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class MovePolygonMutationTest
{
    private final PolygonImageFactory factory = new PolygonImageFactory(new Dimension(200, 200));

    @Test
    public void testMovePolygon()
    {
        List<ColouredPolygon> image = Arrays.asList(factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()));
        List<List<ColouredPolygon>> list = new ArrayList<List<ColouredPolygon>>(1);
        list.add(image);

        EvolutionaryOperator<List<ColouredPolygon>> mutation = new MovePolygonMutation(Probability.ONE);

        List<List<ColouredPolygon>> evolved = mutation.apply(list, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Population size should not be altered by mutation.";
        assert evolved.get(0).size() == image.size() : "Image should have same number of polygons after mutation.";
        // Can't reliably test that the order was mutated because the random selection may have moved
        // the polygon back to its original index.
    }


    @Test
    public void testZeroProbability()
    {
        ColouredPolygon polygon1 = factory.createRandomPolygon(ExamplesTestUtils.getRNG());
        ColouredPolygon polygon2 = factory.createRandomPolygon(ExamplesTestUtils.getRNG());
        ColouredPolygon polygon3 = factory.createRandomPolygon(ExamplesTestUtils.getRNG());
        List<ColouredPolygon> image = Arrays.asList(polygon1, polygon2, polygon3);
        List<List<ColouredPolygon>> list = new ArrayList<List<ColouredPolygon>>(1);
        list.add(image);

        EvolutionaryOperator<List<ColouredPolygon>> mutation = new MovePolygonMutation(Probability.ZERO);

        List<List<ColouredPolygon>> evolved = mutation.apply(list, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Population size should not be altered by mutation.";
        assert evolved.get(0).size() == image.size() : "Image should have same number of polygons.";
        assert evolved.get(0) == image : "Image should not have been changed at all.";
        assert evolved.get(0).get(0) == polygon1 : "First polygon should not have moved.";
        assert evolved.get(0).get(1) == polygon2 : "Second polygon should not have moved.";
        assert evolved.get(0).get(2) == polygon3 : "Third polygon should not have moved.";
    }
}
