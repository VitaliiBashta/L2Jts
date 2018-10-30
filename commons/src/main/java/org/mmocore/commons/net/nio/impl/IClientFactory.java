package org.mmocore.commons.net.nio.impl;

@FunctionalInterface
public interface IClientFactory<T extends MMOClient> {
    T create(MMOConnection<T> con);
}