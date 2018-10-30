package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.Frintezza;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class FrintezzaGatekeeperInstance extends NpcInstance {
    /**
     *
     */
    private static final long serialVersionUID = 3496505324733142696L;
    private static final int frintezzaIzId = 136;

    public FrintezzaGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_frintezza")) {
            ReflectionUtils.simpleEnterInstancedZone(player, Frintezza.class, frintezzaIzId, 8073, 1);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}