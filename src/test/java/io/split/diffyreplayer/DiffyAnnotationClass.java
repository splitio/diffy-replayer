package io.split.diffyreplayer;


import io.split.diffyreplayer.condition.DiffyReplayerCondition;

/**
 * Used by Unit Tests.
 */
public class DiffyAnnotationClass implements DiffyReplayerCondition {
    @Override
    public boolean replay() {
        return false;
    }
}
