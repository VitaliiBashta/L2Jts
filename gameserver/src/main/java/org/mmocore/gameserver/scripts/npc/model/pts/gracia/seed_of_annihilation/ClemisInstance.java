package org.mmocore.gameserver.scripts.npc.model.pts.gracia.seed_of_annihilation;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class ClemisInstance extends NpcInstance {
    private static final int pos_x = -180218;
    private static final int pos_y = 185923;
    private static final int pos_z = -10576;
    private static final String fnEnterFailed = "pts/gracia/seed_of_annihilation/clemis002.htm";

    public ClemisInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-415&")) {
            if (command.endsWith("reply=1")) {
                if (player.getLevel() > 79) {
                    player.teleToLocation(pos_x, pos_y, pos_z);
                } else {
                    showChatWindow(player, fnEnterFailed);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}