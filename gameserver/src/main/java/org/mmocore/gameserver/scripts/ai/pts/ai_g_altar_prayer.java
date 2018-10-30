package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author Mangol
 */
public class ai_g_altar_prayer extends DefaultAI {
    public ai_g_altar_prayer(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(1000, ((60 * 60) * 1000));
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (timer_id == 1000) {
            if (Rnd.get(3) < 1) {
                ChatUtils.shout(actor, NpcString.valueOf(1900152));
            } else if (Rnd.get(2) < 1) {
                ChatUtils.shout(actor, NpcString.valueOf(1900153));
            } else {
                ChatUtils.shout(actor, NpcString.valueOf(1900154));
            }
            AddTimerEx(1000, ((60 * 60) * 1000));
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}
