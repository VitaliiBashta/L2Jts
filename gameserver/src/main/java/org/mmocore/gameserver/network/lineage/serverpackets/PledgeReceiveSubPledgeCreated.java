package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeReceiveSubPledgeCreated extends GameServerPacket {
    private final int type;
    private final String name;
    private final String leader_name;

    public PledgeReceiveSubPledgeCreated(final SubUnit subPledge) {
        type = subPledge.getType();
        name = subPledge.getName();
        leader_name = subPledge.getLeaderName();
    }

    @Override
    protected final void writeData() {
        writeD(0x01);
        writeD(type);
        writeS(name);
        writeS(leader_name);
    }
}