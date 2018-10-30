package org.mmocore.gameserver.scripts.ai.pts.gracia.alliance_base_ketserusa;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author Mangol
 */
public class npc_warmage_artius extends DefaultAI {
    private static final int TM_LINDVIOR_SCENE = 78001;
    private static final int TIME_LINDVIOR_SCENE = 30;

    public npc_warmage_artius(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtScriptEvent(final ScriptEvent arg1, final Object arg2, final Object arg3) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (arg1 == ScriptEvent.SCE_SCENE_LINDVIOR) {
            ChatUtils.shout(actor, NpcString.OBSTRUCTION_WAND_BEWITCHED_THIS_IS);
            AddTimerEx(TM_LINDVIOR_SCENE, TIME_LINDVIOR_SCENE * 1000);
        }
        super.onEvtScriptEvent(arg1, arg2, arg3);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (timer_id == TM_LINDVIOR_SCENE) {
            sendScriptEventOneNpc(ScriptEvent.SCE_SCENE_LINDVIOR, 32552, null, null);
        } else {
            super.onEvtTimerFiredEx(timer_id, arg1, arg2);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}
