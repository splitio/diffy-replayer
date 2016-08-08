package io.split.diffyreplayer.condition;

/**
 * Simple interface to define when a Diffy Request is replayed.
 *
 * <p>
 *     IMPORTANT: If you define your own Condition, since we use reflection,
 *     the condition should have a Constructor with no parameters.
 * </p>
 */
public interface DiffyReplayerCondition {
    /**
     * @return whether to replay or not a request.
     */
    boolean replay();
}
