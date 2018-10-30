package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_guardian_manager extends DefaultAI {
    private static final int guardian_of_antaras = 22857;
    private static final int Course1_Check_Timer = 7734;
    private static final int Course2_Check_Timer = 7735;
    private static final int Course3_Check_Timer = 7736;
    private static final int Course4_Check_Timer = 7737;
    private static final int Max_Spawn_Roaming = 3;
    private static final int Spawn_Interval = 1800;
    private static final int SpawnPosX_1_1 = 140641;
    private static final int SpawnPosY_1_1 = 114525;
    private static final int SpawnPosZ_1_1 = -3752;
    private static final int SpawnPosX_2_1 = 143789;
    private static final int SpawnPosY_2_1 = 110205;
    private static final int SpawnPosZ_2_1 = -3968;
    private static final int SpawnPosX_3_1 = 146466;
    private static final int SpawnPosY_3_1 = 109789;
    private static final int SpawnPosZ_3_1 = -3440;
    private static final int SpawnPosX_4_1 = 145482;
    private static final int SpawnPosY_4_1 = 120250;
    private static final int SpawnPosZ_4_1 = -3944;
    private static int i_ai0;
    private static int i_ai1;
    private static int i_ai2;
    private static int i_ai3;

    public ai_guardian_manager(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(Course1_Check_Timer, Rnd.get(10000));
        AddTimerEx(Course2_Check_Timer, Rnd.get(10000));
        AddTimerEx(Course3_Check_Timer, Rnd.get(10000));
        AddTimerEx(Course4_Check_Timer, Rnd.get(10000));
        i_ai0 = 0;
        i_ai1 = 0;
        i_ai2 = 0;
        i_ai3 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == Course1_Check_Timer) {
            if (i_ai0 < Max_Spawn_Roaming) {
                i_ai0++;
                final NpcInstance npc = NpcUtils.spawnSingle(guardian_of_antaras, SpawnPosX_1_1, SpawnPosY_1_1, SpawnPosZ_1_1);
                npc.setLeader(getActor(), true);
                npc.setParameter("SuperPointMethod", 1);
            }
            AddTimerEx(Course1_Check_Timer, Spawn_Interval * 1000);
        } else if (timer_id == Course2_Check_Timer) {
            if (i_ai1 < Max_Spawn_Roaming) {
                i_ai1++;
                final NpcInstance npc = NpcUtils.spawnSingle(guardian_of_antaras, SpawnPosX_2_1, SpawnPosY_2_1, SpawnPosZ_2_1);
                npc.setLeader(getActor(), true);
                npc.setParameter("SuperPointMethod", 2);
            }
            AddTimerEx(Course2_Check_Timer, Spawn_Interval * 1000);
        } else if (timer_id == Course3_Check_Timer) {
            if (i_ai2 < Max_Spawn_Roaming) {
                i_ai2++;
                final NpcInstance npc = NpcUtils.spawnSingle(guardian_of_antaras, SpawnPosX_3_1, SpawnPosY_3_1, SpawnPosZ_3_1);
                npc.setLeader(getActor(), true);
                npc.setParameter("SuperPointMethod", 3);
            }
            AddTimerEx(Course3_Check_Timer, Spawn_Interval * 1000);
        } else if (timer_id == Course4_Check_Timer) {
            if (i_ai3 < Max_Spawn_Roaming) {
                i_ai3++;
                final NpcInstance npc = NpcUtils.spawnSingle(guardian_of_antaras, SpawnPosX_4_1, SpawnPosY_4_1, SpawnPosZ_4_1);
                npc.setLeader(getActor(), true);
                npc.setParameter("SuperPointMethod", 4);
            }
            AddTimerEx(Course4_Check_Timer, Spawn_Interval * 1000);
        }
    }

    @Override
    protected void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2) {
        final int param1 = (Integer) arg1;
        if (event == ScriptEvent.SCE_WATCHER_DEAD) {
            if (param1 == 1) {
                if (i_ai0 > 0) {
                    i_ai0--;
                    if (i_ai0 < 0) {
                        i_ai0 = 0;
                    }
                }
            } else if (param1 == 2) {
                if (i_ai1 > 0) {
                    i_ai1--;
                    if (i_ai1 < 0) {
                        i_ai1 = 0;
                    }
                }
            } else if (param1 == 3) {
                if (i_ai2 > 0) {
                    i_ai2--;
                    if (i_ai2 < 0) {
                        i_ai2 = 0;
                    }
                }
            } else if (param1 == 4) {
                if (i_ai3 > 0) {
                    i_ai3--;
                    if (i_ai3 < 0) {
                        i_ai3 = 0;
                    }
                }
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}