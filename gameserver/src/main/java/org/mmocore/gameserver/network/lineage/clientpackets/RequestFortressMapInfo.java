package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowFortressMapInfo;
import org.mmocore.gameserver.object.Player;

public class RequestFortressMapInfo extends L2GameClientPacket {
    private int _fortressId;

    @Override
    protected void readImpl() {
        _fortressId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }
        final Fortress fortress = ResidenceHolder.getInstance().getResidence(Fortress.class, _fortressId);
        if (fortress != null) {
            sendPacket(new ExShowFortressMapInfo(fortress));
        }
    }
}