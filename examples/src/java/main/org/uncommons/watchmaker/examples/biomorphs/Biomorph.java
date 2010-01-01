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

/**
 * <p>Candidate representation for a biomorph.  We could just as easily have
 * used an array of integers or a bit string representation with Gray codes
 * but for clarity we will use this more object-oriented representation that
 * conveniently combines state and related logic.</p>
 *
 * <p>The Biomporph class encapsulates 9 genes as described by Dawkins in his
 * "The Evolution of Evolvability" paper.</p>
 *
 * @author Daniel Dyer
 */
public final class Biomorph
{
    /** The total number of genes that make up a biomorph. */
    public static final int GENE_COUNT = 9;
    /** The minimum permitted value for most genes. */
    public static final int GENE_MIN = -5;
    /** The maximum permitted value for most genes. */
    public static final int GENE_MAX = 5;
    /** The index of the gene that controls biomporph size. */
    public static final int LENGTH_GENE_INDEX = 8;
    /** The minimum permitted value for the length gene. */
    public static final int LENGTH_GENE_MIN = 1;
    /** The maximum permitted value for the length gene. */
    public static final int LENGTH_GENE_MAX = 7;

    private final int[] genes;

    private int[][] phenotype;


    /**
     * Creates a biomorph with the specified genes.
     * @param genes A 9-element array.  The final element must be a positive
     * value.
     */
    public Biomorph(int[] genes)
    {
        if (genes.length != GENE_COUNT)
        {
            throw new IllegalArgumentException("Biomorph must have " + GENE_COUNT + " genes.");
        }
        this.genes = genes.clone();
    }


    /**
     * Returns an array of integers that represent the graphical pattern
     * determined by the biomorph's genes.
     * @return A 2-dimensional array containing the 8-element dx and dy
     * arrays required to draw the biomorph.
     */
    public int[][] getPatternPhenotype()
    {
        if (phenotype == null)
        {
            // Decode the genes as per Dawkins' rules.
            int[] dx = new int[GENE_COUNT - 1];
            dx[3] = genes[0];
            dx[4] = genes[1];
            dx[5] = genes[2];

            dx[1] = -dx[3];
            dx[0] = -dx[4];
            dx[7] = -dx[5];

            dx[2] = 0;
            dx[6] = 0;

            int[] dy = new int[GENE_COUNT - 1];
            dy[2] = genes[3];
            dy[3] = genes[4];
            dy[4] = genes[5];
            dy[5] = genes[6];
            dy[6] = genes[7];

            dy[0] = dy[4];
            dy[1] = dy[3];
            dy[7] = dy[5];

            phenotype = new int[][]{dx, dy};
        }
        return phenotype;
    }


    /**
     * @return The value of the gene that controls the size of this biomorph.
     */
    public int getLengthPhenotype()
    {
        return genes[LENGTH_GENE_INDEX];
    }


    /**
     * Returns the 9 genes that make up the biomorph's genotype. 
     * @return A 9-element array containing this biomorph's genes.
     */
    public int[] getGenotype()
    {
        return genes.clone();
    }


    /**
     * Compares the genes of two Biomporph objects and returns true if they are
     * identical.
     * @param obj The object to compare with this one.
     * @return True if the argument is a Biomoprh instance and the 2
     * biomorphs have the same genes, false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        Biomorph biomorph = (Biomorph) obj;

        return Arrays.equals(genes, biomorph.genes);
    }


    /**
     * Over-ridden to be consistent with {@link #equals(Object)}.  Biomorphs
     * with identical genes return identical hash codes.
     * @return This object's hash code.
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(genes);
    }
}
