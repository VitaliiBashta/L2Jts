package org.mmocore.gameserver.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.AuctionDataHolder;
import org.jts.dataparser.data.holder.ItemDataHolder;
import org.jts.dataparser.data.holder.auctiondata.Auction;
import org.jts.dataparser.data.holder.itemdata.ItemData;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.itemauction.AuctionItem;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionInstance;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author n0nam3
 */
public class ItemAuctionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemAuctionManager.class);

    private final Map<Integer, ItemAuctionInstance> itemAuctionInstances = new HashMap<>();

    private ItemAuctionManager() {
        LOGGER.info("Initializing ItemAuctionManager");
    }

    public static ItemAuctionManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void load() {
        for (final Auction auction : AuctionDataHolder.getInstance().getAuctions()) {
            final int npcId = auction.npc_id;

            if (itemAuctionInstances.containsKey(npcId)) {
                throw new IllegalArgumentException("Duplicate npc_id " + npcId);
            }

            final String time = String.valueOf(auction.auction_start_time);
            final int hour = Integer.parseInt(time.substring(0, 2));
            final int minute = Integer.parseInt(time.substring(2, 4));
            final Integer[] weekDays = ArrayUtils.toObject(auction.auction_week_day);
            final Instant endDate = Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(auction.auction_duration));

            final String cron = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(hour, minute, weekDays))
                    .endAt(new Date(endDate.toEpochMilli()))
                    .build().getCronExpression();
            final CronExpression cronExpression = QuartzUtils.createCronExpression(cron);

            final List<AuctionItem> items = new LinkedList<>();

            for (final org.jts.dataparser.data.holder.auctiondata.AuctionItem auctionItem : auction.auctionItems) {
                final int itemId = LinkerFactory.getInstance().findClearValue(auctionItem.item);
                final Optional<ItemData> optionalItemData = ItemDataHolder.getInstance().getItemDatas().stream()
                        .filter(item -> item.itemId == itemId).findFirst();

                if (!optionalItemData.isPresent()) {
                    throw new IllegalArgumentException("Can't find item " + auctionItem.item);
                }

                final ItemData itemData = optionalItemData.get();

                final AuctionItem item = new AuctionItem(itemData.itemId, auctionItem.amount, auctionItem.price, itemData.enchanted);
                items.add(item);
            }

            if (items.isEmpty()) {
                throw new IllegalArgumentException("No items defined for npc_id: " + npcId);
            }

            final long duration = TimeUnit.MINUTES.toMillis(auction.auction_duration);

            final ItemAuctionInstance instance = new ItemAuctionInstance(npcId, auction.auction_doing, cronExpression, duration,
                    auction.item_count, items);

            itemAuctionInstances.put(instance.getInstanceId(), instance);
        }
    }

    public ItemAuctionInstance getItemAuction(final int itemAuctionId) {
        return itemAuctionInstances.get(itemAuctionId);
    }

    public void shutdown() {
        final Collection<ItemAuctionInstance> instances = itemAuctionInstances.values();
        instances.forEach(ItemAuctionInstance::shutdown);
    }

    public void deleteAuction(final int auctionId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?");
            statement.setInt(1, auctionId);
            statement.execute();
            statement.close();

            statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?");
            statement.setInt(1, auctionId);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("ItemAuctionManager: Failed deleting auction: " + auctionId, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static class LazyHolder {
        private static final ItemAuctionManager INSTANCE = new ItemAuctionManager();
    }
}