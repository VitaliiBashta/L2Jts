package org.mmocore.gameserver.network.lineage.clientpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_RecentProductListPacket;
import org.mmocore.gameserver.object.Player;

public class RequestExBR_RecentProductList extends L2GameClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = getClient().getActiveChar();

        if (player == null)
            return;

        player.sendPacket(new ExBR_RecentProductListPacket(player));
    }
}