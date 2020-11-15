package org.uncommons.watchmaker.framework.termination;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;


/**
 * A termination validator that takes into account:
 * 1. A minimum generation count, before even considering termination
 * 2. A maximum generation count, once reached we consider it terminated
 * 3. The history of the fitness function for each generation
 * If we are between min/max number of generations, we look at the history
 * of fitness function values for timesMustBeClose before the current
 * generation.  If each successive value is within closeEnough of the
 * previous generation for timesMustBeClose, we consider it converged.
 */
public class ImprovementTermination implements TerminationCondition {
    final int minGenerationCount;
    final int maxGenerationCount;
    int genCount=0;
    double bestFitness=-1;
    double closeEnough=1e-5;
    double closeHistory[];
    double fitnessHistory[];
    int timesMustBeClose;
    private boolean shouldTerminate=false;
    public ImprovementTermination(int minGenCount, int maxGenCount, double closeEnoughPercent, int timesMustBeClose) {
        maxGenerationCount=maxGenCount;
        minGenerationCount=minGenCount;
        this.closeEnough=closeEnoughPercent;
        closeHistory=new double[maxGenerationCount];
        fitnessHistory=new double[maxGenerationCount];
        this.timesMustBeClose=timesMustBeClose;
    }


    public boolean shouldTerminate(PopulationData<?> arg0) {
        //First see if shouldTerminate flag set
        if (shouldTerminate) {
            //Yes we should terminate, we are done
            return true;
        }
        //Calculate new percent diff
        double newBest=arg0.getBestCandidateFitness();
        double perDiff=percentDiff(bestFitness, newBest);
        if (genCount >= maxGenerationCount) {
            return true;
        }
        closeHistory[genCount]=perDiff;
        fitnessHistory[genCount]=newBest;
        bestFitness=newBest;
        genCount++;
        if (genCount < minGenerationCount)
            return false;
        if (genCount >= timesMustBeClose) {
            if (closeLongEnough()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private boolean closeLongEnough() {
        for(int i=(genCount-timesMustBeClose); i<genCount; i++) {
            if (closeHistory[i] > closeEnough) {
                return false;
            }
        }
        return true;
    }

    public void setTerminate(boolean b) {
        // TODO Auto-generated method stub
        this.shouldTerminate=b;
    }

    /**
     * A standard percent difference calculation where the first
     * parameter is assumed to be known.  If that parameter is
     * zero, then no division by zero occurs.
     * @param oldV The "correct value", as used by percent difference calculations.
     * @param newV The "incorrect value", as used by percent difference calculations.
     * @return absolute value of (newV - oldV)/oldV assuming oldV!=0, or just newV otherwise.
     */
    public static double percentDiff(double oldV, double newV) {
        double diff=Math.abs(newV-oldV);
        if (oldV==0)
            return diff;
        return (diff/Math.abs(oldV));
    }


}
