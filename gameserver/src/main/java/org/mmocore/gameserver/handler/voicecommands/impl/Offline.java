package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public class Offline implements IVoicedCommandHandler {
    private final String[] _commandList = {"offline"};

    @Override
    public boolean useVoicedCommand(final String command, final Player activeChar, final String args) {
        if (!ServicesConfig.SERVICES_OFFLINE_TRADE_ALLOW) {
            return false;
        }

        if (activeChar.getOlympiadObserveGame() != null || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.getKarma() > 0) {
            activeChar.sendActionFailed();
            return false;
        }

        if (!activeChar.isInStoreMode()) {
            activeChar.sendPacket(new HtmlMessage(5).setFile("command/offline_err01.htm"));
            return false;
        }

        if (activeChar.getLevel() < ServicesConfig.SERVICES_OFFLINE_TRADE_MIN_LEVEL) {
            activeChar.sendPacket(new HtmlMessage(5).setFile("command/offline_err02.htm").replace("%level%", String.valueOf(ServicesConfig.SERVICES_OFFLINE_TRADE_MIN_LEVEL)));
            return false;
        }

        if (ServicesConfig.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE && !activeChar.isInZone(ZoneType.offshore)) {
            activeChar.sendPacket(new HtmlMessage(5).setFile("command/offline_err03.htm"));
            return false;
        }

        if (ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE > 0 && ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE_ITEM != 0) {
            if (ItemFunctions.getItemCount(activeChar, ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE_ITEM) < ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE) {
                activeChar.sendPacket(new HtmlMessage(5).setFile("command/offline_err04.htm").replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE_ITEM).replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE))));
                return false;
            }
            ItemFunctions.removeItem(activeChar, ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE_ITEM, ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE, true);
            final ItemInstance item_log = activeChar.getInventory().getItemByItemId(ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE_ITEM);
            Log.items(activeChar, Log.Delete, item_log, ServicesConfig.SERVICES_OFFLINE_TRADE_PRICE);

        }

        activeChar.offline();
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}