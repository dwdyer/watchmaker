package org.uncommons.watchmaker.framework;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Unit tests for the {@link GenerationalEvolutionEngine} class making use of
 * {@alink Releasable} objects.
 * @author wjladams@gmail.com
 */
public class ReleasableTest {

    /**
     * Simple test to verify that Releasable objects are being released.
     */
    @Test
    public void testReleaseable() {
        ReleasablePool<ReleasableInteger> pool = new ReleasablePool<ReleasableInteger>(new ReleasableInteger(0));
        pool.setProtoype(new ReleasableInteger(pool));

        GenerationalEvolutionEngine<ReleasableInteger> engine2 = new GenerationalEvolutionEngine<ReleasableInteger>(new ReleasableStubFactory(pool),
                new ReleasableIntegerZeroMaker(pool),
                new ReleasableIntegerEvaluator(),
                new RouletteWheelSelection(),
                FrameworkTestUtils.getRNG());
        TerminationCondition tc = new TerminationCondition() {
            int count = 0;
            @Override
            public boolean shouldTerminate(PopulationData<?> populationData) {
                if (count < 20) {
                    count++;
                    return false;
                } else {
                    return true;
                }
            }
        };
        int eliteCount=3;
        int popSize = 10;
        engine2.evolve(popSize, eliteCount, tc);
        Assert.assertEquals(pool.size(), popSize - eliteCount);
    }

    /**
     * Simple evaluator for ReleasableIntegers
     * */
    private static final class ReleasableIntegerEvaluator implements FitnessEvaluator<ReleasableInteger> {

        @Override
        public double getFitness(ReleasableInteger candidate, List<? extends ReleasableInteger> population) {
            return candidate.value;
        }

        @Override
        public boolean isNatural() {
            return true;
        }
    }

    /**
     * Simple factory to create all 0 ReleasableInteger's
     * */
    private static final class ReleasableStubFactory extends AbstractCandidateFactory<ReleasableInteger> {
        private ReleasablePool<ReleasableInteger> pool;

        public ReleasableStubFactory(ReleasablePool<ReleasableInteger> pool) {
            this.pool = pool;
        }


        @Override
        public ReleasableInteger generateRandomCandidate(Random rng) {
            ReleasableInteger rval = pool.nextOrCreate();
            if (rval != null) {
                return rval;
            } else {
                rval = new ReleasableInteger(0);
                pool.add(rval);
                return rval;
            }
        }
    }
    /**
     * Trivial test operator that mutates all releasable integers into zeroes.
     */
    private static final class ReleasableIntegerZeroMaker implements EvolutionaryOperator<ReleasableInteger>
    {
        private final ReleasablePool<ReleasableInteger> pool;

        public ReleasableIntegerZeroMaker(ReleasablePool<ReleasableInteger> pool) {
            this.pool = pool;
        }

        public List<ReleasableInteger> apply(List<ReleasableInteger> selectedCandidates, Random rng)
        {
            List<ReleasableInteger> result = new ArrayList<ReleasableInteger>(selectedCandidates.size());
            for (int i = 0; i < selectedCandidates.size(); i++)
            {
                ReleasableInteger ri = pool.nextOrCreate();
                if (ri != null) {
                    result.add(ri);
                } else {
                    ri = new ReleasableInteger(0);
                    pool.add(ri);
                    result.add(ri);
                }
            }
            return result;
        }
    }

    /**
     * Simple Releasable integer class
     */
    private static class ReleasableInteger implements Releasable {
        public int value;
        private boolean isReleased = false;
        private ReleasablePool<ReleasableInteger> pool=null;

        public ReleasableInteger(ReleasablePool<ReleasableInteger> pool) {
            this.pool = pool;
            this.value = 0;
        }

        public ReleasableInteger(ReleasableInteger src) {
            this.pool = src.pool;
            this.value = src.value;
            this.isReleased = false;
        }

        public ReleasableInteger(int val) {
            this.value = val;
        }

        @Override
        public void release() {
            if (pool!=null) {
                pool.add(this);
            }
            isReleased = true;
        }

    }
}
