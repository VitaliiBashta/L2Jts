package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.GuardInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author Mangol
 */
public class BorderOutpostDoormanInstance extends GuardInstance {
    public BorderOutpostDoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-201&")) {
            if (command.endsWith("reply=1")) {
                final DoorInstance door = ReflectionUtils.getDoor(24170001);
                door.openMe();
            } else if (command.endsWith("reply=2")) {
                final DoorInstance door = ReflectionUtils.getDoor(24170001);
                door.closeMe();
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
