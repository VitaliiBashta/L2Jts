package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.net.nio.impl.ReceivablePacket;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.util.List;


/**
 * Packets received by the game server from clients
 */
public abstract class L2GameClientPacket extends ReceivablePacket<GameClient> {
    public static final Logger _log = LoggerFactory.getLogger(L2GameClientPacket.class);

    @Override
    public final boolean read() {
        try {
            readImpl();
            return true;
        } catch (BufferUnderflowException e) {
            _client.onPacketReadFail();
            _log.error("Client: " + _client + " - Failed reading: " + getType() + " - Server Version: " +
                    GameServer.getInstance().getVersion().getRevisionNumber(), e);
        } catch (Exception e) {
            _log.error("Client: " + _client + " - Failed reading: " + getType() + " - Server Version: " +
                    GameServer.getInstance().getVersion().getRevisionNumber(), e);
        }

        return false;
    }

    protected abstract void readImpl() throws Exception;

    @Override
    public final void run() {
        final GameClient client = getClient();
        try {
            runImpl();
        } catch (Exception e) {
            _log.error("Client: " + client + " - Failed running: " + getType() + " - Server Version: " +
                    GameServer.getInstance().getVersion().getRevisionNumber(), e);
        }
    }

    protected abstract void runImpl() throws Exception;

    protected String readS(final int len) {
        final String ret = readS();
        return ret.length() > len ? ret.substring(0, len) : ret;
    }

    protected void sendPacket(final L2GameServerPacket packet) {
        getClient().sendPacket(packet);
    }

    protected void sendPacket(final L2GameServerPacket... packets) {
        getClient().sendPacket(packets);
    }

    protected void sendPackets(final List<L2GameServerPacket> packets) {
        getClient().sendPackets(packets);
    }

    public String getType() {
        return "[C] " + getClass().getSimpleName();
    }
}