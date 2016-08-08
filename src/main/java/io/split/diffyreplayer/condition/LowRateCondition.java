package io.split.diffyreplayer.condition;

import io.split.diffyreplayer.DiffyReplayerProperties;

/**
 * DiffyReplayerCondition that loads from the properties file the DIFFY_LOW_RATE and use
 * that to define whether to accept or not a Diffy Request.
 */
public class LowRateCondition extends PercentageCondition {

    public LowRateCondition() {
        super(DiffyReplayerProperties.INSTANCE.getLowRate());
    }
}
