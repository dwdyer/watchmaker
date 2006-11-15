package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Evolutionary operator that simply returns the selected candidates unaltered.
 * This can be useful when combined with {@link SplitEvolution} so that a
 * proportion of the selected candidates can be copied unaltered into the next
 * generation while the remainder are evolved.
 * @author Daniel Dyer
 */
public class IdentityOperator implements EvolutionaryOperator<Object>
{
    public <S> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        return new ArrayList<S>(selectedCandidates);
    }
}
