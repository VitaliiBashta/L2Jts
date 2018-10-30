package org.mmocore.gameserver.utils;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class TradeHelper {
    private TradeHelper() {
    }

    public static boolean checksIfCanOpenStore(final Player player, final int storeType) {
        if (!player.getPlayerAccess().UseTrade) {
            player.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
            return false;
        }

        if (player.getLevel() < ServicesConfig.SERVICES_TRADE_MIN_LEVEL) {
            player.sendMessage(new CustomMessage("trade.NotHavePermission").addNumber(ServicesConfig.SERVICES_TRADE_MIN_LEVEL));
            return false;
        }

        final String tradeBan = player.getPlayerVariables().get(PlayerVariables.TRADE_BAN);
        if (tradeBan != null && ("-1".equals(tradeBan) || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
            player.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
            return false;
        }
        if (!OtherConfig.checkTradeZone(player)) {
            player.sendMessage(new CustomMessage("trade_zone.error"));
            return false;
        }
        final String BLOCK_ZONE = storeType == Player.STORE_PRIVATE_MANUFACTURE ? Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : Zone.BLOCKED_ACTION_PRIVATE_STORE;
        if (player.isActionBlocked(BLOCK_ZONE)) {
            if (!ServicesConfig.SERVICES_NO_TRADE_ONLY_OFFLINE || ServicesConfig.SERVICES_NO_TRADE_ONLY_OFFLINE && player.isInOfflineMode()) {
                player.sendPacket(storeType == Player.STORE_PRIVATE_MANUFACTURE ? new SystemMessage(SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE) : SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
                return false;
            }
        }

        if (player.isCastingNow()) {
            player.sendPacket(SystemMsg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
            return false;
        }

        if (player.isInCombat()) {
            player.sendPacket(SystemMsg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }

        if (player.isActionsDisabled() || player.isMounted() || player.isInOlympiadMode() || player.isInDuel() || player.isProcessingRequest()) {
            return false;
        }

        if (ServicesConfig.SERVICES_TRADE_ONLY_FAR) {
            boolean tradenear = false;
            for (final Player p : World.getAroundPlayers(player, ServicesConfig.SERVICES_TRADE_RADIUS, 200)) {
                if (p.isInStoreMode()) {
                    tradenear = true;
                    break;
                }
            }

            if (!World.getAroundNpc(player, ServicesConfig.SERVICES_TRADE_RADIUS + 100, 200).isEmpty()) {
                tradenear = true;
            }

            if (tradenear) {
                player.sendMessage(new CustomMessage("trade.OtherTradersNear"));
                return false;
            }
        }

        return true;
    }

    public static void purchaseItem(final Player buyer, final Player seller, final TradeItem item) {
        final long price = item.getCount() * item.getOwnersPrice();
        if (!item.getItem().isStackable()) {
            if (item.getEnchantLevel() > 0) {
                seller.sendPacket(new SystemMessage(SystemMsg.S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA).addString(buyer.getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()).addNumber(price));
                buyer.sendPacket(new SystemMessage(SystemMsg.S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA).addString(seller.getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()).addNumber(price));
            } else {
                seller.sendPacket(new SystemMessage(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA).addString(buyer.getName()).addItemName(item.getItemId()).addNumber(price));
                buyer.sendPacket(new SystemMessage(SystemMsg.S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA).addString(seller.getName()).addItemName(item.getItemId()).addNumber(price));
            }
        } else {
            seller.sendPacket(new SystemMessage(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA).addString(buyer.getName()).addItemName(item.getItemId()).addNumber(item.getCount()).addNumber(price));
            buyer.sendPacket(new SystemMessage(SystemMsg.S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA).addString(seller.getName()).addItemName(item.getItemId()).addNumber(item.getCount()).addNumber(price));
        }
    }

    public static long getTax(final Player seller, final long price) {
        long tax = (long) (price * ServicesConfig.SERVICES_TRADE_TAX / 100);
        if (seller.isInZone(ZoneType.offshore)) {
            tax = (long) (price * ServicesConfig.SERVICES_OFFSHORE_TRADE_TAX / 100);
        }
        if (ServicesConfig.SERVICES_TRADE_TAX_ONLY_OFFLINE && !seller.isInOfflineMode()) {
            tax = 0;
        }
        if (ServicesConfig.SERVICES_PARNASSUS_NOTAX && seller.getReflection() == ReflectionManager.PARNASSUS) {
            tax = 0;
        }

        return tax;
    }

    /**
     * Отключение режима торговли у персонажа, оф. трейдеров кикает.
     */
    public static void cancelStore(final Player activeChar) {
        activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
        if (activeChar.isInOfflineMode()) {
            activeChar.setOfflineMode(false);
            activeChar.kick();
        } else {
            activeChar.broadcastCharInfo();
        }
    }

    public static int restoreOfflineTraders() throws Exception {
        int count = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            //Убираем просроченных
            if (ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0) {
                final int expireTimeSecs = (int) (System.currentTimeMillis() / 1000L - ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);

                statement = con.prepareStatement("DELETE FROM character_variables WHERE name = '" + PlayerVariables.OFFLINE + "' AND value < ?");
                statement.setInt(1, expireTimeSecs);
                statement.executeUpdate();

                DbUtils.close(statement);
            }

            //Убираем забаненных
            statement = con.prepareStatement("DELETE FROM character_variables WHERE name = '" + PlayerVariables.OFFLINE + "' AND obj_id IN (SELECT obj_id FROM characters WHERE accessLevel < 0)");
            statement.executeUpdate();

            DbUtils.close(statement);

            statement = con.prepareStatement("SELECT obj_id, value FROM character_variables WHERE name = '" + PlayerVariables.OFFLINE + '\'');
            rset = statement.executeQuery();

            int objectId;
            int expireTimeSecs;
            Player p;

            while (rset.next()) {
                objectId = rset.getInt("obj_id");
                expireTimeSecs = rset.getInt("value");

                p = Player.restore(objectId);
                if (p == null) {
                    continue;
                }

                if (p.isDead()) {
                    p.kick(false);
                    continue;
                }

                if (ServicesConfig.SERVICES_OFFLINE_TRADE_CHANGE_NAME_COLOR)
                    p.getAppearanceComponent().setNameColor(ServicesConfig.SERVICES_OFFLINE_TRADE_NAME_COLOR);
                else if (p.getPlayerVariables().get(PlayerVariables.NAME_COLOR) == null)
                    if (p.isGM())
                        p.getAppearanceComponent().setNameColor(OtherConfig.GM_NAME_COLOUR);
                    else if (p.getClan() != null && p.getClan().getLeaderId() == p.getObjectId())
                        p.getAppearanceComponent().setNameColor(OtherConfig.CLANLEADER_NAME_COLOUR);
                    else
                        p.getAppearanceComponent().setNameColor(OtherConfig.NORMAL_NAME_COLOUR);
                else
                    p.getAppearanceComponent().setNameColor(Integer.decode("0x" + p.getPlayerVariables().get(PlayerVariables.NAME_COLOR)));
                p.setOfflineMode(true);
                p.setIsOnline(true);

                p.spawnMe();

                if (p.getClan() != null && p.getClan().getAnyMember(p.getObjectId()) != null) {
                    p.getClan().getAnyMember(p.getObjectId()).setPlayerInstance(p, false);
                }

                if (ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0) {
                    p.startKickTask((ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK + expireTimeSecs - System.currentTimeMillis() / 1000L) * 1000L);
                }

                count++;
            }
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return count;
    }
}