package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeDefenderList;
import org.mmocore.gameserver.object.Player;

public class RequestCastleSiegeDefenderList extends L2GameClientPacket {
    private int _unitId;

    @Override
    protected void readImpl() {
        _unitId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _unitId);
        if (castle == null || castle.getOwner() == null) {
            return;
        }

        player.sendPacket(new CastleSiegeDefenderList(castle));
    }
}