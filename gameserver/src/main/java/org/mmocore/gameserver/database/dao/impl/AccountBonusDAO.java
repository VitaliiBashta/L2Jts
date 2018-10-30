package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.components.items.PremiumItem;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author VISTALL
 * @date 20:22/07.04.2011
 */
public class AccountBonusDAO {
    public static final String SELECT_SQL_QUERY_ITEM_LIST = "SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?";
    public static final String DELETE_SQL_QUERY_ITEM_LIST = "DELETE FROM character_premium_items WHERE charId=? AND itemNum=?";
    public static final String UPDATE_SQL_QUERY_ITEM_LIST = "UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=?";
    public static final String SELECT_SQL_QUERY = "SELECT rate_xp, rate_sp, rate_adena, rate_drop, rate_spoil, rate_epaulette, rate_enchant, rate_attribute, rate_craft, bonus_expire FROM account_bonus WHERE account=?";
    public static final String DELETE_SQL_QUERY = "DELETE FROM account_bonus WHERE account=?";
    public static final String INSERT_SQL_QUERY = "REPLACE INTO account_bonus(account, rate_xp, rate_sp, rate_adena, rate_drop, rate_spoil, rate_epaulette, rate_enchant, rate_attribute, rate_craft, bonus_expire) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBonusDAO.class);
    private static final AccountBonusDAO _instance = new AccountBonusDAO();

    public static AccountBonusDAO getInstance() {
        return _instance;
    }

    public PremiumBonus select(final String account) {
        final PremiumBonus premium = new PremiumBonus();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setString(1, account);
            rset = statement.executeQuery();
            if (rset.next()) {
                premium.setRateXp(rset.getDouble("rate_xp"));
                premium.setRateSp(rset.getDouble("rate_sp"));
                premium.setDropAdena(rset.getDouble("rate_adena"));
                premium.setDropItems(rset.getDouble("rate_drop"));
                premium.setDropSpoil(rset.getDouble("rate_spoil"));
                premium.setBonusEpaulette(rset.getDouble("rate_epaulette"));
                premium.setEnchantChance(rset.getDouble("rate_enchant"));
                premium.setAttributeChance(rset.getDouble("rate_attribute"));
                premium.setCraftChance(rset.getDouble("rate_craft"));
                premium.setBonusExpire(rset.getInt("bonus_expire"));
            }
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.select(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return premium;
    }

    public void delete(final String account) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setString(1, account);
            statement.execute();
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.delete(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void insert(final String account, final PremiumBonus premium) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, account);
            statement.setDouble(2, premium.getRateXp());
            statement.setDouble(3, premium.getRateSp());
            statement.setDouble(4, premium.getDropAdena());
            statement.setDouble(5, premium.getDropItems());
            statement.setDouble(6, premium.getDropSpoil());
            statement.setDouble(7, premium.getBonusEpaulette());
            statement.setDouble(8, premium.getEnchantChance());
            statement.setDouble(9, premium.getAttributeChance());
            statement.setDouble(10, premium.getCraftChance());
            statement.setLong(11, premium.getBonusExpire());
            statement.execute();
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.insert(): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public Map<Integer, PremiumItem> loadPremiumItemList(final int playerObjectId) {
        final Map<Integer, PremiumItem> itemList = new TreeMap<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY_ITEM_LIST);
            statement.setInt(1, playerObjectId);
            rs = statement.executeQuery();
            while (rs.next()) {
                final int itemNum = rs.getInt("itemNum");
                final int itemId = rs.getInt("itemId");
                final long itemCount = rs.getLong("itemCount");
                final String itemSender = rs.getString("itemSender");
                final PremiumItem item = new PremiumItem(itemId, itemCount, itemSender);
                itemList.put(itemNum, item);
            }
        } catch (final Exception e) {
            LOGGER.error("AccountBonusDAO.loadPremiumItemList(int):", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
        return itemList;
    }

    public void updatePremiumItem(final int itemNum, final long newcount, final int playerObjectId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY_ITEM_LIST);
            statement.setLong(1, newcount);
            statement.setInt(2, playerObjectId);
            statement.setInt(3, itemNum);
            statement.execute();
        } catch (final Exception e) {
            LOGGER.error("AccountBonusDAO.updatePremiumItem(int, long, int):", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void deletePremiumItem(final int itemNum, final int playerObjectId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY_ITEM_LIST);
            statement.setInt(1, playerObjectId);
            statement.setInt(2, itemNum);
            statement.execute();
        } catch (final Exception e) {
            LOGGER.error("AccountBonusDAO.deletePremiumItem(int, int):", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}