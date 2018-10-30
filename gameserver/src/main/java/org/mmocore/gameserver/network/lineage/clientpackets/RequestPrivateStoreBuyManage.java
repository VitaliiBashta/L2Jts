package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.PrivateStoreManageListBuy;
import org.mmocore.gameserver.object.Player;

public class RequestPrivateStoreBuyManage extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();

        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (player.isAlikeDead()) {
            player.sendActionFailed();
            return;
        }

        if (player.isInOlympiadMode()) {
            player.sendActionFailed();
            return;
        }
        if (player.getMountType() != 0) {
            return;
        }
        if (player.getPrivateStoreType() == Player.STORE_PRIVATE_BUY || player.getPrivateStoreType() == Player.STORE_PRIVATE_BUY + 1) {
            player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
            player.standUp();
            player.broadcastCharInfo();
        }

        if (player.getPrivateStoreType() == Player.STORE_PRIVATE_NONE) {
            player.setPrivateStoreType(Player.STORE_PRIVATE_BUY + 1);
            player.standUp();
            player.broadcastCharInfo();
            player.sendPacket(new PrivateStoreManageListBuy(player));
        }
    }
}