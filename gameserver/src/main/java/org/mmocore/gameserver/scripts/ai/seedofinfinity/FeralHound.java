package org.mmocore.gameserver.scripts.ai.seedofinfinity;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */

public class FeralHound extends Fighter {
    public FeralHound(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}