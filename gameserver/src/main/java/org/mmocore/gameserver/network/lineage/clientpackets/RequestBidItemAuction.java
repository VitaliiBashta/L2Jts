package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.ItemAuctionManager;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuction;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author n0nam3
 */
public final class RequestBidItemAuction extends L2GameClientPacket {
    private int _instanceId;
    private long _bid;

    @Override
    protected final void readImpl() {
        _instanceId = readD();
        _bid = readQ();
    }

    @Override
    protected final void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        final ItemInstance adena = activeChar.getInventory().getItemByItemId(57);
        if (_bid < 0 || adena == null || _bid > adena.getCount()) {
            return;
        }

        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getItemAuction(_instanceId);
        final NpcInstance broker = activeChar.getLastNpc();
        if (broker == null || broker.getNpcId() != _instanceId || activeChar.getDistance(broker.getX(), broker.getY()) > activeChar.getInteractDistance(broker)) {
            return;
        }
        if (instance != null) {
            final ItemAuction auction = instance.getCurrentAuction();
            if (auction != null) {
                auction.registerBid(activeChar, _bid);
            }
        }
    }
}
