package org.mmocore.gameserver.scripts.npc.model.pts.aerial_cleft;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public final class CleftTownTeleporterInstance extends NpcInstance {
    public CleftTownTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/gracia/aerial_cleft/cleft_gate_red001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.equalsIgnoreCase("teleport_request")) {
            if (player.getLevel() >= 75) {
                player.teleToLocation(-204288, 242026, 1728);
            } else {
                showChatWindow(player, "pts/gracia/aerial_cleft/cleft_gate_red002.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}