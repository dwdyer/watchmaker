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
package org.uncommons.watchmaker.examples.biomorphs;

import java.util.Arrays;
import org.testng.annotations.Test;

/**
 * Some basic sanity checks for the {@link Biomorph} type used in the
 * interactive evolution example program.
 * @author Daniel Dyer
 */
public class BiomorphTest
{
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInsufficientGenes()
    {
        new Biomorph(new int[Biomorph.GENE_COUNT - 1]);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTooManyGenes()
    {
        new Biomorph(new int[Biomorph.GENE_COUNT + 1]);
    }


    @Test
    public void testEquality()
    {
        Biomorph biomorph1 = new Biomorph(new int[Biomorph.GENE_COUNT]);
        Biomorph biomorph2 = new Biomorph(new int[Biomorph.GENE_COUNT]);
        int[] genes = new int[Biomorph.GENE_COUNT];
        Arrays.fill(genes, 2);
        Biomorph biomorph3 = new Biomorph(genes);

        assert biomorph1.equals(biomorph1) : "Equality must be reflexive.";
        assert biomorph1.equals(biomorph2) : "Biomorphs with identical genes should be considered equal.";
        assert biomorph2.equals(biomorph1) : "Equality must be reflective.";
        assert biomorph1.hashCode() == biomorph2.hashCode() : "Equal objects must have identical hash codes.";
        assert !biomorph1.equals(biomorph3) : "Biomorphs with different genes should not be considered equal.";

        assert !biomorph1.equals(null) : "No object should be considered equal to a null reference.";
        assert !biomorph3.equals(genes) : "Biomorphs should not be considered equal to objects of different types.";
    }
}
