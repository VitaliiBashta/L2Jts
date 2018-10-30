package org.mmocore.gameserver.scripts.ai.pts.hellbound;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author KilRoy
 */
public class ai_dolmen extends CharacterAI {
    public ai_dolmen(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(5001, 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 5001) {
            broadCastScriptEvent(ScriptEvent.SCE_QUARRY_SLAVE_SEE_SH, 500);
            AddTimerEx(5001, 1000);
        }
    }
}