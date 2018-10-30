package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.CrystalCaverns;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class SteamCorridorControllerInstance extends NpcInstance {
    public SteamCorridorControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32273:
                if (player.isInParty()) {
                    showChatWindow(player, "pts/crystalcave/b_telecube1001.htm");
                } else {
                    showChatWindow(player, "pts/crystalcave/b_telecube1001a.htm");
                }
                break;
            case 32274:
                if (player.isInParty()) {
                    showChatWindow(player, "pts/crystalcave/b_telecube2001.htm");
                } else {
                    showChatWindow(player, "pts/crystalcave/b_telecube1001a.htm");
                }
                break;
            case 32275:
                if (player.isInParty()) {
                    showChatWindow(player, "pts/crystalcave/b_telecube3001.htm");
                } else {
                    showChatWindow(player, "pts/crystalcave/b_telecube1001a.htm");
                }
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

        if (command.equalsIgnoreCase("move_next")) {
            if (getReflection().getInstancedZoneId() == 10) {
                ((CrystalCaverns) getReflection()).notifyNextLevel(this);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}