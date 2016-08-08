package io.split.diffyreplayer.condition;

import io.split.diffyreplayer.DiffyReplayerProperties;

/**
 * Example on how to give a different condition based on environment.
 */
public class ProdMediumCondition extends PercentageCondition {

    public ProdMediumCondition() {
        super("prod".equals(DiffyReplayerProperties.INSTANCE.getEnvironment()) ?
                DiffyReplayerProperties.INSTANCE.getMediumRate() :
                DiffyReplayerProperties.INSTANCE.getLowRate());
    }
}
