package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.ItemAuctionManager;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuction;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExItemAuctionInfo;
import org.mmocore.gameserver.object.Player;

/**
 * @author n0nam3
 */
public final class RequestInfoItemAuction extends L2GameClientPacket {
    private int _instanceId;

    @Override
    protected final void readImpl() {
        _instanceId = readD();
    }

    @Override
    protected final void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.getAndSetLastItemAuctionRequest();

        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getItemAuction(_instanceId);
        if (instance == null) {
            return;
        }

        final ItemAuction auction = instance.getCurrentAuction();
        final NpcInstance broker = activeChar.getLastNpc();
        if (auction == null || broker == null || broker.getNpcId() != _instanceId || activeChar.getDistance(broker.getX(), broker.getY()) >
                activeChar.getInteractDistance(broker)) {
            return;
        }

        activeChar.sendPacket(new ExItemAuctionInfo(true, auction, instance.getNextAuction()));
    }
}