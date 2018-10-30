package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00    ....H...........
 * 0010: 00 00 00 00 00                                     .....
 * <p/>
 * format   ddddd
 */
public class PledgeStatusChanged extends GameServerPacket {
    private final int leader_id;
    private final int clan_id;
    private final int level;
    private final int crestId, largeCrestId;

    public PledgeStatusChanged(final Clan clan) {
        leader_id = clan.getLeaderId();
        clan_id = clan.getClanId();
        level = clan.getLevel();
        crestId = clan.getCrestId();
        largeCrestId = clan.getCrestLargeId();
    }

    @Override
    protected final void writeData() {
        writeD(leader_id);
        writeD(clan_id);
        writeD(crestId);
        writeD(level);
        writeD(0);
        writeD(largeCrestId);
        writeD(0);
    }
}