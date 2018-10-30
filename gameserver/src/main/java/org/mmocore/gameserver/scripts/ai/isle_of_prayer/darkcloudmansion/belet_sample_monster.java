package org.mmocore.gameserver.scripts.ai.isle_of_prayer.darkcloudmansion;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author KilRoy
 */
public class belet_sample_monster extends Fighter {
    private static final int SCE_RECIVEAI_INDEX = 12508;
    private static final int SCE_REAL_SHOUT = 12574;
    private static final int SCE_REAL_DESPAWN = 12536;
    private static final int SCE_REAL_HINT_SPAWN = 12537;
    private static final int SCE_IAM_IMI = 12571;
    private static final int DOOR_6 = 24230006;
    private static final int[] BS = new int[]{18371, 18372, 18373, 18374, 18375, 18376, 18377};
    private static final String beletSampleGobletGroup = "goblet_belet_sample_group";
    private static final String beletSampleGroup = "belet_sample_group";
    private static int i_ai1 = 0;
    private static int i_ai5 = 0;
    private static int i_ai6 = 0;
    private int i_ai0 = 0;
    private int i_ai2 = 0;
    private int i_ai3 = 0;
    private int i_ai4 = 0;
    private int i_quest0 = 0;

    public belet_sample_monster(final NpcInstance actor) {
        super(actor);
    }

    private void BroadcastScriptEvent(final int script_event_arg1, final int script_event_arg2) {
        if (script_event_arg1 == SCE_RECIVEAI_INDEX) {
            i_ai0 = script_event_arg2;
            i_ai4 = 1;
            BroadcastScriptEvent(SCE_REAL_SHOUT, 0);
        } else if (script_event_arg1 == SCE_REAL_SHOUT) {
            i_quest0 = 7;
            if (i_quest0 == 7) {
                if (i_ai0 == 1 || i_ai0 == 2 || i_ai0 == 3) {
                    final int i0 = Rnd.get(100);
                    if (i0 < 20) {
                        Functions.npcSay(getActor(), NpcString.I_M_THE_REAL);
                    } else if (i0 < 40) {
                        Functions.npcSay(getActor(), NpcString.PICK_ME);
                    } else if (i0 < 60) {
                        Functions.npcSay(getActor(), NpcString.TRUST_ME);
                    } else if (i0 < 80) {
                        Functions.npcSay(getActor(), NpcString.NOT_THAT_DUDE_I_M_THE_REAL_ONE);
                    } else {
                        Functions.npcSay(getActor(), NpcString.DONT_BE_FOOLED_DONT_BE_FOOLED_I_M_THE_REAL);
                    }
                } else if (Rnd.get(100) < 70) {
                    final int i0 = Rnd.get(100);
                    if (i0 < 20) {
                        Functions.npcSay(getActor(), NpcString.I_M_THE_REAL);
                    } else if (i0 < 40) {
                        Functions.npcSay(getActor(), NpcString.PICK_ME);
                    } else if (i0 < 60) {
                        Functions.npcSay(getActor(), NpcString.TRUST_ME);
                    } else if (i0 < 80) {
                        Functions.npcSay(getActor(), NpcString.NOT_THAT_DUDE_I_M_THE_REAL_ONE);
                    } else {
                        Functions.npcSay(getActor(), NpcString.DONT_BE_FOOLED_DONT_BE_FOOLED_I_M_THE_REAL);
                    }
                } else if (Rnd.get(100) <= 10) {
                    Functions.npcSay(getActor(), NpcString.JUST_ACT_LIKE_THE_REAL_ONE);
                }
            }
        } else if (script_event_arg1 == SCE_REAL_DESPAWN) {
            final int c0 = script_event_arg2;
            for (final NpcInstance npcs : getActor().getReflection().getNpcs()) {
                if (c0 != npcs.getObjectId()) {
                    if (i_ai0 == 1 || i_ai0 == 2 || i_ai0 == 3) {
                        final int i0 = Rnd.get(100);
                        if (i0 < 33) {
                            Functions.npcSay(getActor(), NpcString.I_M_THE_REAL_ONE_PHEW);
                        } else if (i0 < 66) {
                            Functions.npcSay(getActor(), NpcString.CANT_YOU_EVEN_FIND_OUT);
                        } else {
                            Functions.npcSay(getActor(), NpcString.FIND_ME);
                        }
                    }
                }
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    TimerEx(3004);
                }
            }, 3000L);
        } else if (script_event_arg1 == SCE_REAL_HINT_SPAWN) {
            i_ai1++;
            if (i_ai1 >= 3) {
                if (i_ai0 == 1 || i_ai0 == 2 || i_ai0 == 3) {
                    i_ai1 = 0;
                    i_ai6 = 0;
                    i_ai5 = 0;
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            spawnGroup(true);
                        }
                    }, 3000L);
                }
            }
        } else if (script_event_arg1 == SCE_IAM_IMI) {
            i_ai3 = 1;
        }
    }

    private void TimerEx(final int timer_id) {
        if (timer_id == 3001) {
            getActor().deleteMe();
        } else if (timer_id == 3003) {
            BroadcastScriptEvent(SCE_REAL_DESPAWN, getActor().getObjectId());
        } else if (timer_id == 3004) {
            getActor().deleteMe();
        } else if (timer_id == 3005) {
            if (i_ai0 == 1 || i_ai0 == 2 || i_ai0 == 3) {
                final int i0 = Rnd.get(100);
                if (i0 < 20) {
                    Functions.npcSay(getActor(), NpcString.I_M_THE_REAL);
                } else if (i0 < 40) {
                    Functions.npcSay(getActor(), NpcString.PICK_ME);
                } else if (i0 < 60) {
                    Functions.npcSay(getActor(), NpcString.TRUST_ME);
                } else if (i0 < 80) {
                    Functions.npcSay(getActor(), NpcString.NOT_THAT_DUDE_I_M_THE_REAL_ONE);
                } else {
                    Functions.npcSay(getActor(), NpcString.DONT_BE_FOOLED_DONT_BE_FOOLED_I_M_THE_REAL);
                }
            } else if (Rnd.get(100) < 50) {
                final int i0 = Rnd.get(100);
                if (i0 < 20) {
                    Functions.npcSay(getActor(), NpcString.I_M_THE_REAL);
                } else if (i0 < 40) {
                    Functions.npcSay(getActor(), NpcString.PICK_ME);
                } else if (i0 < 60) {
                    Functions.npcSay(getActor(), NpcString.TRUST_ME);
                } else if (i0 < 80) {
                    Functions.npcSay(getActor(), NpcString.NOT_THAT_DUDE_I_M_THE_REAL_ONE);
                } else {
                    Functions.npcSay(getActor(), NpcString.DONT_BE_FOOLED_DONT_BE_FOOLED_I_M_THE_REAL);
                }
            } else if (Rnd.get(100) <= 10) {
                Functions.npcSay(getActor(), NpcString.JUST_ACT_LIKE_THE_REAL_ONE);
            }
        }
    }

    @Override
    protected void onEvtSpawn() {
        BroadcastScriptEvent(SCE_RECIVEAI_INDEX, Rnd.get(3));
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (i_ai4 == 1) {
            i_ai4 = 2;
            if (i_ai2 == 0) {
                if (getActor().getReflection().getDoor(DOOR_6).isOpen()) {
                    getActor().getReflection().getDoor(DOOR_6).closeMe();
                }
                i_ai2 = 1;
            }
            if (i_ai0 == 1 || i_ai0 == 2 || i_ai0 == 3) {
                if (i_ai3 == 0) {
                    final int i0 = Rnd.get(100);
                    if (i0 < 25) {
                        Functions.npcSay(getActor(), NpcString.HUH_HOW_DID_YOU_KNOW_IT_WAS_ME);
                    } else if (i0 < 50) {
                        Functions.npcSay(getActor(), NpcString.EXCELLENT_CHOISE_TEEHEE);
                    } else if (i0 < 75) {
                        Functions.npcSay(getActor(), NpcString.YOU_VE_DONE_WELL);
                    } else {
                        Functions.npcSay(getActor(), NpcString.OH_VERY_SENSIBLE);
                    }
                    i_ai3 = 1;
                    BroadcastScriptEvent(SCE_REAL_HINT_SPAWN, 0);
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            TimerEx(3001);
                            int count = 0;
                            for (final NpcInstance npcs : getActor().getReflection().getNpcs()) {
                                if (ArrayUtils.contains(BS, npcs.getNpcId())) {
                                    count++;
                                }
                            }
                            if (count <= 1) {
                                spawnGroup(false);
                            }
                        }
                    }, 2000L);
                }
            } else {
                if (Rnd.get(100) < 50) {
                    Functions.npcSay(getActor(), NpcString.YOU_VE_BEEN_FOOLED);
                } else {
                    Functions.npcSay(getActor(), NpcString.SORRY_BUT_I_M_THE_FAKE_ONE);
                }
                BroadcastScriptEvent(SCE_IAM_IMI, 0);
                i_ai6++;
                if (i_ai6 >= 2 && i_ai1 <= 2) {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            i_ai1 = 0;
                            i_ai6 = 0;
                            i_ai5 = 0;
                            spawnGroup(false);
                        }
                    }, 3000L);
                } else {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            TimerEx(3003);
                            int count = 0;
                            for (final NpcInstance npcs : getActor().getReflection().getNpcs()) {
                                if (ArrayUtils.contains(BS, npcs.getNpcId())) {
                                    count++;
                                }
                            }
                            if (count <= 1) {
                                spawnGroup(false);
                            }
                        }
                    }, 3000L);
                }
            }
        }
    }

    private void spawnGroup(final boolean goblet) {
        if (goblet) {
            getActor().getReflection().spawnByGroup(beletSampleGobletGroup);
            getActor().getReflection().despawnByGroup(beletSampleGroup);
        } else {
            getActor().getReflection().despawnByGroup(beletSampleGroup);
            getActor().getReflection().spawnByGroup(beletSampleGroup);
        }
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        i_ai5++;
        if (i_ai5 >= 3 && i_ai1 <= 2) {
            i_ai1 = 0;
            i_ai5 = 0;
            i_ai6 = 0;
            spawnGroup(false);
        } else {
            int count = 0;
            for (final NpcInstance npcs : getActor().getReflection().getNpcs()) {
                if (ArrayUtils.contains(BS, npcs.getNpcId())) {
                    count++;
                }
            }
            if (count <= 1) {
                i_ai1 = 0;
                i_ai5 = 0;
                i_ai6 = 0;
                spawnGroup(false);
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}