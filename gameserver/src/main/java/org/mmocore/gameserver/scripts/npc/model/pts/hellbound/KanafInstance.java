package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author KilRoy
 */
public class KanafInstance extends NpcInstance {
    public KanafInstance(int objectId, NpcTemplate template) {
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
                final int i0 = HellboundManager.getHellboundLevel();
                if (i0 >= 10) {
                    if (player.getParty() != null) {
                        ReflectionUtils.simpleEnterInstancedZone(player, 2);
                    } else {
                        showChatWindow(player, "pts/hellbound/kcaien002b.htm");
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/kcaien002a.htm");
                }
            } else if (command.endsWith("reply=2")) {
                final int i1 = Rnd.get(3);
                switch (i1) {
                    case 0:
                        showChatWindow(player, "pts/hellbound/kcaien001a.htm");
                        break;
                    case 1:
                        showChatWindow(player, "pts/hellbound/kcaien001b.htm");
                        break;
                    case 2:
                        showChatWindow(player, "pts/hellbound/kcaien001c.htm");
                        break;
                    case 3:
                        break;
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}