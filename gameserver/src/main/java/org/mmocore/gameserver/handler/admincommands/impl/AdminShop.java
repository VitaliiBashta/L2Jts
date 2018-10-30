package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.xml.holder.CustomBuyListHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuyList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.object.Player;

public class AdminShop implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().UseGMShop) {
            return false;
        }

        switch (command) {
            case admin_buy:
                try {
                    handleBuyRequest(activeChar, fullString.substring(10));
                } catch (IndexOutOfBoundsException e) {
                    activeChar.sendAdminMessage("Please specify buylist.");
                }
                break;
            case admin_gmshop:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/gmshops.htm"));
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleBuyRequest(final Player activeChar, final String command) {
        int val = -1;

        try {
            val = Integer.parseInt(command);
        } catch (Exception e) {

        }

        final BuyList list = CustomBuyListHolder.getInstance().getBuyList(val);
        if (list != null) {
            activeChar.setLastNpc(null);
            activeChar.sendPacket(new ExBuyList(list, activeChar));
            activeChar.sendPacket(new ExBuySellList(activeChar, false));
        }
        activeChar.sendActionFailed();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_buy,
        admin_gmshop
    }
}