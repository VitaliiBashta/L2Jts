package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.FreyaHard;
import org.mmocore.gameserver.scripts.instances.FreyaNormal;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class JiniaNpcInstance extends NpcInstance {
    private static final int normalFreyaIzId = 139;
    private static final int extremeFreyaIzId = 144;

    public JiniaNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_normalfreya")) {
            ReflectionUtils.simpleEnterInstancedZone(player, FreyaNormal.class, normalFreyaIzId);
        } else if (command.equalsIgnoreCase("request_extremefreya")) {
            ReflectionUtils.simpleEnterInstancedZone(player, FreyaHard.class, extremeFreyaIzId);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}