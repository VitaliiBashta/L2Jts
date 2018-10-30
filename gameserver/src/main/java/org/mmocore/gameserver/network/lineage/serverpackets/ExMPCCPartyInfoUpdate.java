package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * ch Sddd
 */
public class ExMPCCPartyInfoUpdate extends GameServerPacket {
    final Player leader;
    private final int mode;
    private final int count;

    /**
     * @param party
     * @param mode  0 = Remove, 1 = Add
     */
    public ExMPCCPartyInfoUpdate(final Party party, final int mode) {
        Party party1 = party;
        this.mode = mode;
        count = party1.getMemberCount();
        leader = party1.getGroupLeader();
    }

    @Override
    protected void writeData() {
        writeS(leader.getName());
        writeD(leader.getObjectId());
        writeD(count);
        writeD(mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
    }
}