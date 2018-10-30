package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author KilRoy
 */
public class adv_manager_town extends DefaultAI {
    public adv_manager_town(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(1001, 60 * 60 * 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() != null) {
            if (timer_id == 1001) {
                getActor().deleteMe();
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}