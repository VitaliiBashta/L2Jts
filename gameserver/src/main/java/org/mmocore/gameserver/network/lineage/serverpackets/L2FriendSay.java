package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Send Private (Friend) Message
 * <p/>
 * Format: c dSSS
 * <p/>
 * d: Unknown
 * S: Sending Player
 * S: Receiving Player
 * S: Message
 */
public class L2FriendSay extends GameServerPacket {
    private final String sender;
    private final String receiver;
    private final String message;

    public L2FriendSay(final String sender, final String reciever, final String message) {
        this.sender = sender;
        this.receiver = reciever;
        this.message = message;
    }

    @Override
    protected final void writeData() {
        writeD(0);
        writeS(receiver);
        writeS(sender);
        writeS(message);
    }
}