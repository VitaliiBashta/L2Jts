package org.mmocore.gameserver.scripts.npc.model.pts.defaults;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author Magister
 * @date 13/01/2015
 */
public final class GruffManInstance extends NpcInstance {
    private static final int inzone_id = 158;

    public GruffManInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32862:
                showChatWindow(player, "pts/default/ssq2_door_elcardia001.htm");
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
        if (command.startsWith("menu_select?ask=10292&")) {
            if (command.endsWith("reply=1")) {
                //if((player.getQuestState(10292) != null && player.getQuestState(10292).getInt("ssq2_mystery_girl") > 1 && player.getQuestState(10292).getInt("ssq2_mystery_girl") < 9) || (player.getQuestState(10293) != null && player.getQuestState(10293).getInt("ssq2_forbidden_book") > 8) || (player.getQuestState(10293) == null && player.getQuestState(10292).isCompleted()) || (player.getQuestState(10294) == null && player.getQuestState(10293).isCompleted()) || (player.getQuestState(10294) != null && player.getQuestState(10294).getInt("ssq2_going_to_the_silence") < 9) || (player.getQuestState(10296) != null && player.getQuestState(10296).getInt("ssq2_emergence_of_embryo") > 2 && player.getQuestState(10296).getInt("ssq2_emergence_of_embryo") < 4))
                //{
                ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
                //}
                //else
                //showChatWindow(player, "pts/default/ssq2_door_elcardia003.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}