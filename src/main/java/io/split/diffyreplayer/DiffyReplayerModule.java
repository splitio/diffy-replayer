package io.split.diffyreplayer;

import com.google.inject.AbstractModule;

/**
 * Guice module for DiffyReplayer.
 *
 * If you use Guice for configuration all you need is to use this module.
 */
public class DiffyReplayerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DiffyReplayerFilter.class);
    }
}
