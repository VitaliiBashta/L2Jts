package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.RankPrivs;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgePowerGradeList extends GameServerPacket {
    private final RankPrivs[] privs;

    public PledgePowerGradeList(final RankPrivs[] privs) {
        this.privs = privs;
    }

    @Override
    protected final void writeData() {
        writeD(privs.length);
        for (final RankPrivs element : privs) {
            writeD(element.getRank());
            writeD(element.getParty());
        }
    }
}