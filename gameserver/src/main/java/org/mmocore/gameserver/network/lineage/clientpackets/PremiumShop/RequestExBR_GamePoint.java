package org.mmocore.gameserver.network.lineage.clientpackets.PremiumShop;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePoint;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.Player;

public class RequestExBR_GamePoint extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();

        if (player == null)
            return;

        AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePoint(player));
    }
}