package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 * type:
 * 0 - player name
 * 1 - clan name
 * reason
 * 0 - during the server merge, your character name, S1, conflicted with another. Your name may still be available. Please enter your desired name
 * 1 - name is incorrect
 */
public class ExNeedToChangeName extends GameServerPacket {
    public static final int TYPE_PLAYER_NAME = 0;
    public static final int TYPE_CLAN_NAME = 1;

    public static final int REASON_EXISTS = 0;
    public static final int REASON_INVALID = 1;

    private final int type;
    private final int reason;
    private final String origName;

    public ExNeedToChangeName(final int type, final int reason, final String origName) {
        this.type = type;
        this.reason = reason;
        this.origName = origName;
    }

    @Override
    protected final void writeData() {
        writeD(type);
        writeD(reason);
        writeS(origName);
    }
}