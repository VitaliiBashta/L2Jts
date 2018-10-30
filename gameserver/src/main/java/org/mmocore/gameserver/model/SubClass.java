package org.mmocore.gameserver.model;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.DeathPenalty;
import org.mmocore.gameserver.utils.PlayerUtils;

/**
 * Character Sub-Class<?> Definition
 * <BR>
 * Used to store key information about a character's sub-class.
 *
 * @author Tempy
 */
public class SubClass {
    public static final int CERTIFICATION_65 = 1 << 0;
    public static final int CERTIFICATION_70 = 1 << 1;
    public static final int CERTIFICATION_75 = 1 << 2;
    public static final int CERTIFICATION_80 = 1 << 3;

    private int _class = 0;
    private long _exp = ExpDataHolder.getInstance().getExpForLevel(40), minExp = ExpDataHolder.getInstance().getExpForLevel(40), maxExp = ExpDataHolder.getInstance().getExpForLevel(ExpDataHolder.getInstance().getExpTableData().length - 1);
    private int _sp = 0;
    private int _level = 40, _certification;
    private double _Hp = 1, _Mp = 1, _Cp = 1;
    private boolean _active = false, _isBase = false;
    private DeathPenalty _dp;

    public SubClass() {
    }

    public int getClassId() {
        return _class;
    }

    public void setClassId(final int classId) {
        _class = classId;
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(long val) {
        val = Math.max(val, minExp);
        val = Math.min(val, maxExp);

        _exp = val;
        _level = ExpDataHolder.getInstance().getLevelForExp(_exp);
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void addExp(final long val) {
        setExp(_exp + val);
    }

    public long getSp() {
        return Math.min(_sp, Integer.MAX_VALUE);
    }

    public void setSp(long spValue) {
        spValue = Math.max(spValue, 0);
        spValue = Math.min(spValue, Integer.MAX_VALUE);

        _sp = (int) spValue;
    }

    public void addSp(final long val) {
        setSp(_sp + val);
    }

    public int getLevel() {
        return _level;
    }

    public double getHp() {
        return _Hp;
    }

    public void setHp(final double hpValue) {
        _Hp = hpValue;
    }

    public double getMp() {
        return _Mp;
    }

    public void setMp(final double mpValue) {
        _Mp = mpValue;
    }

    public double getCp() {
        return _Cp;
    }

    public void setCp(final double cpValue) {
        _Cp = cpValue;
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(final boolean active) {
        _active = active;
    }

    public boolean isBase() {
        return _isBase;
    }

    public void setBase(final boolean base) {
        _isBase = base;
        minExp = ExpDataHolder.getInstance().getExpForLevel(_isBase ? 1 : 40);
        maxExp = ExpDataHolder.getInstance().getExpForLevel((_isBase ? PlayerUtils.getMaxLevel() : PlayerUtils.getMaxSubLevel()) + 1) - 1;
    }

    public DeathPenalty getDeathPenalty(final Player player) {
        if (_dp == null) {
            _dp = new DeathPenalty(player, 0);
        }
        return _dp;
    }

    public void setDeathPenalty(final DeathPenalty dp) {
        _dp = dp;
    }

    public int getCertification() {
        return _certification;
    }

    public void setCertification(final int certification) {
        _certification = certification;
    }

    public void addCertification(final int c) {
        _certification |= c;
    }

    public boolean isCertificationGet(final int v) {
        return (_certification & v) == v;
    }

    @Override
    public String toString() {
        return ClassId.VALUES[_class].toString() + ' ' + _level;
    }
}