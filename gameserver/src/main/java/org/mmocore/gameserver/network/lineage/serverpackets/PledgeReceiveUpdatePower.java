package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeReceiveUpdatePower extends GameServerPacket {
    private final int privs;

    public PledgeReceiveUpdatePower(final int privs) {
        this.privs = privs;
    }

    @Override
    protected final void writeData() {
        writeD(privs); //Filler??????
    }
}