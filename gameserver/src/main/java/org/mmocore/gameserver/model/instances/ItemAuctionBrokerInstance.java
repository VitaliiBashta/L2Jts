package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.manager.ItemAuctionManager;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuction;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionInstance;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionState;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExItemAuctionInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public final class ItemAuctionBrokerInstance extends NpcInstance {
    private ItemAuctionInstance auctionInstance;

    public ItemAuctionBrokerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        switch (getNpcId()) {
            case 32320:
                showChatWindow(player, "pts/auction_manager/auction_manager1001.htm");
                break;
            case 32321:
                showChatWindow(player, "pts/auction_manager/auction_manager2001.htm");
                break;
            case 32322:
                showChatWindow(player, "pts/auction_manager/auction_manager3001.htm");
                break;
        }
    }

    @Override
    public final void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("item_auction_bid")) {
            if (auctionInstance == null) {
                auctionInstance = ItemAuctionManager.getInstance().getItemAuction(getNpcId());
                if (auctionInstance == null) {
                    player.sendPacket(SystemMsg.IT_IS_NOT_AN_AUCTION_PERIOD);
                    return;
                }
            }

            final ItemAuction currentAuction = auctionInstance.getCurrentAuction();
            final ItemAuction nextAuction = auctionInstance.getNextAuction();

            if (currentAuction == null) {
                player.sendPacket(SystemMsg.IT_IS_NOT_AN_AUCTION_PERIOD);
                return;
            }

            if (!player.getAndSetLastItemAuctionRequest()) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                return;
            }

            player.sendPacket(new ExItemAuctionInfo(false, currentAuction, nextAuction));
        } else if (command.equalsIgnoreCase("item_auction_withdraw")) {
            if (auctionInstance == null) {
                auctionInstance = ItemAuctionManager.getInstance().getItemAuction(getNpcId());
                if (auctionInstance == null) {
                    player.sendPacket(SystemMsg.IT_IS_NOT_AN_AUCTION_PERIOD);
                    return;
                }
            }

            final ItemAuction[] auctions = auctionInstance.getAuctionsByBidder(player.getObjectId());

            if (auctions == null || auctions.length == 0) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                return;
            }
            for (final ItemAuction auction : auctions) {
                if (auction.getAuctionState() == ItemAuctionState.FINISHED) {
                    auction.cancelBid(player);
                } else {
                    player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                    continue;
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}