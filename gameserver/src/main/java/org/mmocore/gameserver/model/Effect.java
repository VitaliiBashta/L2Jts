package org.mmocore.gameserver.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.listener.actor.OnAttackListener;
import org.mmocore.gameserver.listener.actor.OnMagicUseListener;
import org.mmocore.gameserver.listeners.impl.OnReceiveCriticalHit;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.funcs.FuncOwner;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner {
    public static final Effect[] EMPTY_EFFECT_ARRAY = new Effect[0];
    //Состояние, при котором работает задача запланированного эффекта
    public static final int SUSPENDED = -1;
    public static final int STARTING = 0;
    public static final int STARTED = 1;
    public static final int ACTING = 2;
    public static final int FINISHING = 3;
    public static final int FINISHED = 4;
    protected static final Logger _log = LoggerFactory.getLogger(Effect.class);
    /**
     * Накладывающий эффект
     */
    protected final Creature _effector;
    /**
     * Тот, на кого накладывают эффект
     */
    protected final Creature _effected;
    protected final SkillEntry _skill;
    protected final int _displayId;
    protected final int _displayLevel;
    protected final EffectTemplate _template;
    // the value of an update
    private final double _value;
    // the current state
    private final AtomicInteger _state;
    /**
     * Тот, кто отражает эффект
     */
    protected Creature reflector;
    // counter
    private int _count;
    // period, milliseconds
    private long _period;
    private long _startTimeMillis;
    private long _duration;
    private boolean _inUse = false;
    private Effect _next = null;
    private boolean _active = false;
    private Future<?> _effectTask;
    private ActionDispelListener _listener;
    private OnReceiveCriticalHit onCriticalHitListener;

    protected Effect(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        _skill = skill;
        _effector = creature;
        _effected = target;

        _template = template;
        _value = template._value;
        _count = template.getCount();
        _period = template.getPeriod();

        _duration = _period * _count;

        _displayId = template._displayId != 0 ? template._displayId : _skill.getDisplayId();
        _displayLevel = template._displayLevel != 0 ? template._displayLevel : _skill.getDisplayLevel();

        _state = new AtomicInteger(STARTING);
    }

    public long getPeriod() {
        return _period;
    }

    public void setPeriod(final long time) {
        _period = time;
        _duration = _period * _count;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(final int count) {
        _count = count;
        _duration = _period * _count;
    }

    public boolean isOneTime() {
        return _period == 0;
    }

    /**
     * Используется для того чтобы "сдвинуть в прошлое" время старта
     * для корректной сортировки эффектов запущенных в одно и то же время
     * (загрузка, комьюнити-бафер).
     * Крайне не рекомендуется корректировать время старта больше чем на
     * несколько десятков миллисекунд, а также "сдвигать в будущее".
     *
     * @param fixConst время коррекции времени старта в миллисекундах
     */
    public final void fixStartTime(final int fixConst) {
        if (_startTimeMillis == 0L) {
            _startTimeMillis = System.currentTimeMillis() - fixConst;
        } else {
            _startTimeMillis -= fixConst;
        }
    }

    /**
     * Возвращает время старта эффекта, если время не установлено, возвращается текущее
     */
    public long getStartTime() {
        if (_startTimeMillis == 0L) {
            return System.currentTimeMillis();
        }
        return _startTimeMillis;
    }

    /**
     * Возвращает общее время действия эффекта в миллисекундах.
     */
    public long getTime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * Возвращает длительность эффекта в миллисекундах.
     */
    public long getDuration() {
        return _duration;
    }

    /**
     * Возвращает оставшееся время в секундах.
     */
    public int getTimeLeft() {
        return (int) ((getDuration() - getTime()) / 1000L);
    }

    /**
     * Возвращает true, если осталось время для действия эффекта
     */
    public boolean isTimeLeft() {
        return getDuration() - getTime() > 0L;
    }

    public boolean isInUse() {
        return _inUse;
    }

    public void setInUse(final boolean inUse) {
        _inUse = inUse;
    }

    public boolean isActive() {
        return _active;
    }

    /**
     * Для неактивных эфектов не вызывается onActionTime.
     */
    public void setActive(final boolean set) {
        _active = set;
    }

    public EffectTemplate getTemplate() {
        return _template;
    }

    public String getStackType() {
        return getTemplate()._stackType;
    }

    public String getStackType2() {
        return getTemplate()._stackType2;
    }

    public boolean checkStackType(final String param) {
        return getStackType().equalsIgnoreCase(param) || getStackType2().equalsIgnoreCase(param);
    }

    public boolean checkStackType(final Effect param) {
        return checkStackType(param.getStackType()) || checkStackType(param.getStackType2());
    }

    public int getStackOrder() {
        return getTemplate()._stackOrder;
    }

    public SkillEntry getSkill() {
        return _skill;
    }

    public Creature getEffector() {
        return _effector;
    }

    public Creature getEffected() {
        return _effected;
    }

    public double calc() {
        return _value;
    }

    public boolean isEnded() {
        return isFinished() || isFinishing();
    }

    public boolean isFinishing() {
        return getState() == FINISHING;
    }

    public boolean isFinished() {
        return getState() == FINISHED;
    }

    private int getState() {
        return _state.get();
    }

    private boolean setState(final int oldState, final int newState) {
        return _state.compareAndSet(oldState, newState);
    }

    public boolean checkCondition() {
        return true;
    }

    /**
     * Notify started
     */
    protected void onStart() {
        getEffected().addStatFuncs(getStatFuncs());
        getEffected().addTriggers(getTemplate());
        if (getTemplate()._abnormalEffects != null) {
            for (final AbnormalEffect e : getTemplate()._abnormalEffects) {
                getEffected().startAbnormalEffect(e);
            }
        }
        if (_template._cancelOnAction) {
            getEffected().addListener(_listener = new ActionDispelListener());
        }
        if (getEffected().isPlayer() && !getSkill().getTemplate().canUseTeleport()) {
            getEffected().getPlayer().getPlayerAccess().UseTeleport = false;
        }
        if (_template.isRemoveOnCritical()) {
            onCriticalHitListener = getEffected().listeners().add(OnReceiveCriticalHit.class, (attacker, victim) -> {
                if (Rnd.chance(_template.getRemoveOnCriticalChance()))
                    exit();
            });
        }
    }


    /**
     * Return true for continuation of this effect
     */
    protected abstract boolean onActionTime();

    /**
     * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
     */
    protected void onExit() {
        getEffected().removeStatsByOwner(this);
        getEffected().removeTriggers(getTemplate());
        if (getTemplate()._abnormalEffects != null) {
            for (final AbnormalEffect e : getTemplate()._abnormalEffects) {
                getEffected().stopAbnormalEffect(e);
            }
        }
        if (_template._cancelOnAction) {
            getEffected().removeListener(_listener);
        }
        if (getEffected().isPlayer() && getStackType() == EffectTemplate.HP_RECOVER_CAST) {
            getEffected().sendPacket(new ShortBuffStatusUpdate());
        }
        if (getEffected().isPlayer() && !getSkill().getTemplate().canUseTeleport() && !getEffected().getPlayer().getPlayerAccess().UseTeleport) {
            getEffected().getPlayer().getPlayerAccess().UseTeleport = true;
        }
        if (onCriticalHitListener != null)
            getEffected().listeners().remove(onCriticalHitListener);
    }

    private void stopEffectTask() {
        if (_effectTask != null) {
            _effectTask.cancel(false);
        }
    }

    private void startEffectTask() {
        if (_effectTask == null) {
            _startTimeMillis = System.currentTimeMillis();
            _effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, _period, _period);
        }
    }

    /**
     * Добавляет эффект в список эффектов, в случае успешности вызывается метод start
     */
    public final void schedule() {
        final Creature effected = getEffected();
        if (effected == null) {
            return;
        }

        if (!checkCondition()) {
            return;
        }

        getEffected().getEffectList().addEffect(this);
    }

    /**
     * Переводит эффект в "фоновый" режим, эффект может быть запущен методом schedule
     */
    private void suspend() {
        // Эффект создан, запускаем задачу в фоне
        if (setState(STARTING, SUSPENDED)) {
            startEffectTask();
        } else if (setState(STARTED, SUSPENDED) || setState(ACTING, SUSPENDED)) {
            synchronized (this) {
                if (isInUse()) {
                    setInUse(false);
                    setActive(false);
                    onExit();
                }
            }
            getEffected().getEffectList().removeEffect(this);
        }
    }

    /**
     * Запускает задачу эффекта, в случае если эффект успешно добавлен в список
     */
    public final void start() {
        if (setState(STARTING, STARTED)) {
            synchronized (this) {
                if (isInUse()) {
                    setActive(true);
                    onStart();
                    startEffectTask();
                }
            }
        }

        run();
    }

    @Override
    public final void runImpl() throws Exception {
        if (setState(STARTED, ACTING)) {
            // Отображать сообщение только для первого эффекта скилла
            if (!getSkill().getTemplate().isHideStartMessage() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1) {
                getEffected().sendPacket(new SystemMessage(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));
            }

            return;
        }

        if (getState() == SUSPENDED) {
            if (isTimeLeft()) {
                _count--;
                if (isTimeLeft()) {
                    return;
                }
            }

            exit();
            return;
        }

        if (getState() == ACTING) {
            if (isTimeLeft()) {
                _count--;
                if ((!isActive() || onActionTime()) && isTimeLeft()) {
                    return;
                }
            }
        }

        if (setState(ACTING, FINISHING)) {
            setInUse(false);
        }

        if (setState(FINISHING, FINISHED)) {
            synchronized (this) {
                setActive(false);
                stopEffectTask();
                onExit();
            }

            // Добавляем следующий запланированный эффект
            final Effect next = getNext();
            if (next != null) {
                if (next.setState(SUSPENDED, STARTING)) {
                    next.schedule();
                }
            }

            if (getSkill().getTemplate().getDelayedEffect() > 0) {
                SkillTable.getInstance().getSkillEntry(getSkill().getTemplate().getDelayedEffect(), 1).getEffects(_effector, _effected, false, false);
            }

            final boolean msg = !isHidden() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1;

            getEffected().getEffectList().removeEffect(this);

            // Отображать сообщение только для последнего оставшегося эффекта скилла
            if (msg) {
                getEffected().sendPacket(new SystemMessage(SystemMsg.S1_HAS_WORN_OFF).addSkillName(_displayId, _displayLevel));
            }
        }
    }

    /**
     * Завершает эффект и все связанные, удаляет эффект из списка эффектов
     */
    public final void exit() {
        final Effect next = getNext();
        if (next != null) {
            next.exit();
        }
        removeNext();

        //Эффект запланирован на запуск, удаляем
        if (setState(STARTING, FINISHED)) {
            getEffected().getEffectList().removeEffect(this);
        }
        //Эффект работает в "фоне", останавливаем задачу в планировщике
        else if (setState(SUSPENDED, FINISHED)) {
            stopEffectTask();
        } else if (setState(STARTED, FINISHED) || setState(ACTING, FINISHED)) {
            synchronized (this) {
                if (isInUse()) {
                    setInUse(false);
                    setActive(false);
                    stopEffectTask();
                    onExit();
                }
            }
            getEffected().getEffectList().removeEffect(this);
        }
    }

    /**
     * Поставить в очередь эффект
     *
     * @param e
     * @return true, если эффект поставлен в очередь
     */
    private boolean scheduleNext(final Effect e) {
        if (e == null || e.isEnded()) {
            return false;
        }

        final Effect next = getNext();
        if (next != null && !next.maybeScheduleNext(e)) {
            return false;
        }

        _next = e;

        return true;
    }

    public Effect getNext() {
        return _next;
    }

    private void removeNext() {
        _next = null;
    }

    /**
     * @return false - игнорировать новый эффект, true - использовать новый эффект
     */
    public boolean maybeScheduleNext(final Effect newEffect) {
        if (newEffect.getStackOrder() < getStackOrder()) // новый эффект слабее
        {
            if (newEffect.getTimeLeft() > getTimeLeft()) // новый эффект длинее
            {
                newEffect.suspend();
                scheduleNext(newEffect); // пробуем пристроить новый эффект в очередь
            }

            return false; // более слабый эффект всегда игнорируется, даже если не попал в очередь
        } else // если старый не дольше, то просто остановить его, при стак ордере "-2", эффект заменяется перманентно.
            if (newEffect.getTimeLeft() >= getTimeLeft() || newEffect.getStackOrder() == -2) {
                // наследуем зашедуленый старому, если есть смысл
                if (getNext() != null && getNext().getTimeLeft() > newEffect.getTimeLeft() || newEffect.getStackOrder() == -2) {
                    newEffect.scheduleNext(getNext());
                    // отсоединяем зашедуленные от текущего
                    removeNext();
                }
                exit();
            } else
            // если новый короче то зашедулить старый
            {
                suspend();
                newEffect.scheduleNext(this);
            }

        return true;
    }

    public Func[] getStatFuncs() {
        return getTemplate().getStatFuncs(this);
    }

    public void addIcon(final AbnormalStatusUpdate mi) {
        if (!isActive() || isHidden()) {
            return;
        }
        final int duration = _skill.getTemplate().isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        mi.addEffect(_displayId, _displayLevel, duration);
    }

    public void addPartySpelledIcon(final PartySpelled ps) {
        if (!isActive() || isHidden()) {
            return;
        }
        final int duration = _skill.getTemplate().isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        ps.addPartySpelledEffect(_displayId, _displayLevel, duration);
    }

    public void addOlympiadSpelledIcon(final Player player, final ExOlympiadSpelledInfo os) {
        if (!isActive() || isHidden()) {
            return;
        }
        final int duration = _skill.getTemplate().isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        os.addSpellRecivedPlayer(player);
        os.addEffect(_displayId, _displayLevel, duration);
    }

    protected int getLevel() {
        return _skill.getLevel();
    }

    public EffectType getEffectType() {
        return getTemplate()._effectType;
    }

    public boolean isHidden() {
        return _displayId < 0;
    }

    @Override
    public int compareTo(final Effect obj) {
        if (obj == this) {
            return 0;
        }
        return 1;
    }

    public boolean isSaveable() {
        return _template.isSaveable(getSkill().getTemplate().isSaveable()) && getTimeLeft() >= AllSettingsConfig.ALT_SAVE_EFFECTS_REMAINING_TIME;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    public boolean isCancelable() {
        return _template.isCancelable(getSkill().getTemplate().isCancelable());
    }

    @Override
    public String toString() {
        return "Skill: " + _skill + ", state: " + getState() + ", inUse: " + _inUse + ", active : " + _active;
    }

    @Override
    public boolean isFuncEnabled() {
        return isInUse();
    }

    @Override
    public boolean overrideLimits() {
        return false;
    }

    public boolean isOffensive() {
        return _template.isOffensive(getSkill().getTemplate().isOffensive());
    }

    public boolean refreshHpOnAdd() {
        return _template.refreshHpOnAdd();
    }

    public boolean refreshMpOnAdd() {
        return _template.refreshMpOnAdd();
    }

    public boolean refreshCpOnAdd() {
        return _template.refreshCpOnAdd();
    }

    private class ActionDispelListener implements OnAttackListener, OnMagicUseListener {
        @Override
        public void onMagicUse(final Creature actor, final SkillEntry skill, final Creature target, final boolean alt) {
            exit();
        }

        @Override
        public void onAttack(final Creature actor, final Creature target) {
            exit();
        }
    }
}