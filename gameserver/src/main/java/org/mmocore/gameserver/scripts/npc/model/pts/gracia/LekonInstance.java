package org.mmocore.gameserver.scripts.npc.model.pts.gracia;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL, KilRoy
 */
public class LekonInstance extends NpcInstance {
    private static final String engineerSay1 = "pts/gracia/engineer_recon003.htm";
    private static final String engineerSay2 = "pts/gracia/engineer_recon004.htm";
    private static final String engineerSay3 = "pts/gracia/engineer_recon005.htm";
    private static final int EnergyStone = 13277;
    private static final int AirshipConfirm = 13559;

    public LekonInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1724&")) {
            if (command.endsWith("reply=2")) {
                if (player.getClan() != null) {
                    if (!player.isClanLeader() || player.getClan().getLevel() < 5) {
                        showChatWindow(player, engineerSay1);
                    } else if (ItemFunctions.getItemCount(player, AirshipConfirm) > 0) {
                        showChatWindow(player, engineerSay3);
                    } else if (player.getClan().getAirship() != null) {
                        ChatUtils.say(this, NpcString.THE_AWARDED_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_RECEIVED);
                    } else if (ItemFunctions.getItemCount(player, EnergyStone) >= 10) {
                        ItemFunctions.removeItem(player, EnergyStone, 10);
                        ItemFunctions.addItem(player, AirshipConfirm, 1);
                    } else {
                        showChatWindow(player, engineerSay2);
                    }
                } else {
                    showChatWindow(player, engineerSay1);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}