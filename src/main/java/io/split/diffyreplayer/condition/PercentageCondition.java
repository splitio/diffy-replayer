package io.split.diffyreplayer.condition;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Base Definition for all % based conditions, simply send a double between 0 and 1.
 */
public abstract class PercentageCondition implements DiffyReplayerCondition {

    private final double expected;
    private final static Random secureRandom = new SecureRandom();

    public PercentageCondition(double expected) {
        this.expected = expected;
    }

    @Override
    public boolean replay() {
        double actual = secureRandom.nextDouble();
        return actual < expected;
    }
}
