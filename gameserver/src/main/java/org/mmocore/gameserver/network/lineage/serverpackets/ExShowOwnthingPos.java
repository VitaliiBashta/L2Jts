package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.events.objects.TerritoryWardObject;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 */
public class ExShowOwnthingPos extends GameServerPacket {
    private final List<WardInfo> wardList = new ArrayList<>(9);

    public ExShowOwnthingPos() {
        for (final Dominion dominion : ResidenceHolder.getInstance().getResidenceList(Dominion.class)) {
            if (dominion.getSiegeDate() == Residence.MIN_SIEGE_DATE)
                continue;

            final Integer[] flags = dominion.getFlags();
            for (final int dominionId : flags) {
                final TerritoryWardObject wardObject = dominion.getSiegeEvent().getFirstObject("ward_" + dominionId);
                final Location loc = wardObject.getWardLocation();
                if (loc != null) {
                    wardList.add(new WardInfo(dominionId, loc.x, loc.y, loc.z));
                }
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(wardList.size());
        for (final WardInfo wardInfo : wardList) {
            writeD(wardInfo.dominionId);
            writeD(wardInfo.x);
            writeD(wardInfo.y);
            writeD(wardInfo.z);
        }
    }

    private static class WardInfo {
        private final int dominionId;
        private final int x;
        private final int y;
        private final int z;

        public WardInfo(final int territoryId, final int x, final int y, final int z) {
            dominionId = territoryId;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}