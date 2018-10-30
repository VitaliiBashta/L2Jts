package org.mmocore.gameserver.scripts.npc.model.pts.pagan_temple;

import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author : Mangol
 */
public class SanctuaryDoorGuardBasicInstance extends NpcInstance {
    private static final int q_mark_of_sacrifice = 8064;
    private static final int q_faded_mark_of_sac = 8065;
    private static final int q_mark_of_heresy = 8067;
    private static final String GateOpenHTML = "pts/pagan_temple/sanctuary_door_guard002.htm";
    private static final String GateOpenFailHTML = "pts/pagan_temple/sanctuary_door_guard003.htm";

    public SanctuaryDoorGuardBasicInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=502&") && command.endsWith("reply=1")) {
            if (ItemFunctions.getItemCount(player, q_mark_of_sacrifice) >= 1 || ItemFunctions.getItemCount(player, q_faded_mark_of_sac) >= 1 || ItemFunctions.getItemCount(player, q_mark_of_heresy) >= 1) {
                final DoorInstance door = ReflectionUtils.getDoor(19160001);
                door.openMe();
                showChatWindow(player, GateOpenHTML);
            } else {
                showChatWindow(player, GateOpenFailHTML);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
