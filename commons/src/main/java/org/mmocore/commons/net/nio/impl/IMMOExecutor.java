package org.mmocore.commons.net.nio.impl;


@FunctionalInterface
public interface IMMOExecutor<T extends MMOClient> {
    void execute(Runnable r);
}