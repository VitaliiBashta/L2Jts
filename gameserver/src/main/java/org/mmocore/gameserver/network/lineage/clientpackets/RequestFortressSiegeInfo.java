package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowFortressSiegeInfo;
import org.mmocore.gameserver.object.Player;

import java.util.List;

public class RequestFortressSiegeInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        final List<Fortress> fortressList = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
        for (final Fortress fort : fortressList) {
            if (fort != null && fort.getSiegeEvent().isInProgress()) {
                activeChar.sendPacket(new ExShowFortressSiegeInfo(fort));
            }
        }
    }
}