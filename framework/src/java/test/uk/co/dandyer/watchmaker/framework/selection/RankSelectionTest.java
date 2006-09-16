package uk.co.dandyer.watchmaker.framework.selection;

import org.testng.annotations.Test;
import uk.co.dandyer.watchmaker.framework.SelectionStrategy;
import uk.co.dandyer.watchmaker.framework.Pair;
import uk.co.dandyer.maths.random.MersenneTwisterRNG;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Unit test for rank-proportionate selection.
 * @author Daniel Dyer
 */
public class RankSelectionTest
{
    /**
     * Test selection when scores are normalised (higher is better).
     */
    @Test
    public void testNormalisedSelection()
    {
        SelectionStrategy selector = new RankSelection();
        List<Pair<String, Double>> population = new ArrayList<Pair<String, Double>>(4);
        // Higher score is better.
        Pair<String, Double> steve = new Pair<String, Double>("Steve", 10.0);
        Pair<String, Double> john = new Pair<String, Double>("John", 4.5);
        Pair<String, Double> mary = new Pair<String, Double>("Mary", 1.0);
        Pair<String, Double> gary = new Pair<String, Double>("Gary", 0.5);
        population.add(steve);
        population.add(john);
        population.add(mary);
        population.add(gary);
        List<String> selection = selector.select(population, 4, new MersenneTwisterRNG());
        assert selection.size() == 4 : "Selection size is " + selection.size() + ", should be 4.";
        int steveCount = Collections.frequency(selection, steve.getFirst());
        int johnCount = Collections.frequency(selection, john.getFirst());
        int garyCount = Collections.frequency(selection, gary.getFirst());
        int maryCount = Collections.frequency(selection, mary.getFirst());
        assert steveCount >= 1 && steveCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + steveCount + ")";
        assert johnCount >= 1 && johnCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + johnCount + ")";
        assert garyCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + garyCount + ")";
        assert maryCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + maryCount + ")";
    }


    /**
     * Test selection when scores are de-normalised (lower is better).
     */
    @Test
    public void testDenormalisedSelection()
    {
        SelectionStrategy selector = new RankSelection();
        List<Pair<String, Double>> population = new ArrayList<Pair<String, Double>>(4);
        // Lower score is better.
        Pair<String, Double> gary = new Pair<String, Double>("Gary", 0.5);
        Pair<String, Double> mary = new Pair<String, Double>("Mary", 1.0);
        Pair<String, Double> john = new Pair<String, Double>("John", 4.5);
        Pair<String, Double> steve = new Pair<String, Double>("Steve", 10.0);
        population.add(gary);
        population.add(mary);
        population.add(john);
        population.add(steve);
        List<String> selection = selector.select(population, 4, new MersenneTwisterRNG());
        assert selection.size() == 4 : "Selection size is " + selection.size() + ", should be 4.";
        int garyCount = Collections.frequency(selection, gary.getFirst());
        int maryCount = Collections.frequency(selection, mary.getFirst());
        int johnCount = Collections.frequency(selection, john.getFirst());
        int steveCount = Collections.frequency(selection, steve.getFirst());
        assert garyCount >= 1 && garyCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + garyCount + ")";
        assert maryCount >= 1 && maryCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + maryCount + ")";
        assert johnCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + johnCount + ")";
        assert steveCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + steveCount + ")";
    }
}
