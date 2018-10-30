package org.mmocore.gameserver.stats;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.funcs.FuncOwner;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 * <p/>
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 * <p/>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * <p/>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 */
public final class Calculator {
    public final Stats _stat;
    public final Creature _character;
    private Func[] _functions;
    private double _base;
    private double _last;

    public Calculator(final Stats stat, final Creature character) {
        _stat = stat;
        _character = character;
        _functions = Func.EMPTY_FUNC_ARRAY;
    }

    /**
     * Return the number of Funcs in the Calculator.<BR><BR>
     */
    public int size() {
        return _functions.length;
    }

    /**
     * Add a Func to the Calculator.<BR><BR>
     */
    public void addFunc(final Func f) {
        _functions = ArrayUtils.add(_functions, f);
        ArrayUtils.eqSort(_functions);
    }

    /**
     * Remove a Func from the Calculator.<BR><BR>
     */
    public void removeFunc(final Func f) {
        _functions = ArrayUtils.remove(_functions, f);
        if (_functions.length == 0) {
            _functions = Func.EMPTY_FUNC_ARRAY;
        } else {
            ArrayUtils.eqSort(_functions);
        }
    }

    /**
     * Remove each Func with the specified owner of the Calculator.<BR><BR>
     */
    public void removeOwner(final Object owner) {
        final Func[] tmp = _functions;
        for (final Func element : tmp) {
            if (element.owner == owner) {
                removeFunc(element);
            }
        }
    }

    /**
     * Run each Func of the Calculator.<BR><BR>
     */
    public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
        final Func[] funcs = _functions;

        double value = initialValue;

        _base = value;

        boolean overrideLimits = false;
        for (final Func func : funcs) {
            if (func == null) {
                continue;
            }

            if (func.owner instanceof FuncOwner) {
                if (!((FuncOwner) func.owner).isFuncEnabled()) {
                    continue;
                }
                if (((FuncOwner) func.owner).overrideLimits()) {
                    overrideLimits = true;
                }
            }
            if (func.getCondition() == null || func.getCondition().test(creature, target, skill, value)) {
                value = func.calc(creature, target, skill, value);
            }
        }

        if (!overrideLimits) {
            value = _stat.validate(value);
        }

        if (Math.abs(value - _last) > 0.00001) {
            //double last = _last; //TODO [G1ta0] найти приминение в StatsChangeRecorder
            _last = value;
        }

        return value;
    }

    /**
     * Для отладки
     */
    public Func[] getFunctions() {
        return _functions;
    }

    public double getBase() {
        return _base;
    }

    public double getLast() {
        return _last;
    }
}