package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance extends NpcInstance {
    public WarehouseInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        String pom = "";
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        if (getTemplate().getHtmRoot() != null) {
            return getTemplate().getHtmRoot() + pom + ".htm";
        } else {
            return "warehouse/" + pom + ".htm";
        }
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player.getEnchantScroll() != null) {
            Log.audit("[WarehouseInstance]", "Player " + player.getName() + " trying to use enchant exploit[Warehouse], ban this player! illegal-actions");
            player.setEnchantScroll(null);
            return;
        }

        if (command.startsWith("WithdrawP")) {
            final int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                player.sendPacket(new HtmlMessage(this).setFile("warehouse/personal.htm"));
            } else {
                WarehouseFunctions.showRetrieveWindow(player, val);
            }
        } else if ("DepositP".equals(command)) {
            WarehouseFunctions.showDepositWindow(player);
        } else if (command.startsWith("WithdrawC")) {
            final int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                player.sendPacket(new HtmlMessage(this).setFile("warehouse/clan.htm"));
            } else {
                WarehouseFunctions.showWithdrawWindowClan(player, val);
            }
        } else if ("DepositC".equals(command)) {
            WarehouseFunctions.showDepositWindowClan(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}