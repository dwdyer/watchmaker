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
 * Unit test for the {@link RemovePolygonMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class RemovePolygonMutationTest
{
    private final PolygonImageFactory factory = new PolygonImageFactory(new Dimension(200, 200));

    @Test
    public void testRemovePolygon()
    {
        List<ColouredPolygon> image = Arrays.asList(factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()));
        List<List<ColouredPolygon>> list = new ArrayList<List<ColouredPolygon>>(1);
        list.add(image);

        EvolutionaryOperator<List<ColouredPolygon>> mutation = new RemovePolygonMutation(Probability.ONE);

        List<List<ColouredPolygon>> evolved = mutation.apply(list, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Population size should not be altered by mutation.";
        assert evolved.get(0).size() == image.size() - 1 : "Image should have 1 fewer polygon after mutation.";
    }


    @Test
    public void testZeroProbability()
    {
        List<ColouredPolygon> image = Arrays.asList(factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()),
                                                    factory.createRandomPolygon(ExamplesTestUtils.getRNG()));
        List<List<ColouredPolygon>> list = new ArrayList<List<ColouredPolygon>>(1);
        list.add(image);

        EvolutionaryOperator<List<ColouredPolygon>> mutation = new RemovePolygonMutation(Probability.ZERO);

        List<List<ColouredPolygon>> evolved = mutation.apply(list, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Population size should not be altered by mutation.";
        assert evolved.get(0).size() == image.size() : "Image should have same number of polygons.";
        assert evolved.get(0) == image : "Image should not have been changed at all.";
    }


    /**
     * If the image already has the minimum permitted number of polygons, further polygons
     * should not be removed by mutation.
     */
    @Test
    public void testRemoveMinPolygons()
    {
        List<ColouredPolygon> image = factory.generateRandomCandidate(ExamplesTestUtils.getRNG());
        assert image.size() == 2 : "Image should have 2 polygons";
        List<List<ColouredPolygon>> list = new ArrayList<List<ColouredPolygon>>(1);
        list.add(image);

        EvolutionaryOperator<List<ColouredPolygon>> mutation = new RemovePolygonMutation(Probability.ONE);

        List<List<ColouredPolygon>> evolved = mutation.apply(list, ExamplesTestUtils.getRNG());
        assert evolved.size() == 1 : "Population size should not be altered by mutation.";
        assert evolved.get(0).size() == image.size() : "Image should have no fewer than the minimum number of polygons.";
        assert evolved.get(0) == image : "Image should not have been changed at all.";
    }
}
