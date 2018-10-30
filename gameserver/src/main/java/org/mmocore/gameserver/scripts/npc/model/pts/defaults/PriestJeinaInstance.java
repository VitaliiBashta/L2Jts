package org.mmocore.gameserver.scripts.npc.model.pts.defaults;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Magister
 * @date 11/01/2015
 */
public final class PriestJeinaInstance extends NpcInstance {
    public PriestJeinaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32617:
                showChatWindow(player, "pts/default/priest_jeina001.htm");
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
        if (command.startsWith("menu_select?ask=198&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_198_SevenSignsEmbryo/priest_jeina_q0198_01.htm");
            } else if (command.endsWith("reply=2")) {
                showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_198_SevenSignsEmbryo/priest_jeina_q0198_02.htm");
                reflection.collapse();
            } else if (command.endsWith("reply=3")) {
                showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_198_SevenSignsEmbryo/priest_jeina_q0198_02a.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}