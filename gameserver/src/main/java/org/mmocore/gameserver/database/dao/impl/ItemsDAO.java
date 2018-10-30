package org.mmocore.gameserver.database.dao.impl;

import org.infinispan.Cache;
import org.mmocore.commons.database.dao.JdbcDAO;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dao.JdbcEntityStats;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class ItemsDAO implements JdbcDAO<Integer, ItemInstance> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsDAO.class);

    private static final String RESTORE_ITEM = "SELECT object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, variation_stone_id, variation1_id, variation2_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, agathion_energy, agathion_max_energy, visual_id, is_costume FROM items WHERE object_id = ?";
    private static final String RESTORE_OWNER_ITEMS = "SELECT object_id FROM items WHERE owner_id = ? AND loc = ?";
    private static final String STORE_ITEM = "INSERT INTO items (object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, variation_stone_id, variation1_id, variation2_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, agathion_energy, agathion_max_energy, visual_id, is_costume) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_ITEM = "UPDATE items SET owner_id = ?, item_id = ?, count = ?, enchant_level = ?, loc = ?, loc_data = ?, custom_type1 = ?, custom_type2 = ?, life_time = ?, custom_flags = ?, variation_stone_id = ?, variation1_id = ?, variation2_id = ?, attribute_fire = ?, attribute_water = ?, attribute_wind = ?, attribute_earth = ?, attribute_holy = ?, attribute_unholy = ?, agathion_energy=?, agathion_max_energy=?, visual_id=?, is_costume=? WHERE object_id = ?";
    private static final String REMOVE_ITEM = "DELETE FROM items WHERE object_id = ?";

    private final AtomicLong load = new AtomicLong();
    private final AtomicLong insert = new AtomicLong();
    private final AtomicLong update = new AtomicLong();
    private final AtomicLong delete = new AtomicLong();

    private final Cache<Integer, ItemInstance> cache = GameServer.getInstance().getCacheManager().getCache(getClass().getName());

    private final JdbcEntityStats stats = new JdbcEntityStats() {
        @Override
        public long getLoadCount() {
            return load.get();
        }

        @Override
        public long getInsertCount() {
            return insert.get();
        }

        @Override
        public long getUpdateCount() {
            return update.get();
        }

        @Override
        public long getDeleteCount() {
            return delete.get();
        }
    };

    private ItemsDAO() {
    }

    public static ItemsDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Cache<Integer, ItemInstance> getCache() {
        return cache;
    }

    @Override
    public JdbcEntityStats getStats() {
        return stats;
    }

    private ItemInstance load0(final int objectId) throws SQLException {
        ItemInstance item = null;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(RESTORE_ITEM);
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
            item = load0(rset);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        load.incrementAndGet();
        return item;
    }

    private ItemInstance load0(final ResultSet rset) throws SQLException {
        ItemInstance item = null;

        if (rset.next()) {
            final int objectId = rset.getInt(1);
            item = new ItemInstance(objectId);
            //item.setObjectId(rset.getInt(1));
            item.setOwnerId(rset.getInt(2));
            item.setItemId(rset.getInt(3));
            item.setCount(rset.getLong(4));
            item.setEnchantLevel(rset.getInt(5));
            item.setLocName(rset.getString(6));
            item.setLocData(rset.getInt(7));
            item.setCustomType1(rset.getInt(8));
            item.setCustomType2(rset.getInt(9));
            item.setLifeTime(rset.getInt(10));
            item.setCustomFlags(rset.getInt(11));
            item.setVariationStoneId(rset.getInt(12));
            item.setVariation1Id(rset.getInt(13));
            item.setVariation2Id(rset.getInt(14));
            item.getAttributes().setFire(rset.getInt(15));
            item.getAttributes().setWater(rset.getInt(16));
            item.getAttributes().setWind(rset.getInt(17));
            item.getAttributes().setEarth(rset.getInt(18));
            item.getAttributes().setHoly(rset.getInt(19));
            item.getAttributes().setUnholy(rset.getInt(20));
            item.setAgathionEnergy(rset.getInt(21));
            item.setAgathionMaxEnergy(rset.getInt(22));
            item.setVisualItemId(rset.getInt(23));
            item.setCostume(rset.getInt(24) == 1);
        }

        return item;
    }

    private void save0(final ItemInstance item, final PreparedStatement statement) throws SQLException {
        statement.setInt(1, item.getObjectId());
        statement.setInt(2, item.getOwnerId());
        statement.setInt(3, item.getItemId());
        statement.setLong(4, item.getCount());
        statement.setInt(5, item.getTrueEnchantLevel());
        statement.setString(6, item.getLocName());
        statement.setInt(7, item.getLocData());
        statement.setInt(8, item.getCustomType1());
        statement.setInt(9, item.getCustomType2());
        statement.setInt(10, item.getLifeTime());
        statement.setInt(11, item.getCustomFlags());
        statement.setInt(12, item.getVariationStoneId());
        statement.setInt(13, item.getVariation1Id());
        statement.setInt(14, item.getVariation2Id());
        statement.setInt(15, item.getAttributes().getFire());
        statement.setInt(16, item.getAttributes().getWater());
        statement.setInt(17, item.getAttributes().getWind());
        statement.setInt(18, item.getAttributes().getEarth());
        statement.setInt(19, item.getAttributes().getHoly());
        statement.setInt(20, item.getAttributes().getUnholy());
        statement.setInt(21, item.getAgathionEnergy());
        statement.setInt(22, item.getAgathionMaxEnergy());
        statement.setInt(23, item.getVisualItemId());
        statement.setInt(24, item.isCostume() ? 1 : 0);
    }

    private void save0(final ItemInstance item) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(STORE_ITEM);
            save0(item, statement);
            statement.execute();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        insert.incrementAndGet();
    }

    private void delete0(final ItemInstance item, final PreparedStatement statement) throws SQLException {
        statement.setInt(1, item.getObjectId());
    }

    private void delete0(final ItemInstance item) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(REMOVE_ITEM);
            delete0(item, statement);
            statement.execute();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        delete.incrementAndGet();
    }

    private void update0(final ItemInstance item, final PreparedStatement statement) throws SQLException {
        statement.setInt(24, item.getObjectId());
        statement.setInt(1, item.getOwnerId());
        statement.setInt(2, item.getItemId());
        statement.setLong(3, item.getCount());
        statement.setInt(4, item.getTrueEnchantLevel());
        statement.setString(5, item.getLocName());
        statement.setInt(6, item.getLocData());
        statement.setInt(7, item.getCustomType1());
        statement.setInt(8, item.getCustomType2());
        statement.setInt(9, item.getLifeTime());
        statement.setInt(10, item.getCustomFlags());
        statement.setInt(11, item.getVariationStoneId());
        statement.setInt(12, item.getVariation1Id());
        statement.setInt(13, item.getVariation2Id());
        statement.setInt(14, item.getAttributes().getFire());
        statement.setInt(15, item.getAttributes().getWater());
        statement.setInt(16, item.getAttributes().getWind());
        statement.setInt(17, item.getAttributes().getEarth());
        statement.setInt(18, item.getAttributes().getHoly());
        statement.setInt(19, item.getAttributes().getUnholy());
        statement.setInt(20, item.getAgathionEnergy());
        statement.setInt(21, item.getAgathionMaxEnergy());
        statement.setInt(22, item.getVisualItemId());
        statement.setInt(23, item.isCostume() ? 1 : 0);
    }

    private void update0(final ItemInstance item) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_ITEM);
            update0(item, statement);
            statement.execute();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        update.incrementAndGet();
    }

    @Override
    public ItemInstance load(final Integer objectId) {
        if (cache.containsKey(objectId))
            return cache.get(objectId);

        final ItemInstance item;

        try {
            item = load0(objectId);
            if (item == null) {
                return null;
            }

            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOGGER.error("Error while restoring item : " + objectId, e);
            return null;
        }

        cache.put(item.getObjectId(), item);

        return item;
    }

    public Collection<ItemInstance> load(final Collection<Integer> objectIds) {
        Collection<ItemInstance> list = Collections.emptyList();

        if (objectIds.isEmpty()) {
            return list;
        }

        list = new ArrayList<>(objectIds.size());

        ItemInstance item;
        for (final Integer objectId : objectIds) {
            item = load(objectId);
            if (item != null) {
                list.add(item);
            }
        }

        return list;
    }

    @Override
    public void save(final ItemInstance item) {
        if (!item.getJdbcState().isSavable()) {
            return;
        }

        try {
            save0(item);
            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOGGER.error("Error while saving item : " + item, e);
            return;
        }

        cache.put(item.getObjectId(), item);
    }

    public void save(final Collection<ItemInstance> items) {
        if (items.isEmpty()) {
            return;
        }

        items.forEach(this::save);
    }

    @Override
    public void update(final ItemInstance item) {
        if (!item.getJdbcState().isUpdatable()) {
            return;
        }

        try {
            update0(item);
            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOGGER.error("Error while updating item : " + item, e);
            return;
        }

        cache.putIfAbsent(item.getObjectId(), item);
    }

    public void update(final Collection<ItemInstance> items) {
        if (items.isEmpty()) {
            return;
        }

        items.forEach(this::update);
    }

    @Override
    public void saveOrUpdate(final ItemInstance item) {
        if (item.getJdbcState().isSavable()) {
            save(item);
        } else if (item.getJdbcState().isUpdatable()) {
            update(item);
        }
    }

    public void saveOrUpdate(final Collection<ItemInstance> items) {
        if (items.isEmpty()) {
            return;
        }

        items.forEach(this::saveOrUpdate);
    }

    @Override
    public void delete(final ItemInstance item) {
        if (!item.getJdbcState().isDeletable()) {
            return;
        }

        try {
            delete0(item);
            item.setJdbcState(JdbcEntityState.DELETED);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting item : " + item, e);
            return;
        }

        cache.remove(item.getObjectId());
    }

    public void delete(final Collection<ItemInstance> items) {
        if (items.isEmpty()) {
            return;
        }

        items.forEach(this::delete);
    }

    public Collection<ItemInstance> getItemsByOwnerIdAndLoc(final int ownerId, final ItemLocation loc) {
        Collection<Integer> objectIds = Collections.emptyList();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(RESTORE_OWNER_ITEMS);
            statement.setInt(1, ownerId);
            statement.setString(2, loc.name());
            rset = statement.executeQuery();
            objectIds = new ArrayList<>();
            while (rset.next()) {
                objectIds.add(rset.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error("Error while restore items of owner : " + ownerId, e);
            objectIds.clear();
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return load(objectIds);
    }

    private static class LazyHolder {
        private static final ItemsDAO INSTANCE = new ItemsDAO();
    }
}
