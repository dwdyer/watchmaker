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

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.*;

/**
 * Path2D helper functions.
 * <p/>
 * @see http://stackoverflow.com/questions/4479010/finding-if-path2d-self-intersects
 * @author Gili Tzabari
 */
public class Path2Ds
{
    /**
     * Indicates if a Path2D intersects itself.
     * <p/>
     * @return true if a Path2D intersects itself
     */
    public static boolean isSelfIntersecting(PathIterator path)
    {
        SortedSet<Line2D> lines = getLines(path);
        if (lines.size() <= 1)
            return false;

        Set<Line2D> candidates = new HashSet<Line2D>();
        for (Line2D line: lines)
        {
            if (Double.compare(line.getP1().distance(line.getP2()), 0) <= 0)
            {
                // Lines of length 0 do not cause self-intersection
                continue;
            }
            for (Iterator<Line2D> i = candidates.iterator(); i.hasNext();)
            {
                Line2D candidate = i.next();

                // Logic borrowed from Line2D.intersectsLine()
                int lineRelativeToCandidate1 = Line2D.relativeCCW(line.getX1(), line.getY1(), line.
                    getX2(),
                    line.getY2(), candidate.getX1(), candidate.getY1());
                int lineRelativeToCandidate2 = Line2D.relativeCCW(line.getX1(), line.getY1(), line.
                    getX2(),
                    line.getY2(), candidate.getX2(), candidate.getY2());
                int candidateRelativeToLine1 = Line2D.relativeCCW(candidate.getX1(),
                    candidate.getY1(),
                    candidate.getX2(), candidate.getY2(), line.getX1(), line.getY1());
                int candidateRelativeToLine2 = Line2D.relativeCCW(candidate.getX1(),
                    candidate.getY1(),
                    candidate.getX2(), candidate.getY2(), line.getX2(), line.getY2());
                boolean intersection = (lineRelativeToCandidate1 * lineRelativeToCandidate2 <= 0)
                    && (candidateRelativeToLine1 * candidateRelativeToLine2 <= 0);
                if (intersection)
                {
                    // Lines may share a point, so long as they extend in different directions
                    if (lineRelativeToCandidate1 == 0 && lineRelativeToCandidate2 != 0)
                    {
                        // candidate.P1 shares a point with line
                        if (candidateRelativeToLine1 == 0 && candidateRelativeToLine2 != 0)
                        {
                            // line.P1 == candidate.P1
                            continue;
                        }
                        if (candidateRelativeToLine1 != 0 && candidateRelativeToLine2 == 0)
                        {
                            // line.P2 == candidate.P1
                            continue;
                        }
                        // else candidate.P1 intersects line
                    }
                    else if (lineRelativeToCandidate1 != 0 && lineRelativeToCandidate2 == 0)
                    {
                        // candidate.P2 shares a point with line
                        if (candidateRelativeToLine1 == 0 && candidateRelativeToLine2 != 0)
                        {
                            // line.P1 == candidate.P2
                            continue;
                        }
                        if (candidateRelativeToLine1 != 0 && candidateRelativeToLine2 == 0)
                        {
                            // line.P2 == candidate.P2
                            continue;
                        }
                        // else candidate.P2 intersects line
                    }
                    else
                    {
                        // line and candidate overlap
                    }
                    return true;
                }
                if (candidate.getX2() < line.getX1())
                    i.remove();
            }
            candidates.add(line);
        }
        return false;
    }


    /**
     * Returns all lines in a path. The lines are constructed such that the starting point is found
     * on the left (or same x-coordinate) of the ending point.
     * <p/>
     * @param path the path
     * @return the lines, sorted in ascending order of the x-coordinate of the starting point and
     * ending point, respectively
     */
    private static SortedSet<Line2D> getLines(PathIterator path)
    {
        double[] coords = new double[6];
        SortedSet<Line2D> result = new TreeSet<Line2D>(new Comparator<Line2D>()
        {
            @Override
            public int compare(Line2D o1, Line2D o2)
            {
                int result = Double.compare(o1.getX1(), o2.getX1());
                if (result == 0)
                {
                    // Ensure we are consistent with equals()
                    return Double.compare(o1.getX2(), o2.getX2());
                }
                return result;
            }
        });
        if (path.isDone())
            return result;
        int type = path.currentSegment(coords);
        assert (type == PathIterator.SEG_MOVETO): type;
        Point.Double startPoint = new Point.Double(coords[0], coords[1]);
        Point.Double openPoint = startPoint;
        path.next();

        while (!path.isDone())
        {
            type = path.currentSegment(coords);
            assert (type != PathIterator.SEG_CUBICTO && type != PathIterator.SEG_QUADTO): type;
            switch (type)
            {
                case PathIterator.SEG_MOVETO:
                {
                    openPoint = startPoint;
                    break;
                }
                case PathIterator.SEG_CLOSE:
                {
                    coords[0] = openPoint.x;
                    coords[1] = openPoint.y;
                    break;
                }
            }
            Point.Double endPoint = new Point.Double(coords[0], coords[1]);
            if (Double.compare(startPoint.getX(), endPoint.getX()) < 0)
                result.add(new Line2D.Double(startPoint, endPoint));
            else
                result.add(new Line2D.Double(endPoint, startPoint));
            path.next();
            startPoint = endPoint;
        }
        return result;
    }
}
