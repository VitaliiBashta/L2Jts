package org.mmocore.gameserver.scripts.ai.pts.gracia.alliance_base_ketserusa;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SceneMovie;
import org.mmocore.gameserver.world.World;

/**
 * @author Mangol
 */
public class ai_lindvior_camera_01 extends DefaultAI {
    private static final int TM_LINDVIOR_SCENE = 78001;
    private static final int TIME_LINDVIOR_SCENE = 46;

    public ai_lindvior_camera_01(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtScriptEvent(final ScriptEvent arg1, final Object arg2, final Object arg3) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (arg1 == ScriptEvent.SCE_SCENE_LINDVIOR) {
            World.getAroundPlayers(actor, 4000, 3100).stream().filter(player -> !player.isInBoat() && !player.isInFlyingTransform()).forEach(player -> {
                player.showQuestMovie(SceneMovie.LINDVIOR_SPAWN);
            });
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
