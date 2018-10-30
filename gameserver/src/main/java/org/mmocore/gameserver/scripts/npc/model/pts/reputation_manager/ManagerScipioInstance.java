package org.mmocore.gameserver.scripts.npc.model.pts.reputation_manager;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowInfoUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 25/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public final class ManagerScipioInstance extends NpcInstance {
    // dialogs
    private static final String fnNoPvpPoint = "pts/reputation_manager/pvp_merchant_scipio002.htm";
    private static final String fnNoPledge = "pts/reputation_manager/pvp_merchant_scipio004.htm";
    private static final String fnFameUpSuccess = "pts/reputation_manager/pvp_merchant_scipio005.htm";
    private static final String fnNoPkCount = "pts/reputation_manager/pvp_merchant_scipio006.htm";
    private static final String fnPkDownSuccess = "pts/reputation_manager/pvp_merchant_scipio007.htm";
    // multisell
    private static final int pvp_point_trade = 638;
    private static final int pvp_point_back_trade = 639;
    private static final int pvp_item_sell = 640;

    public ManagerScipioInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 36480:
                if (player.getFame() > 0) {
                    if ((player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third) || player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth)) && player.getLevel() >= 40)
                        showChatWindow(player, "pts/reputation_manager/pvp_merchant_scipio001.htm");
                    else
                        showChatWindow(player, fnNoPvpPoint);
                } else
                    showChatWindow(player, fnNoPvpPoint);
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
        if (command.startsWith("menu_select?ask=-3001&")) {
            if (command.endsWith("reply=1")) {
                if (player.getFame() < 0)
                    showChatWindow(player, fnNoPvpPoint);
                else
                    MultiSellHolder.getInstance().SeparateAndSend(pvp_point_trade, player, getObjectId(), 0);
            } else if (command.endsWith("reply=2")) {
                if (player.getFame() < 0)
                    showChatWindow(player, fnNoPvpPoint);
                else
                    MultiSellHolder.getInstance().SeparateAndSend(pvp_point_back_trade, player, getObjectId(), 0);
            } else if (command.endsWith("reply=3")) {
                if (player.getFame() < 0)
                    showChatWindow(player, fnNoPvpPoint);
                else
                    MultiSellHolder.getInstance().SeparateAndSend(pvp_item_sell, player, getObjectId(), 0);
            }
        } else if (command.startsWith("menu_select?ask=-4001&")) {
            if (command.endsWith("reply=1")) {
                if (player.getPkKills() > 0) {
                    if (player.getFame() >= 5000) {
                        if ((player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third) || player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth)) && player.getLevel() >= 40) {
                            player.setFame(player.getFame() - 5000, "PK_Count");
                            player.setPkKills(player.getPkKills() - 1);
                            showChatWindow(player, fnPkDownSuccess);
                        } else
                            showChatWindow(player, fnNoPvpPoint);
                    } else
                        showChatWindow(player, fnNoPvpPoint);
                } else
                    showChatWindow(player, fnNoPkCount);
            } else if (command.endsWith("reply=2")) {
                if (player.getClan() != null && player.getClan().getLevel() >= 5) {
                    if (player.getFame() > 1000) {
                        if ((player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third) || player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth)) && player.getLevel() >= 40) {
                            player.setFame(player.getFame() - 1000, "CRP");
                            player.getClan().incReputation(50, false, "Reputation Manager from " + player.getName());
                            player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                            player.sendPacket(SystemMsg.YOU_HAVE_ACQUIRED_50_CLAN_FAME_POINTS);
                            showChatWindow(player, fnFameUpSuccess);
                        } else
                            showChatWindow(player, fnNoPvpPoint);
                    } else
                        showChatWindow(player, fnNoPvpPoint);
                } else
                    showChatWindow(player, fnNoPledge);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}