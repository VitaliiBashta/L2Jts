package org.mmocore.gameserver.stats.funcs;

import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FuncTemplate {
    public static final FuncTemplate[] EMPTY_ARRAY = new FuncTemplate[0];
    private static final Logger _log = LoggerFactory.getLogger(FuncTemplate.class);
    public final Condition _applyCond;
    public final Stats _stat;
    public final int _order;
    public final double _value;
    public final String _operate;
    public Class<?> _func;
    public Constructor<?> _constructor;

    public FuncTemplate(final Condition applyCond, final String func, final Stats stat, final int order, final double value) {
        _applyCond = applyCond;
        _stat = stat;
        _order = order;
        _value = value;
        _operate = func;

        try {
            _func = Class.forName("org.mmocore.gameserver.stats.funcs.Func" + func);

            _constructor = _func.getConstructor(new Class[]{
                    Stats.class, // stats to update
                    Integer.TYPE, // order of execution
                    Object.class, // owner
                    Double.TYPE // value for function
            });
        } catch (Exception e) {
            _log.error("", e);
        }
    }

    public Func getFunc(final Object owner) {
        try {
            final Func f = (Func) _constructor.newInstance(_stat, _order, owner, _value);
            if (_applyCond != null) {
                f.setCondition(_applyCond);
            }
            return f;
        } catch (IllegalAccessException e) {
            _log.error("", e);
            return null;
        } catch (InstantiationException e) {
            _log.error("", e);
            return null;
        } catch (InvocationTargetException e) {
            _log.error("", e);
            return null;
        }
    }
}