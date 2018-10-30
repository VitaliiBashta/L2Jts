package org.mmocore.gameserver.templates;

import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.player.StatAttributes;

public class CharTemplate {
    private static final int[] EMPTY_ATTRIBUTES = new int[6];
    private final StatAttributes baseAttr;
    private final double baseHpRate;
    private final int[] baseAttributeAttack;
    private final int[] baseAttributeDefence;
    private final WeaponTemplate.WeaponType baseAttackType;
    private final int baseAtkRange;
    private double baseHpMax;
    private final double baseCpMax;
    private double baseMpMax;
    private final double baseHpReg;
    private final double baseMpReg;
    private final double baseCpReg;
    private double basePAtk;
    private double baseMAtk;
    private double basePDef;
    private double baseMDef;
    private final double basePAtkSpd;
    private final double baseMAtkSpd;
    private final double baseShldDef;
    private final double baseShldRate;
    private final double baseCritRate;
    private final double baseMCritRate;
    private final int baseRunSpd;
    private final double baseWalkSpd;
    private final double baseWaterRunSpd;
    private final double baseWaterWalkSpd;
    private final double baseHitModify;
    private final double baseAvoidModify;
    private final double collisionRadius;
    private final double collisionHeight;

    public CharTemplate(StatsSet set) {
        baseAttr = new StatAttributes(set.getInteger("baseINT", 0), set.getInteger("baseSTR", 0), set.getInteger("baseCON", 0), set.getInteger("baseMEN", 0), set.getInteger("baseDEX", 0), set.getInteger("baseWIT", 0));
        baseHpMax = set.getDouble("baseHpMax", 0);
        baseCpMax = set.getDouble("baseCpMax", 0);
        baseMpMax = set.getDouble("baseMpMax", 0);
        baseHpReg = set.getDouble("baseHpReg", 3.e-3f);
        baseCpReg = set.getDouble("baseCpReg", 3.e-3f);
        baseMpReg = set.getDouble("baseMpReg", 3.e-3f);
        basePAtk = set.getDouble("basePAtk", 0);
        baseMAtk = set.getDouble("baseMAtk", 0);
        basePDef = set.getDouble("basePDef", 100);
        baseMDef = set.getDouble("baseMDef", 100);
        basePAtkSpd = set.getDouble("basePAtkSpd", 300);
        baseMAtkSpd = set.getDouble("baseMAtkSpd", 333);
        baseShldDef = set.getDouble("baseShldDef", 0);
        baseAtkRange = set.getInteger("baseAtkRange", 0);
        baseShldRate = set.getDouble("baseShldRate", 0);
        baseCritRate = set.getDouble("baseCritRate", 0);
        baseMCritRate = set.getDouble("baseMCritRate", 0);
        baseRunSpd = set.getInteger("baseRunSpd", 0);
        baseWalkSpd = set.getDouble("baseWalkSpd", 0);
        baseWaterRunSpd = set.getDouble("baseWaterRunSpd", 50);
        baseWaterWalkSpd = set.getDouble("baseWaterWalkSpd", 50);
        baseAttributeAttack = set.getIntegerArray("baseAttributeAttack", EMPTY_ATTRIBUTES);
        baseAttributeDefence = set.getIntegerArray("baseAttributeDefence", EMPTY_ATTRIBUTES);
        baseHitModify = set.getDouble("baseHitModify", 6);
        baseAvoidModify = set.getDouble("baseAvoidModify", 6);
        // Geometry
        collisionRadius = set.getDouble("collision_radius", 5);
        collisionHeight = set.getDouble("collision_height", 5);
        baseAttackType = WeaponTemplate.WeaponType.valueOf(set.getString("baseAttackType", "FIST").toUpperCase());
        baseHpRate = set.getDouble("baseHpRate", 1);
    }

    public static StatsSet getEmptyStatsSet() {
        StatsSet npcDat = new StatsSet();
        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);
        npcDat.set("baseHpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseHpReg", 3.e-3f);
        npcDat.set("baseCpReg", 0);
        npcDat.set("baseMpReg", 3.e-3f);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePDef", 100);
        npcDat.set("baseMDef", 100);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("baseShldDef", 0);
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseCritRate", 0);
        npcDat.set("baseMCritRate", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("baseWalkSpd", 0);
        npcDat.set("baseHitModify", 6);
        npcDat.set("baseAvoidModify", 6);
        npcDat.set("baseAtkType", WeaponTemplate.WeaponType.SWORD);
        return npcDat;
    }

    public StatAttributes getBaseAttr() {
        return baseAttr;
    }

    public double getBaseHpMax() {
        return baseHpMax;
    }

    public void setBaseHpMax(double baseHpMax) {
        this.baseHpMax = baseHpMax;
    }

    public double getBaseCpMax() {
        return baseCpMax;
    }

    public double getBaseMpMax() {
        return baseMpMax;
    }

    public void setBaseMpMax(double baseMpMax) {
        this.baseMpMax = baseMpMax;
    }

    public double getBaseHpReg() {
        return baseHpReg;
    }

    public double getBaseMpReg() {
        return baseMpReg;
    }

    public double getBaseCpReg() {
        return baseCpReg;
    }

    public double getBasePAtk() {
        return basePAtk;
    }

    public void setBasePAtk(double basePAtk) {
        this.basePAtk = basePAtk;
    }

    public double getBaseMAtk() {
        return baseMAtk;
    }

    public void setBaseMAtk(double baseMAtk) {
        this.baseMAtk = baseMAtk;
    }

    public double getBasePDef() {
        return basePDef;
    }

    public void setBasePDef(double basePDef) {
        this.basePDef = basePDef;
    }

    public double getBaseMDef() {
        return baseMDef;
    }

    public void setBaseMDef(double baseMDef) {
        this.baseMDef = baseMDef;
    }

    public double getBasePAtkSpd() {
        return basePAtkSpd;
    }

    public double getBaseMAtkSpd() {
        return baseMAtkSpd;
    }

    public double getBaseShldDef() {
        return baseShldDef;
    }

    public int getBaseAtkRange() {
        return baseAtkRange;
    }

    public double getBaseShldRate() {
        return baseShldRate;
    }

    public double getBaseCritRate() {
        return baseCritRate;
    }

    public double getBaseMCritRate() {
        return baseMCritRate;
    }

    public int getBaseRunSpd() {
        return baseRunSpd;
    }

    public int getBaseWalkSpd() {
        return (int) baseWalkSpd;
    }

    public int getBaseWaterRunSpd() {
        return (int) baseWaterRunSpd;
    }

    public int getBaseWaterWalkSpd() {
        return (int) baseWaterWalkSpd;
    }

    public int[] getBaseAttributeAttack() {
        return baseAttributeAttack;
    }

    public int[] getBaseAttributeDefence() {
        return baseAttributeDefence;
    }

    public double getBaseHitModify() {
        return baseHitModify;
    }

    public double getBaseAvoidModify() {
        return baseAvoidModify;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public double getCollisionHeight() {
        return collisionHeight;
    }

    public WeaponTemplate.WeaponType getBaseAttackType() {
        return baseAttackType;
    }

    public int getNpcId() {
        return 0;
    }

    public double getBaseHpRate() {
        return baseHpRate;
    }
}
