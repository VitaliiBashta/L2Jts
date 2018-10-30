package org.mmocore.commons.net.nio.impl;

import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface IAcceptFilter {
    boolean accept(SocketChannel sc);
}
