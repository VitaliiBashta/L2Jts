package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;


public class PledgeReceiveWarList extends GameServerPacket {
    private final List<WarInfo> infos = new ArrayList<>();
    private final int updateType;
    @SuppressWarnings("unused")
    private int page;

    public PledgeReceiveWarList(final Clan clan, final int type, final int page) {
        updateType = type;
        this.page = page;

        final List<Clan> clans = updateType == 1 ? clan.getAttackerClans() : clan.getEnemyClans();
        for (final Clan _clan : clans) {
            if (_clan == null) {
                continue;
            }
            infos.add(new WarInfo(_clan.getName(), updateType, 0));
        }
    }

    @Override
    protected final void writeData() {
        writeD(updateType); //which type of war list sould be revamped by this packet
        writeD(0x00); //page number goes here(_page ), made it static cuz not sure how many war to add to one page so TODO here
        writeD(infos.size());
        for (final WarInfo _info : infos) {
            writeS(_info.clan_name);
            writeD(_info.unk1);
            writeD(_info.unk2); //filler ??
        }
    }

    static class WarInfo {
        public final String clan_name;
        public final int unk1;
        public final int unk2;

        public WarInfo(final String _clan_name, final int _unk1, final int _unk2) {
            clan_name = _clan_name;
            unk1 = _unk1;
            unk2 = _unk2;
        }
    }
}