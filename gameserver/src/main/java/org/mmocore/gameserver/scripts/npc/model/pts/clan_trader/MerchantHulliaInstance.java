package org.mmocore.gameserver.scripts.npc.model.pts.clan_trader;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowInfoUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 26/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public final class MerchantHulliaInstance extends NpcInstance {
    // dialogs
    private static final String fnNoPledge = "pts/clan_trader/clan_merchant_hullia004.htm";
    private static final String fnPledgeFameValue = "pts/clan_trader/clan_merchant_hullia005.htm";
    private static final String fnNotEnoughItem = "pts/clan_trader/clan_merchant_hullia006.htm";
    private static final String fnUpdateFameSuccess = "pts/clan_trader/clan_merchant_hullia007.htm";
    private static final String fnLowerPledgeLvReq = "pts/clan_trader/clan_merchant_hullia008.htm";
    // multisell
    private static final int pledge_point_a_armor = 551;
    private static final int apella_upgrade = 628;
    // clan
    private static final int pledge_lv_req = 5;
    // etcitem
    private static final int item_oath = 9911;
    private static final int num_oath = 1;
    private static final int fame_oath = 500;
    private static final int item_proof = 9910;
    private static final int num_proof = 10;
    private static final int fame_proof = 100;
    private static final int item_strap = 9912;
    private static final int num_strap = 100;
    private static final int fame_strap = 12;

    public MerchantHulliaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32024:
                if (player.getClan() != null && (player.isClanLeader() || (player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) == Clan.CP_CL_TROOPS_FAME))
                    showChatWindow(player, getFnHi());
                else
                    showChatWindow(player, "pts/clan_trader/clan_merchant_hullia002.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-302&")) {
            if (command.endsWith("reply=-1")) {
                if (player.getClan() != null) {
                    if (player.getClan().getLevel() < pledge_lv_req)
                        showChatWindow(player, fnLowerPledgeLvReq);
                    if (player.isClanLeader())
                        showChatWindow(player, fnPledgeFameValue, "<?pledge_name?>", player.getClan().getName(), "<?fame_value?>", String.valueOf(player.getClan().getReputationScore()));
                }
            } else if (command.endsWith("reply=0")) {
                showChatWindow(player, "pts/clan_trader/clan_merchant_hullia003.htm");
            } else if (command.endsWith("reply=1")) {
                if (player.getInventory().containsItem(item_oath)) {
                    if (player.getClan() == null) {
                        showChatWindow(player, fnNoPledge);
                    }
                    player.getInventory().destroyItemByItemId(item_oath, num_oath);
                    player.getClan().incReputation(fame_oath, false, "Clan Trader from " + player.getName());
                    player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                    player.sendPacket(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_ADDED_S1_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(fame_oath));
                    showChatWindow(player, fnUpdateFameSuccess);
                } else {
                    showChatWindow(player, fnNotEnoughItem);
                }
            } else if (command.endsWith("reply=2")) {
                if (player.getInventory().containsItem(item_proof)) {
                    if (player.getClan() == null) {
                        showChatWindow(player, fnNoPledge);
                    }
                    player.getInventory().destroyItemByItemId(item_proof, num_proof);
                    player.getClan().incReputation(fame_proof, false, "Clan Trader from " + player.getName());
                    player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                    player.sendPacket(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_ADDED_S1_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(fame_proof));
                    showChatWindow(player, fnUpdateFameSuccess);
                } else {
                    showChatWindow(player, fnNotEnoughItem);
                }
            } else if (command.endsWith("reply=3")) {
                if (player.getInventory().containsItem(item_strap)) {
                    if (player.getClan() == null) {
                        showChatWindow(player, fnNoPledge);
                    }
                    player.getInventory().destroyItemByItemId(item_strap, num_strap);
                    player.getClan().incReputation(fame_strap, false, "Clan Trader from " + player.getName());
                    player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                    player.sendPacket(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_ADDED_S1_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(fame_strap));
                    showChatWindow(player, fnUpdateFameSuccess);
                } else {
                    showChatWindow(player, fnNotEnoughItem);
                }
            }
        } else if (command.startsWith("menu_select?ask=-303&")) {
            if (command.endsWith("reply=551")) {
                MultiSellHolder.getInstance().SeparateAndSend(pledge_point_a_armor, player, getObjectId(), 0);
            } else if (command.endsWith("reply=628")) {
                MultiSellHolder.getInstance().SeparateAndSend(apella_upgrade, player, getObjectId(), 0);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}