package org.mmocore.gameserver.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.mmocore.commons.text.PrintfFormat;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;


public class Log {
    public static final String Create = "Create";
    public static final String Delete = "Delete";
    public static final String Drop = "Drop";
    public static final String PvPDrop = "PvPDrop";
    public static final String Crystalize = "Crystalize";
    public static final String EnchantSucces = "EnchantSucces";
    public static final String EnchantFail = "EnchantFail";
    public static final String Pickup = "Pickup";
    public static final String PartyPickup = "PartyPickup";
    public static final String PrivateStoreBuy = "PrivateStoreBuy";
    public static final String PrivateStoreSell = "PrivateStoreSell";
    public static final String PrivateStoreSellerGiveAdena = "PrivateStoreSellerGiveAdena";
    public static final String PrivateStoreBuyerDeleteAdena = "PrivateStoreBuyerDeleteAdena";
    public static final String TradeBuy = "TradeBuy";
    public static final String TradeSell = "TradeSell";
    public static final String PostRecieve = "PostRecieve";
    public static final String PostSend = "PostSend";
    public static final String PostCancel = "PostCancel";
    public static final String PostExpire = "PostExpire";
    public static final String RefundSell = "RefundSell";
    public static final String RefundReturn = "RefundReturn";
    public static final String WarehouseDeposit = "WarehouseDeposit";
    public static final String WarehouseWithdraw = "WarehouseWithdraw";
    public static final String FreightWithdraw = "FreightWithdraw";
    public static final String FreightDeposit = "FreightDeposit";
    public static final String ClanWarehouseDeposit = "ClanWarehouseDeposit";
    public static final String ClanWarehouseWithdraw = "ClanWarehouseWithdraw";
    private static final Logger LOGGER = LogManager.getLogger(Log.class);
    private static final Marker CHAT_MARKER = MarkerManager.getMarker("CHAT");
    private static final Marker GM_ACTIONS_MARKER = MarkerManager.getMarker("GM_ACTIONS");
    private static final Marker ITEMS_MARKER = MarkerManager.getMarker("ITEMS");
    private static final Marker GAME_MARKER = MarkerManager.getMarker("GAME");
    private static final Marker DEBUG_MARKER = MarkerManager.getMarker("DEBUG");
    private static final Marker AUDIT_MARKER = MarkerManager.getMarker("AUDIT");
    private static final Marker SERVICE_MARKER = MarkerManager.getMarker("SERVICE");
    private static final Marker AUTH_MAKER = MarkerManager.getMarker("AUTH");
    private static final Marker DEBUG_TEMP_MARKER = MarkerManager.getMarker("DEBUG_TEMP");

    public static void add(final PrintfFormat fmt, final Object[] o, final String cat) {
        add(fmt.sprintf(o), cat);
    }

    public static void add(final String text, final String cat, final Player player) {
        final StringBuilder output = new StringBuilder();

        output.append(cat);
        if (player != null) {
            output.append(' ');
            output.append(player);
        }
        output.append(' ');
        output.append(text);

        LOGGER.debug(GAME_MARKER, output.toString());
    }

    public static void service(final String type, final Player player, final String text) {
        final StringBuilder output = new StringBuilder();
        output.append('[');
        output.append(type);
        output.append(']');
        output.append(' ');
        output.append('[');
        output.append(player.getName());
        output.append('-');
        output.append(player.getObjectId());
        output.append(']');
        output.append(' ');
        output.append(text);
        LOGGER.debug(SERVICE_MARKER, output.toString());
    }

    public static void add(final String text, final String cat) {
        add(text, cat, null);
    }

    public static void debug(final String text) {
        LOGGER.debug(DEBUG_MARKER, text);
    }

    public static void debug(final String text, final Throwable t) {
        LOGGER.debug(DEBUG_MARKER, text, t);
    }

    public static void chat(final String type, final String player, final String target, final String text) {
        if (!ServerConfig.LOG_CHAT) {
            return;
        }

        final StringBuilder output = new StringBuilder();
        output.append(type);
        output.append(' ');
        output.append('[');
        output.append(player);
        if (target != null) {
            output.append(" -> ");
            output.append(target);
        }
        output.append(']');
        output.append(' ');
        output.append(text);

        LOGGER.debug(CHAT_MARKER, output.toString());
    }

    public static void gmActions(final Player player, final GameObject target, final String command, final boolean success) {
        //if(!Config.LOG_GM)
        //	return;

        final StringBuilder output = new StringBuilder();

        if (success) {
            output.append("SUCCESS");
        } else {
            output.append("FAIL   ");
        }

        output.append(' ');
        output.append(player);
        if (target != null) {
            output.append(" -> ");
            output.append(target);
        }
        output.append(' ');
        output.append(command);

        LOGGER.debug(GM_ACTIONS_MARKER, output.toString());
    }

    public static void items(final Creature activeChar, final String process, final ItemInstance item) {
        items(activeChar, process, item, item.getCount());
    }

    public static void items(final Creature activeChar, final String process, final ItemInstance item, final long count) {
        //if(!Config.LOG_ITEM)
        //	return;

        final StringBuilder output = new StringBuilder();
        output.append(process);
        output.append(' ');
        output.append(item);
        output.append(' ');
        output.append(activeChar);
        output.append(' ');
        output.append(count);

        LOGGER.debug(ITEMS_MARKER, output.toString());
    }

    public static void petition(final Player fromChar, final Integer Petition_type, final String Petition_text) {
        //TODO: implement
    }

    public static void audit(final String identifier, final String msg) {
        LOGGER.debug(AUDIT_MARKER, "[{}] {}", identifier, msg);
    }

    public static void auth(String msg) {
        LOGGER.debug(AUTH_MAKER, msg);
    }

    public static void auth(Player player) {
        auth("Logged in - " + player.getNetConnection() + ", HWID: " + player.getNetConnection().getHWID());
    }

    public static void debugTemp(String msg) {
        LOGGER.debug(DEBUG_TEMP_MARKER, msg);
    }
}