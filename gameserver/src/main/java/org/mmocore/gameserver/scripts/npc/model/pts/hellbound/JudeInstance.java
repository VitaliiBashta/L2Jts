package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class JudeInstance extends NpcInstance {
    private static final int treasure_of_natives = 9684;
    private static final int ring_of_wind_government_order = 9677;

    public JudeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (i0 < 3) {
            showChatWindow(talker, "pts/hellbound/jude001.htm");
        } else if (i0 == 3 || i0 == 4) {
            showChatWindow(talker, "pts/hellbound/jude001c.htm");
        } else if (i0 == 5) {
            showChatWindow(talker, "pts/hellbound/jude001a.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/jude001b.htm");
        }
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
                if (ItemFunctions.getItemCount(player, treasure_of_natives) >= 40) {
                    ItemFunctions.addItem(player, ring_of_wind_government_order, 1);
                    ItemFunctions.removeItem(player, treasure_of_natives, 40);
                    showChatWindow(player, "pts/hellbound/jude002.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/jude002a.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}