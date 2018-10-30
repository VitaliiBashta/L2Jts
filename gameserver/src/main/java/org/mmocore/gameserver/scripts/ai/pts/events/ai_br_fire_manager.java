package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.events.NcSoft.EvasInferno.EvasInferno;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_fire_manager extends DefaultAI {
    private final int event_end = 0;
    private final int event_doing = 1;
    private int i_ai0;
    private int i_ai1;
    private int i_ai2;
    private int i_ai3;
    private int i_ai4;

    public ai_br_fire_manager(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        i_ai0 = event_end;
        final int i0 = GameObjectsStorage.getAllPlayersSize();
        if (i0 >= 4000) {
            i_ai1 = 4000;
        } else if (i0 >= 2501) {
            i_ai1 = 3000;
        } else if (i0 >= 1501) {
            i_ai1 = 2000;
        } else {
            i_ai1 = 1500;
        }
        i_ai2 = -1;
        i_ai3 = 0;
        i_ai4 = 2000000000;
        final long i1 = EvasInferno.getDBValue();
        final long i2 = 1 + EvasInferno.getEventElapsedTime() / 3600 / 24;
        if (i1 / 10000000 == i2) {
            EvasInferno.setEventValue(i1 % 10000000);
        } else {
            EvasInferno.setEventValue(0);
        }
        AddTimerEx(7777, 1000 * 10);
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2) {
        if (event == ScriptEvent.SEND_DIE_FROM_FE) {
            if (i_ai0 == event_doing) {
                EvasInferno.setEventValue(EvasInferno.getEventValue() + (int) arg1);
            }
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 7777) {
            EvasInferno.setDBValue(EvasInferno.getEventValue() + 10000000 * i_ai2);
            int i0 = 0;
            final long i2 = i_ai2;
            i_ai2 = (int) (1 + EvasInferno.getEventElapsedTime() / 3600 / 24);
            if (i_ai2 < 1) {
                AddTimerEx(7777, 1000 * 10);
                return;
            }
            if (i2 != i_ai2) {
                i0 = 1;
                i_ai3 = 0;
                if (i2 > 0) {
                    EvasInferno.setEventValue(0);
                }
                if (i_ai2 >= 1 && i_ai2 <= 14) {
                    i_ai0 = event_doing;
                    switch (i_ai2) {
                        case 1: {
                            int kill_count_1 = 207360;
                            i_ai4 = kill_count_1;
                            break;
                        }
                        case 2: {
                            int kill_count_2 = 241920;
                            i_ai4 = kill_count_2;
                            break;
                        }
                        case 3: {
                            int kill_count_3 = 276480;
                            i_ai4 = kill_count_3;
                            break;
                        }
                        case 4: {
                            int kill_count_4 = 321408;
                            i_ai4 = kill_count_4;
                            break;
                        }
                        case 5: {
                            int kill_count_5 = 328320;
                            i_ai4 = kill_count_5;
                            break;
                        }
                        case 6: {
                            int kill_count_6 = 328320;
                            i_ai4 = kill_count_6;
                            break;
                        }
                        case 7: {
                            int kill_count_7 = 328320;
                            i_ai4 = kill_count_7;
                            break;
                        }
                        case 8: {
                            int kill_count_8 = 328320;
                            i_ai4 = kill_count_8;
                            break;
                        }
                        case 9: {
                            int kill_count_9 = 328320;
                            i_ai4 = kill_count_9;
                            break;
                        }
                        case 10: {
                            int kill_count_10 = 335232;
                            i_ai4 = kill_count_10;
                            break;
                        }
                        case 11: {
                            int kill_count_11 = 335232;
                            i_ai4 = kill_count_11;
                            break;
                        }
                        case 12: {
                            int kill_count_12 = 338688;
                            i_ai4 = kill_count_12;
                            break;
                        }
                        case 13: {
                            int kill_count_13 = 342144;
                            i_ai4 = kill_count_13;
                            break;
                        }
                        case 14: {
                            int kill_count_14 = 352512;
                            i_ai4 = kill_count_14;
                            break;
                        }
                    }
                    i_ai4 = i_ai4 * i_ai1 / 4000;
                } else if (i_ai2 > 14) {
                    i_ai0 = event_end;
                }
            }
            if (i_ai0 == event_doing && i_ai4 > 0) {
                final int i3 = i_ai3;
                i_ai3 = (int) (EvasInferno.getEventValue() * 100 / i_ai4);
                if (i_ai3 > 100) {
                    i_ai3 = 100;
                }
                if (i_ai3 >= 100) {
                    i0 = 1;
                    int event_reward = 2;
                    i_ai0 = event_reward;
                    final int i5 = 3600;
                    switch (i_ai2) {
                        case 1: {
                            EvasInferno.startBuffEvent(1, 10, i5 * 1);
                            break;
                        }
                        case 2: {
                            EvasInferno.startBuffEvent(1, 10, i5 * 2);
                            break;
                        }
                        case 3: {
                            EvasInferno.startBuffEvent(1, 10, i5 * 2);
                            break;
                        }
                        case 4: {
                            EvasInferno.startBuffEvent(1, 15, i5 * 2);
                            break;
                        }
                        case 5: {
                            EvasInferno.startBuffEvent(1, 20, i5 * 2);
                            break;
                        }
                        case 6: {
                            //AddTimerEx(1000, i5 * 5 * 1000);
                            EvasInferno.startBuffEvent(2, 20573, i5 * 5);
                            break;
                        }
                        case 7: {
                            EvasInferno.startBuffEvent(1, 20, i5 * 2);
                            break;
                        }
                        case 8: {
                            EvasInferno.startBuffEvent(1, 25, i5 * 2);
                            break;
                        }
                        case 9: {
                            EvasInferno.startBuffEvent(1, 25, i5 * 3);
                            break;
                        }
                        case 10: {
                            //AddTimerEx(1000, i5 * 5 * 1000);
                            EvasInferno.startBuffEvent(2, 20574, i5 * 5);
                            break;
                        }
                        case 11: {
                            EvasInferno.startBuffEvent(1, 30, i5 * 3);
                            break;
                        }
                        case 12: {
                            EvasInferno.startBuffEvent(1, 30, i5 * 3);
                            break;
                        }
                        case 13: {
                            EvasInferno.startBuffEvent(1, 40, i5 * 3);
                            break;
                        }
                        case 14: {
                            //AddTimerEx( 1000, i5 * 8 * 1000 );
                            EvasInferno.startBuffEvent(2, 20575, i5 * 8);
                            break;
                        }
                    }
                } else if (i3 / 10 != i_ai3 / 10) {
                    i0 = 1;
                }
            }
            if (i0 > 0) {
                EvasInferno.sendEventState(20090801, i_ai0, i_ai2, i_ai3);
            }
            AddTimerEx(7777, 1000 * 10);
        } else if (timer_id == 1000) {
            //RESTART_EVENT TODO
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}