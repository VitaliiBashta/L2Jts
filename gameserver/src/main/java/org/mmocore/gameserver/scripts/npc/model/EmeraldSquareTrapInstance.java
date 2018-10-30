package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class EmeraldSquareTrapInstance extends NpcInstance {
    public EmeraldSquareTrapInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("release_lock")) {
            if (getReflection().getInstancedZoneId() == 10) {
                getReflection().getDoor(24220001).openMe();
                deleteMe();
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
