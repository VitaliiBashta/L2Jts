package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("formulas.json")
public class FormulasConfig {
    @Setting(name = "SkillsChanceMod")
    public static double SKILLS_CHANCE_MOD;

    @Setting(name = "SkillsChancePow")
    public static double SKILLS_CHANCE_POW;

    @Setting(name = "SkillsChanceMin")
    public static double SKILLS_CHANCE_MIN;

    @Setting(name = "SkillsChanceCap")
    public static double SKILLS_CHANCE_CAP;

    @Setting(name = "SkillsCastTimeMin")
    public static int SKILLS_CAST_TIME_MIN;

    @Setting(name = "AbsorbDamageModifier")
    public static double ALT_ABSORB_DAMAGE_MODIFIER;

    @Setting(name = "LimitHP")
    public static int LIM_HP;

    @Setting(name = "LimitMP")
    public static int LIM_MP;

    @Setting(name = "LimitCP")
    public static int LIM_CP;

    @Setting(name = "LimitPatk")
    public static int LIM_PATK;

    @Setting(name = "LimitMAtk")
    public static int LIM_MATK;

    @Setting(name = "LimitPDef")
    public static int LIM_PDEF;

    @Setting(name = "LimitMDef")
    public static int LIM_MDEF;

    @Setting(name = "LimitPatkSpd")
    public static int LIM_PATK_SPD;

    @Setting(name = "LimitMatkSpd")
    public static int LIM_MATK_SPD;

    @Setting(name = "LimitCriticalDamage")
    public static int LIM_CRIT_DAM;

    @Setting(name = "LimitCritical")
    public static int LIM_CRIT;

    @Setting(name = "LimitMCritical")
    public static int LIM_MCRIT;

    @Setting(name = "LimitAccuracy")
    public static int LIM_ACCURACY;

    @Setting(name = "LimitEvasion")
    public static int LIM_EVASION;

    @Setting(name = "LimitMove")
    public static int LIM_MOVE;

    @Setting(name = "LimitFame")
    public static int LIM_FAME;

    @Setting(name = "NpcPAtkModifier")
    public static double ALT_NPC_PATK_MODIFIER;

    @Setting(name = "NpcMAtkModifier")
    public static double ALT_NPC_MATK_MODIFIER;

    @Setting(name = "NpcMaxHpModifier")
    public static double ALT_NPC_MAXHP_MODIFIER;

    @Setting(name = "NpcMaxMpModifier")
    public static double ALT_NPC_MAXMP_MODIFIER;

    @Setting(name = "PoleDamageModifier")
    public static double ALT_POLE_DAMAGE_MODIFIER;

    @Setting(name = "LimitFame")
    public static int ALT_LETHAL_DIFF_LEVEL;

    @Setting(name = "SkillLethalPenalty")
    public static boolean ALT_LETHAL_PENALTY;

    @Setting(name = "AlternateDebuffFormula")
    public static boolean ALT_DEBUFF_CALCULATE;
}
