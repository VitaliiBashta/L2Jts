package org.mmocore.gameserver.stats.funcs;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.Condition;

/**
 * A Func object is a component of a Calculator created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 * <p/>
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 * <p/>
 * When the calc method of a calculator is launched, each mathematical function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order.
 */
public abstract class Func implements Comparable<Func> {
    public static final Func[] EMPTY_FUNC_ARRAY = new Func[0];

    /**
     * Statistics, that is affected by this function (See L2Character.CALCULATOR_XXX constants)
     */
    public final Stats stat;

    /**
     * Order of functions calculation.
     * Functions with lower order are executed first.
     * Functions with the same order are executed in unspecified order.
     * Usually add/substruct functions has lowest order,
     * then bonus/penalty functions (multiplay/divide) are
     * applied, then functions that do more complex calculations
     * (non-linear functions).
     */
    public final int order;

    /**
     * Owner can be an armor, weapon, skill, system event, quest, etc
     * Used to remove all functions added by this owner.
     */
    public final Object owner;

    public final double value;

    protected Condition cond;

    public Func(final Stats stat, final int order, final Object owner) {
        this(stat, order, owner, 0.);
    }

    public Func(final Stats stat, final int order, final Object owner, final double value) {
        this.stat = stat;
        this.order = order;
        this.owner = owner;
        this.value = value;
    }

    /**
     * Для отладки
     */
    public Condition getCondition() {
        return cond;
    }

    public void setCondition(final Condition cond) {
        this.cond = cond;
    }

    public abstract double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue);

    @Override
    public int compareTo(final Func f) throws NullPointerException {
        return order - f.order;
    }
}