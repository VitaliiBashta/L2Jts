package org.mmocore.gameserver.model.entity.itemauction;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author n0nam3
 */
public class ItemAuction {
    private static final Logger _log = LoggerFactory.getLogger(ItemAuction.class);

    private static final long ENDING_TIME_EXTEND_5 = TimeUnit.MINUTES.toMillis(5);
    private static final long ENDING_TIME_EXTEND_8 = TimeUnit.MINUTES.toMillis(8);

    private final int _auctionId;
    private final int _instanceId;
    private final long _startingTime;
    private final long _endingTime;
    private final AuctionItem _auctionItem;
    private final TIntObjectHashMap<ItemAuctionBid> _auctionBids;

    private AtomicReference<ItemAuctionState> _auctionState;
    private int _scheduledAuctionEndingExtendState;
    private int _auctionEndingExtendState;

    private ItemAuctionBid _highestBid;
    private int _lastBidPlayerObjId;

    public ItemAuction(final int auctionId, final int instanceId, final long startingTime, final long endingTime, final AuctionItem auctionItem, final ItemAuctionState auctionState) {
        _auctionId = auctionId;
        _instanceId = instanceId;
        _startingTime = startingTime;
        _endingTime = endingTime;
        _auctionItem = auctionItem;
        _auctionBids = new TIntObjectHashMap<>();
        _auctionState = new AtomicReference<>(auctionState);
    }

    void addBid(final ItemAuctionBid bid) {
        _auctionBids.put(bid.getCharId(), bid);
        if (_highestBid == null || _highestBid.getLastBid() < bid.getLastBid()) {
            _highestBid = bid;
        }
    }

    public ItemAuctionState getAuctionState() {
        return _auctionState.get();
    }

    public boolean setAuctionState(final ItemAuctionState expected, final ItemAuctionState wanted) {
        if (!_auctionState.compareAndSet(expected, wanted))
            return false;

        store();

        return true;
    }

    public int getAuctionId() {
        return _auctionId;
    }

    public int getInstanceId() {
        return _instanceId;
    }

    public AuctionItem getAuctionItem() {
        return _auctionItem;
    }

    public ItemInstance createNewItemInstance() {
        return _auctionItem.createNewItemInstance();
    }

    public long getAuctionInitBid() {
        return _auctionItem.getPrice();
    }

    public ItemAuctionBid getHighestBid() {
        return _highestBid;
    }

    public int getAuctionEndingExtendState() {
        return _auctionEndingExtendState;
    }

    public int getScheduledAuctionEndingExtendState() {
        return _scheduledAuctionEndingExtendState;
    }

    public void setScheduledAuctionEndingExtendState(final int state) {
        _scheduledAuctionEndingExtendState = state;
    }

    public long getStartingTime() {
        return _startingTime;
    }

    public long getEndingTime() {
        if (_auctionEndingExtendState == 0) {
            return _endingTime;
        } else if (_auctionEndingExtendState == 1) {
            return _endingTime + ENDING_TIME_EXTEND_5;
        } else {
            return _endingTime + ENDING_TIME_EXTEND_8;
        }
    }

    public long getStartingTimeRemaining() {
        return Math.max(getEndingTime() - System.currentTimeMillis(), 0L);
    }

    public long getFinishingTimeRemaining() {
        return Math.max(getEndingTime() - System.currentTimeMillis(), 0L);
    }

    public void store() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO item_auction (auctionId,instanceId,auctionItemId,startingTime,endingTime,auctionStateId) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE auctionStateId=?");
            statement.setInt(1, _auctionId);
            statement.setInt(2, _instanceId);
            statement.setInt(3, _auctionItem.getItemId());
            statement.setLong(4, _startingTime);
            statement.setLong(5, _endingTime);
            statement.setInt(6, _auctionState.get().ordinal());
            statement.setInt(7, _auctionState.get().ordinal());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public int getAndSetLastBidPlayerObjectId(final int playerObjId) {
        final int lastBid = _lastBidPlayerObjId;
        _lastBidPlayerObjId = playerObjId;
        return lastBid;
    }

    public void updatePlayerBid(final ItemAuctionBid bid, final boolean delete) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            if (delete) {
                statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=? AND playerObjId=?");
                statement.setInt(1, _auctionId);
                statement.setInt(2, bid.getCharId());
            } else {
                statement = con.prepareStatement("INSERT INTO item_auction_bid (auctionId,playerObjId,playerBid) VALUES (?,?,?) ON DUPLICATE KEY UPDATE playerBid=?");
                statement.setInt(1, _auctionId);
                statement.setInt(2, bid.getCharId());
                statement.setLong(3, bid.getLastBid());
                statement.setLong(4, bid.getLastBid());
            }

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void registerBid(final Player player, final long newBid) {
        if (player == null) {
            throw new NullPointerException();
        }

        if (newBid < getAuctionInitBid()) {
            player.sendPacket(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            return;
        }

        if (newBid > AllSettingsConfig.ALT_ITEM_AUCTION_MAX_BID) {
            player.sendPacket(SystemMsg.BIDDING_IS_NOT_ALLOWED_BECAUSE_THE_MAXIMUM_BIDDING_PRICE_EXCEEDS_100_BILLION);
            return;
        }

        if (getAuctionState() != ItemAuctionState.STARTED) {
            return;
        }

        final int charId = player.getObjectId();

        synchronized (_auctionBids) {
            if (_highestBid != null && newBid < _highestBid.getLastBid()) {
                player.sendPacket(SystemMsg.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
                return;
            }

            ItemAuctionBid bid = getBidFor(charId);

            if (bid == null) {
                if (!reduceItemCount(player, newBid)) {
                    return;
                }

                bid = new ItemAuctionBid(charId, newBid);
                _auctionBids.put(charId, bid);

                onPlayerBid(player, bid);
                updatePlayerBid(bid, false);

                player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addNumber(newBid));
                return;
            }

            if (!AllSettingsConfig.ALT_ITEM_AUCTION_CAN_REBID) {
                player.sendPacket(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
                return;
            }

            if (bid.getLastBid() >= newBid) {
                player.sendPacket(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID);
                return;
            }

            if (!reduceItemCount(player, newBid - bid.getLastBid())) {
                return;
            }

            bid.setLastBid(newBid);
            onPlayerBid(player, bid);
            updatePlayerBid(bid, false);

            player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addNumber(newBid));
        }
    }

    private void onPlayerBid(final Player player, final ItemAuctionBid bid) {
        if (_highestBid == null) {
            _highestBid = bid;
        } else if (_highestBid.getLastBid() < bid.getLastBid()) {
            final Player old = _highestBid.getPlayer();
            if (old != null) {
                old.sendPacket(SystemMsg.YOU_HAVE_BEEN_OUTBID);
            }

            _highestBid = bid;
        }

        if (getEndingTime() - System.currentTimeMillis() <= 1000 * 60 * 10) {
            if (_auctionEndingExtendState == 0) {
                _auctionEndingExtendState = 1;
                broadcastToAllBidders(SystemMsg.BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES);
            } else if (_auctionEndingExtendState == 1 && getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                _auctionEndingExtendState = 2;
                broadcastToAllBidders(SystemMsg.BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES);
            }
        }
    }

    public void broadcastToAllBidders(final IBroadcastPacket packet) {
        final TIntObjectIterator<ItemAuctionBid> itr = _auctionBids.iterator();
        ItemAuctionBid bid = null;
        while (itr.hasNext()) {
            itr.advance();
            bid = itr.value();
            final Player player = bid.getPlayer();
            if (player != null) {
                player.sendPacket(packet);
            }
        }
    }

    public void cancelBid(final Player player) {
        if (player == null) {
            throw new NullPointerException();
        }

        switch (getAuctionState()) {
            case CREATED:
                player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                return;

            case FINISHED:
                if (_startingTime < System.currentTimeMillis() - AllSettingsConfig.ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS) {
                    player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                    return;
                } else {
                    break;
                }
        }

        final int charId = player.getObjectId();

        synchronized (_auctionBids) {
            // _highestBid == null logical consequence is that no one bet yet
            if (_highestBid == null) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                return;
            }

            final ItemAuctionBid bid = getBidFor(charId);
            if (bid == null || bid.isCanceled()) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU);
                return;
            }

            if (bid.getCharId() == _highestBid.getCharId()) {
                player.sendMessage(new CustomMessage("itemauction.custom"));
                return;
            }

            increaseItemCount(player, bid.getLastBid());
            player.sendPacket(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID);

            if (AllSettingsConfig.ALT_ITEM_AUCTION_CAN_REBID) {
                _auctionBids.remove(charId);
                updatePlayerBid(bid, true);
            } else {
                bid.cancelBid();
                updatePlayerBid(bid, false);
            }
        }
    }

    private boolean reduceItemCount(final Player player, final long count) {
        if (AllSettingsConfig.ALT_ITEM_AUCTION_BID_ITEM_ID == 57) {
            if (!player.reduceAdena(count, true)) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                return false;
            }

            return true;
        }

        return player.getInventory().destroyItemByItemId(AllSettingsConfig.ALT_ITEM_AUCTION_BID_ITEM_ID, count);
    }

    private void increaseItemCount(final Player player, final long count) {
        if (AllSettingsConfig.ALT_ITEM_AUCTION_BID_ITEM_ID == 57) {
            player.addAdena(count);
        } else {
            player.getInventory().addItem(AllSettingsConfig.ALT_ITEM_AUCTION_BID_ITEM_ID, count);
        }
        player.sendPacket(SystemMessage.obtainItems(AllSettingsConfig.ALT_ITEM_AUCTION_BID_ITEM_ID, count, 0));
    }

    /**
     * Returns the last bid for the given player or -1 if he did not made one yet.
     *
     * @param player The player that made the bid
     * @return The last bid the player made or -1
     */
    public long getLastBid(final Player player) {
        final ItemAuctionBid bid = getBidFor(player.getObjectId());
        return bid != null ? bid.getLastBid() : -1L;
    }

    public ItemAuctionBid getBidFor(final int charId) {
        return _auctionBids.get(charId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ItemAuction))
            return false;

        final ItemAuction that = (ItemAuction) o;

        if (_auctionId != that._auctionId)
            return false;
        if (_instanceId != that._instanceId)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _auctionId;
        result = 31 * result + _instanceId;
        return result;
    }
}