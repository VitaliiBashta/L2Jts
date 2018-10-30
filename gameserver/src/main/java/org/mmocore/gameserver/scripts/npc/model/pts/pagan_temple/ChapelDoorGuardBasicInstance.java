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
public class ChapelDoorGuardBasicInstance extends NpcInstance {
    private static final String GateOpenHTML = "pts/pagan_temple/chapel_door_guard002.htm";
    private static final String GateOpenFailHTML = "pts/pagan_temple/chapel_door_guard003.htm";

    public ChapelDoorGuardBasicInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=502&") && command.endsWith("reply=3")) {
            int q_mark_of_heresy = 8067;
            if (ItemFunctions.getItemCount(player, q_mark_of_heresy) >= 1) {
                final DoorInstance door1 = ReflectionUtils.getDoor(19160010);
                door1.openMe();
                final DoorInstance door2 = ReflectionUtils.getDoor(19160011);
                door2.openMe();
                showChatWindow(player, GateOpenHTML);
            } else {
                showChatWindow(player, GateOpenFailHTML);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
