package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_antaras_cave_raid_basic extends Fighter {
    private static final int SPAWN_HOLD_MON = 1001;
    private static final int SPAWN_VALIDATE_CHEKER = 1002;
    private static final int SPAWN_HOLD_MON_TIME = 50;
    private static final int underling = 25728;
    private static final int underling1 = 25729;
    private int i_ai0;
    private int i_ai1;
    private int i_ai2;
    private int i_ai3;

    public ai_antaras_cave_raid_basic(NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
        i_ai1 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() == null || getActor().isDead()) {
            return;
        }

        if (timer_id == SPAWN_VALIDATE_CHEKER) {
            if (!getActor().getAggroList().isEmpty()) {
                i_ai0 = 0;
            } else {
                i_ai1 = 0;
            }
        } else if (timer_id == SPAWN_HOLD_MON) {
            if (i_ai0 == 0) {
                i_ai0 = 1;
                AddTimerEx(SPAWN_HOLD_MON, ((SPAWN_HOLD_MON_TIME + Rnd.get(20)) * 1000));
                AddTimerEx(SPAWN_VALIDATE_CHEKER, 300000L);
                for (final NpcInstance npcs : GameObjectsStorage.getAllByNpcId(underling, true)) {
                    if (npcs.isInRangeZ(getActor(), 1500)) {
                        i_ai2++;
                    } else {
                        npcs.deleteMe();
                    }
                }
                if (i_ai2 <= 20) {
                    for (int i2 = 0; i2 < 3; i2++) {
                        if (getActor().getAggroList() != null && !getActor().getAggroList().isEmpty()) {
                            final Creature target = getActor().getAggroList().getRandomHated();
                            if (target != null) {
                                final NpcInstance npc = NpcUtils.spawnSingle(underling, Location.findAroundPosition(target, Rnd.get(100)), 1800000L);
                                npc.getAggroList().addDamageHate(target, 0, 1);
                                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                            }
                        }
                    }
                }
                for (final NpcInstance npcs : GameObjectsStorage.getAllByNpcId(underling1, true)) {
                    if (npcs.isInRangeZ(getActor(), 1500)) {
                        i_ai3++;
                    } else {
                        npcs.deleteMe();
                    }
                }
                if (i_ai3 <= 20) {
                    for (int i3 = 0; i3 < 2; i3++) {
                        if (getActor().getAggroList() != null && !getActor().getAggroList().isEmpty()) {
                            final Creature target = getActor().getAggroList().getRandomHated();
                            if (target != null) {
                                final NpcInstance npc = NpcUtils.spawnSingleStablePoint(underling1, Location.findAroundPosition(target, Rnd.get(50)), 1800000L);
                                npc.getAggroList().addDamageHate(target, 0, 1);
                                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (i_ai1 == 0) {
            i_ai1 = 1;
            AddTimerEx(SPAWN_HOLD_MON, ((SPAWN_HOLD_MON_TIME + Rnd.get(20)) * 1000));
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}