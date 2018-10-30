package org.mmocore.gameserver.scripts.ai.pts.gracia.alliance_base_ketserusa;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

/**
 * @author Mangol
 */
public class npc_soldier_tomaris extends DefaultAI {
    private static final int TM_LINDVIOR_SCENE_CHECK = 78003;
    private static final int TIME_LINDVIOR_SCENE_CHECK = 30;
    private static final int TM_LINDVIOR_SCENE_1 = 78001;
    private static final int TIME_LINDVIOR_SCENE_1 = 60;
    private static final int TM_LINDVIOR_SCENE_2 = 78002;
    private static final int TIME_LINDVIOR_SCENE_2 = 30;
    private int i_ai0;

    public npc_soldier_tomaris(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        i_ai0 = 0;
        AddTimerEx(TM_LINDVIOR_SCENE_CHECK, 100000);// TODO 1000 Поставил
        // затычку чтобы игроки
        // смоги зайти вовремя
        // малоли...
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtScriptEvent(final ScriptEvent arg1, final Object arg2, final Object arg3) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (arg1 == ScriptEvent.SCE_SCENE_LINDVIOR && i_ai0 == 1) {
            ChatUtils.shout(actor, NpcString.BE_CAREFUL_SOMETHINGS_COMING);
            i_ai0 = 2;
            AddTimerEx(TM_LINDVIOR_SCENE_2, TIME_LINDVIOR_SCENE_2 * 1000);
        } else if (arg1 == ScriptEvent.SCE_SCENE_LINDVIOR && i_ai0 == 2) {
            i_ai0 = 0;
        }
        super.onEvtScriptEvent(arg1, arg2, arg3);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (timer_id == TM_LINDVIOR_SCENE_1) {
            sendScriptEventOneNpc(ScriptEvent.SCE_SCENE_LINDVIOR, 32559, null, null);
        } else if (timer_id == TM_LINDVIOR_SCENE_2) {
            sendScriptEventOneNpc(ScriptEvent.SCE_SCENE_LINDVIOR, 18669, null, null);
        } else if (timer_id == TM_LINDVIOR_SCENE_CHECK) {
            final ZonedDateTime now = ZonedDateTime.now();
            if (now.getMinute() == 58 && now.getHour() == 18 && (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.TUESDAY) && i_ai0 == 0) {
                ChatUtils.shout(actor, NpcString.HUH_THE_SKY_LOOKS_FUNNY_WHATS_THAT);
                i_ai0 = 1;
                AddTimerEx(TM_LINDVIOR_SCENE_1, TIME_LINDVIOR_SCENE_1 * 1000);
            }
            AddTimerEx(TM_LINDVIOR_SCENE_CHECK, TIME_LINDVIOR_SCENE_CHECK * 1000);
        } else {
            super.onEvtTimerFiredEx(timer_id, arg1, arg2);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}
