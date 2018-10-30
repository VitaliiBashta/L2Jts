package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExShowAgitInfo extends GameServerPacket {
    private List<AgitInfo> clanHalls = Collections.emptyList();

    public ExShowAgitInfo() {
        final List<ClanHall> chs = ResidenceHolder.getInstance().getResidenceList(ClanHall.class);
        clanHalls = new ArrayList<>(chs.size());

        for (final ClanHall clanHall : chs) {
            final int ch_id = clanHall.getId();
            final int getType;
            if (clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
                getType = 0;
            } else if (clanHall.getSiegeEvent().getClass() == ClanHallMiniGameEvent.class) {
                getType = 2;
            } else {
                getType = 1;
            }

            final Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
            final String clan_name = clanHall.getOwnerId() == 0 || clan == null ? StringUtils.EMPTY : clan.getName();
            final String leader_name = clanHall.getOwnerId() == 0 || clan == null ? StringUtils.EMPTY : clan.getLeaderName();
            clanHalls.add(new AgitInfo(clan_name, leader_name, ch_id, getType));
        }
    }

    @Override
    protected final void writeData() {
        writeD(clanHalls.size());
        for (final AgitInfo info : clanHalls) {
            writeD(info.ch_id);
            writeS(info.clan_name);
            writeS(info.leader_name);
            writeD(info.getType);
        }
    }

    static class AgitInfo {
        public final String clan_name;
        public final String leader_name;
        public final int ch_id;
        public final int getType;

        public AgitInfo(final String clan_name, final String leader_name, final int ch_id, final int lease) {
            this.clan_name = clan_name;
            this.leader_name = leader_name;
            this.ch_id = ch_id;
            this.getType = lease;
        }
    }
}