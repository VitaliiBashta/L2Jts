package org.mmocore.gameserver.skills;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.effects.*;
import org.mmocore.gameserver.stats.Stats;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EffectType {
    c_agathion_energy(Effect_c_agathion_energy.class, null, true),
    // Основные эффекты
    AddSkills(EffectAddSkills.class, null, false),
    AgathionResurrect(EffectAgathionRes.class, null, true),
    i_agathion_energy(Effect_i_agathion_energy.class, null, true),
    Aggression(EffectAggression.class, null, true),
    i_align_direction(i_align_direction.class, null, true),
    i_bonuscount_up(Effect_i_bonuscount_up.class, null, true),
    Betray(EffectBetray.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    BlessNoblesse(EffectBlessNoblesse.class, null, true),
    BlockStat(EffectBlockStat.class, null, true),
    Buff(EffectBuff.class, null, false),
    Bluff(EffectBluff.class, AbnormalEffect.NULL, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    BuffImmunity(EffectBuffImmunity.class, null, false),
    DebuffImmunity(EffectDebuffImmunity.class, null, true),
    DispelEffects(EffectDispelEffects.class, null, true),
    CallSkills(EffectCallSkills.class, null, false),
    i_consume_body(Effect_i_consume_body.class, null, true),
    i_change_face(Effect_i_change_face.class, null, true),
    i_change_hair_color(Effect_i_change_hair_color.class, null, true),
    i_change_hair_style(Effect_i_change_hair_style.class, null, true),
    i_give_contribution(Effect_i_give_contribution.class, null, true),
    Cancel(EffectCancel.class, null, Stats.CANCEL_RESIST, Stats.CANCEL_POWER, true),
    CombatPointHealOverTime(EffectCombatPointHealOverTime.class, null, true),
    ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, null, true),
    Charge(EffectCharge.class, null, false),
    CharmOfCourage(EffectCharmOfCourage.class, null, true),
    CPDamPercent(EffectCPDamPercent.class, null, true),
    DamOverTime(EffectDamOverTime.class, null, false),
    DamOverTimeHP(EffectDamOverTimeHP.class, null, false),
    DamOverTimeLethal(EffectDamOverTimeLethal.class, null, false),
    DestroySummon(EffectDestroySummon.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Disarm(EffectDisarm.class, null, true),
    Discord(EffectDiscord.class, AbnormalEffect.CONFUSED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Enervation(EffectEnervation.class, null, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, false),
    FakeDeath(EffectFakeDeath.class, null, true),
    Fear(EffectFear.class, AbnormalEffect.SOUL_SHOCK, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    Grow(EffectGrow.class, AbnormalEffect.GROW, false),
    Hate(EffectHate.class, null, false),
    Heal(EffectHeal.class, null, false),
    HealBlock(EffectHealBlock.class, null, true),
    HealCPPercent(EffectHealCPPercent.class, null, true),
    HealOverTime(EffectHealOverTime.class, null, false),
    HealPercent(EffectHealPercent.class, null, false),
    HPDamPercent(EffectHPDamPercent.class, null, true),
    IgnoreSkill(EffectBuff.class, null, false),
    Immobilize(EffectImmobilize.class, null, true),
    Interrupt(EffectInterrupt.class, null, true),
    Invulnerable(EffectInvulnerable.class, null, false),
    Invisible(EffectInvisible.class, null, false),
    LockInventory(EffectLockInventory.class, null, false),
    CurseOfLifeFlow(EffectCurseOfLifeFlow.class, null, true),
    LDManaDamOverTime(EffectLDManaDamOverTime.class, null, true),
    ManaDamOverTime(EffectManaDamOverTime.class, null, true),
    ManaHeal(EffectManaHeal.class, null, false),
    ManaHealOverTime(EffectManaHealOverTime.class, null, false),
    ManaHealPercent(EffectManaHealPercent.class, null, false),
    Meditation(EffectMeditation.class, null, false),
    MPDamPercent(EffectMPDamPercent.class, null, true),
    Mute(EffectMute.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MuteAll(EffectMuteAll.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MuteAttack(EffectMuteAttack.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    MutePhisycal(EffectMutePhisycal.class, AbnormalEffect.MUTED, Stats.MENTAL_RESIST, Stats.MENTAL_POWER, true),
    NegateEffects(EffectNegateEffects.class, null, false),
    NegateMusic(EffectNegateMusic.class, null, false),
    Paralyze(EffectParalyze.class, AbnormalEffect.HOLD_1, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
    Petrification(EffectPetrification.class, AbnormalEffect.HOLD_2, Stats.PARALYZE_RESIST, Stats.PARALYZE_POWER, true),
    RandomHate(EffectRandomHate.class, null, true),
    Relax(EffectRelax.class, null, true),
    RemoveTarget(EffectRemoveTarget.class, null, true),
    i_restoration_random(Effect_i_restoration_random.class, null, true),
    i_restoration(Effect_i_restoration.class, null, true),
    i_remove_soul(Effect_i_remove_soul.class, null, true),
    i_summon_npc(Effect_i_summon_npc.class, null, true),
    i_set_skill(Effect_i_set_skill.class, null, true),
    i_summon_agathion(Effect_i_summon_agathion.class, null, true),
    i_summon_cubic(Effect_i_summon_cubic.class, null, true),
    Root(EffectRoot.class, AbnormalEffect.ROOT, Stats.ROOT_RESIST, Stats.ROOT_POWER, true),
    Hourglass(EffectHourglass.class, null, true),
    i_sp(Effect_i_sp.class, null, true),
    Salvation(EffectSalvation.class, null, true),
    ServitorShare(EffectServitorShare.class, null, true),
    SilentMove(EffectSilentMove.class, AbnormalEffect.STEALTH, true),
    Sleep(EffectSleep.class, AbnormalEffect.SLEEP, Stats.SLEEP_RESIST, Stats.SLEEP_POWER, true),
    Stun(EffectStun.class, AbnormalEffect.STUN, Stats.STUN_RESIST, Stats.STUN_POWER, true),
    Symbol(EffectSymbol.class, null, false),
    UnAggro(EffectUnAggro.class, null, true),
    i_vp_up(Effect_i_vp_up.class, null, true),
    p_recovery_vp(Effect_p_recovery_vp.class, AbnormalEffect.VITALITY, true),
    p_recharge_vital_point_noncount(Effect_p_recharge_vital_point_noncount.class, AbnormalEffect.VITALITY, true),
    p_transform(Effect_p_transform.class, null, true),
    p_transform_hangover(Effect_p_transform_hangover.class, null, true),
    p_dominion_transform(Effect_p_dominion_transform.class, null, true),
    CancelTransform(EffectCancelTransform.class, null, true),
    // Производные от основных эффектов
    Poison(EffectDamOverTime.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
    PoisonLethal(EffectDamOverTimeLethal.class, null, Stats.POISON_RESIST, Stats.POISON_POWER, false),
    Bleed(EffectDamOverTime.class, null, Stats.BLEED_RESIST, Stats.BLEED_POWER, false),
    Debuff(EffectBuff.class, null, false),
    WatcherGaze(EffectBuff.class, null, false),
    i_unsummon_agathion(Effect_i_unsummon_agathion.class, null, true),
    i_show_reuse_delay(Effect_i_show_reuse_delay.class, null, true),
    i_event_agathion_reuse_delay(Effect_i_event_agathion_reuse_delay.class, null, true),
    BlockHPMP(EffectBlockHPMP.class, null, false),
    AbsorbDamageToEffector(EffectBuff.class, null, false),
    // абсорбирует часть дамага к еффектора еффекта
    AbsorbDamageToMp(EffectBuff.class, null, false),
    // абсорбирует часть дамага в мп
    AbsorbDamageToSummon(EffectLDManaDamOverTime.class, null, true),
    // абсорбирует часть дамага к сумону
    EventEvaswrath(EffectBuff.class, AbnormalEffect.E_EVASWRATH, true),
    ReportBlock(EffectBuff.class, null, false);
    private final Constructor<? extends Effect> _constructor;
    private final AbnormalEffect _abnormal;
    private final Stats _resistType;
    private final Stats _attributeType;
    private final boolean _isRaidImmune;

    EffectType(final Class<? extends Effect> clazz, final AbnormalEffect abnormal, final boolean isRaidImmune) {
        this(clazz, abnormal, null, null, isRaidImmune);
    }

    EffectType(final Class<? extends Effect> clazz, final AbnormalEffect abnormal, final Stats resistType, final Stats attributeType, final boolean isRaidImmune) {
        try {
            _constructor = clazz.getConstructor(Creature.class, Creature.class, SkillEntry.class, EffectTemplate.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        _abnormal = abnormal;
        _resistType = resistType;
        _attributeType = attributeType;
        _isRaidImmune = isRaidImmune;
    }

    public AbnormalEffect getAbnormal() {
        return _abnormal;
    }

    public Stats getResistType() {
        return _resistType;
    }

    public Stats getAttributeType() {
        return _attributeType;
    }

    public boolean isRaidImmune() {
        return _isRaidImmune;
    }

    public Effect makeEffect(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return _constructor.newInstance(creature, target, skill, template);
    }
}