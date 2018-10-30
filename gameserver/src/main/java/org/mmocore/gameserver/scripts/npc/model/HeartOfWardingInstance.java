package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.AntharasManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */

public final class HeartOfWardingInstance extends NpcInstance {
    private static final Location TELEPORT_POSITION1 = new Location(179892, 114915, -7704);

    public HeartOfWardingInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("enter_lair")) {
            // Has an item (leader only)
            if (ItemFunctions.getItemCount(player, 3865) < 1) {
                showChatWindow(player, 4);
                return;
            }

            CommandChannel cc = null;
            if (player.isInParty() && player.getParty().isInCommandChannel()) {
                cc = player.getParty().getCommandChannel();
            }
            // If in CC, is CC leader
            if (cc != null && player != cc.getGroupLeader()) {
                showChatWindow(player, 5);
                return;
            }
            switch (AntharasManager.checkNestEntranceCond(cc != null ? cc.getMemberCount() : 1)) {
                case NOT_AVAILABLE:
                    showChatWindow(player, 3);
                    break;
                case ALREADY_ATTACKED:
                    showChatWindow(player, 2);
                    break;
                case LIMIT_EXCEEDED:
                    showChatWindow(player, 1);
                    break;
                case ALLOW:
                    if (cc != null) {
                        for (Player member : cc) {
                            if (member.isInRange(this, 500) && !member.isCursedWeaponEquipped()) {
                                member.teleToLocation(TELEPORT_POSITION1);
                            }
                        }
                    } else {
                        player.teleToLocation(TELEPORT_POSITION1);
                    }

                    //notify Manager
                    AntharasManager.notifyEntrance();
                    break;
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}