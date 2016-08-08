package io.split.diffyreplayer.condition;

/**
 * Simple DiffyReplayerCondition that accepts all requests.
 */
public class AcceptAllCondition implements DiffyReplayerCondition {
    @Override
    public boolean replay() {
        return true;
    }
}
