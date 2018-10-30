package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExReplyDominionInfo extends GameServerPacket {
    private List<TerritoryInfo> dominionList = Collections.emptyList();

    public ExReplyDominionInfo() {
        final List<Dominion> dominions = ResidenceHolder.getInstance().getResidenceList(Dominion.class);
        dominionList = new ArrayList<>(dominions.size());

        for (final Dominion dominion : dominions) {
            if (dominion.getSiegeDate() == Residence.MIN_SIEGE_DATE)
                continue;

            dominionList.add(new TerritoryInfo(dominion.getId(), dominion.getName(), dominion.getOwner().getName(), dominion.getFlags(),
                    (int) (dominion.getSiegeDate().toEpochSecond())));
        }
    }

    @Override
    protected void writeData() {
        writeD(dominionList.size());
        for (final TerritoryInfo cf : dominionList) {
            writeD(cf.id);
            writeS(cf.terr);
            writeS(cf.clan);
            writeD(cf.flags.length);
            for (final int f : cf.flags) {
                writeD(f);
            }
            writeD(cf.startTime);
        }
    }

    private static class TerritoryInfo {
        public final int id;
        public final String terr;
        public final String clan;
        public final Integer[] flags;
        public final int startTime;

        public TerritoryInfo(final int id, final String terr, final String clan, final Integer[] flags,
                             final int startTime) {
            this.id = id;
            this.terr = terr;
            this.clan = clan;
            this.flags = Arrays.copyOf(flags, flags.length);
            this.startTime = startTime;
        }
    }
}