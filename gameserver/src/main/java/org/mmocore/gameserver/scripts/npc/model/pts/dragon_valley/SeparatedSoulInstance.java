package org.mmocore.gameserver.scripts.npc.model.pts.dragon_valley;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 * Ai: ai_separated_soul
 */
public final class SeparatedSoulInstance extends NpcInstance {
    private static final int TelPosX_01 = 117046;
    private static final int TelPosY_01 = 76798;
    private static final int TelPosZ_01 = -2696;
    private static final int TelPosX_02 = 99218;
    private static final int TelPosY_02 = 110283;
    private static final int TelPosZ_02 = -3688;
    private static final int TelPosX_03 = 116992;
    private static final int TelPosY_03 = 113716;
    private static final int TelPosZ_03 = -3056;
    private static final int TelPosX_04 = 113203;
    private static final int TelPosY_04 = 121063;
    private static final int TelPosZ_04 = -3712;
    private static final int TelPosX_05 = 146129;
    private static final int TelPosY_05 = 111232;
    private static final int TelPosZ_05 = -3568;
    private static final int TelPosX_06 = 148447;
    private static final int TelPosY_06 = 110582;
    private static final int TelPosZ_06 = -3944;
    private static final int TelPosX_07 = 73122;
    private static final int TelPosY_07 = 118351;
    private static final int TelPosZ_07 = -3784;
    private static final int TelPosX_08 = 131116;
    private static final int TelPosY_08 = 114333;
    private static final int TelPosZ_08 = -3704;
    private static final String fnNotEnoughLevel = "pts/dragon_valley/separated_soul_09001.htm";
    private static final String antaras_items = "pts/dragon_valley/separated_soul_01002.htm";
    private static final String fnNo_items = "pts/dragon_valley/separated_soul_01003.htm";

    public SeparatedSoulInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32864:
                showChatWindow(player, "pts/dragon_valley/separated_soul_01001.htm");
                break;
            case 32865:
                showChatWindow(player, "pts/dragon_valley/separated_soul_02001.htm");
                break;
            case 32866:
                showChatWindow(player, "pts/dragon_valley/separated_soul_03001.htm");
                break;
            case 32867:
                showChatWindow(player, "pts/dragon_valley/separated_soul_04001.htm");
                break;
            case 32868:
                showChatWindow(player, "pts/dragon_valley/separated_soul_05001.htm");
                break;
            case 32869:
                showChatWindow(player, "pts/dragon_valley/separated_soul_06001.htm");
                break;
            case 32870:
                showChatWindow(player, "pts/dragon_valley/separated_soul_07001.htm");
                break;
            case 32891:
                showChatWindow(player, "pts/dragon_valley/separated_soul_08001.htm");
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1&")) {
            if (player.getLevel() < 80) {
                showChatWindow(player, fnNotEnoughLevel);
                return;
            }

            if (command.endsWith("reply=1")) {
                player.teleToLocation(TelPosX_01, TelPosY_01, TelPosZ_01);
            } else if (command.endsWith("reply=2")) {
                player.teleToLocation(TelPosX_02, TelPosY_02, TelPosZ_02);
            } else if (command.endsWith("reply=3")) {
                player.teleToLocation(TelPosX_03, TelPosY_03, TelPosZ_03);
            } else if (command.endsWith("reply=4")) {
                player.teleToLocation(TelPosX_04, TelPosY_04, TelPosZ_04);
            } else if (command.endsWith("reply=5")) {
                player.teleToLocation(TelPosX_05, TelPosY_05, TelPosZ_05);
            } else if (command.endsWith("reply=6")) {
                player.teleToLocation(TelPosX_06, TelPosY_06, TelPosZ_06);
            } else if (command.endsWith("reply=7")) {
                player.teleToLocation(TelPosX_07, TelPosY_07, TelPosZ_07);
            } else if (command.endsWith("reply=8")) {
                player.teleToLocation(TelPosX_08, TelPosY_08, TelPosZ_08);
            }
        } else if (command.startsWith("menu_select?ask=-2324&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, 17266) > 0 && ItemFunctions.getItemCount(player, 17267) > 0) {
                    if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                        player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                        return;
                    }
                    ItemFunctions.removeItem(player, 17266, 1);
                    ItemFunctions.removeItem(player, 17267, 1);
                    ItemFunctions.addItem(player, 17268, 1);
                } else {
                    showChatWindow(player, fnNo_items);
                }
            } else if (command.endsWith("reply=2")) {
                showChatWindow(player, antaras_items);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}