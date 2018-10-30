package org.mmocore.gameserver.model.entity.itemauction;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.manager.ItemAuctionManager;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.model.mail.Mail.SenderType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.TimeUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemAuctionInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemAuctionInstance.class);

    private static final long START_TIME_SPACE = TimeUnit.MINUTES.toMillis(1);
    private static final long FINISH_TIME_SPACE = TimeUnit.MINUTES.toMillis(10);
    private static final AtomicInteger nextId;

    static {
        nextId = new AtomicInteger();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1");
            rset = statement.executeQuery();
            if (rset.next()) {
                nextId.set(rset.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error("ItemAuctionManager: Failed loading auctions.", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    private final int npcId;
    private final int doing;
    private final CronExpression startTime;
    private final long duration;
    // ЕЩВЩ зачем нужен itemCount ?
    private final int itemCount;
    private final List<AuctionItem> items;
    private final TIntObjectHashMap<ItemAuction> _auctions;
    private ItemAuction _currentAuction;
    private ItemAuction _nextAuction;
    private ScheduledFuture<?> _stateTask;

    public ItemAuctionInstance(final int npcId, final int doing, final CronExpression startTime, final long duration, final int itemCount,
                               final List<AuctionItem> items) {
        this.npcId = npcId;
        this.doing = doing;
        this.startTime = startTime;
        this.duration = duration;
        this.itemCount = itemCount;
        this.items = new LinkedList<>(items);

        _auctions = new TIntObjectHashMap<>();

        load();

        LOGGER.info("ItemAuction: Loaded {} item(s) and registered {} auction(s) for instance {}" + '.', this.items.size(), _auctions.size(), npcId);

        checkAndSetCurrentAndNextAuction();
    }

    private void load() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT auctionId FROM item_auction WHERE instanceId=?");
            statement.setInt(1, npcId);
            rset = statement.executeQuery();

            while (rset.next()) {
                final int auctionId = rset.getInt(1);
                try {
                    final ItemAuction auction = loadAuction(auctionId);
                    if (auction != null) {
                        _auctions.put(auctionId, auction);
                    } else {
                        ItemAuctionManager.getInstance().deleteAuction(auctionId);
                    }
                } catch (SQLException e) {
                    LOGGER.warn("ItemAuction: Failed loading auction: " + auctionId, e);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("ItemAuction: Failed loading auctions.", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public int getInstanceId() {
        return npcId;
    }

    public int getDoing() {
        return doing;
    }

    public CronExpression getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public int getItemCount() {
        return itemCount;
    }

    public ItemAuction getCurrentAuction() {
        return _currentAuction;
    }

    public ItemAuction getNextAuction() {
        return _nextAuction;
    }

    public void shutdown() {
        final ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
    }

    private AuctionItem getAuctionItem(final int auctionItemId) {
        for (int i = items.size(); i-- > 0; ) {
            try {
                final AuctionItem item = items.get(i);
                if (item.getItemId() == auctionItemId) {
                    return item;
                }
            } catch (IndexOutOfBoundsException ignored) {

            }
        }
        return null;
    }

    void checkAndSetCurrentAndNextAuction() {
        final ItemAuction[] auctions = _auctions.values(new ItemAuction[_auctions.size()]);

        ItemAuction currentAuction = null;
        ItemAuction nextAuction = null;

        switch (auctions.length) {
            case 0: {
                nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                break;
            }

            case 1: {
                switch (auctions[0].getAuctionState()) {
                    case CREATED: {
                        if (auctions[0].getStartingTime() < System.currentTimeMillis() + START_TIME_SPACE) {
                            currentAuction = auctions[0];
                            nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        } else {
                            nextAuction = auctions[0];
                        }
                        break;
                    }

                    case STARTED: {
                        currentAuction = auctions[0];
                        nextAuction = createAuction(Math.max(currentAuction.getEndingTime() + FINISH_TIME_SPACE, System.currentTimeMillis() + START_TIME_SPACE));
                        break;
                    }

                    case FINISHED: {
                        currentAuction = auctions[0];
                        nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        break;
                    }

                    default:
                        throw new IllegalArgumentException();
                }
                break;
            }

            default: {
                Arrays.sort(auctions, (o1, o2) -> Long.compare(o2.getStartingTime(), o1.getStartingTime()));

                // just to make sure we won`t skip any auction because of little different times
                final long currentTime = System.currentTimeMillis();

                for (final ItemAuction auction : auctions) {
                    if (auction.getAuctionState() == ItemAuctionState.STARTED) {
                        currentAuction = auction;
                        break;
                    } else if (auction.getStartingTime() <= currentTime) {
                        currentAuction = auction;
                        break;
                    }
                }

                for (final ItemAuction auction : auctions) {
                    if (auction.getStartingTime() > currentTime && !currentAuction.equals(auction)) {
                        nextAuction = auction;
                        break;
                    }
                }

                if (nextAuction == null) {
                    nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                }
                break;
            }
        }

        _auctions.put(nextAuction.getAuctionId(), nextAuction);

        _currentAuction = currentAuction;
        _nextAuction = nextAuction;

        if (currentAuction != null && currentAuction.getAuctionState() == ItemAuctionState.STARTED) {
            setStateTask(ThreadPoolManager.getInstance().schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getEndingTime() - System
                    .currentTimeMillis(), 0L)));
            LOGGER.info("ItemAuction: Schedule current auction {} for instance {}", currentAuction.getAuctionId(), npcId);
        } else {
            setStateTask(ThreadPoolManager.getInstance().schedule(new ScheduleAuctionTask(nextAuction), Math.max(nextAuction.getStartingTime() - System.currentTimeMillis(), 0L)));
            final Instant instant = Instant.ofEpochMilli(nextAuction.getStartingTime());
            final ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            LOGGER.info("ItemAuction: Schedule next auction {} on {} for instance {}", nextAuction.getAuctionId(), TimeUtils.dateTimeFormat(dateTime), npcId);
        }
    }

    public ItemAuction getAuction(final int auctionId) {
        return _auctions.get(auctionId);
    }

    public ItemAuction[] getAuctionsByBidder(final int bidderObjId) {
        final ItemAuction[] auctions = getAuctions();
        final List<ItemAuction> stack = new ArrayList<>(auctions.length);
        for (final ItemAuction auction : getAuctions()) {
            if (auction.getAuctionState() != ItemAuctionState.CREATED) {
                final ItemAuctionBid bid = auction.getBidFor(bidderObjId);
                if (bid != null) {
                    stack.add(auction);
                }
            }
        }
        return stack.toArray(new ItemAuction[stack.size()]);
    }

    public ItemAuction[] getAuctions() {
        synchronized (_auctions) {
            return _auctions.values(new ItemAuction[_auctions.size()]);
        }
    }

    void onAuctionFinished(final ItemAuction auction) {
        auction.broadcastToAllBidders(new SystemMessage(SystemMsg.S1_S_AUCTION_HAS_ENDED).addNumber(auction.getAuctionId()));
        final ItemAuctionBid bid = auction.getHighestBid();
        if (bid != null) {
            final ItemInstance item = auction.createNewItemInstance();
            final Player player = bid.getPlayer();
            if (player != null) {
                if (player.getWarehouse().addItem(item) == null) {
                    player.sendDebugMessage("Yoy warehouse is full! Item not delayed to you. Please, send info to Administration. ItemId: " + item.getItemId() + " count: " + item.getCount());
                } else {
                    player.sendPacket(SystemMsg.YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL_WAREHOUSE);

                    LOGGER.info("ItemAuction: Auction {} has finished. Highest bid by (name) {} for instance {}",
                            auction.getAuctionId(), player.getName(), npcId);
                }
            } else {
                final Mail mail = new Mail();
                mail.setSenderId(1);
                mail.setSenderName("Item auction");
                mail.setReceiverId(bid.getCharId());
                mail.setReceiverName(CharacterDAO.getInstance().getNameByObjectId(bid.getCharId()));
                mail.setTopic("You won!");
                mail.setBody("Congratulations!");

                item.setOwnerId(bid.getCharId());
                item.setLocation(ItemLocation.MAIL);
                item.save();

                mail.addAttachment(item);
                mail.setUnread(true);
                mail.setType(SenderType.NEWS_INFORMER);
                mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
                mail.save();

                LOGGER.info("ItemAuction: Auction {} has finished. Highest bid by (id) {} for instance {}",
                        auction.getAuctionId(), bid.getCharId(), npcId);
            }
        } else {
            LOGGER.info("ItemAuction: Auction {} has finished. There have not been any bid for instance {}",
                    auction.getAuctionId(), npcId);
        }
    }

    void setStateTask(final ScheduledFuture<?> future) {
        final ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }

        _stateTask = future;
    }

    private ItemAuction createAuction(final long after) {
        final AuctionItem auctionItem = items.get(Rnd.get(items.size()));
        final long startingTime = startTime.getNextValidTimeAfter(new Date(after)).getTime();
        final long endingTime = startingTime + duration;
        final int auctionId = nextId.incrementAndGet();
        final ItemAuction auction = new ItemAuction(auctionId, npcId, startingTime, endingTime, auctionItem, ItemAuctionState.CREATED);

        auction.store();

        return auction;
    }

    private ItemAuction loadAuction(final int auctionId) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT auctionItemId,startingTime,endingTime,auctionStateId FROM item_auction WHERE auctionId=?");
            statement.setInt(1, auctionId);
            rset = statement.executeQuery();

            if (!rset.next()) {
                LOGGER.warn("ItemAuction: Auction data not found for auction: " + auctionId);
                return null;
            }

            final int auctionItemId = rset.getInt(1);
            final long startingTime = rset.getLong(2);
            final long endingTime = rset.getLong(3);
            final int auctionStateId = rset.getInt(4);

            DbUtils.close(statement, rset);

            if (startingTime >= endingTime) {
                LOGGER.warn("ItemAuction: Invalid starting/ending paramaters for auction: " + auctionId);
                return null;
            }

            final AuctionItem auctionItem = getAuctionItem(auctionItemId);
            if (auctionItem == null) {
                LOGGER.warn("ItemAuction: AuctionItem: " + auctionItemId + ", not found for auction: " + auctionId);
                return null;
            }

            final ItemAuctionState auctionState = ItemAuctionState.stateForStateId(auctionStateId);
            if (auctionState == null) {
                LOGGER.warn("ItemAuction: Invalid auctionStateId: " + auctionStateId + ", for auction: " + auctionId);
                return null;
            }

            final ItemAuction auction = new ItemAuction(auctionId, npcId, startingTime, endingTime, auctionItem, auctionState);

            statement = con.prepareStatement("SELECT playerObjId,playerBid FROM item_auction_bid WHERE auctionId=?");
            statement.setInt(1, auctionId);
            rset = statement.executeQuery();

            while (rset.next()) {
                final int charId = rset.getInt(1);
                final long playerBid = rset.getLong(2);
                final ItemAuctionBid bid = new ItemAuctionBid(charId, playerBid);
                auction.addBid(bid);
            }

            return auction;
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    private class ScheduleAuctionTask extends RunnableImpl {
        private final ItemAuction _auction;

        public ScheduleAuctionTask(final ItemAuction auction) {
            _auction = auction;
        }

        @Override
        public void runImpl() {
            final ItemAuctionState state = _auction.getAuctionState();

            switch (state) {
                case CREATED: {
                    if (!_auction.setAuctionState(state, ItemAuctionState.STARTED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.STARTED + ", expected: " + state);
                    }

                    LOGGER.info("ItemAuction: Auction " + _auction.getAuctionId() + " has started for instance " + _auction.getInstanceId());
                    if (AllSettingsConfig.ALT_ITEM_AUCTION_START_ANNOUNCE) {
                        AnnouncementUtils.announceToAll(new CustomMessage("itemauction.announce." + _auction.getInstanceId()));
                    }
                    checkAndSetCurrentAndNextAuction();
                    break;
                }

                case STARTED: {
                    switch (_auction.getAuctionEndingExtendState()) {
                        case 1: {
                            if (_auction.getScheduledAuctionEndingExtendState() == 0) {
                                _auction.setScheduledAuctionEndingExtendState(1);
                                setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }

                        case 2: {
                            if (_auction.getScheduledAuctionEndingExtendState() != 2) {
                                _auction.setScheduledAuctionEndingExtendState(2);
                                setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                    }

                    if (!_auction.setAuctionState(state, ItemAuctionState.FINISHED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.FINISHED + ", expected: " + state);
                    }

                    onAuctionFinished(_auction);
                    checkAndSetCurrentAndNextAuction();
                    break;
                }

                default:
                    throw new IllegalStateException("Invalid state: " + state);
            }
        }
    }
}