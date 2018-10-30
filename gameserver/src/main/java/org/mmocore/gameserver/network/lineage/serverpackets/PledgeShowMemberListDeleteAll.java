package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeShowMemberListDeleteAll extends GameServerPacket {
    public static final GameServerPacket STATIC = new PledgeShowMemberListDeleteAll();

    @Override
    protected final void writeData() {
    }
}