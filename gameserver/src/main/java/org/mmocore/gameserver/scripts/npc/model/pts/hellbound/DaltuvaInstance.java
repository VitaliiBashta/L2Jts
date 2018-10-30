package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class DaltuvaInstance extends NpcInstance {
    private static final int curiosity_of_matras = 132;

    public DaltuvaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1006&")) {
            if (command.endsWith("reply=1")) {
                if (player.getQuestState(curiosity_of_matras) != null && player.getQuestState(curiosity_of_matras).isCompleted()) {
                    player.teleToLocation(17934, 283189, -9701);
                } else {
                    showChatWindow(player, "pts/hellbound/daltuva002.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}