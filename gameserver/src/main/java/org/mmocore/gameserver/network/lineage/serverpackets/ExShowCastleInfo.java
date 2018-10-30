package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ExShowCastleInfo extends GameServerPacket {
    private List<CastleInfo> infos = Collections.emptyList();

    public ExShowCastleInfo() {
        String ownerName;
        int id, tax, nextSiege;

        final List<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        infos = new ArrayList<>(castles.size());
        for (final Castle castle : castles) {
            ownerName = ClanTable.getInstance().getClanName(castle.getOwnerId());
            id = castle.getId();
            tax = castle.getTaxPercent();
            nextSiege = (int) castle.getSiegeDate().toEpochSecond();
            infos.add(new CastleInfo(ownerName, id, tax, nextSiege));
        }
    }

    @Override
    protected final void writeData() {
        writeD(infos.size());
        for (final CastleInfo info : infos) {
            writeD(info.id);
            writeS(info.ownerName);
            writeD(info.tax);
            writeD(info.nextSiege);
        }
        infos.clear();
    }

    private static class CastleInfo {
        public final String ownerName;
        public final int id;
        public final int tax;
        public final int nextSiege;

        public CastleInfo(final String ownerName, final int id, final int tax, final int nextSiege) {
            this.ownerName = ownerName;
            this.id = id;
            this.tax = tax;
            this.nextSiege = nextSiege;
        }
    }
}