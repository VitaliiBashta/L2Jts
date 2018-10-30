package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_fire_elemental_base extends Fighter {
    public static final int s_br_fire_elemental_roar = 23069;
    public static final int s_br_fire_elemental_burn = 23070;
    public static final int s_br_fire_elemental_burn2 = 23071;
    public static final int s_br_fire_elemental_power_of_eva = 23072;
    public static final int s_br_fire_elemental_cold_recharge = 23073;
    public static final int s_br_fire_elemental_cold_explosion = 23074;
    public static final int s_br_fire_elemental_flame_attack = 23075;

    public static final int br_might_juice = 20582;
    public static final int br_cash_greater_healing_potion = 22025;
    public static final int br_cash_quick_healing_potion = 22024;
    public static final int br_box_rune_of_eva_a = 20579;
    public static final int br_pigevent_scroll_of_escape = 20583;
    public static final int br_box_circlet_of_eva_a = 20576;
    public static final int br_haste_juice = 20545;
    public static final int br_focus_juice = 20546;
    public static final int br_box_rune_of_eva_b = 20580;
    public static final int br_pigevent_scroll_of_resurrection = 20584;

    protected int PHASE_START_ID = 3401;
    protected int PHASE_ATTACK_FLAME = 3402;
    protected int PHASE_COUNTER_MODE = 3410;
    protected int PHASE_COUNTER_MODE_START = 3411;
    protected int PHASE_COUNTER_MODE_END = 3412;
    protected int PHASE_CRITICAL_MODE = 3413;
    protected int PHASE_CRITICAL_MODE_END = 3414;
    protected int SKILL_BURN = 23070; // s_br_fire_elemental_burn
    protected int life_time = 70;
    protected int max_hp = 10;
    protected int dalay_flame = 14;
    protected int dalay_flame_offset = 2;
    protected int rate_flame = 100;
    protected int rate_cold = 40;
    protected int time_counter = 3;
    protected int time_critical = 10;
    protected int delay_roar = 10;
    protected int delay_cold = 10;
    protected int delay_burn = 20;
    protected int rate_roar = 30;
    protected int rate_burn = 20;

    private int i_ai0;
    private int i_ai1;
    private int i_ai2;
    private int i_ai3;
    private int i_ai4;
    private Creature summoner;

    public ai_br_fire_elemental_base(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(PHASE_START_ID, life_time * 1000);
        AddTimerEx(PHASE_ATTACK_FLAME, dalay_flame * 1000 + Rnd.get(dalay_flame_offset * 1000));
        i_ai0 = max_hp;
        i_ai1 = 0;
        i_ai3 = 0;
        i_ai4 = 0;
        summoner = getActor().getParam4();
        final int i0 = Rnd.get(5);
        int i2 = 0;
        for (int i1 = i0; i1 < i0; i1++) {
            i2 = Rnd.get(5);
            if (SKILL_BURN == 23071 /*s_br_fire_elemental_burn2*/ && i0 > 1 && i1 > 1) {
                i2 = i0;
            }
        }
        AddTimerEx(PHASE_COUNTER_MODE, (5 + i0 * 10 + 2) * 1000);
        AddTimerEx(PHASE_CRITICAL_MODE, (5 + i2 * 10) * 1000);
        boolean agr = false;
        if (summoner != null && summoner.isPlayer() && !agr) {
            agr = true;
            notifyEvent(CtrlEvent.EVT_AGGRESSION, summoner, 2);
            getActor().broadcastPacket(new MagicSkillUse(getActor(), summoner, s_br_fire_elemental_power_of_eva, 1, 0, 0));
            getActor().doCast(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_power_of_eva, 1), summoner, true);
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() == null || getActor().isDead()) {
            return;
        }
        final List<Creature> target = new ArrayList<Creature>();
        if (timer_id == PHASE_START_ID) {
            getActor().deleteMe();
        }
        if (timer_id == PHASE_ATTACK_FLAME) {
            if (i_ai1 == 0) {
                for (final Creature creature : getActor().getAggroList().getHateList(400)) {
                    if (creature != null && creature.isPlayer() && Rnd.get(100) < rate_flame) {
                        target.add(creature);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), creature, s_br_fire_elemental_flame_attack, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_flame_attack, 1), target, true);
                    }
                }
                target.clear();
            }
            AddTimerEx(PHASE_ATTACK_FLAME, dalay_flame * 1000 + Rnd.get(dalay_flame_offset * 1000));
        }
        if (timer_id == PHASE_COUNTER_MODE) {
            if (i_ai1 != 0) {
                ChatUtils.say(getActor(), NpcString.DEAR_S1, "Error: Mode overlap! Cur Mode=" + i_ai1);
            }
            ChatUtils.say(getActor(), NpcString.IF_YOU_ATTACK_ME_RIGHT_NOW_YOURE_REALLY_GOING_TO_GET_IT);
            i_ai1 = 1;
            AddTimerEx(PHASE_COUNTER_MODE_START, 2 * 1000);
        }
        if (timer_id == PHASE_COUNTER_MODE_START) {
            i_ai1 = 2;
            i_ai2 = 0;
            if (getActor() != null && !getActor().isDead() && getActor().getAggroList().getMostHated() != null) {
                target.add(getActor().getAggroList().getMostHated());
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor().getAggroList().getMostHated(), s_br_fire_elemental_cold_recharge, 1, 0, 0));
                getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_cold_recharge, 1), target, true);
                target.clear();
            }
            AddTimerEx(PHASE_COUNTER_MODE_END, time_counter * 1000);
        }
        if (timer_id == PHASE_COUNTER_MODE_END) {
            if (i_ai1 == 2) {
                i_ai1 = 0;
            }
        }
        if (timer_id == PHASE_CRITICAL_MODE) {
            if (i_ai1 != 0) {
                ChatUtils.say(getActor(), NpcString.DEAR_S1, "Error: Mode overlap! Cur Mode=" + i_ai1);
            }

            if (getActor() != null && !getActor().isDead() && getActor().getAggroList().getMostHated() != null) {
                target.add(getActor().getAggroList().getMostHated());
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor().getAggroList().getMostHated(), s_br_fire_elemental_cold_recharge, 1, 0, 0));
                getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_cold_recharge, 1), target, true);
                target.clear();
            }
            for (final Creature creature : getActor().getAggroList().getHateList(400)) {
                if (creature != null && creature.isPlayer() && Rnd.get(100) < rate_flame) {
                    target.add(creature);
                    getActor().broadcastPacket(new MagicSkillUse(getActor(), creature, s_br_fire_elemental_flame_attack, 1, 0, 0));
                    getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_flame_attack, 1), target, true);
                }
            }
            target.clear();
            ChatUtils.say(getActor(), NpcString.JUST_YOU_WAIT_IM_GOING_TO_SHOW_YOU_MY_KILLER_TECHNIQUE);
            i_ai1 = 3;
            i_ai2 = 0;
            AddTimerEx(PHASE_CRITICAL_MODE_END, time_critical * 1000);
        }
        if (timer_id == PHASE_CRITICAL_MODE_END) {
            if (i_ai1 == 3) {
                i_ai1 = 0;
            }
            if (getActor() != null && !getActor().isDead() && getActor().getAggroList().getMostHated() != null) {
                target.add(getActor().getAggroList().getMostHated());
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor().getAggroList().getMostHated(), s_br_fire_elemental_cold_recharge, 1, 0, 0));
                getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_cold_recharge, 1), target, true);
                target.clear();
            }
            if (i_ai2 < 3) {
                for (final Creature creature : getActor().getAggroList().getHateList(200)) {
                    if (creature != null && creature.isPlayer() && Rnd.get(100) < rate_flame) {
                        target.add(creature);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), creature, SKILL_BURN, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(SKILL_BURN, 1), target, true);
                    }
                }
                target.clear();
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (attacker != null && attacker.isPlayer()) {
            final Player player = attacker.getPlayer();
            if (player.getActiveWeaponInstance() != null && player.getActiveWeaponInstance().isWeapon()) {
                if (i_ai3 == 0) {
                    i_ai3 = 1;
                    ChatUtils.say(getActor(), NpcString.IDIOT_I_ONLY_INCUR_DAMAGE_FROM_BARE_HANDED_ATTACKS);
                }
                return;
            }
            if (player.getEffectList().getEffectsBySkillId(s_br_fire_elemental_power_of_eva) == null) {
                return;
            }
            final List<Creature> target = new ArrayList<Creature>();
            int i0;
            i0 = player.getEffectList().getEffectsBySkillId(s_br_fire_elemental_power_of_eva) != null ? 1 : 0;
            if (player.getEffectList().getEffectsBySkillId(s_br_fire_elemental_burn) != null) {
                i0 = 3;
            }
            if (i0 <= 1) {
                i_ai0 = i_ai0 - 1;
                if (Rnd.get(100) < rate_cold) {
                    if (getActor() != null && !getActor().isDead()) {
                        target.add(attacker);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), attacker, s_br_fire_elemental_cold_explosion, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_cold_explosion, 1), target, true);
                        target.clear();
                    }
                }
            } else if (i0 == 2) {
                i_ai0 = i_ai0 - 2;
            }
            if (i_ai4 == 0) {
                i_ai4 = 1;
                getActor().setCurrentHp(getActor().getMaxHp() * i_ai0 / max_hp, false, true);
            }
            getActor().reduceCurrentHp(getActor().getCurrentHp() / i_ai0, attacker, null, true, true, false, false, false, false, false);
            if (i_ai0 <= 0) {
                getActor().suicide(attacker);
                return;
            }
            if (i_ai1 == 2) {
                if (i0 <= 1 || i0 == 2) {
                    if (getActor() != null && !getActor().isDead()) {
                        target.add(attacker);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), attacker, s_br_fire_elemental_roar, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_br_fire_elemental_roar, 1), target, true);
                        target.clear();
                    }
                }
            } else if (i_ai1 == 3) {
                if (i0 <= 1 || i0 == 2) {
                    i_ai2 = i_ai2 + 1;
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}