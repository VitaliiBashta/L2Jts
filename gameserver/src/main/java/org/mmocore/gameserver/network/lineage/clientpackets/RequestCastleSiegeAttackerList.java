package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeAttackerList;
import org.mmocore.gameserver.object.Player;

public class RequestCastleSiegeAttackerList extends L2GameClientPacket {
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

        final Residence residence = ResidenceHolder.getInstance().getResidence(_unitId);
        if (residence != null) {
            sendPacket(new CastleSiegeAttackerList(residence));
        }
    }
}