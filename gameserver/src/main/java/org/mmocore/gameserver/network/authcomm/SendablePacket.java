package org.mmocore.gameserver.network.authcomm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class SendablePacket extends org.mmocore.commons.net.nio.SendablePacket<AuthServerCommunication> {
    private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

    @Override
    public AuthServerCommunication getClient() {
        return AuthServerCommunication.getInstance();
    }

    @Override
    protected ByteBuffer getByteBuffer() {
        return getClient().getWriteBuffer();
    }

    public boolean write() {
        try {
            writeImpl();
        } catch (Exception e) {
            _log.error("", e);
        }
        return true;
    }

    protected abstract void writeImpl();
}
