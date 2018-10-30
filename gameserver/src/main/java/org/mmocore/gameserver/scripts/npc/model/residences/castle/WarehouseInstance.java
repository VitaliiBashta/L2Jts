package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    protected static final int COND_OWNER = 2;

    private static final int ITEM_BLOOD_ALLI = 9911; // Blood Alliance
    private static final int ITEM_BLOOD_OATH = 9910; // Blood Oath

    public WarehouseInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (player.getEnchantScroll() != null) {
            Log.add("Player " + player.getName() + " trying to use enchant exploit[CastleWarehouse], ban this player!", "illegal-actions");
            player.kick();
            return;
        }

        if (command.startsWith("WithdrawP")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                HtmlMessage html = new HtmlMessage(this);
                html.setFile("warehouse/personal.htm");
                player.sendPacket(html);
            } else {
                WarehouseFunctions.showRetrieveWindow(player, val);
            }
        } else if (command.equals("DepositP")) {
            WarehouseFunctions.showDepositWindow(player);
        } else if (command.startsWith("WithdrawC")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                HtmlMessage html = new HtmlMessage(this);
                html.setFile("warehouse/clan.htm");
                player.sendPacket(html);
            } else {
                WarehouseFunctions.showWithdrawWindowClan(player, val);
            }
        } else if (command.equals("DepositC")) {
            WarehouseFunctions.showDepositWindowClan(player);
        } else if (command.equalsIgnoreCase("CheckHonoraryItems")) {
            String filename;
            if (!player.isClanLeader()) {
                filename = "castle/warehouse/castlewarehouse-notcl.htm";
            } else {
                filename = "castle/warehouse/castlewarehouse-5.htm";
            }

            HtmlMessage html = new HtmlMessage(this);
            html.setFile(filename);
            html.replace("%total_items%", String.valueOf(getCastle().getOwner().getCastleDefendCount()));
            player.sendPacket(html);
        } else if (command.equalsIgnoreCase("ExchangeBloodAlli")) {
            if (!player.isClanLeader()) {
                HtmlMessage html = new HtmlMessage(this);
                html.setFile("castle/warehouse/castlewarehouse-notcl.htm");
                player.sendPacket(html);
            } else if (ItemFunctions.removeItem(player, ITEM_BLOOD_ALLI, 1) == 0) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            } else {
                ItemFunctions.addItem(player, ITEM_BLOOD_OATH, 30, true);
            }
        } else if (command.equalsIgnoreCase("ReciveBloodAlli")) {
            Castle castle = getCastle();
            String filename;

            int count = castle.getOwner().getCastleDefendCount();
            if (!player.isClanLeader()) {
                filename = "castle/warehouse/castlewarehouse-notcl.htm";
            } else if (count > 0) {
                filename = "castle/warehouse/castlewarehouse-3.htm";

                castle.getOwner().setCastleDefendCount(0);
                castle.getOwner().updateClanInDB();

                ItemFunctions.addItem(player, ITEM_BLOOD_ALLI, count);
            } else {
                filename = "castle/warehouse/castlewarehouse-4.htm";
            }

            HtmlMessage html = new HtmlMessage(this);
            html.setFile(filename);
            player.sendPacket(html);
        } else if (command.startsWith("Chat")) {
            int val = 0;
            try {
                val = Integer.parseInt(command.substring(5));
            } catch (IndexOutOfBoundsException ioobe) {
            } catch (NumberFormatException nfe) {
            }
            showChatWindow(player, val);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        player.sendActionFailed();
        String filename = "castle/warehouse/castlewarehouse-no.htm";

        int condition = validateCondition(player);
        if (condition > COND_ALL_FALSE) {
            if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
                filename = "castle/warehouse/castlewarehouse-busy.htm"; // Busy because of siege
            } else if (condition == COND_OWNER) {
                if (val == 0) {
                    filename = "castle/warehouse/castlewarehouse.htm";
                } else {
                    filename = "castle/warehouse/castlewarehouse-" + val + ".htm";
                }
            }
        }

        HtmlMessage html = new HtmlMessage(this);
        html.setFile(filename);
        player.sendPacket(html);
    }

    protected int validateCondition(Player player) {
        if (player.isGM()) {
            return COND_OWNER;
        }

        final Castle castle = getCastle();
        if (castle != null && castle.getId() > 0) {
            if (player.getClan() != null) {
                if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
                    return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
                } else if (castle.getOwnerId() == player.getClanId()) // Clan owns castle
                {
                    return COND_OWNER;
                }
            }
        }
        return COND_ALL_FALSE;
    }
}