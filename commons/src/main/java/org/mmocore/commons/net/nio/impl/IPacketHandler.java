package org.mmocore.commons.net.nio.impl;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface IPacketHandler<T extends MMOClient> {
    ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}