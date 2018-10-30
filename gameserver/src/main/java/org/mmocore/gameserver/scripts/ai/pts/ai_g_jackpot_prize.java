package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_g_jackpot_prize extends Fighter {
    private static final int ItemName_52_A = 14678;
    private static final int ItemName_52_B = 8755;
    private static final int ItemName_70_A = 14679;
    private static final int ItemName_70_B_1 = 5577;
    private static final int ItemName_70_B_2 = 5578;
    private static final int ItemName_70_B_3 = 5579;
    private static final int ItemName_80_A = 14680;
    private static final int ItemName_80_B_1 = 9552;
    private static final int ItemName_80_B_2 = 9553;
    private static final int ItemName_80_B_3 = 9554;
    private static final int ItemName_80_B_4 = 9555;
    private static final int ItemName_80_B_5 = 9556;
    private static final int ItemName_80_B_6 = 9557;
    private int param2;
    private int param3;

    public ai_g_jackpot_prize(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        param2 = getActor().getParam2();
        param3 = getActor().getParam3();
        final int i0 = Rnd.get(1);
        if (i0 == 0) {
            ChatUtils.shout(getActor(), NpcString.OH_MY_WINGS_DISAPPEARED_ARE_YOU_GONNA_HIT_ME_IF_YOU_HIT_ME_I_LL_THROW_UP_EVERYTHING_THAT_I_ATE);
        } else {
            ChatUtils.shout(getActor(), NpcString.OH_MY_WINGS_ACK_ARE_YOU_GONNA_HIT_ME_SCARY_SCARY_IF_YOU_HIT_ME_SOMETHING_BAD_WILL_HAPPEN);
        }
        AddTimerEx(1001, (60 * 1000) * 10);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 1001) {
            getActor().deleteMe();
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();
        if (killer != null && killer.isPlayer() || killer.isPlayable()) {
            switch (param3) {
                case 52:
                    if (param2 >= 5) {
                        actor.dropItem(killer.getPlayer(), ItemName_52_A, 1);
                    } else if (param2 >= 2 && param2 < 5) {
                        actor.dropItem(killer.getPlayer(), ItemName_52_B, 2);
                    } else {
                        actor.dropItem(killer.getPlayer(), ItemName_52_B, 1);
                    }
                    break;
                case 70:
                    if (param2 >= 5) {
                        actor.dropItem(killer.getPlayer(), ItemName_70_A, 1);
                        break;
                    } else if (param2 >= 2 && param2 < 5) {
                        final int i0 = Rnd.get(3);
                        if (i0 == 2) {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_1, 2);
                        } else if (i0 == 1) {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_2, 2);
                        } else {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_3, 2);
                        }
                        break;
                    } else {
                        final int i0 = Rnd.get(3);
                        if (i0 == 2) {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_1, 1);
                        } else if (i0 == 1) {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_2, 1);
                        } else {
                            actor.dropItem(killer.getPlayer(), ItemName_70_B_3, 1);
                        }
                        break;
                    }
                case 80:
                    if (param2 >= 5) {
                        actor.dropItem(killer.getPlayer(), ItemName_80_A, 1);
                        break;
                    } else if (param2 >= 2 && param2 < 5) {
                        final int i0 = Rnd.get(6);
                        if (i0 == 5) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_1, 2);
                        } else if (i0 == 4) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_2, 2);
                        } else if (i0 == 3) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_3, 2);
                        } else if (i0 == 2) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_4, 2);
                        } else if (i0 == 1) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_5, 2);
                        } else {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_6, 2);
                        }
                        break;
                    } else {

                        final int i0 = Rnd.get(6);
                        if (i0 == 5) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_1, 1);
                        } else if (i0 == 4) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_2, 1);
                        } else if (i0 == 3) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_3, 1);
                        } else if (i0 == 2) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_4, 1);
                        } else if (i0 == 1) {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_5, 1);
                        } else {
                            actor.dropItem(killer.getPlayer(), ItemName_80_B_6, 1);
                        }
                        break;
                    }
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        getActor().suicide(attacker);
    }
}