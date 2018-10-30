package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeInfo extends GameServerPacket {
    private final int clan_id;
    private final String clan_name;
    private final String ally_name;

    public PledgeInfo(final Clan clan) {
        clan_id = clan.getClanId();
        clan_name = clan.getName();
        ally_name = clan.getAlliance() == null ? "" : clan.getAlliance().getAllyName();
    }

    @Override
    protected final void writeData() {
        writeD(clan_id);
        writeS(clan_name);
        writeS(ally_name);
    }
}