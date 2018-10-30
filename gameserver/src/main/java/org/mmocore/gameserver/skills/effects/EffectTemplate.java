package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.EffectList;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.StatTemplate;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class EffectTemplate extends StatTemplate {
    public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];
    public static final String NO_STACK = "none";
    public static final String HP_RECOVER_CAST = "HpRecoverCast";
    private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);
    public final double _value;
    public final int _count;
    public final long _period; // in milliseconds
    public final EffectType _effectType;
    public final String _stackType;
    public final String _stackType2;
    public final int _stackOrder;
    public final int _displayId;
    public final int _displayLevel;
    public final boolean _applyOnCaster;
    public final boolean _applyOnSummon;
    public final boolean _cancelOnAction;
    public final boolean _isReflectable;
    public final boolean _ignoreInvul;
    private final Boolean _isSaveable;
    private final Boolean _isCancelable;
    private final Boolean _isOffensive;
    private final StatsSet _paramSet;
    private final int _chance;
    public Condition _attachCond;
    public AbnormalEffect[] _abnormalEffects;
    private boolean _refreshHpOnAdd = false;
    private boolean _refreshMpOnAdd = false;
    private boolean _refreshCpOnAdd = false;
    private boolean removeOnCritical;
    private int removeOnCriticalChance;

    public EffectTemplate(final StatsSet set) {
        _effectType = set.getEnum("name", EffectType.class);
        _value = set.getDouble("value");
        _count = set.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("count", 1);
        _period = Math.min(Integer.MAX_VALUE, 1000 * (set.getInteger("time", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("time", 1)));
        final String abnormalEffect = set.getString("abnormal", null);
        if (abnormalEffect != null) {
            final String[] sp = abnormalEffect.split(";");
            _abnormalEffects = new AbnormalEffect[sp.length];
            for (int i = 0; i < sp.length; i++) {
                _abnormalEffects[i] = AbnormalEffect.getByName(sp[i]);
            }
        }

        if (_effectType.getAbnormal() != null) {
            if (_abnormalEffects != null) {
                final AbnormalEffect[] ef = new AbnormalEffect[_abnormalEffects.length + 1];
                System.arraycopy(_abnormalEffects, 0, ef, 0, _abnormalEffects.length);
                ef[_abnormalEffects.length] = _effectType.getAbnormal();
                _abnormalEffects = ef;
            } else {
                _abnormalEffects = new AbnormalEffect[]{_effectType.getAbnormal()};
            }
        }
        _stackType = set.getString("stackType", NO_STACK);
        _stackType2 = set.getString("stackType2", NO_STACK);
        _stackOrder = set.getInteger("stackOrder", _stackType.equals(NO_STACK) && _stackType2.equals(NO_STACK) ? 1 : 0);
        _applyOnCaster = set.getBool("applyOnCaster", false);
        _applyOnSummon = set.getBool("applyOnSummon", true);
        _cancelOnAction = set.getBool("cancelOnAction", false);
        _isReflectable = set.getBool("isReflectable", true);
        _isSaveable = set.isSet("isSaveable") ? set.getBool("isSaveable") : null;
        _isCancelable = set.isSet("isCancelable") ? set.getBool("isCancelable") : null;
        _isOffensive = set.isSet("isOffensive") ? set.getBool("isOffensive") : null;
        _displayId = set.getInteger("displayId", 0);
        _displayLevel = set.getInteger("displayLevel", 0);
        _ignoreInvul = set.getBool("ignoreInvul", false);
        removeOnCritical = set.getBool("removeOnCritical", false);
        removeOnCriticalChance = set.getInteger("removeOnCriticalChance", 100);

        _chance = set.getInteger("chance", Integer.MAX_VALUE);
        _paramSet = set;
    }

    public Effect getEffect(final Creature creature, final Creature target, final SkillEntry skill) {
        if (_attachCond != null && !_attachCond.test(creature, target, skill)) {
            return null;
        }
        try {
            return _effectType.makeEffect(creature, target, skill, this);
        } catch (Exception e) {
            _log.error("", e);
        }

        return null;
    }

    public Effect getEffect(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
        if (_attachCond != null && !_attachCond.test(creature, target, skill, initialValue)) {
            return null;
        }
        try {
            return _effectType.makeEffect(creature, target, skill, this);
        } catch (Exception e) {
            _log.error("", e);
        }

        return null;
    }

    @Override
    public void attachFunc(final FuncTemplate f) {
        super.attachFunc(f);
        if (f._stat == Stats.MAX_HP) {
            _refreshHpOnAdd = true;
        } else if (f._stat == Stats.MAX_MP) {
            _refreshMpOnAdd = true;
        } else if (f._stat == Stats.MAX_CP) {
            _refreshCpOnAdd = true;
        }
    }

    public void attachCond(final Condition c) {
        _attachCond = c;
    }

    public int getCount() {
        return _count;
    }

    public long getPeriod() {
        return _period;
    }

    public EffectType getEffectType() {
        return _effectType;
    }

    public Effect getSameByStackType(final List<Effect> list) {
        for (final Effect ef : list) {
            if (ef != null && EffectList.checkStackType(ef.getTemplate(), this)) {
                return ef;
            }
        }
        return null;
    }

    public Effect getSameByStackType(final EffectList list) {
        return getSameByStackType(list.getAllEffects());
    }

    public Effect getSameByStackType(final Creature actor) {
        return getSameByStackType(actor.getEffectList().getAllEffects());
    }

    public StatsSet getParam() {
        return _paramSet;
    }

    public int chance(final int val) {
        return _chance == Integer.MAX_VALUE ? val : _chance;
    }

    public boolean isSaveable(final boolean def) {
        return _isSaveable != null ? _isSaveable : def;
    }

    public boolean isCancelable(final boolean def) {
        return _isCancelable != null ? _isCancelable : def;
    }

    public boolean isRemoveOnCritical() {
        return removeOnCritical;
    }

    public int getRemoveOnCriticalChance() {
        return removeOnCriticalChance;
    }

    public boolean isOffensive(final boolean def) {
        return _isOffensive != null ? _isOffensive : def;
    }

    public boolean refreshHpOnAdd() {
        return _refreshHpOnAdd;
    }

    public boolean refreshMpOnAdd() {
        return _refreshMpOnAdd;
    }

    public boolean refreshCpOnAdd() {
        return _refreshCpOnAdd;
    }
}