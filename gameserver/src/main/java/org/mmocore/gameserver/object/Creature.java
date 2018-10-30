package org.mmocore.gameserver.object;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.listener.Listener;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.commons.utils.concurrent.atomic.AtomicState;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.PlayableAI.NextAction;
import org.mmocore.gameserver.configuration.config.*;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.geoengine.GeoMove;
import org.mmocore.gameserver.listeners.Listeners;
import org.mmocore.gameserver.manager.DimensionalRiftManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.EffectList;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Skill.SkillTargetType;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.base.AttackType;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.model.base.SkillMastery;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.reference.L2Reference;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.clientpackets.Moving.ValidatePosition;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.ChangeMoveType.EnvType;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.*;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.creatures.recorders.CharStatsChangeRecorder;
import org.mmocore.gameserver.object.components.creatures.tasks.*;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.*;
import org.mmocore.gameserver.skills.effects.*;
import org.mmocore.gameserver.stats.*;
import org.mmocore.gameserver.stats.Formulas.AttackInfo;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;
import org.mmocore.gameserver.stats.triggers.TriggerType;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.mmocore.gameserver.taskmanager.RegenTaskManager;
import org.mmocore.gameserver.templates.CharTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_CAST;

public abstract class Creature extends GameObject {
    public static final double HEADINGS_IN_PI = 10430.378350470452724949566316381;
    public static final int INTERACTION_DISTANCE = 150;
    public static final int CLIENT_BAR_SIZE = 352; // 352 - размер полоски CP/HP/MP в клиенте, в пикселях
    private static final long serialVersionUID = 9000662041033086674L;
    private static final Logger _log = LoggerFactory.getLogger(Creature.class);
    private static final double[] POLE_VAMPIRIC_MOD = {1, 0.5, 0, 25, 0.125, 0.06, 0.03, 0.01};
    protected final Map<Integer, SkillEntry> skills = new ConcurrentSkipListMap<>();
    protected final Map<Integer, TimeStamp> skillReuses = new ConcurrentHashMap<>();
    protected final Map<TriggerType, Set<TriggerInfo>> triggers = new ConcurrentHashMap<>();
    protected final CharStatsChangeRecorder<? extends Creature> _statsRecorder = initializeStatsRecorder();
    protected final AtomicBoolean isDead = new AtomicBoolean();
    protected final AtomicBoolean isTeleporting = new AtomicBoolean();
    protected final CharTemplate _baseTemplate;
    protected final CharListenerList oldListeners = initializeListeners();
    protected final HardReference<? extends Creature> reference;
    private final int[] _abnormalEffects = new int[AbnormalEffectType.VALUES.length];
    private final AtomicState _afraid = new AtomicState();
    private final AtomicState _muted = new AtomicState();
    private final AtomicState _pmuted = new AtomicState();
    private final AtomicState _amuted = new AtomicState();
    private final AtomicState _paralyzed = new AtomicState();
    private final AtomicState _rooted = new AtomicState();
    private final AtomicState _sleeping = new AtomicState();
    private final AtomicState _stunned = new AtomicState();
    private final AtomicState _immobilized = new AtomicState();
    private final AtomicState _confused = new AtomicState();

    private final AtomicBoolean _healBlocked = new AtomicBoolean();
    private final AtomicBoolean _damageBlocked = new AtomicBoolean();
    private final AtomicBoolean _buffImmunity = new AtomicBoolean(); // Иммунитет к бафам
    private final AtomicBoolean _debuffImmunity = new AtomicBoolean(); // Иммунитет к дебафам

    private final AtomicState _effectImmunity = new AtomicState(); // Иммунитет ко всем эффектам

    private final AtomicState _weaponEquipBlocked = new AtomicState();
    private final Lock moveLock = new ReentrantLock(true);

    /**
     * при moveToLocation используется для хранения геокоординат в которые мы двигаемся для того что бы избежать повторного построения одного и того же пути
     * при followToCharacter используется для хранения мировых координат в которых находилась последний раз преследуемая цель для отслеживания необходимости перестраивания пути
     */
    private final Location movingDestTempPos = new Location();
    private final List<List<Location>> _targetRecorder = new ArrayList<>();
    private final Calculator[] _calculators;
    private final Lock regenLock = new ReentrantLock();
    private final List<Zone> _zones = new ArrayList<>();
    /**
     * Блокировка для чтения/записи объектов из региона
     */
    private final ReadWriteLock zonesLock = new ReentrantReadWriteLock();
    private final Lock zonesRead = zonesLock.readLock();
    private final Lock zonesWrite = zonesLock.writeLock();
    private final Lock statusListenersLock = new ReentrantLock();
    public int _scheduledCastCount;
    public int _scheduledCastInterval;
    public Future<?> _skillTask;
    public Future<?> _skillLaunchedTask;
    public Future<?> _skillGeoCheckTask; // используется и как флаг: null - проверка успешно пройдена или не нужна
    public boolean isMoving;
    public boolean isFollow;
    protected double _currentCp;
    protected double currentHp = 1;
    protected double _currentMp = 1;
    protected boolean _isAttackAborted;
    protected long _attackEndTime;
    protected long _attackReuseEndTime;
    protected volatile EffectList _effectList;
    protected CharTemplate _template;
    protected volatile CharacterAI _ai;
    protected String _name;
    protected String _title;
    protected TeamType _team = TeamType.NONE;
    private InvisibleType _invisibleType = InvisibleType.NONE;

    private boolean _isUndying = false;
    private AtomicBoolean _undyingFlag = new AtomicBoolean(false);
    private SkillEntry _castingSkill;

    private long _castInterruptTime;
    private long _animationEndTime;
    private Future<?> _stanceTask;
    private Runnable _stanceTaskRunnable;
    private long _stanceEndTime;
    private int _lastCpBarUpdate = -1;
    private int _lastHpBarUpdate = -1;
    private int _lastMpBarUpdate = -1;
    private int _poleAttackCount;
    private List<Stats> _blockedStats;
    private volatile int _skillMasteryId;
    private boolean _isInvul;
    private boolean _fakeDeath;
    private boolean _isBlessedByNoblesse; // Восстанавливает все бафы после смерти
    private boolean _isSalvation; // Восстанавливает все бафы после смерти и полностью CP, MP, HP
    private boolean _meditated;
    private boolean _lockedTarget;
    private boolean _blocked;
    private boolean _frozen;
    private boolean _flying;
    private boolean _running;
    private boolean _dominionTransform;
    private volatile Location _lastClientPosition;
    private volatile Location _lastServerPosition;
    private Future<?> _moveTask;
    private MoveNextTask _moveTaskRunnable;
    private List<Location> moveList;
    private Location destination;
    private int _offset;
    private boolean _forestalling;
    private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> castingTarget = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> followTarget = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> _aggressionTarget = HardReferences.emptyRef();
    private long _followTimestamp, _startMoveTime;
    private int _previousSpeed;
    private int _heading;
    private boolean _isRegenerating;
    private Future<?> _regenTask;
    private Runnable _regenTaskRunnable;
    private Listeners listeners = new Listeners();
    /**
     * Список игроков, которым необходимо отсылать информацию об изменении состояния персонажа
     */
    private List<Player> _statusListeners;
    private Servitor fearRunningSummon;
    private Location _flyLoc;

    public Creature(int objectId, CharTemplate template) {
        super(objectId);

        _template = template;
        _baseTemplate = template;

        _calculators = new Calculator[Stats.NUM_STATS];

        StatFunctions.addPredefinedFuncs(this);

        reference = new L2Reference<>(this);

        GameObjectsStorage.put(this);
    }

    @Override
    public HardReference<? extends Creature> getRef() {
        return reference;
    }

    public boolean isAttackAborted() {
        return _isAttackAborted;
    }

    public final void abortAttack(boolean force, boolean message) {
        if (isAttackingNow()) {
            _attackEndTime = 0;
            if (force) {
                _isAttackAborted = true;
            }

            getAI().setIntention(AI_INTENTION_ACTIVE);

            if (isPlayer() && message) {
                sendActionFailed();
                sendPacket(new SystemMessage(SystemMsg.C1S_ATTACK_FAILED).addName(this));
            }
        }
    }

    public final void abortCast(boolean force, boolean message) {
        if (isCastingNow() && (force || canAbortCast())) {
            final SkillEntry castingSkill = _castingSkill;
            final Future<?> skillTask = _skillTask;
            final Future<?> skillLaunchedTask = _skillLaunchedTask;
            final Future<?> skillGeoCheckTask = _skillGeoCheckTask;

            finishFly(); // Броадкаст пакета FlyToLoc уже выполнен, устанавливаем координаты чтобы не было визуальных глюков
            clearCastVars();

            if (skillTask != null) {
                skillTask.cancel(false); // cancels the skill hit scheduled task
            }

            if (skillLaunchedTask != null) {
                skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task
            }

            if (skillGeoCheckTask != null) {
                skillGeoCheckTask.cancel(false); // cancels the skill hit scheduled task
            }

            if (castingSkill != null && castingSkill.getTemplate().isUsingWhileCasting()) {
                Creature target = getAI().getAttackTarget();
                if (target != null) {
                    target.getEffectList().stopEffect(castingSkill.getId());
                }
            }

            broadcastPacket(new MagicSkillCanceled(getObjectId())); // broadcast packet to stop animations client-side

            getAI().setIntention(AI_INTENTION_ACTIVE);

            if (isPlayer() && message) {
                sendPacket(SystemMsg.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
            }
        }
    }

    public final boolean canAbortCast() {
        return _castInterruptTime > System.currentTimeMillis();
    }

    private double absorbAndReflect(Creature target, SkillEntry skillEntry, double damage) {
        if (target.isDead()) {
            return 0;
        }

        boolean bow = getActiveWeaponItem() != null && getActiveWeaponItem().getItemType() != null
                && (getActiveWeaponItem().getItemType() == WeaponType.BOW
                || getActiveWeaponItem().getItemType() == WeaponType.CROSSBOW);

        double value = 0;

        Skill skill = skillEntry == null ? null : skillEntry.getTemplate();
        if (skill != null && skill.isMagic() && !isPet() && !isSummon()) {
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0, this, skillEntry);
        } else if (skill != null && skill.getCastRange() <= 200) {
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0, this, skillEntry);
        } else if (skill == null && !bow) {
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0, this, null);
        }

        //Цель отразила весь урон
        if (value > 0 && Rnd.chance(value)) {
            reduceCurrentHp(damage, target, null, true, true, false, false, false, false, true);
            if (isDamageBlocked()) {
                return 0;
            }
            return -1;
        }

        if (skill != null && skill.isMagic()) {
            value = target.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0, this, skillEntry);
        } else if (skill != null && skill.getCastRange() <= 200) {
            value = target.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0, this, skillEntry);
        } else if (skill == null && !bow) {
            value = target.calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, this, null);
        }

        double reflectedDamage = 0;
        if (value > 0) {
            //Цель в состоянии отразить часть урона
            if (target.getCurrentHp() + target.getCurrentCp() > damage) {
                reflectedDamage = value / 100. * damage;
                reduceCurrentHp(reflectedDamage, target, null, true, true, false, false, false, false, false);
            }
        }

        if (skill != null || bow) {
            if (isDamageBlocked()) {
                return 0;
            }
            return reflectedDamage;
        }

        // вампирик
        double damageToHp = damage - target.getCurrentCp();
        if (damageToHp <= 0) {
            if (isDamageBlocked()) {
                return 0;
            }
            return reflectedDamage;
        }

        damageToHp = Math.min(damageToHp, target.getCurrentHp());
        final double poleMod = _poleAttackCount < POLE_VAMPIRIC_MOD.length ? POLE_VAMPIRIC_MOD[_poleAttackCount] : 0;
        double absorb = poleMod * calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, target, null);
        double limit;
        if (absorb > 0 && !target.isDamageBlocked()) {
            limit = calcStat(Stats.HP_LIMIT, null, null) * getMaxHp() / 100.;
            if (getCurrentHp() < limit) {
                setCurrentHp(Math.min(currentHp + damageToHp * absorb * FormulasConfig.ALT_ABSORB_DAMAGE_MODIFIER / 100., limit), false);
            }
        }

        absorb = poleMod * calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
        if (absorb > 0 && !target.isDamageBlocked()) {
            limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100.;
            if (getCurrentMp() < limit) {
                setCurrentMp(Math.min(_currentMp + damageToHp * absorb * FormulasConfig.ALT_ABSORB_DAMAGE_MODIFIER / 100., limit));
            }
        }

        if (isDamageBlocked()) {
            return 0;
        }

        return reflectedDamage;
    }

    public double absorbToEffector(Creature attacker, double damage) {
        double transferToEffectorDam = calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.);
        if (transferToEffectorDam > 0) {
            Effect effect = getEffectList().getEffectByType(EffectType.AbsorbDamageToEffector);
            if (effect == null) {
                return damage;
            }

            Creature effector = effect.getEffector();
            // на мертвого чара, не онлайн игрока - не даем абсорб, и не на самого себя
            if (effector == this || effector.isDead() || !isInRange(effector, 1200)) {
                return damage;
            }

            Player thisPlayer = getPlayer();
            Player effectorPlayer = effector.getPlayer();
            if (thisPlayer != null && effectorPlayer != null) {
                if (thisPlayer != effectorPlayer && (!thisPlayer.isOnline() || !thisPlayer.isInParty() || thisPlayer.getParty() != effectorPlayer.getParty())) {
                    return damage;
                }
            } else {
                return damage;
            }

            double transferDamage = damage * transferToEffectorDam * .01;
            damage -= transferDamage;

            effector.reduceCurrentHp(transferDamage, effector, null, false, false, !attacker.isPlayable(), false, true, false, true);
        }
        return damage;
    }

    public double absorbToMp(Creature attacker, double damage) {
        double transferToMpDamPercent = calcStat(Stats.TRANSFER_TO_MP_DAMAGE_PERCENT, 0.);
        if (transferToMpDamPercent > 0) {
            double transferDamage = damage * transferToMpDamPercent * .01;

            double currentMp = getCurrentMp();
            if (currentMp > transferDamage) {
                sendPacket(new SystemMessage(SystemMsg.DUE_TO_THE_EFFECT_OF_THE_ARCANE_SHIELD_MP_RATHER_THAN_HP_RECEIVED_S1S_DAMAGE).addNumber((long) transferDamage));
                setCurrentMp(getCurrentMp() - transferDamage);
                return 0;
            } else {
                if (currentMp > 0) {
                    damage -= currentMp;
                    setCurrentMp(0);
                    sendPacket(SystemMsg.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                }
                getEffectList().stopEffects(EffectType.AbsorbDamageToMp);
            }

            return damage;
        }
        return damage;
    }

    public double absorbToSummon(Creature attacker, double damage) {
        final Servitor summon = getServitor();
        if (summon == null || summon.isDead() || !summon.isSummon() || !summon.isInRangeZ(this, 1000)) {
            return 0;
        }
        if (summon.getEffectList().getEffect(effect -> effect.getSkill().getId() == Skill.SKILL_RAID_CURSE).isPresent()) {
            return 0;
        }
        double transferDamage = calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0);
        if (transferDamage <= 0) {
            return 0;
        }

        transferDamage = transferDamage * damage * 0.01;
        if (summon.getCurrentHp() > transferDamage + 1.) {
            summon.reduceCurrentHp(transferDamage, summon, null, false, false, false, false, true, false, true);
        } else {
            return -transferDamage;
        }

        return transferDamage;
    }

    public void addBlockStats(List<Stats> stats) {
        if (_blockedStats == null) {
            _blockedStats = new ArrayList<>();
        }
        _blockedStats.addAll(stats);
    }

    public SkillEntry addSkill(SkillEntry newSkill) {
        if (newSkill == null) {
            return null;
        }

        SkillEntry oldSkill = getAllMapSkills().get(newSkill.getId());

        if (oldSkill != null && oldSkill.getLevel() == newSkill.getLevel()) {
            return newSkill;
        }

        getAllMapSkills().put(newSkill.getId(), newSkill);

        if (oldSkill != null) {
            removeStatsByOwner(oldSkill);
            removeTriggers(oldSkill.getTemplate());
        }

        if (!newSkill.isDisabled()) {
            addTriggers(newSkill.getTemplate());

            addStatFuncs(newSkill.getStatFuncs());
        }

        return oldSkill;
    }

    public Calculator[] getCalculators() {
        return _calculators;
    }

    public final void addStatFunc(Func f) {
        if (f == null) {
            return;
        }
        int stat = f.stat.ordinal();
        synchronized (_calculators) {
            if (_calculators[stat] == null) {
                _calculators[stat] = new Calculator(f.stat, this);
            }
            _calculators[stat].addFunc(f);
        }
    }

    public final void addStatFuncs(Func[] funcs) {
        for (Func f : funcs) {
            addStatFunc(f);
        }
    }

    public final void removeStatFunc(Func f) {
        if (f == null) {
            return;
        }
        int stat = f.stat.ordinal();
        synchronized (_calculators) {
            if (_calculators[stat] != null) {
                _calculators[stat].removeFunc(f);
            }
        }
    }

    public final void removeStatFuncs(Func[] funcs) {
        for (Func f : funcs) {
            removeStatFunc(f);
        }
    }

    public final void removeStatsByOwner(Object owner) {
        synchronized (_calculators) {
            for (Calculator _calculator : _calculators) {
                if (_calculator != null) {
                    _calculator.removeOwner(owner);
                }
            }
        }
    }

    public void altOnMagicUseTimer(Creature aimingTarget, SkillEntry skillEntry) {
        if (isDead()) {
            return;
        }
        Skill skill = skillEntry.getTemplate();
        int magicId = skill.getDisplayId();
        int level = Math.max(1, getSkillDisplayLevel(skill.getId()));
        List<Creature> targets = skill.getTargets(this, aimingTarget, true);

        int[] intObjList = Util.objectToIntArray(targets);
        double mpConsume2 = skill.getMpConsume2();
        if (mpConsume2 > 0) {
            if (_currentMp < mpConsume2) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                return;
            }
            if (skill.isMagic()) {
                reduceCurrentMp(calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skillEntry), null);
            } else {
                reduceCurrentMp(calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skillEntry), null);
            }
        }

        callSkill(skillEntry, targets, false);
        removeSkillMastery(skillEntry);

        broadcastPacket(new MagicSkillLaunched(getObjectId(), magicId, level, intObjList));
    }

    public void altUseSkill(SkillEntry skillEntry, Creature target) {
        altUseSkill(skillEntry, target, true);
    }

    public void altUseSkill(SkillEntry skillEntry, Creature target, final boolean reuse) {
        if (skillEntry == null || skillEntry.isDisabled()) {
            return;
        }
        Skill skill = skillEntry.getTemplate();
        int magicId = skill.getId();

        if (reuse && isSkillDisabled(skillEntry)) {
            sendReuseMessage(skillEntry);
            return;
        }
        if (target == null) {
            target = skill.getAimingTarget(this, getTarget());
            if (target == null) {
                return;
            }
        }

        if (!skill.checkPreConditions(skillEntry, this, target)) {
            return;
        }

        getListeners().onMagicUse(skillEntry, target, true);

        final double mpConsume1 = skill.getMpConsume1();
        if (mpConsume1 > 0) {
            if (_currentMp < mpConsume1) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                return;
            }
            reduceCurrentMp(mpConsume1, null);
        }

        if (skill.getReferenceItemId() > 0) {
            if (!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume())) {
                return;
            }
        }

        if (skill.getSoulsConsume() > getConsumedSouls()) {
            sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
            return;
        }

        if (skill.getSoulsConsume() > 0) {
            setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);
        }

        int level = Math.max(1, getSkillDisplayLevel(magicId));
        Formulas.calcSkillMastery(skillEntry, this);
        long reuseDelay = Formulas.calcSkillReuseDelay(this, skillEntry);
        if (!skill.isToggle()) {
            broadcastPacket(new MagicSkillUse(this, target, skill.getDisplayId(), level, skill.getHitTime(), reuseDelay));
        }
        // Не показывать сообщение для хербов и кубиков
        if (!skill.isHideUseMessage()) {
            if (skill.getSkillType() == SkillType.PET_SUMMON) {
                sendPacket(SystemMsg.SUMMONING_YOUR_PET);
            } else if (skill.getItemConsumeId().length == 0 || !skill.isHandler()) {
                sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
            } else {
                sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addItemName(skill.getItemConsumeId()[0]));
            }
        }

        int[] itemConsume = skill.getItemConsume();
        if (itemConsume.length > 0) {
            for (int i = 0; i < itemConsume.length; i++) {
                if (!consumeItem(skill.getItemConsumeId()[i], itemConsume[i])) {
                    sendPacket(skill.isHandler() ? SystemMsg.NOT_ENOUGH_ITEMS : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                    return;
                }
            }
        }

        if (!skill.isHandler()) {
            disableSkill(skillEntry, reuseDelay);
        }

        ThreadPoolManager.getInstance().schedule(new AltMagicUseTask(this, target, skillEntry), skill.getHitTime());
    }

    public void sendReuseMessage(SkillEntry skill) {
    }

    public void broadcastPacket(IBroadcastPacket... packets) {
        sendPacket(packets);
        broadcastPacketToOthers(packets);
    }

    public void broadcastPacket(List<IBroadcastPacket> packets) {
        sendPacket(packets);
        broadcastPacketToOthers(packets);
    }

    public void broadcastPacketToOthers(IBroadcastPacket... packets) {
        if (!isVisible() || packets.length == 0) {
            return;
        }

        final List<Player> players = World.getAroundObservers(this);
        for (final Player target : players)
            target.sendPacket(packets);
    }

    public void broadcastPacketToOthers(List<IBroadcastPacket> packets) {
        if (!isVisible() || packets.isEmpty()) {
            return;
        }

        List<Player> players = World.getAroundObservers(this);
        Player target;
        for (Player player : players) {
            target = player;
            target.sendPacket(packets);
        }
    }

    public void broadcastToStatusListeners(L2GameServerPacket... packets) {
        if (!isVisible() || packets.length == 0) {
            return;
        }

        statusListenersLock.lock();
        try {
            if (_statusListeners == null || _statusListeners.isEmpty()) {
                return;
            }

            for (Player _statusListener : _statusListeners) {
                _statusListener.sendPacket(packets);
            }
        } finally {
            statusListenersLock.unlock();
        }
    }

    public void addStatusListener(Player cha) {
        if (cha == this) {
            return;
        }

        statusListenersLock.lock();
        try {
            if (_statusListeners == null) {
                _statusListeners = new ArrayList<>();
            }
            if (!_statusListeners.contains(cha)) {
                _statusListeners.add(cha);
            }
        } finally {
            statusListenersLock.unlock();
        }
    }

    public void removeStatusListener(Creature cha) {
        statusListenersLock.lock();
        try {
            if (_statusListeners == null) {
                return;
            }
            _statusListeners.remove(cha);
        } finally {
            statusListenersLock.unlock();
        }
    }

    public void clearStatusListeners() {
        statusListenersLock.lock();
        try {
            if (_statusListeners == null) {
                return;
            }
            _statusListeners.clear();
        } finally {
            statusListenersLock.unlock();
        }
    }

    public StatusUpdate makeStatusUpdate(int... fields) {
        StatusUpdate su = new StatusUpdate(getObjectId());
        for (int field : fields) {
            switch (field) {
                case StatusUpdate.CUR_HP:
                    su.addAttribute(field, (int) getCurrentHp());
                    break;
                case StatusUpdate.MAX_HP:
                    su.addAttribute(field, (int) getMaxHp());
                    break;
                case StatusUpdate.CUR_MP:
                    su.addAttribute(field, (int) getCurrentMp());
                    break;
                case StatusUpdate.MAX_MP:
                    su.addAttribute(field, getMaxMp());
                    break;
                case StatusUpdate.KARMA:
                    su.addAttribute(field, getKarma());
                    break;
                case StatusUpdate.CUR_CP:
                    su.addAttribute(field, (int) getCurrentCp());
                    break;
                case StatusUpdate.MAX_CP:
                    su.addAttribute(field, getMaxCp());
                    break;
                case StatusUpdate.PVP_FLAG:
                    su.addAttribute(field, getPvpFlag());
                    break;
            }
        }
        return su;
    }

    public void broadcastStatusUpdate() {
        if (!needStatusUpdate()) {
            return;
        }

        StatusUpdate su = makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP, StatusUpdate.CUR_MP, StatusUpdate.MAX_MP);
        broadcastToStatusListeners(su);
    }

    public int calcHeading(int x_dest, int y_dest) {
        return (int) (Math.atan2(getY() - y_dest, getX() - x_dest) * HEADINGS_IN_PI) + 32768;
    }

    public final double calcStat(Stats stat, double init) {
        return calcStat(stat, init, null, null);
    }

    public final double calcStat(Stats stat, double init, Creature target, SkillEntry skill) {
        int id = stat.ordinal();
        Calculator c = _calculators[id];
        if (c == null) {
            return init;
        }
        return c.calc(this, target, skill, init);
    }

    public final double calcStat(Stats stat, Creature target, SkillEntry skill) {
        int id = stat.ordinal();
        Calculator c = _calculators[id];

        if (c != null) {
            return c.calc(this, target, skill, stat.getInit());
        }

        return stat.getInit();
    }

    public void callSkill(SkillEntry skillEntry, List<Creature> targets, boolean useActionSkills) {
        final Skill skill = skillEntry.getTemplate();
        try {
            if (useActionSkills && !skill.isUsingWhileCasting()) {
                if (skill.isOffensive()) {
                    if (skill.isMagic()) {
                        useTriggers(getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, skillEntry, 0);
                    } else {
                        useTriggers(getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, skillEntry, 0);
                    }
                } else if (skill.isMagic()) // для АоЕ, пати/клан бафов и селфов триггер накладывается на кастера
                {
                    final boolean targetSelf = skill.isAoE() || skill.isNotTargetAoE() || skill.getTargetType() == SkillTargetType.TARGET_SELF;
                    useTriggers(targetSelf ? this : getTarget(), TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, skillEntry, 0);
                }
            }

            Set<Creature> filteredTargets = targets.stream().filter(input -> ((Predicate<Creature>) creature -> {
                if (skill.isOffensive() && input.isInvul() && (!skill.isIgnoreInvul() && !skill.haveEffectsIgnoreInvul()))
                    return false;
                final Effect ie = input.getEffectList().getEffectByType(EffectType.IgnoreSkill);
                return !(ie != null && ArrayUtils.contains(ie.getTemplate().getParam().getIntegerArray("skillId"), skill.getId()));
            }).test(input)).collect(Collectors.toSet());


            Player pl = getPlayer();

            for (Creature target : filteredTargets) {
                target.getListeners().onMagicHit(skillEntry, this);

                if (pl != null) {
                    if (target.isNpc()) {
                        NpcInstance npc = (NpcInstance) target;
                        List<QuestState> ql = pl.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL);
                        if (ql != null) {
                            for (QuestState qs : ql) {
                                qs.getQuest().notifySkillUse(npc, skill, qs);
                            }
                        }
                    }
                }

                if (skill.getNegateSkill() > 0) {
                    for (Effect e : target.getEffectList().getAllEffects()) {
                        SkillEntry efs = e.getSkill();
                        if (efs.getId() == skill.getNegateSkill() && e.isCancelable() && (skill.getNegatePower() <= 0 || efs.getTemplate().getPower() <= skill.getNegatePower())) {
                            e.exit();
                        }
                    }
                }

                if (skill.getCancelTarget() > 0) {
                    if (Rnd.chance(skill.getCancelTarget())) {
                        if ((target.getCastingSkill() == null || !(target.getCastingSkill().getSkillType() == SkillType.TAKECASTLE || target.getCastingSkill().getSkillType() == SkillType.TAKEFORTRESS || target.getCastingSkill().getSkillType() == SkillType.TAKEFLAG)) && !target.isRaid()) {
                            target.abortAttack(true, true);
                            target.abortCast(true, true);
                            target.setTarget(null);
                        }
                    }
                }
            }

            if (skill.isOffensive()) {
                startAttackStanceTask();
            }

            // Применяем селфэффекты на кастера
            // Особое условие для атакующих аура-скиллов (Vengeance 368):
            // если ни одна цель не задета то селфэффекты не накладываются
            if (!(skill.isNotTargetAoE() && skill.isOffensive() && targets.isEmpty())) {
                skillEntry.getEffects(this, this, false, true);
            }

            skill.useSkill(skillEntry, this, new ArrayList<>(filteredTargets));
        } catch (Exception e) {
            _log.error("", e);
        }
    }

    public void useTriggers(GameObject target, TriggerType type, SkillEntry ex, SkillEntry owner, double damage) {
        Set<TriggerInfo> killsOnSkillAttack = triggers.get(type);
        if (killsOnSkillAttack != null) {
            killsOnSkillAttack.stream().filter(t -> t.getSkill() != ex).forEach(t -> {
                useTriggerSkill(target == null ? getTarget() : target, null, t, owner, damage);
            });
        }
    }

    public void useTriggerSkill(GameObject target, List<Creature> targets, TriggerInfo trigger, SkillEntry owner, double damage) {
        SkillEntry skillEntry = trigger.getSkill();
        Skill skill = skillEntry.getTemplate();
        if (skill.getReuseDelay() > 0 && isSkillDisabled(skillEntry)) {
            return;
        }

        Creature aimTarget = skill.getAimingTarget(this, target);
        // DS: Для шансовых скиллов с TARGET_SELF и условием "пвп" сам кастер будет являться aimTarget,
        // поэтому в условиях для триггера проверяем реальную цель.
        Creature realTarget = target != null && target.isCreature() ? (Creature) target : null;
        if (Rnd.chance(trigger.getChance()) && trigger.checkCondition(this, realTarget, aimTarget, owner, damage) && skillEntry.checkCondition(this, aimTarget, false, true, true)) {
            if (targets == null) {
                targets = skill.getTargets(this, aimTarget, false);
            }

            int displayId = 0, displayLevel = 0;

            if (skill.hasEffects()) {
                displayId = skill.getEffectTemplates()[0]._displayId;
                displayLevel = skill.getEffectTemplates()[0]._displayLevel;
            }

            if (displayId == 0) {
                displayId = skill.getDisplayId();
            }
            if (displayLevel == 0) {
                displayLevel = skill.getDisplayLevel();
            }

            if (trigger.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE) {
                for (Creature cha : targets) {
                    broadcastPacket(new MagicSkillUse(this, cha, displayId, displayLevel, 0, 0));
                }
            }

            Formulas.calcSkillMastery(skillEntry, this);
            callSkill(skillEntry, targets, false);
            disableSkill(skillEntry, skill.getReuseDelay());
            removeSkillMastery(skillEntry);
        }
    }

    public boolean checkBlockedStat(Stats stat) {
        return _blockedStats != null && _blockedStats.contains(stat);
    }

    public boolean checkReflectSkill(Creature attacker, SkillEntry skillEntry) {
        Skill skill = skillEntry.getTemplate();
        if (!skill.isReflectable()) {
            return false;
        }
        // Не отражаем, если есть неуязвимость, иначе она может отмениться
        if (isInvul() || attacker.isInvul() || !skill.isOffensive()) {
            return false;
        }
        // Из магических скилов отражаются только скилы наносящие урон по ХП.
        if (skill.isMagic() && skill.getSkillType() != SkillType.MDAM) {
            return false;
        }
        if (Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0, attacker, skillEntry))) {
            sendPacket(new SystemMessage(SystemMsg.YOU_COUNTERED_C1S_ATTACK).addName(attacker));
            attacker.sendPacket(new SystemMessage(SystemMsg.C1_DODGES_THE_ATTACK).addName(this));
            return true;
        }
        return false;
    }

    public void doCounterAttack(SkillEntry skillEntry, Creature attacker, boolean blow) {
        if (isDead()) // если персонаж уже мертв, контратаки быть не должно
        {
            return;
        }
        if (isDamageBlocked() || attacker.isDamageBlocked()) // Не контратакуем, если есть неуязвимость, иначе она может отмениться
        {
            return;
        }
        Skill skill = skillEntry == null ? null : skillEntry.getTemplate();
        if (skill == null || !skill.canCounterAttack() || skill.isMagic() || !skill.isOffensive() || skill.getCastRange() > 200) {
            return;
        }
        if (Rnd.chance(calcStat(Stats.COUNTER_ATTACK, 0, attacker, skillEntry))) {
            double damage = 1189.0d * getPAtk(attacker) / Math.max(attacker.getPDef(this), 1);
            attacker.sendPacket(new SystemMessage(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
            sendPacket(new SystemMessage(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
            for (int counterCount = blow ? 2 : 1; counterCount > 0; counterCount--) { // урон х2 для отражения blow скиллов
                if (attacker.isPlayer())
                    attacker.getPlayer().displayReceiveDamageMessage(attacker, (int) damage, 0, 0);
                attacker.reduceCurrentHp(damage, this, skillEntry, true, true, false, false, false, false, true);
            }
        }
    }

    /**
     * Disable this skill id for the duration of the delay in milliseconds.
     *
     * @param skill
     * @param delay (seconds * 1000)
     */
    public void disableSkill(SkillEntry skill, long delay) {
        if (delay > 0) {
            skillReuses.put(skill.hashCode(), new TimeStamp(skill, delay));
        }
    }

    public double getMinDistance(GameObject obj) {
        double distance = isPlayer() ? ((Player) this).getPlayerTemplateComponent().getPcCollisionBox()[0] : getTemplate().getCollisionRadius();

        if (obj != null && obj.isCreature()) {
            distance += obj.isPlayer() ? ((Player) obj).getPlayerTemplateComponent().getPcCollisionBox()[0] : ((Creature) obj).getTemplate().getCollisionRadius();
        }

        return distance;
    }

    /**
     * Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
     */
    public int calculateAttackDelay() {
        return Formulas.calcPAtkSpd(getPAtkSpd());
    }

    public abstract boolean isAutoAttackable(Creature attacker);

    public void doAttack(Creature target) {
        if (target == null || isAMuted() || isAttackingNow() || isAlikeDead() || target.isAlikeDead() || target.isDead() || !isInRange(target, 2000) || isPlayer() && getPlayer().isInRidingTransform()) {
            return;
        }

        getListeners().onAttack(target);

        // Get the Attack Speed of the creature (delay (in milliseconds) before next attack)
        int sAtk = 0;
        if (isPlayer())
            sAtk = Math.max(calculateAttackDelay(), getPlayer().getPlayerTemplateComponent().getBaseAttackSpeed());
        else
            sAtk = Math.max(calculateAttackDelay(), 333);
        int ssGrade = 0;

        WeaponTemplate weaponItem = getActiveWeaponItem();
        if (weaponItem != null) {
            if (isPlayer() && weaponItem.getAttackReuseDelay() > 0) {
                int reuse = (int) (weaponItem.getAttackReuseDelay() * getReuseModifier(target) * 666 * calcStat(Stats.ATK_BASE, 0, target, null) / 293. / getPAtkSpd());
                if (reuse > 0) {
                    sendPacket(new SetupGauge(this, SetupGauge.RED, reuse));
                    _attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
                    if (reuse > sAtk) {
                        ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT, null, null), reuse);
                    }
                }
            }

            ssGrade = weaponItem.getCrystalType().externalOrdinal;
        }

        // DS: скорректировано на 1/100 секунды поскольку AI task вызывается с небольшой погрешностью
        // особенно на слабых машинах и происходит обрыв автоатаки по isAttackingNow() == true
        _attackEndTime = sAtk + System.currentTimeMillis() - ServerConfig.ATTACK_END_DELAY;
        _isAttackAborted = false;

        Attack attack = new Attack(this, target, getChargedSoulShot(), ssGrade);
        boolean isBowAttack = false;

        setHeading(PositionUtils.calculateHeadingFrom(this, target));

        // Select the type of attack to
        if (weaponItem == null) {
            doAttackHitSimple(attack, target, 1., !isPlayer(), sAtk, true);
        } else {
            switch (weaponItem.getItemType()) {
                case BOW:
                case CROSSBOW:
                    doAttackHitByBow(attack, target, sAtk);
                    isBowAttack = true;
                    break;
                case POLE:
                    doAttackHitByPole(attack, target, sAtk);
                    break;
                case DUAL:
                case DUALFIST:
                case DUALDAGGER:
                    doAttackHitByDual(attack, target, sAtk);
                    break;
                default:
                    doAttackHitSimple(attack, target, 1., true, sAtk, true);
            }
        }

        if (attack.hasHits()) {
            if (!isBowAttack)
                ValidatePosition.correctPosition(getPlayer(), ValidatePosition.CorrectType.values()[GeodataConfig.correctType]);
            broadcastPacket(attack);
        }
    }

    private void doAttackHitSimple(Attack attack, Creature target, double multiplier, boolean unchargeSS, int sAtk, boolean notify) {
        int damage1 = 0;
        boolean shld1 = false;
        boolean crit1 = false;
        boolean miss1 = Formulas.calcHitMiss(this, target);

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack.soulshot, false);
            damage1 = (int) (info.damage * multiplier);
            shld1 = info.shld;
            crit1 = info.crit;
        }

        float hitMultiplier = 0.7f;
        if (isMonster() || isBoss() || isRaid()) {
            hitMultiplier = 0.5f;
        }
        ThreadPoolManager.getInstance().schedule(new HitTask(this, target, AttackType.CLOSE_COMBAT, damage1, crit1, miss1, attack.soulshot, shld1, unchargeSS), (long) (sAtk * hitMultiplier));
        if (notify) {
            ThreadPoolManager.getInstance().schedule(new HitEndTask(this), sAtk);
        }
        attack.addHit(target, damage1, miss1, crit1, shld1);
    }

    private void doAttackHitByBow(Attack attack, Creature target, int sAtk) {
        WeaponTemplate activeWeapon = getActiveWeaponItem();
        if (activeWeapon == null) {
            return;
        }

        int damage1 = 0;
        boolean shld1 = false;
        boolean crit1 = false;

        // Calculate if hit is missed or not
        boolean miss1 = Formulas.calcHitMiss(this, target);

        reduceArrowCount();

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack.soulshot, false);
            damage1 = (int) info.damage;
            shld1 = info.shld;
            crit1 = info.crit;

            int range = activeWeapon.getAttackRange();
            damage1 *= Math.min(range, getDistance(target)) / range * .4 + 0.8; // разброс 20% в обе стороны
        }

        float hitMultiplier = 1.0f;
        if (isMonster() || isBoss() || isRaid())
            hitMultiplier = 0.8f;
        ThreadPoolManager.getInstance().schedule(new HitTask(this, target, AttackType.BOW_OR_CROSSBOW, damage1, crit1, miss1,
                attack.soulshot, shld1, true), (long) (sAtk * hitMultiplier));
        ThreadPoolManager.getInstance().schedule(new HitEndTask(this), sAtk);
        attack.addHit(target, damage1, miss1, crit1, shld1);
    }

    private void doAttackHitByDual(Attack attack, Creature target, int sAtk) {
        int damage1 = 0;
        int damage2 = 0;
        boolean shld1 = false;
        boolean shld2 = false;
        boolean crit1 = false;
        boolean crit2 = false;

        boolean miss1 = Formulas.calcHitMiss(this, target);
        boolean miss2 = Formulas.calcHitMiss(this, target);

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack.soulshot, false);
            damage1 = (int) info.damage;
            shld1 = info.shld;
            crit1 = info.crit;
        }

        if (!miss2) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack.soulshot, false);
            damage2 = (int) info.damage;
            shld2 = info.shld;
            crit2 = info.crit;
        }

        // Create a new hit task with Medium priority for hit 1 and for hit 2 with a higher delay
        float hitMultiplier1 = 0.4f;
        float hitMultiplier2 = 0.6f;
        if (isMonster() || isBoss() || isRaid()) {
            hitMultiplier1 = 0.3f;
            hitMultiplier2 = 0.4f;
        }
        ThreadPoolManager.getInstance().schedule(new HitTask(this, target, AttackType.CLOSE_COMBAT, damage1, crit1, miss1, attack.soulshot, shld1, true), (long) (sAtk * hitMultiplier1));
        ThreadPoolManager.getInstance().schedule(new HitTask(this, target, AttackType.CLOSE_COMBAT, damage2, crit2, miss2, attack.soulshot, shld2, false), (long) (sAtk * hitMultiplier2));
        ThreadPoolManager.getInstance().schedule(new HitEndTask(this), sAtk);

        attack.addHit(target, damage1, miss1, crit1, shld1);
        attack.addHit(target, damage2, miss2, crit2, shld2);
    }

    private void doAttackHitByPole(Attack attack, Creature target, int sAtk) {
        int angle = (int) calcStat(Stats.POLE_ATTACK_ANGLE, 90, target, null);
        int range = (int) calcStat(Stats.POWER_ATTACK_RANGE, getBaseAtkRange(), target, null);

        // Используем Math.round т.к. обычный кастинг обрезает к меньшему
        // double d = 2.95. int i = (int)d, выйдет что i = 2
        // если 1% угла или 1 дистанции не играет огромной роли, то для
        // количества целей это критично
        int attackcountmax = (int) Math.round(calcStat(Stats.POLE_TARGET_COUNT, 0, target, null));

        if (isBoss()) {
            attackcountmax += 27;
        } else if (isRaid()) {
            attackcountmax += 12;
        } else if (isMonster() && getLevel() > 0) {
            attackcountmax += getLevel() / 7.5;
        }

        double mult = 1.;
        _poleAttackCount = 1;

        if (!isInZonePeace())// Гварды с пикой, будут атаковать только одиночные цели в городе
        {
            for (Creature t : getAroundCharacters(range, 200)) {
                if (_poleAttackCount <= attackcountmax) {
                    if (t == target || t.isDead() || !PositionUtils.isFacing(this, t, angle)) {
                        continue;
                    }

                    // Не флагаемся если рядом стоит флагнутый и его может задеть
                    if (t.isPlayable() ? ((Playable) t).isAttackable(this, false, true) : t.isAutoAttackable(this)) {
                        doAttackHitSimple(attack, t, mult, false, sAtk, false);
                        mult *= FormulasConfig.ALT_POLE_DAMAGE_MODIFIER;
                        _poleAttackCount++;
                    }
                } else {
                    break;
                }
            }
        }

        _poleAttackCount = 0;
        doAttackHitSimple(attack, target, 1., true, sAtk, true);
    }

    public long getAnimationEndTime() {
        return _animationEndTime;
    }

    public void doCast(SkillEntry skillEntry, Creature target, boolean forceUse) {
        doCast(skillEntry, target, forceUse, false);
    }

    public void doCast(SkillEntry skillEntry, Creature target, boolean forceUse, boolean regular) {
        if (skillEntry == null) {
            return;
        }

        Skill skill = skillEntry.getTemplate();

        if (skill.getReferenceItemId() > 0) {
            if (!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume())) {
                return;
            }
        }

        int magicId = skill.getId();

        if (target == null) {
            target = skill.getAimingTarget(this, getTarget());
        }
        if (target == null) {
            return;
        }

        getListeners().onMagicUse(skillEntry, target, false);

        if (target != this) {
            setHeading(PositionUtils.calculateHeadingFrom(this, target));
        }

        int level = Math.max(1, getSkillDisplayLevel(magicId));

        int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime() : Formulas.calcMAtkSpd(this, skill, skill.getHitTime());
        int skillInterruptTime = skill.isMagic() ? Formulas.calcMAtkSpd(this, skill, skill.getSkillInterruptTime()) : 0;

        int minCastTime = Math.min(FormulasConfig.SKILLS_CAST_TIME_MIN, skill.getHitTime());
        if (skillTime < minCastTime) {
            skillTime = minCastTime;
            skillInterruptTime = 0;
        }

        _animationEndTime = System.currentTimeMillis() + skillTime;

        if (skill.isMagic() && !skill.isSkillTimePermanent() && getChargedSpiritShot() > 0
                && (!regular || AllSettingsConfig.regularSpiritShotCastBonus)) {
            skillTime = (int) (0.70 * skillTime);
            skillInterruptTime = (int) (0.70 * skillInterruptTime);
        }

        Formulas.calcSkillMastery(skillEntry, this); // Calculate skill mastery for current cast
        long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skillEntry));

        broadcastPacket(new MagicSkillUse(this, target, skill.getDisplayId(), level, skillTime, reuseDelay));

        if (!skill.isHandler()) {
            disableSkill(skillEntry, reuseDelay);
        }

        if (isPlayer()) {
            if (skill.getSkillType() == SkillType.PET_SUMMON) {
                sendPacket(SystemMsg.SUMMONING_YOUR_PET);
            } else if (skill.getItemConsumeId().length == 0 || !skill.isHandler()) {
                sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
            } else if (skill.getHitTime() > 0) {
                sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addItemName(skill.getItemConsumeId()[0]));
            } else {
                sendPacket(new SystemMessage(SystemMsg.USE_S1).addItemName(skill.getItemConsumeId()[0]));
            }
        }

        int[] itemConsume = skill.getItemConsume();

        if (itemConsume.length > 0) {
            for (int i = 0; i < itemConsume.length; i++) {
                if (!consumeItem(skill.getItemConsumeId()[i], itemConsume[i])) {
                    sendPacket(skill.isHandler() ? SystemMsg.NOT_ENOUGH_ITEMS : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                    return;
                }
            }
        }

        if (skill.getTargetType() == SkillTargetType.TARGET_HOLY) {
            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);
        }

        double mpConsume1 = skill.isUsingWhileCasting() ? skill.getMpConsume() : skill.getMpConsume1();
        if (mpConsume1 > 0) {
            if (_currentMp < mpConsume1) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                onCastEndTime(skillEntry);
                return;
            }
            reduceCurrentMp(mpConsume1, null);
        }

        _flyLoc = null;
        switch (skill.getFlyType()) {
            case DUMMY:
                if (getFlyLocation(target, skillEntry) == null) {
                    sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return;
                }
                break;
            case CHARGE:
                _flyLoc = getFlyLocation(target, skillEntry);
                if (_flyLoc != null) {
                    broadcastPacket(new FlyToLocation(this, _flyLoc, skill.getFlyType()));
                } else {
                    sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return;
                }
        }

        _castingSkill = skillEntry;
        _castInterruptTime = System.currentTimeMillis() + skillInterruptTime;
        setCastingTarget(target);

        if (skill.isUsingWhileCasting()) {
            callSkill(skillEntry, skill.getTargets(this, target, forceUse), true);
        }

        if (isPlayer()) {
            sendPacket(new SetupGauge(this, SetupGauge.BLUE, skillTime));
        }

        _scheduledCastCount = skill.getCastCount();
        _scheduledCastInterval = skill.getCastCount() > 0 ? skillTime / _scheduledCastCount : skillTime;

        // Create a task MagicUseTask with Medium priority to launch the MagicSkill at the end of the casting time
        _skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse), skillInterruptTime);
        _skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse), skill.getCastCount() > 0 ? skillTime / skill.getCastCount() : skillTime);

        _skillGeoCheckTask = null;
        if (skill.getCastRange() < 32767 && skill.getSkillType() != SkillType.TAKECASTLE && skill.getSkillType() != SkillType.TAKEFORTRESS && _scheduledCastInterval > 600)
            _skillGeoCheckTask = ThreadPoolManager.getInstance().schedule(new MagicGeoCheckTask(this), (long) (_scheduledCastInterval * 0.5));
    }

    public Location getFlyLocation(GameObject target, SkillEntry skillEntry) {
        Skill skill = skillEntry.getTemplate();
        if (target != null && target != this) {
            Location loc;

            double radian = PositionUtils.convertHeadingToRadian(target.getHeading());
            if (skill.isFlyToBack()) {
                loc = new Location(target.getX() + (int) (Math.sin(radian) * 40), target.getY() - (int) (Math.cos(radian) * 40), target.getZ());
            } else {
                loc = new Location(target.getX() - (int) (Math.sin(radian) * 40), target.getY() + (int) (Math.cos(radian) * 40), target.getZ());
            }

            if (isFlying()) {
                if (isPlayer() && ((Player) this).isInFlyingTransform() && (loc.z <= 0 || loc.z >= 6000)) {
                    return null;
                }
                if (GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getColRadius(), getGeoIndex()) == null) {
                    return null;
                }
            } else {
                loc.correctGeoZ();

                if (!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex())) {
                    loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
                    if (!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex())) {
                        final List<Location> flyList = GeoEngine.MoveList(getX(), getY(), getZ(), loc.x, loc.y, getGeoIndex(), false);
                        return flyList.isEmpty() ? null : flyList.get(flyList.size() - 1).geo2world();
                    }
                }
            }

            return loc;
        }

        double radian = PositionUtils.convertHeadingToRadian(getHeading());
        int x1 = -(int) (Math.sin(radian) * skill.getFlyRadius());
        int y1 = (int) (Math.cos(radian) * skill.getFlyRadius());

        if (isFlying())
            return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ(), getColRadius(), getGeoIndex());
        else
            return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, getGeoIndex());
    }

    public final void doDie(Creature killer) {
        // killing is only possible one time
        if (!isDead.compareAndSet(false, true)) {
            return;
        }
        World.getAroundServitors(this).stream().filter(servitor -> !servitor.isDead() && servitor.getAI().getAttackTarget() != null).filter(servitor -> servitor.getAI().getAttackTarget() == this && servitor.isFollowMode()).forEach(org.mmocore.gameserver.object.Servitor::moveToOwner);

        onDeath(killer);
    }

    protected void onDeath(Creature killer) {
        if (killer != null) {
            Player killerPlayer = killer.getPlayer();
            if (killerPlayer != null) {
                killerPlayer.getListeners().onKillIgnorePetOrSummon(this);
            }

            killer.getListeners().onKill(this);

            if (isPlayer() && killer.isPlayable()) {
                _currentCp = 0;
            }
        }

        setTarget(null);
        stopMove();
        stopRegeneration();

        currentHp = 0;

        // Stop all active skills effects in progress on the L2Character
        if (isBlessedByNoblesse() || isSalvation()) {
            if (isSalvation() && isPlayer() && !getPlayer().isInOlympiadMode()) {
                getPlayer().reviveRequest(getPlayer(), 100, false);
            }
            for (Effect e : getEffectList().getAllEffects())
            // Noblesse Blessing Buff/debuff effects are retained after
            // death. However, Noblesse Blessing and Lucky Charm are lost as normal.
            {
                if (e.getEffectType() == EffectType.BlessNoblesse || e.getSkill().getId() == Skill.SKILL_FORTUNE_OF_NOBLESSE || e.getSkill().getId() == Skill.SKILL_RAID_BLESSING) {
                    e.exit();
                } else if (e.getEffectType() == EffectType.AgathionResurrect) {
                    if (isPlayer()) {
                        getPlayer().setAgathionRes(true);
                    }
                    e.exit();
                }
            }
        } else {
            getEffectList().getAllEffects().stream().filter(e -> (!e.getSkill().getTemplate().isPreservedOnDeath() && !e.getSkill().getTemplate().isFlyingTransform())).forEach(Effect::exit);
        }

        ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer, null));

        getListeners().onDeath(killer);

        updateEffectIcons();
        updateStats();
        broadcastStatusUpdate();
    }

    protected void onRevive() {

    }

    public void enableSkill(SkillEntry skill) {
        skillReuses.remove(skill.hashCode());
    }

    public Optional<TimeStamp> removeSkillReuse(SkillEntry skill) {
        return Optional.ofNullable(skillReuses.remove(skill.hashCode()));
    }

    public Map<Integer, TimeStamp> getSkillsReuse() {
        return skillReuses;
    }

    public int getAbnormalEffect(AbnormalEffectType t) {
        return _abnormalEffects[t.ordinal()];
    }

    public Map<Integer, SkillEntry> getAllMapSkills() {
        return skills;
    }

    /**
     * Возвращает коллекцию скиллов для быстрого перебора
     */
    public Collection<SkillEntry> getAllSkills() {
        return skills.values();
    }

    /**
     * Возвращает массив скиллов для безопасного перебора
     */
    public final SkillEntry[] getAllSkillsArray() {
        Collection<SkillEntry> vals = getAllSkills();
        return vals.toArray(new SkillEntry[vals.size()]);
    }

    public final double getAttackSpeedMultiplier() {
        return 1.1 * getPAtkSpd() / (isPlayer() ? getPlayer().getPlayerTemplateComponent().getBaseAttackSpeed() : getTemplate().getBasePAtkSpd());
    }

    public int getBuffLimit() {
        return (int) calcStat(Stats.BUFF_LIMIT, AllSettingsConfig.ALT_BUFF_LIMIT, null, null);
    }

    public SkillEntry getCastingSkill() {
        return _castingSkill;
    }

    /**
     * Возвращает шанс физического крита (1000 == 100%)
     */
    public int getCriticalHit(Creature target, SkillEntry skill) {
        return (int) calcStat(Stats.CRITICAL_BASE, _template.getBaseCritRate(), target, skill);
    }

    /**
     * Возвращает шанс магического крита в процентах
     */
    public double getMagicCriticalRate(Creature target, SkillEntry skill) {
        return calcStat(Stats.MCRITICAL_RATE, target, skill);
    }

    public int getAccuracy() {
        return (int) calcStat(Stats.ACCURACY_COMBAT, 0, null, null);
    }

    public int getEvasionRate(final Creature target) {
        return (int) calcStat(Stats.EVASION_RATE, 0, target, null);
    }

    public int getINT() {
        return (int) calcStat(Stats.STAT_INT, _template.getBaseAttr().getINT(), null, null);
    }

    public int getSTR() {
        return (int) calcStat(Stats.STAT_STR, _template.getBaseAttr().getSTR(), null, null);
    }

    public int getCON() {
        return (int) calcStat(Stats.STAT_CON, _template.getBaseAttr().getCON(), null, null);
    }

    public int getMEN() {
        return (int) calcStat(Stats.STAT_MEN, _template.getBaseAttr().getMEN(), null, null);
    }

    public int getDEX() {
        return (int) calcStat(Stats.STAT_DEX, _template.getBaseAttr().getDEX(), null, null);
    }

    public int getWIT() {
        return (int) calcStat(Stats.STAT_WIT, _template.getBaseAttr().getWIT(), null, null);
    }

    /**
     * Return the current CP of the L2Character.
     */
    public final double getCurrentCp() {
        return _currentCp;
    }

    public final void setCurrentCp(double newCp) {
        setCurrentCp(newCp, true);
    }

    public final double getCurrentCpRatio() {
        return getCurrentCp() / getMaxCp();
    }

    public final double getCurrentCpPercents() {
        return getCurrentCpRatio() * 100.;
    }

    public final boolean isCurrentCpFull() {
        return getCurrentCp() >= getMaxCp();
    }

    public final boolean isCurrentCpZero() {
        return getCurrentCp() < 1;
    }

    public final double getCurrentHp() {
        return currentHp;
    }

    public final double getCurrentHpRatio() {
        return getCurrentHp() / getMaxHp();
    }

    public final double getCurrentHpPercents() {
        return getCurrentHpRatio() * 100.;
    }

    public final boolean isCurrentHpFull() {
        return getCurrentHp() >= getMaxHp();
    }

    public final boolean isCurrentHpZero() {
        return getCurrentHp() < 1;
    }

    public final double getCurrentMp() {
        return _currentMp;
    }

    public final void setCurrentMp(double newMp) {
        setCurrentMp(newMp, true);
    }

    public final double getCurrentMpRatio() {
        return getCurrentMp() / getMaxMp();
    }

    public final double getCurrentMpPercents() {
        return getCurrentMpRatio() * 100.;
    }

    public final boolean isCurrentMpFull() {
        return getCurrentMp() >= getMaxMp();
    }

    public final boolean isCurrentMpZero() {
        return getCurrentMp() < 1;
    }

    public Location getDestination() {
        return destination;
    }

    public List<Creature> getAroundCharacters(int radius, int height) {
        if (!isVisible()) {
            return Collections.emptyList();
        }
        return World.getAroundCharacters(this, radius, height);
    }

    public List<NpcInstance> getAroundNpc(int range, int height) {
        if (!isVisible()) {
            return Collections.emptyList();
        }
        return World.getAroundNpc(this, range, height);
    }

    public boolean knowsObject(GameObject obj) {
        return World.getAroundObjectById(this, obj.getObjectId()) != null;
    }

    public final SkillEntry getKnownSkill(int skillId) {
        return getAllMapSkills().get(skillId);
    }

    public final int getMagicalAttackRange(SkillEntry skill) {
        if (skill != null) {
            return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getTemplate().getCastRange(), null, skill);
        }
        return getBaseAtkRange();
    }

    public int getMAtk(Creature target, SkillEntry skill) {
        if (skill != null && skill.getTemplate().getMatak() > 0) {
            return skill.getTemplate().getMatak();
        }
        return (int) calcStat(Stats.MAGIC_ATTACK, _template.getBaseMAtk(), target, skill);
    }

    public int getMAtkSpd() {
        return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, _template.getBaseMAtkSpd(), null, null);
    }

    public int getMaxCp() {
        return (int) calcStat(Stats.MAX_CP, _template.getBaseCpMax(), null, null);
    }

    public double getMaxHp() {
        return calcStat(Stats.MAX_HP, _template.getBaseHpMax(), null, null);
    }

    public int getMaxMp() {
        return (int) calcStat(Stats.MAX_MP, _template.getBaseMpMax(), null, null);
    }

    public int getMDef(Creature target, SkillEntry skill) {
        return Math.max((int) calcStat(Stats.MAGIC_DEFENCE, _template.getBaseMDef(), target, skill), 1);
    }

    public double getHpRegen() {
        return calcStat(Stats.REGENERATE_HP_RATE, getTemplate().getBaseHpReg());
    }

    public double getMpRegen() {
        return calcStat(Stats.REGENERATE_MP_RATE, getTemplate().getBaseMpReg());
    }

    public double getCpRegen() {
        return 0.0D;
    }

    public double getMovementSpeedMultiplier() {
        if (isRunning())
            return getRunSpeed() * 1. / _template.getBaseRunSpd();
        return getWalkSpeed() * 1. / _template.getBaseWalkSpd();
    }

    @Override
    public int getMoveSpeed() {
        if (isRunning()) {
            return getRunSpeed();
        }

        return getWalkSpeed();
    }

    @Override
    public String getName() {
        return StringUtils.defaultString(_name);
    }

    public final void setName(String name) {
        _name = name;
    }

    public int getPAtk(Creature target) {
        return (int) calcStat(Stats.POWER_ATTACK, _template.getBasePAtk(), target, null);
    }

    public int getPAtkSpd() {
        return (int) calcStat(Stats.POWER_ATTACK_SPEED, _template.getBasePAtkSpd(), null, null);
    }

    public int getPDef(Creature target) {
        return (int) calcStat(Stats.POWER_DEFENCE, _template.getBasePDef(), target, null);
    }

    public int getBaseAtkRange() {
        return getTemplate().getBaseAtkRange();
    }

    public final int getPhysicalAttackRange() {
        return (int) calcStat(Stats.POWER_ATTACK_RANGE, getBaseAtkRange(), null, null);
    }

    public int getRandomDamage() {
        WeaponTemplate weaponItem = getActiveWeaponItem();
        if (weaponItem == null) {
            return 5 + (int) Math.sqrt(getLevel());
        }
        return weaponItem.getRandomDamage();
    }

    public double getReuseModifier(Creature target) {
        return calcStat(Stats.ATK_REUSE, 1, target, null);
    }

    public final int getShldDef() {
        if (isPlayer()) {
            return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
        }
        return (int) calcStat(Stats.SHIELD_DEFENCE, _template.getBaseShldDef(), null, null);
    }

    public final int getSkillDisplayLevel(int skillId) {
        SkillEntry skill = getAllMapSkills().get(skillId);
        if (skill == null) {
            return -1;
        }
        return skill.getDisplayLevel();
    }

    public final int getSkillLevel(int skillId) {
        return getSkillLevel(skillId, -1);
    }

    public final int getSkillLevel(int skillId, int def) {
        SkillEntry skill = getAllMapSkills().get(skillId);
        if (skill == null) {
            return def;
        }
        return skill.getLevel();
    }

    public SkillMastery getSkillMastery(Skill skill) {
        return skill.getId() == _skillMasteryId ? skill.getDefaultSkillMastery() : SkillMastery.NONE;
    }

    public void removeSkillMastery(SkillEntry skill) {
        if (skill.getId() == _skillMasteryId) {
            _skillMasteryId = 0;
        }
    }

    public int getSpeed(int baseSpeed) {
        if (isInWater()) {
            if (isRunning())
                return getSwimRunSpeed();
            else
                return getSwimWalkSpeed();
        }
        return (int) calcStat(Stats.RUN_SPEED, baseSpeed, null, null);
    }

    public int getRunSpeed() {
        return getSpeed(_template.getBaseRunSpd());
    }

    public int getWalkSpeed() {
        return getSpeed(_template.getBaseWalkSpd());
    }

    public int getSwimRunSpeed() {
        return (int) calcStat(Stats.RUN_SPEED, _template.getBaseWaterRunSpd(), null, null);
    }

    public int getSwimWalkSpeed() {
        return (int) calcStat(Stats.RUN_SPEED, _template.getBaseWaterWalkSpd(), null, null);
    }

    public GameObject getTarget() {
        return target.get();
    }

    public void setTarget(GameObject object) {
        if (object != null && !object.isVisible()) {
            object = null;
        }

		/* DS: на оффе сброс текущей цели не отменяет атаку или каст.
		if(object == null)
		{
			if(isAttackingNow() && getAI().getAttackTarget() == getTarget())
				abortAttack(false, true);
			if(isCastingNow() && getAI().getAttackTarget() == getTarget())
				abortCast(false, true);
		}
		*/

        if (object == null) {
            target = HardReferences.emptyRef();
        } else {
            target = object.getRef();
        }
    }

    public final int getTargetId() {
        GameObject target = getTarget();
        return target == null ? -1 : target.getObjectId();
    }

    public CharTemplate getTemplate() {
        return _template;
    }

    public void setTemplate(CharTemplate template) {
        _template = template;
    }

    public CharTemplate getBaseTemplate() {
        return _baseTemplate;
    }

    public String getTitle() {
        return StringUtils.defaultString(_title);
    }

    public void setTitle(String title) {
        _title = title;
    }

    public double headingToRadians(int heading) {
        return (heading - 32768) / HEADINGS_IN_PI;
    }

    public boolean isAlikeDead() {
        return _fakeDeath || isDead();
    }

    public boolean isAttackingNow() {
        return _attackEndTime > System.currentTimeMillis();
    }

    public final boolean isBlessedByNoblesse() {
        return _isBlessedByNoblesse;
    }

    public final boolean isSalvation() {
        return _isSalvation;
    }

    public boolean isEffectImmune() {
        return _effectImmunity.get();
    }

    public boolean isBuffImmune() {
        return _buffImmunity.get();
    }

    public boolean isDebuffImmune() {
        return _debuffImmunity.get();
    }

    public boolean isDead() {
        return currentHp < 0.5 || isDead.get();
    }

    @Override
    public final boolean isFlying() {
        return _flying;
    }

    public final void setFlying(boolean mode) {
        _flying = mode;
        if (mode) {
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_AIR));
        } else {
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_GROUND));
        }
    }

    /**
     * Находится ли персонаж в боевой позе
     *
     * @return true, если персонаж в боевой позе, атакован или атакует
     */
    public final boolean isInCombat() {
        return System.currentTimeMillis() < _stanceEndTime;
    }

    public boolean isInvul() {
        return _isInvul;
    }

    public boolean isMageClass() {
        return getTemplate().getBaseMAtk() > 3;
    }

    public final boolean isRunning() {
        return _running;
    }

    public boolean isSkillDisabled(SkillEntry skill) {
        TimeStamp sts = skillReuses.get(skill.hashCode());
        if (sts == null) {
            return false;
        }
        if (sts.hasNotPassed()) {
            return true;
        }
        skillReuses.remove(skill.hashCode());
        return false;
    }

    public final boolean isTeleporting() {
        return isTeleporting.get();
    }

    /**
     * Возвращает позицию цели, в которой она будет через пол секунды.
     */
    public Location getIntersectionPoint(Creature target) {
        if (!PositionUtils.isFacing(this, target, 90)) {
            return new Location(target.getX(), target.getY(), target.getZ());
        }
        double angle = PositionUtils.convertHeadingToDegree(target.getHeading()); // угол в градусах
        double radian = Math.toRadians(angle - 90); // угол в радианах
        double range = target.getMoveSpeed() / 2.0d; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
        return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
    }

    public Location applyOffset(Location point, int offset) {
        if (offset <= 0) {
            return point;
        }

        long dx = point.x - getX();
        long dy = point.y - getY();
        long dz = point.z - getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance <= offset) {
            point.set(getX(), getY(), getZ());
            return point;
        }

        if (distance >= 1) {
            double cut = offset / distance;
            point.x -= (int) (dx * cut + 0.5);
            point.y -= (int) (dy * cut + 0.5);
            point.z -= (int) (dz * cut + 0.5);

            if (!isFlying() && !isInBoat() && !isInWater() && !isBoat()) {
                point.correctGeoZ();
            }
        }

        return point;
    }

    public List<Location> applyOffset(List<Location> points, int offset) {
        offset >>= 4;
        if (offset <= 0) {
            return points;
        }

        long dx = points.get(points.size() - 1).x - points.get(0).x;
        long dy = points.get(points.size() - 1).y - points.get(0).y;
        long dz = points.get(points.size() - 1).z - points.get(0).z;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= offset) {
            Location point = points.get(0);
            points.clear();
            points.add(point);
            return points;
        }

        if (distance >= 1) {
            double cut = offset / distance;
            int num = (int) (points.size() * cut + 0.5);
            for (int i = 1; i <= num && !points.isEmpty(); i++) {
                points.remove(points.size() - 1);
            }
        }

        return points;
    }

    private boolean setSimplePath(Location dest) {
        List<Location> moveList = GeoMove.constructMoveList(getLoc(), dest);
        if (moveList.isEmpty()) {
            return false;
        }
        _targetRecorder.clear();
        _targetRecorder.add(moveList);
        return true;
    }

    private boolean buildPathTo(int x, int y, int z, int offset, boolean pathFind) {
        return buildPathTo(x, y, z, offset, null, false, pathFind);
    }

    private boolean buildPathTo(int x, int y, int z, int offset, Creature follow, boolean forestalling, boolean pathFind) {
        int geoIndex = getGeoIndex();

        Location dest;

        if (forestalling && follow != null && follow.isMoving) {
            dest = getIntersectionPoint(follow);
        } else {
            dest = new Location(x, y, z);
        }

        if (isInBoat() || isBoat() || !GeodataConfig.ALLOW_GEODATA) {
            applyOffset(dest, offset);
            return setSimplePath(dest);
        }

        if (isFlying() || isInWater()) {
            applyOffset(dest, offset);

            Location nextloc;

            if (isFlying()) {
                if (GeoEngine.canSeeCoord(this, dest.x, dest.y, dest.z, true)) {
                    return setSimplePath(dest);
                }

                // DS: При передвижении обсервера клавишами клиент шлет очень далекие (дистанция больше 2000) координаты,
                // поэтому обычная процедура проверки не работает. Используем имитацию плавания в воде.
                if (isObservePoint()) {
                    nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, 15000, geoIndex);
                } else {
                    nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
                }
                if (nextloc != null && !nextloc.equals(getX(), getY(), getZ())) {
                    return setSimplePath(nextloc);
                }
            } else {
                int waterZ = getWaterZ();
                nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, waterZ, geoIndex);
                if (nextloc == null)
                    return false;

                int waterBottomZ = getWaterBottomZ();
                if (dest.z < waterBottomZ) // ниже дна не ныряем
                {
                    return false;
                }

                List<Location> moveList = GeoMove.constructMoveList(getLoc(), nextloc.clone());
                _targetRecorder.clear();
                if (!moveList.isEmpty()) {
                    _targetRecorder.add(moveList);
                }

                int dz = dest.z - nextloc.z;
                // если пытаемся выбратся на берег, считаем путь с точки выхода до точки назначения
                if (dz > 0 && dz < 128) {
                    moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
                    if (moveList != null) // null - до конца пути дойти нельзя
                    {
                        if (!moveList.isEmpty()) // уже стоим на нужной клетке
                        {
                            _targetRecorder.add(moveList);
                        }
                    }
                }

                return !_targetRecorder.isEmpty();
            }
            return false;
        }

        List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, true); // onlyFullPath = true - проверяем весь путь до конца
        if (moveList != null) // null - до конца пути дойти нельзя
        {
            if (moveList.isEmpty()) // уже стоим на нужной клетке
            {
                return false;
            }
            applyOffset(moveList, offset);
            if (moveList.isEmpty()) // уже стоим на нужной клетке
            {
                return false;
            }
            _targetRecorder.clear();
            _targetRecorder.add(moveList);
            return true;
        }

        if (pathFind) {
            final List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.getX(), dest.getY(), dest.getZ(), this, geoIndex);
            if (!targets.isEmpty()) {
                moveList = targets.remove(targets.size() - 1);
                applyOffset(moveList, offset);
                if (!moveList.isEmpty()) {
                    targets.add(moveList);
                }
                if (!targets.isEmpty()) {
                    _targetRecorder.clear();
                    _targetRecorder.addAll(targets);
                    return true;
                }
            }
        }

        if (follow != null) // DS: возможны застревания в режиме следования
        {
            return false;
        }

        if (isPlayable()) // расчитываем путь куда сможем дойти, только для игровых персонажей
        {
            applyOffset(dest, offset);

            moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, false); // onlyFullPath = false - идем до куда можем
            if (moveList != null && !moveList.isEmpty()) // null - нет геодаты, empty - уже стоим на нужной клетке
            {
                _targetRecorder.clear();

                // При движении за персонажем (режим follow), есть возможность застревания персонажа (если пачнод отключен),
                // после того, как тот доходит до препятствия. В итоге овнер будет бесконечно направлятся на одну и ту же точку (текущую).
                if (moveList.size() == 1 && moveList.get(0).equals(getLoc().world2geo())) {
                    return false;
                }

                _targetRecorder.add(moveList);
                return true;
            }
        }

        return false;
    }

    public Creature getFollowTarget() {
        return followTarget.get();
    }

    public void setFollowTarget(Creature target) {
        followTarget = target == null ? HardReferences.<Creature>emptyRef() : target.getRef();
    }

    public boolean followToCharacter(Creature target, int offset, boolean forestalling) {
        return followToCharacter(target.getLoc(), target, offset, forestalling);
    }

    public boolean followToCharacter(Location loc, Creature target, int offset, boolean forestalling) {
        moveLock.lock();
        try {
            if (isMovementDisabled() || target == null || isInBoat()) {
                return false;
            }

            offset = Math.max(offset, 10);
            if (isFollow && target == getFollowTarget() && offset == _offset) {
                return true;
            }

            if (Math.abs(getZ() - target.getZ()) > 1000 && !isFlying()) {
                sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                return false;
            }

            getAI().clearNextAction();

            stopMove(false, false);

//			validateLocation(0); // при каждой остановке сверяем координаты с клиентом

            // Маленькое пояснение - если мы кастуем скил или пытаемся атаковать игрока, но в это
            // время он находится за препятствием - мы не должны оббегать его, просто останавливаемся перед ним.
            //boolean pathFind = !target.isDoor() && getAI().getIntention() != AI_INTENTION_CAST;
            //pathFind = pathFind && (!isPlayer() || getAI().getIntention() != AI_INTENTION_ATTACK);
            if (buildPathTo(loc.x, loc.y, loc.z, offset, target, forestalling, !target.isDoor() && getAI().getIntention() != AI_INTENTION_CAST))
                movingDestTempPos.set(loc.x, loc.y, loc.z);
            else {
                getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, getLoc(), null);
                return false;
            }

            isMoving = true;
            isFollow = true;
            _forestalling = forestalling;
            _offset = offset;
            setFollowTarget(target);

            moveNext(true);

            return true;
        } finally {
            moveLock.unlock();
        }
    }

    public boolean moveToLocation(Location loc, int offset, boolean pathfinding) {
        return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding);
    }

    public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding) {
        moveLock.lock();
        try {
            offset = Math.max(offset, 0);
            Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
            if (isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc)) {
                sendActionFailed();
                return true;
            }

            if (isMovementDisabled()) {
                getAI().setNextAction(NextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
                sendActionFailed();
                return false;
            }

            try {
                getAI().clearNextAction();
            } catch (NullPointerException e) {
                _log.error("ai is null for npc {}, {}.", getNpcId(), getName());
            }

            if (isPlayer()) {
                getAI().changeIntention(AI_INTENTION_ACTIVE, null, null);
            }

            stopMove(false, false);

            if (buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding)) {
                movingDestTempPos.set(dst_geoloc);
            } else {
                sendActionFailed();
                return false;
            }

            isMoving = true;

            moveNext(true);

            return true;
        } finally {
            moveLock.unlock();
        }
    }

    private void moveNext(boolean firstMove) {
        if (!isMoving || isMovementDisabled()) {
            stopMove();
            return;
        }

        _previousSpeed = getMoveSpeed();
        if (_previousSpeed <= 0) {
            stopMove();
            return;
        }

        if (!firstMove) {
            Location dest = destination;
            if (dest != null) {
                setLoc(dest, true);
            }
        }

        if (_targetRecorder.isEmpty()) {
            CtrlEvent ctrlEvent = isFollow ? CtrlEvent.EVT_ARRIVED_TARGET : CtrlEvent.EVT_ARRIVED;
            stopMove(false, true); // рассылка StopMove для предотвращения "залипания" при преследовании
            ThreadPoolManager.getInstance().execute(new NotifyAITask(this, ctrlEvent));
            return;
        }

        moveList = _targetRecorder.remove(0);
        Location begin = moveList.get(0).clone().geo2world();
        Location end = moveList.get(moveList.size() - 1).clone().geo2world();
        destination = end;
        double distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end); //клиент при передвижении не учитывает поверхность

        if (distance != 0) {
            setHeading(PositionUtils.calculateHeadingFrom(getX(), getY(), destination.x, destination.y));
        }

        broadcastMove();

        _startMoveTime = _followTimestamp = System.currentTimeMillis();
        if (_moveTaskRunnable == null) {
            _moveTaskRunnable = new MoveNextTask();
        }
        _moveTask = ThreadPoolManager.getInstance().schedule(_moveTaskRunnable.setDist(distance), getMoveTickInterval());
    }

    public int getMoveTickInterval() {
        return (isPlayer() ? 16000 : 32000) / Math.max(getMoveSpeed(), 1);
    }

    public void broadcastStopMove() {
        broadcastPacket(stopMovePacket());
    }

    private void broadcastMove() {
        validateLocation(isPlayer() ? 2 : 1);
        broadcastPacket(movePacket());
    }

    /**
     * Останавливает движение и рассылает StopMove, ValidateLocation
     */
    public void stopMove() {
        stopMove(true, true);
    }

    /**
     * Останавливает движение и рассылает StopMove
     *
     * @param validate - рассылать ли ValidateLocation
     */
    public void stopMove(boolean validate) {
        stopMove(true, validate);
    }

    /**
     * Останавливает движение
     *
     * @param stop     - рассылать ли StopMove
     * @param validate - рассылать ли ValidateLocation
     */
    public void stopMove(boolean stop, boolean validate) {
        if (!isMoving) {
            return;
        }

        moveLock.lock();
        try {
            if (!isMoving) {
                return;
            }

            isMoving = false;
            isFollow = false;

            if (_moveTask != null) {
                _moveTask.cancel(false);
                _moveTask = null;
            }

            destination = null;
            moveList = null;

            _targetRecorder.clear();

            if (validate) {
                validateLocation(isPlayer() ? 2 : 1);
            }
            if (stop) {
                broadcastPacket(stopMovePacket());
            }
        } finally {
            moveLock.unlock();
        }
    }

    /**
     * Возвращает координаты поверхности воды, если мы находимся в ней, или над ней.
     */
    public int getWaterZ() {
        if (!isInWater()) {
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_GROUND));
            return Integer.MIN_VALUE;
        }

        int waterZ = Integer.MIN_VALUE;
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getType() == ZoneType.water) {
                    if (waterZ == Integer.MIN_VALUE || waterZ < zone.getTerritory().getZmax()) {
                        waterZ = zone.getTerritory().getZmax();
                    }
                }
            }
        } finally {
            zonesRead.unlock();
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_UNDERWATER));
        }

        return waterZ;
    }

    /**
     * Возвращает координаты "дна" водяной зоны
     */
    public int getWaterBottomZ() {
        if (!isInWater())
            return Integer.MAX_VALUE;

        int waterBottomZ = Integer.MAX_VALUE;
        zonesRead.lock();
        try {
            Zone zone;
            for (int i = 0; i < _zones.size(); i++) {
                zone = _zones.get(i);
                if (zone.getType() == ZoneType.water)
                    if (waterBottomZ == Integer.MAX_VALUE || waterBottomZ > zone.getTerritory().getZmin())
                        waterBottomZ = zone.getTerritory().getZmin();
            }
        } finally {
            zonesRead.unlock();
        }

        return waterBottomZ;
    }

    protected L2GameServerPacket stopMovePacket() {
        return new StopMove(this);
    }

    public L2GameServerPacket movePacket() {
        return new CharMoveToLocation(this);
    }

    public void updateZones() {
        Zone[] zones = isVisible() ? getCurrentRegion().getZones() : Zone.EMPTY_ZONE_ARRAY;

        List<Zone> entering = null;
        List<Zone> leaving = null;

        zonesWrite.lock();
        try {
            if (!_zones.isEmpty()) {
                leaving = new LazyArrayList<>(_zones.size());
                for (Zone _zone : _zones) {
                    // зоны больше нет в регионе, либо вышли за территорию зоны
                    if (!ArrayUtils.contains(zones, _zone) || !_zone.checkIfInZone(getX(), getY(), getZ(), getReflection())) {
                        leaving.add(_zone);
                    }
                }

                //Покинули зоны, убираем из списка зон персонажа
                if (!leaving.isEmpty()) {
                    leaving.forEach(_zones::remove);
                }
            }

            if (zones.length > 0) {
                entering = new LazyArrayList<>(zones.length);
                for (Zone zone1 : zones) {
                    // в зону еще не заходили и зашли на территорию зоны
                    if (!_zones.contains(zone1) && zone1.checkIfInZone(getX(), getY(), getZ(), getReflection())) {
                        entering.add(zone1);
                    }
                }

                //Вошли в зоны, добавим в список зон персонажа
                if (!entering.isEmpty()) {
                    _zones.addAll(entering.stream().collect(Collectors.toList()));
                }
            }
        } finally {
            zonesWrite.unlock();
        }

        onUpdateZones(leaving, entering);

        if (leaving != null) {
            leaving.clear();
        }

        if (entering != null) {
            entering.clear();
        }

    }

    protected void onUpdateZones(List<Zone> leaving, List<Zone> entering) {
        Zone zone;

        if (leaving != null && !leaving.isEmpty()) {
            for (Zone aLeaving : leaving) {
                zone = aLeaving;
                zone.doLeave(this);
            }
        }

        if (entering != null && !entering.isEmpty()) {
            for (Zone anEntering : entering) {
                zone = anEntering;
                zone.doEnter(this);
            }
        }
    }

    public boolean isInZonePeace() {
        return isInZone(ZoneType.peace_zone) && !isInZoneBattle();
    }

    public boolean isInZoneBattle() {
        return isInZone(ZoneType.battle_zone);
    }

    public boolean isInWater() {
        return isInZone(ZoneType.water) && !(isInBoat() || isBoat() || isFlying());
    }

    public boolean isInZone(ZoneType type) {
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getType() == type) {
                    return true;
                }
            }
        } finally {
            zonesRead.unlock();
        }

        return false;
    }

    public List<Event> getZoneEvents() {
        List<Event> e = Collections.emptyList();
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (!zone.getEvents().isEmpty()) {
                    if (e.isEmpty()) {
                        e = new ArrayList<>(2);
                    }

                    e.addAll(zone.getEvents());
                }
            }
        } finally {
            zonesRead.unlock();
        }

        return e;
    }

    public boolean isInZone(String name) {
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getName().equals(name)) {
                    return true;
                }
            }
        } finally {
            zonesRead.unlock();
        }

        return false;
    }

    public boolean isInZone(Zone zone) {
        zonesRead.lock();
        try {
            return _zones.contains(zone);
        } finally {
            zonesRead.unlock();
        }
    }

    public Zone getZone(ZoneType type) {
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getType() == type) {
                    return zone;
                }
            }
        } finally {
            zonesRead.unlock();
        }
        return null;
    }

    public Location getRestartPoint() {
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getRestartPoints() != null) {
                    ZoneType type = zone.getType();
                    if (type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy) {
                        return zone.getSpawn();
                    }
                }
            }
        } finally {
            zonesRead.unlock();
        }

        return null;
    }

    public Location getPKRestartPoint() {
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : _zones) {
                zone = _zone;
                if (zone.getRestartPoints() != null) {
                    ZoneType type = zone.getType();
                    if (type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy) {
                        return zone.getPKSpawn();
                    }
                }
            }
        } finally {
            zonesRead.unlock();
        }

        return null;
    }

    @Override
    public int getGeoZ(Location loc) {
        if (isFlying() || isInWater() || isInBoat() || isBoat() || isDoor()) {
            return loc.z;
        }

        return super.getGeoZ(loc);
    }

    protected boolean needStatusUpdate() {
        if (!isVisible()) {
            return false;
        }

        boolean result = false;

        int bar;
        bar = (int) (getCurrentHp() * CLIENT_BAR_SIZE / getMaxHp());
        if (bar == 0 || bar != _lastHpBarUpdate) {
            _lastHpBarUpdate = bar;
            result = true;
        }

        bar = (int) (getCurrentMp() * CLIENT_BAR_SIZE / getMaxMp());
        if (bar == 0 || bar != _lastMpBarUpdate) {
            _lastMpBarUpdate = bar;
            result = true;
        }

        if (isPlayer()) {
            bar = (int) (getCurrentCp() * CLIENT_BAR_SIZE / getMaxCp());
            if (bar == 0 || bar != _lastCpBarUpdate) {
                _lastCpBarUpdate = bar;
                result = true;
            }
        }

        return result;
    }

    @Override
    public void onForcedAttack(Player player, boolean shift) {
        player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));

        if (!isAttackable(player) || player.isConfused() || player.isBlocked()) {
            player.sendActionFailed();
            return;
        }

        player.getAI().Attack(this, true, shift);
    }

    public void onHitTimer(Creature target, AttackType attackType, int damage, boolean crit, boolean miss, boolean soulshot,
                           boolean shld, boolean unchargeSS) {
        if (isAlikeDead()) {
            sendActionFailed();
            return;
        }

        if (target.isDead() || !isInRange(target, 2000)) {
            sendActionFailed();
            return;
        }

        if (isPlayable() && target.isPlayable() && isInZoneBattle() != target.isInZoneBattle()) {
            Player player = getPlayer();
            if (player != null) {
                player.sendPacket(SystemMsg.INVALID_TARGET);
                player.sendActionFailed();
            }
            return;
        }

        target.getListeners().onAttackHit(this);

        // if hitted by a cursed weapon, Cp is reduced to 0, if a cursed weapon is hitted by a Hero, Cp is reduced to 0
        if (!miss && target.isPlayer() && (isCursedWeaponEquipped() || getActiveWeaponInstance() != null && getActiveWeaponInstance().isHeroWeapon() && target.isCursedWeaponEquipped())) {
            target.setCurrentCp(0);
        }

        if (target.isStunned() && Formulas.calcStunBreak(this, target, crit)) {
            target.getEffectList().stopEffects(EffectType.Stun);
        }

        displayGiveDamageMessage(target, damage, crit, miss, shld, false);

        ThreadPoolManager.getInstance().execute(new NotifyAITask(target, CtrlEvent.EVT_ATTACKED, this, null, damage));

        boolean checkPvP = checkPvP(target, null);
        // Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
        if (!miss && damage > 0) {
            target.reduceCurrentHp(damage, this, null, true, true, false, true, false, false, true);

            // Скиллы, кастуемые при физ атаке
            if (!target.isDead()) {
                if (crit) {
                    useTriggers(target, TriggerType.CRIT, null, null, damage);
                }

                useTriggers(target, TriggerType.ATTACK, null, null, damage);

                // Manage attack or cast break of the target (calculating rate, sending message...)
                if (Formulas.calcCastBreak(this, target, damage)) {
                    target.abortCast(false, true);
                }
            }

            if (soulshot && unchargeSS) {
                unChargeShots(false);
            }
        }

        if (miss) {
            target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, damage);
        }

        startAttackStanceTask();

        if (checkPvP) {
            startPvPFlag(target);
        }
    }

    public void onMagicUseTimer(Creature aimingTarget, SkillEntry skillEntry, boolean forceUse) {
        _castInterruptTime = 0;

        Skill skill = skillEntry.getTemplate();
        if (skill.isUsingWhileCasting()) {
            aimingTarget.getEffectList().stopEffect(skill.getId());
            onCastEndTime(skillEntry);
            return;
        }

        if (!skill.isOffensive() && getAggressionTarget() != null) {
            forceUse = true;
        }

        if (!skillEntry.checkCondition(this, aimingTarget, forceUse, false, false)) {
            if (skill.getSkillType() == SkillType.PET_SUMMON && isPlayer()) {
                getPlayer().setPetControlItem(null);
            }
            onCastEndTime(skillEntry);
            return;
        }

        // TODO: DS: прикинуть точное время проверки видимости цели.
        if (_skillGeoCheckTask != null && !GeoEngine.canSeeTarget(this, aimingTarget, isFlying())) {
            sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            broadcastPacket(new MagicSkillCanceled(getObjectId()));
            onCastEndTime(skillEntry);
            return;
        }

        List<Creature> targets = skill.getTargets(this, aimingTarget, forceUse);

        int hpConsume = skill.getHpConsume();
        if (hpConsume > 0) {
            setCurrentHp(Math.max(0, currentHp - hpConsume), false);
        }

        double mpConsume2 = skill.getMpConsume2();
        if (mpConsume2 > 0) {
            if (skill.isMusic()) {
                double inc = mpConsume2 / 2;
                double add = 0;
                for (Effect e : getEffectList().getAllEffects()) {
                    if (e.getSkill().getId() != skill.getId() && e.getSkill().getTemplate().isMusic() && e.getTimeLeft() > 30) {
                        add += inc;
                    }
                }
                mpConsume2 += add;
                mpConsume2 = calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, aimingTarget, skillEntry);
            } else if (skill.isMagic()) {
                mpConsume2 = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skillEntry);
            } else {
                mpConsume2 = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skillEntry);
            }

            if (_currentMp < mpConsume2 && isPlayable()) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                onCastEndTime(skillEntry);
                return;
            }
            reduceCurrentMp(mpConsume2, null);
        }

        callSkill(skillEntry, targets, true);

        if (skill.getNumCharges() > 0) {
            setIncreasedForce(getIncreasedForce() - skill.getNumCharges());
        }

        if (skill.isSoulBoost()) {
            setConsumedSouls(getConsumedSouls() - Math.min(getConsumedSouls(), 5), null);
        } else if (skill.getSoulsConsume() > 0) {
            setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);
        }

        Location flyLoc;
        switch (skill.getFlyType()) {
            case THROW_UP:
            case THROW_HORIZONTAL:
                for (Creature target : targets) {
                    //target.setHeading(this, false); //TODO [VISTALL] set heading of target ? Oo
                    flyLoc = getFlyLocation(null, skillEntry);
                    target.setLoc(flyLoc);
                    broadcastPacket(new FlyToLocation(target, flyLoc, skill.getFlyType()));
                }
                break;
            case DUMMY:
                if (aimingTarget != null) {
                    _flyLoc = getFlyLocation(aimingTarget, skillEntry);
                    if (_flyLoc != null) {
                        broadcastPacket(new FlyToLocation(this, _flyLoc, skill.getFlyType()));
                    }
                }
                break;
        }

        if (_scheduledCastCount > 0) {
            _scheduledCastCount--;
            _skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse), _scheduledCastInterval);
            _skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse), _scheduledCastInterval);
            return;
        }

        final SkillMastery sm = getSkillMastery(skill);
        if (sm.hasDoubleTime()) {
            sendPacket(SystemMsg.A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_REUSE_COUNTER_TIME_HAS_INCREASED);
        } else if (sm.hasZeroReuse()) {
            sendPacket(SystemMsg.A_SKILL_IS_READY_TO_BE_USED_AGAIN);
        }

        int skillCoolTime = Formulas.calcMAtkSpd(this, skill, skill.getCoolTime());
        if (skillCoolTime > 0) {
            ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(this, skillEntry), skillCoolTime);
        } else if (skill.hasEffects()) {
            ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(this, skillEntry), 20);
        } else {
            onCastEndTime(skillEntry);
        }
    }

    public void onCastEndTime(SkillEntry se) {
        finishFly();
        removeSkillMastery(se);
        clearCastVars();
        getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, se, null);
    }

    public void clearCastVars() {
        _animationEndTime = 0;
        _castInterruptTime = 0;
        _scheduledCastCount = 0;
        _castingSkill = null;
        _skillTask = null;
        _skillLaunchedTask = null;
        _skillGeoCheckTask = null;
        _flyLoc = null;
    }

    private void finishFly() {
        Location flyLoc = _flyLoc;
        _flyLoc = null;
        if (flyLoc != null) {
            setLoc(flyLoc);
            validateLocation(1);
        }
    }

    public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, false);
    }

    public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        if (attacker == null || isDead() || attacker.isDead() && !isDot) {
            return;
        }

        if (isDamageBlocked() && transferDamage) {
            return;
        }

        if (isDamageBlocked() && attacker != this) {
            if (sendMessage) {
                attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
            }
            return;
        }

        double damageToPet = 0;
        double reflectedDamage = 0;
        if (canReflect) {
            reflectedDamage = attacker.absorbAndReflect(this, skill, damage);
            if (reflectedDamage < 0) // all damage was reflected
                return;
        }

        if (!lethal) {
            damage = absorbToEffector(attacker, damage);
            double absorbedDamage = damage - absorbToMp(attacker, damage);
            damage -= absorbedDamage;
            if (skill == null || !skill.getTemplate().isToggle())
                damageToPet = absorbToSummon(attacker, damage); // Minus if not enough HP
            if (damageToPet > 0) {
                damage -= damageToPet;
            } else {
                damageToPet = -damageToPet;
            }

            getListeners().onCurrentHpDamage(damage, attacker, skill);

            if (attacker != this) {
                if (sendMessage && (damage > 0 || damageToPet > 0 || absorbedDamage > 0)) {
                    displayReceiveDamageMessage(attacker, damage == 0 ? (int) absorbedDamage : (int) damage, (int) damageToPet, (int) reflectedDamage);
                }

                if (!isDot) {
                    useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, null, null, damage);
                }
            }
        }

        onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    protected void onReduceCurrentHp(final double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp) {
        onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, false);
    }

    protected void onReduceCurrentHp(final double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                     boolean lethal) {
        if (awake && isSleeping() && !lethal) {
            getEffectList().stopEffects(EffectType.Sleep);
        }

        boolean isUndying = isUndying(attacker);

        if (attacker != this || skill != null && skill.getTemplate().isOffensive()) {
            if (isMeditated()) {
                final Effect effect = getEffectList().getEffectByType(EffectType.Meditation);
                if (effect != null) {
                    getEffectList().stopEffect(effect.getSkill());
                }
            }

            startAttackStanceTask();
            checkAndRemoveInvisible();

            if (getCurrentHp() - damage < 0.5 && !isUndying) {
                useTriggers(attacker, TriggerType.DIE, null, null, damage);
            }
        }

        // undying mode
        setCurrentHp(Math.max(getCurrentHp() - damage, isUndying ? 0.5 : 0), false);
        if (isUndying) {
            if (getCurrentHp() == 0.5) {
                if (_undyingFlag.compareAndSet(false, true)) {
                    getListeners().onDeathFromUndying(attacker);
                }
            }
        } else if (getCurrentHp() < 0.5) {
            doDie(attacker);
        }
    }

    public void reduceCurrentMp(double i, Creature attacker) {
        if (attacker != null && attacker != this) {
            if (isSleeping()) {
                getEffectList().stopEffects(EffectType.Sleep);
            }

            if (isMeditated()) {
                Effect effect = getEffectList().getEffectByType(EffectType.Meditation);
                if (effect != null) {
                    getEffectList().stopEffect(effect.getSkill());
                }
            }
        }

        if (isDamageBlocked() && attacker != null && attacker != this) {
            attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
            return;
        }

        // 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
        if (attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10) {
            // ПК не может нанести урон чару с блессингом
            if (attacker.getKarma() > 0 && getEffectList().getEffectsBySkillId(5182) != null && !isInZone(ZoneType.SIEGE)) {
                return;
            }
            // чар с блессингом не может нанести урон ПК
            if (getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.SIEGE)) {
                return;
            }
        }

        i = _currentMp - i;

        if (i < 0) {
            i = 0;
        }

        setCurrentMp(i);

        if (attacker != null && attacker != this) {
            startAttackStanceTask();
        }
    }

    public Triple<Double, Double, Double> calculateReflectDamage(final double initialDamage, final Creature attacker, final SkillEntry skill) {
        final double reflectedDamage = attacker.absorbAndReflect(this, skill, initialDamage);
        if (reflectedDamage < 0) // all damage was reflected
        {
            return Triple.of(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        }

        double damage = absorbToEffector(attacker, initialDamage);
        damage = absorbToMp(attacker, damage);
        double damageToPet = absorbToSummon(attacker, damage);
        if (damageToPet > 0) {
            damage -= damageToPet;
        } else {
            damageToPet = -damageToPet;
        }

        return Triple.of(damage, reflectedDamage, damageToPet);
    }

    public double relativeSpeed(GameObject target) {
        return getMoveSpeed() - target.getMoveSpeed() * Math.cos(headingToRadians(getHeading()) - headingToRadians(target.getHeading()));
    }

    public void removeAllSkills() {
        for (SkillEntry s : getAllSkillsArray()) {
            removeSkill(s);
        }
    }

    public void removeBlockStats(List<Stats> stats) {
        if (_blockedStats != null) {
            _blockedStats.removeAll(stats);
            if (_blockedStats.isEmpty()) {
                _blockedStats = null;
            }
        }
    }

    public SkillEntry removeSkill(SkillEntry skill) {
        if (skill == null) {
            return null;
        }
        return removeSkillById(skill.getId());
    }

    public SkillEntry removeSkillById(int id) {
        SkillEntry oldSkill = getAllMapSkills().remove(id);

        if (oldSkill != null) {
            removeTriggers(oldSkill.getTemplate());
            removeStatsByOwner(oldSkill);
        }

        return oldSkill;
    }

    public void addTriggers(StatTemplate f) {
        if (f.getTriggerList().isEmpty()) {
            return;
        }

        f.getTriggerList().forEach(this::addTrigger);
    }

    public void addTrigger(TriggerInfo t) {
        Set<TriggerInfo> hs = triggers.get(t.getType());
        if (hs == null) {
            hs = new CopyOnWriteArraySet<>();
            triggers.put(t.getType(), hs);
        }

        hs.add(t);

        if (t.getType() == TriggerType.ADD) {
            useTriggerSkill(this, null, t, null, 0);
        }
    }

    public void removeTriggers(StatTemplate f) {
        if (f.getTriggerList().isEmpty()) {
            return;
        }

        f.getTriggerList().forEach(this::removeTrigger);
    }

    public void removeTrigger(TriggerInfo t) {
        Set<TriggerInfo> hs = triggers.get(t.getType());
        if (hs == null) {
            return;
        }
        hs.remove(t);
    }

    public void sendActionFailed() {
        sendPacket(ActionFail.STATIC);
    }

    public boolean hasAI() {
        return _ai != null;
    }

    public CharacterAI getAI() {
        if (_ai == null) {
            synchronized (this) {
                if (_ai == null) {
                    _ai = new CharacterAI(this);
                }
            }
        }

        return _ai;
    }

    public void setAI(CharacterAI newAI) {
        if (newAI == null) {
            return;
        }

        CharacterAI oldAI = _ai;

        synchronized (this) {
            _ai = newAI;
        }

        if (oldAI != null) {
            if (oldAI.isActive()) {
                oldAI.stopAITask();
                newAI.startAITask();
                newAI.setIntention(AI_INTENTION_ACTIVE);
            }
        }
    }

    public void setCurrentHp(double newHp, boolean canRessurect, boolean sendInfo) {
        double maxHp = getMaxHp();

        newHp = Math.min(maxHp, Math.max(0, newHp));

        if (currentHp == newHp) {
            return;
        }

        if (newHp >= 0.5 && isDead() && !canRessurect) {
            return;
        }

        double hpStart = currentHp;

        currentHp = newHp;

        if (isDead.compareAndSet(true, false)) {
            onRevive();
        }

        checkHpMessages(hpStart, currentHp);

        if (sendInfo) {
            sendChanges();
            broadcastStatusUpdate();
        }

        if (currentHp < maxHp) {
            startRegeneration();
        }
    }

    public final void setCurrentHp(double newHp, boolean canRessurect) {
        setCurrentHp(newHp, canRessurect, true);
    }

    public void setCurrentMp(double newMp, boolean sendInfo) {
        int maxMp = getMaxMp();

        newMp = Math.min(maxMp, Math.max(0, newMp));

        if (_currentMp == newMp) {
            return;
        }

        if (newMp >= 0.5 && isDead()) {
            return;
        }

        _currentMp = newMp;

        if (sendInfo) {
            sendChanges();
            broadcastStatusUpdate();
        }

        if (_currentMp < maxMp) {
            startRegeneration();
        }
    }

    public void setCurrentCp(double newCp, boolean sendInfo) {
        if (!isPlayer()) {
            return;
        }

        int maxCp = getMaxCp();
        newCp = Math.min(maxCp, Math.max(0, newCp));

        if (_currentCp == newCp) {
            return;
        }

        if (newCp >= 0.5 && isDead()) {
            return;
        }

        _currentCp = newCp;

        if (sendInfo) {
            sendChanges();
            broadcastStatusUpdate();
        }

        if (_currentCp < maxCp) {
            startRegeneration();
        }

    }

    public void setCurrentHpMp(double newHp, double newMp, boolean canRessurect) {
        double maxHp = getMaxHp();
        int maxMp = getMaxMp();

        newHp = Math.min(maxHp, Math.max(0, newHp));
        newMp = Math.min(maxMp, Math.max(0, newMp));

        if (currentHp == newHp && _currentMp == newMp) {
            return;
        }

        if (newHp >= 0.5 && isDead() && !canRessurect) {
            return;
        }

        double hpStart = currentHp;

        currentHp = newHp;
        _currentMp = newMp;

        if (isDead.compareAndSet(true, false)) {
            onRevive();
        }

        checkHpMessages(hpStart, currentHp);

        sendChanges();
        broadcastStatusUpdate();

        if (currentHp < maxHp || _currentMp < maxMp) {
            startRegeneration();
        }
    }

    public void setCurrentHpMp(double newHp, double newMp) {
        setCurrentHpMp(newHp, newMp, false);
    }

    @Override
    public final int getHeading() {
        return _heading;
    }

    public void setHeading(int heading) {
        _heading = heading;
    }

    public final void setIsTeleporting(boolean value) {
        isTeleporting.compareAndSet(!value, value);
    }

    public Creature getCastingTarget() {
        return castingTarget.get();
    }

    public void setCastingTarget(Creature target) {
        if (target == null) {
            castingTarget = HardReferences.emptyRef();
        } else {
            castingTarget = target.getRef();
        }
    }

    public final void setRunning() {
        if (!_running) {
            _running = true;
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_GROUND));
        }
    }

    public void setSkillMastery(Skill skill) {
        _skillMasteryId = skill.getId();
    }

    public Creature getAggressionTarget() {
        return _aggressionTarget.get();
    }

    public void setAggressionTarget(Creature target) {
        if (target == null) {
            _aggressionTarget = HardReferences.emptyRef();
        } else {
            _aggressionTarget = target.getRef();
        }
    }

    public void setWalking() {
        if (_running) {
            _running = false;
            broadcastPacket(new ChangeMoveType(this, EnvType.ET_GROUND));
        }
    }

    public void startAbnormalEffect(AbnormalEffect ae) {
        if (ae == AbnormalEffect.NULL) {
            for (int i = 0; i < _abnormalEffects.length; i++) {
                _abnormalEffects[i] = ae.getMask();
            }
        } else {
            _abnormalEffects[ae.getType().ordinal()] |= ae.getMask();
        }

        sendChanges();
    }

    public void startAttackStanceTask() {
        startAttackStanceTask0();
    }

    /**
     * Запускаем задачу анимации боевой позы. Если задача уже запущена, увеличиваем время, которое персонаж будет в боевой позе на 15с
     */
    protected void startAttackStanceTask0() {
        // предыдущая задача еще не закончена, увеличиваем время
        if (isInCombat()) {
            _stanceEndTime = System.currentTimeMillis() + 15000L;
            return;
        }

        _stanceEndTime = System.currentTimeMillis() + 15000L;

        broadcastPacket(new AutoAttackStart(getObjectId()));

        // отменяем предыдущую
        final Future<?> task = _stanceTask;
        if (task != null) {
            task.cancel(false);
        }

        // Добавляем задачу, которая будет проверять, если истекло время нахождения персонажа в боевой позе,
        // отменяет задачу и останаливает анимацию.
        _stanceTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(_stanceTaskRunnable == null ? _stanceTaskRunnable = new AttackStanceTask() : _stanceTaskRunnable, 1000L, 1000L);
    }

    /**
     * Останавливаем задачу анимации боевой позы.
     */
    public void stopAttackStanceTask() {
        _stanceEndTime = 0L;

        final Future<?> task = _stanceTask;
        if (task != null) {
            task.cancel(false);
            _stanceTask = null;

            broadcastPacket(new AutoAttackStop(getObjectId()));
        }
    }

    /**
     * Остановить регенерацию
     */
    protected void stopRegeneration() {
        regenLock.lock();
        try {
            if (_isRegenerating) {
                _isRegenerating = false;

                if (_regenTask != null) {
                    _regenTask.cancel(false);
                    _regenTask = null;
                }
            }
        } finally {
            regenLock.unlock();
        }
    }

    /**
     * Запустить регенерацию
     */
    protected void startRegeneration() {
        if (!isVisible() || isDead() || getRegenTick() == 0L) {
            return;
        }

        if (_isRegenerating) {
            return;
        }

        regenLock.lock();
        try {
            if (!_isRegenerating) {
                _isRegenerating = true;
                _regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(_regenTaskRunnable == null ? _regenTaskRunnable = new RegenTask() : _regenTaskRunnable, 0, getRegenTick());
            }
        } finally {
            regenLock.unlock();
        }
    }

    public long getRegenTick() {
        return 3000L;
    }

    public void stopAbnormalEffect(AbnormalEffect ae) {
        _abnormalEffects[ae.getType().ordinal()] &= ~ae.getMask();
        sendChanges();
    }

    public void setUndying(boolean val) {
        _isUndying = val;
        _undyingFlag.set(false);
    }

    public boolean isUndying(Creature attacker) {
        return _isUndying;
    }

    /**
     * Блокируем персонажа
     */
    public void block() {
        _blocked = true;
    }

    /**
     * Разблокируем персонажа
     */
    public void unblock() {
        _blocked = false;
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startConfused() {
        return _confused.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopConfused() {
        return _confused.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startFear() {
        return _afraid.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopFear() {
        return _afraid.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startMuted() {
        return _muted.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopMuted() {
        return _muted.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startPMuted() {
        return _pmuted.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopPMuted() {
        return _pmuted.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startAMuted() {
        return _amuted.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopAMuted() {
        return _amuted.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startRooted() {
        return _rooted.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopRooted() {
        return _rooted.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startSleeping() {
        return _sleeping.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopSleeping() {
        return _sleeping.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startStunning() {
        return _stunned.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopStunning() {
        return _stunned.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startParalyzed() {
        return _paralyzed.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopParalyzed() {
        return _paralyzed.setAndGet(false);
    }

    /**
     * Останавливает блокирование входящего лечения, только если на этом персонаже нет другого скилла,
     * который давал бы такой же эффект.
     *
     * @return текущее состояние
     */
    public boolean tryToStopParalyzation(SkillEntry stoppedSkillEntry) {
        Optional<Effect> anotherEffect = getEffectList().getEffect(effect -> (effect instanceof EffectMeditation || effect instanceof EffectParalyze || effect instanceof EffectPetrification) && effect.getSkill().getId() != stoppedSkillEntry.getId());
        return anotherEffect.isPresent() || stopParalyzed();
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startImmobilized() {
        return _immobilized.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopImmobilized() {
        return _immobilized.setAndGet(false);
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startHealBlocked() {
        return _healBlocked.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopHealBlocked() {
        return _healBlocked.compareAndSet(true, false);
    }

    /**
     * Останавливает у персонажа блокирование входящего лечения, только если на этом персонаже нет другого скилла,
     * который давал бы такой же эффект.
     *
     * @return текущее состояние
     */
    public boolean tryToStopHealBlock(SkillEntry stoppedSkillEntry) {
        Optional<Effect> anotherEffect = getEffectList().getEffect(effect ->
                (effect instanceof EffectBlockHPMP || effect instanceof EffectHealBlock
                        || effect instanceof EffectInvulnerable) && effect.getSkill().getId() != stoppedSkillEntry.getId());

        if (anotherEffect.isPresent())
            return true;
        else
            return stopHealBlocked();
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startDamageBlocked() {
        return _damageBlocked.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopDamageBlocked() {
        return _damageBlocked.compareAndSet(true, false);
    }

    /**
     * Останавливает у персонажа блокирование входящего урона, только если на этом персонаже нет другого скилла,
     * который давал бы такой же эффект.
     *
     * @return текущее состояние
     */
    public boolean tryToStopDamageBlock(SkillEntry stoppedSkillEntry) {
        Optional<Effect> anotherEffect = getEffectList().getEffect(effect ->
                (effect instanceof EffectBlockHPMP || effect instanceof EffectInvulnerable
                        || effect instanceof EffectPetrification) && effect.getSkill().getId() != stoppedSkillEntry.getId());

        if (anotherEffect.isPresent())
            return true;
        else
            return stopDamageBlocked();
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startBuffImmunity() {
        return _buffImmunity.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopBuffImmunity() {
        return _buffImmunity.compareAndSet(true, false);
    }

    /**
     * Останавливает у персонажа иммунитет к баффам, только если на этом персонаже нет другого скилла,
     * который давал бы такой же эффект.
     *
     * @return текущее состояние
     */
    public boolean tryToStopBuffImmunity(SkillEntry stoppedSkillEntry) {
        Optional<Effect> anotherEffect = getEffectList().getEffect(effect -> effect instanceof EffectBuffImmunity && effect.getSkill().getId() != stoppedSkillEntry.getId());
        if (anotherEffect.isPresent())
            return true;
        else
            return stopBuffImmunity();
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startDebuffImmunity() {
        return _debuffImmunity.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopDebuffImmunity() {
        return _debuffImmunity.compareAndSet(true, false);
    }

    /**
     * Останавливает у персонажа иммунитет к дебаффам,только если на этом персонаже нет другого скилла,
     * который давал бы такой же эффект.
     *
     * @return текущее состояние
     */
    public boolean tryToStopDebuffImmunity(SkillEntry stoppedSkillEntry) {
        Optional<Effect> anotherEffect = getEffectList().getEffect(effect -> (effect instanceof EffectDebuffImmunity || effect instanceof EffectInvulnerable || effect instanceof EffectPetrification) && effect.getSkill().getId() != stoppedSkillEntry.getId());
        return anotherEffect.isPresent() || stopDebuffImmunity();
    }

    /**
     * @return предыдущее состояние
     */
    public boolean startEffectImmunity() {
        return _effectImmunity.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopEffectImmunity() {
        return _effectImmunity.setAndGet(false);
    }

    /**
     * @return текущее состояние
     */
    public boolean startWeaponEquipBlocked() {
        return _weaponEquipBlocked.getAndSet(true);
    }

    /**
     * @return текущее состояние
     */
    public boolean stopWeaponEquipBlocked() {
        return _weaponEquipBlocked.getAndSet(false);
    }

    public void startFrozen() {
        _frozen = true;
    }

    public void stopFrozen() {
        _frozen = false;
    }

    public void breakFakeDeath() {
        getEffectList().stopAllSkillEffects(EffectType.FakeDeath);
    }

    public final void setIsBlessedByNoblesse(boolean value) {
        _isBlessedByNoblesse = value;
    }

    public final void setIsSalvation(boolean value) {
        _isSalvation = value;
    }

    public void setIsInvul(boolean value) {
        _isInvul = value;
    }

    public boolean isConfused() {
        return _confused.get();
    }

    public boolean isFakeDeath() {
        return _fakeDeath;
    }

    public void setFakeDeath(boolean value) {
        _fakeDeath = value;
    }

    public boolean isAfraid() {
        return _afraid.get();
    }

    public boolean isBlocked() {
        return _blocked;
    }

    public boolean isMuted(SkillEntry skill) {
        return !(skill == null || skill.getTemplate().isNotAffectedByMute()) &&
                (isMMuted() && skill.getTemplate().isMagic() ||
                        isPMuted() && !skill.getTemplate().isMagic());
    }

    public boolean isPMuted() {
        return _pmuted.get();
    }

    public boolean isMMuted() {
        return _muted.get();
    }

    public boolean isAMuted() {
        return _amuted.get();
    }

    public boolean isRooted() {
        return _rooted.get();
    }

    public boolean isSleeping() {
        return _sleeping.get();
    }

    public boolean isStunned() {
        return _stunned.get();
    }

    public boolean isMeditated() {
        return _meditated;
    }

    public void setMeditated(boolean value) {
        _meditated = value;
    }

    public boolean isWeaponEquipBlocked() {
        return _weaponEquipBlocked.get();
    }

    public boolean isParalyzed() {
        return _paralyzed.get();
    }

    public boolean isFrozen() {
        return _frozen;
    }

    public boolean isImmobilized() {
        return _immobilized.get() || getRunSpeed() < 1;
    }

    public boolean isHealBlocked() {
        return isDead() || _healBlocked.get();
    }

    public boolean isDamageBlocked() {
        return isInvul() || _damageBlocked.get();
    }

    public boolean isCastingNow() {
        return _skillTask != null;
    }

    public boolean isLockedTarget() {
        return _lockedTarget;
    }

    public void setLockedTarget(boolean value) {
        _lockedTarget = value;
    }

    public boolean isMovementDisabled() {
        return isBlocked() || isRooted() || isImmobilized() || isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isFrozen();
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean isActionsDisabled() {
        return isBlocked() || isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isFrozen();
    }

    public boolean isCantBuy() {
        return isBlocked() || isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isCastingNow() || isFrozen();
    }

    public final boolean isAttackingDisabled() {
        return _attackReuseEndTime > System.currentTimeMillis();
    }

    public boolean isOutOfControl() {
        return isBlocked() || isConfused() || isAfraid() || isFrozen();
    }

    @Override
    public InvisibleType getInvisibleType() {
        return _invisibleType;
    }

    public void setInvisibleType(final InvisibleType vis) {
        _invisibleType = vis;
    }

    public void teleToLocation(Location loc) {
        teleToLocation(loc.x, loc.y, loc.z, getReflection());
    }

    public void teleToLocation(Location loc, int refId) {
        teleToLocation(loc.x, loc.y, loc.z, refId);
    }

    public void teleToLocation(Location loc, Reflection r) {
        teleToLocation(loc.x, loc.y, loc.z, r);
    }

    public void teleToLocation(int x, int y, int z) {
        teleToLocation(x, y, z, getReflection());
    }

    public void checkAndRemoveInvisible() {
        InvisibleType invisibleType = getInvisibleType();
        if (invisibleType == InvisibleType.EFFECT) {
            getEffectList().stopEffects(EffectType.Invisible);
        }
    }

    public void teleToLocation(int x, int y, int z, int refId) {
        Reflection r = ReflectionManager.getInstance().get(refId);
        if (r == null) {
            return;
        }
        teleToLocation(x, y, z, r);
    }

    public void teleToLocation(int x, int y, int z, Reflection r) {
        if (!isTeleporting.compareAndSet(false, true)) {
            return;
        }

        if (isFakeDeath()) {
            breakFakeDeath();
        }

        abortCast(true, false);
        if (!isLockedTarget()) {
            setTarget(null);
        }
        stopMove();

        if (!isBoat() && !isFlying() && !World.isWater(new Location(x, y, z), r)) {
            z = GeoEngine.getHeight(x, y, z, r.getGeoIndex());
        }

        //TODO [G1ta0] убрать DimensionalRiftManager.teleToLocation
        if (isPlayer() && DimensionalRiftManager.getInstance().checkIfInRiftZone(getLoc(), true)) {
            Player player = (Player) this;
            if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
                x = newCoords.x;
                y = newCoords.y;
                z = newCoords.z;
                player.getParty().getDimensionalRift().usedTeleport(player);
            }
        }

        if (isPlayer()) {
            Player player = (Player) this;

            player.getListeners().onTeleport(x, y, z, r);

            decayMe();

            setXYZ(x, y, z);

            setReflection(r);

            // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
            setLastClientPosition(null);
            setLastServerPosition(null);

            player.sendPacket(new TeleportToLocation(player, x, y, z));
        } else {
            setXYZ(x, y, z);

            setReflection(r);

            broadcastPacket(new TeleportToLocation(this, x, y, z));
            onTeleported();
        }
    }

    public boolean onTeleported() {
        return isTeleporting.compareAndSet(true, false);
    }

    public void sendMessage(CustomMessage message) {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getObjectId() + ']';
    }

    @Override
    public double getColRadius() {
        return getTemplate().getCollisionRadius();
    }

    @Override
    public double getColHeight() {
        return getTemplate() != null ? getTemplate().getCollisionHeight() : 0;
    }

    public EffectList getEffectList() {
        if (_effectList == null) {
            synchronized (this) {
                if (_effectList == null) {
                    _effectList = new EffectList(this);
                }
            }
        }

        return _effectList;
    }

    public boolean paralizeOnAttack(Creature attacker) {
        int max_attacker_level = 0xFFFF;

        NpcInstance leader;
        if (isRaid() || isMinion() && (leader = ((NpcInstance) this).getLeader()) != null && leader.isRaid()) {
            max_attacker_level = getLevel() + ServerConfig.RAID_MAX_LEVEL_DIFF;
        } else if (isNpc()) {
            int max_level_diff = ((NpcInstance) this).getParameter("ParalizeOnAttack", -1000);
            if (max_level_diff != -1000) {
                max_attacker_level = getLevel() + max_level_diff;
            }
        }

        return attacker.getLevel() > max_attacker_level;
    }

    @Override
    protected void onDelete() {
        GameObjectsStorage.remove(this);

        getEffectList().stopAllEffects();

        super.onDelete();
    }

    public void addExpAndSp(long exp, long sp) {
    }

    public void broadcastCharInfo() {
    }

    public void checkHpMessages(double currentHp, double newHp) {
    }

    // ---------------------------- Not Implemented -------------------------------

    public boolean checkPvP(Creature target, Skill skill) {
        return false;
    }

    public boolean consumeItem(int itemConsumeId, long itemCount) {
        return true;
    }

    public boolean consumeItemMp(int itemId, int mp) {
        return true;
    }

    public boolean isFearImmune() {
        return false;
    }

    public boolean isLethalImmune() {
        return false;
    }

    public boolean getChargedSoulShot() {
        return false;
    }

    public int getChargedSpiritShot() {
        return 0;
    }

    public int getIncreasedForce() {
        return 0;
    }

    public void setIncreasedForce(int i) {
    }

    public int getConsumedSouls() {
        return 0;
    }

    public int getKarma() {
        return 0;
    }

    public double getLevelMod() {
        return 1;
    }

    public int getNpcId() {
        return 0;
    }

    public Servitor getServitor() {
        return null;
    }

    public int getPvpFlag() {
        return 0;
    }

    public TeamType getTeam() {
        return _team;
    }

    public void setTeam(TeamType t) {
        _team = t;
    }

    public boolean isUndead() {
        return false;
    }

    public boolean isParalyzeImmune() {
        return false;
    }

    public void reduceArrowCount() {
    }

    public void sendChanges() {
        getStatsRecorder().sendChanges();
    }

    public void sendMessage(String message) {
    }

    public void sendPacket(IBroadcastPacket mov) {
    }

    public void sendPacket(IBroadcastPacket... mov) {
    }

    public void sendPacket(List<? extends IBroadcastPacket> mov) {
    }

    public void setConsumedSouls(int i, NpcInstance monster) {
    }

    public void startPvPFlag(Creature target) {
    }

    public boolean unChargeShots(boolean spirit) {
        return false;
    }

    public void updateEffectIcons() {
    }

    /**
     * Выставить предельные значения HP/MP/CP и запустить регенерацию, если в этом есть необходимость
     */
    protected void refreshHpMpCp() {
        final double maxHp = getMaxHp();
        final int maxMp = getMaxMp();
        final int maxCp = isPlayer() ? getMaxCp() : 0;

        if (currentHp > maxHp) {
            setCurrentHp(maxHp, false);
        }
        if (_currentMp > maxMp) {
            setCurrentMp(maxMp, false);
        }
        if (_currentCp > maxCp) {
            setCurrentCp(maxCp, false);
        }

        if (currentHp < maxHp || _currentMp < maxMp || _currentCp < maxCp) {
            startRegeneration();
        }
    }

    public void updateStats() {
        refreshHpMpCp();
        sendChanges();
    }

    public void setOverhitAttacker(Creature attacker) {
    }

    public void setOverhitDamage(double damage) {
    }

    public boolean isCursedWeaponEquipped() {
        return false;
    }

    public boolean isHero() {
        return false;
    }

    public int getAccessLevel() {
        return 0;
    }

    public Clan getClan() {
        return null;
    }

    public double getRateAdena() {
        return 1.;
    }

    public double getRateItems() {
        return 1.;
    }

    public double getRateExp() {
        return 1.;
    }

    public double getRateSp() {
        return 1.;
    }

    public double getRateSpoil() {
        return 1.;
    }

    public int getFormId() {
        return 0;
    }

    public boolean isNameAbove() {
        return true;
    }

    @Override
    public void setLoc(Location loc) {
        setXYZ(loc.x, loc.y, loc.z);
    }

    public void setLoc(Location loc, boolean MoveTask) {
        setXYZ(loc.x, loc.y, loc.z, MoveTask);
    }

    @Override
    public void setXYZ(int x, int y, int z) {
        setXYZ(x, y, z, false);
    }

    public void setXYZ(int x, int y, int z, boolean MoveTask) {
        if (!MoveTask) {
            stopMove();
        }

        moveLock.lock();
        try {
            super.setXYZ(x, y, z);
        } finally {
            moveLock.unlock();
        }

        updateZones();
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        updateStats();
        updateZones();
    }

    @Override
    public void spawnMe(Location loc) {
        if (loc.h > 0) {
            setHeading(loc.h);
        }
        super.spawnMe(loc);
    }

    @Override
    protected void onDespawn() {
        if (!isLockedTarget()) {
            setTarget(null);
        }
        stopMove();
        stopAttackStanceTask();
        stopRegeneration();

        updateZones();
        clearStatusListeners();

        super.onDespawn();
    }

    public final void doDecay() {
        if (!isDead()) {
            return;
        }

        onDecay();
    }

    protected void onDecay() {
        decayMe();
    }

    public void validateLocation(int broadcast) {
        L2GameServerPacket sp = new ValidateLocation(this);
        if (broadcast == 0) {
            sendPacket(sp);
        } else if (broadcast == 1) {
            broadcastPacket(sp);
        } else {
            broadcastPacketToOthers(sp);
        }
    }

    public abstract int getLevel();

    public abstract ItemInstance getActiveWeaponInstance();

    public abstract WeaponTemplate getActiveWeaponItem();

    public abstract ItemInstance getSecondaryWeaponInstance();

    public abstract WeaponTemplate getSecondaryWeaponItem();

    public synchronized CharListenerList initializeListeners() {
        return new CharListenerList(this);
    }

    public CharListenerList getListeners() {
        return oldListeners;
    }

    public <T extends Listener<Creature>> boolean addListener(T listener) {
        return getListeners().add(listener);
    }

    public <T extends Listener<Creature>> boolean removeListener(T listener) {
        return getListeners().remove(listener);
    }

    public synchronized CharStatsChangeRecorder<? extends Creature> initializeStatsRecorder() {
        return new CharStatsChangeRecorder<>(this);
    }

    public CharStatsChangeRecorder<? extends Creature> getStatsRecorder() {
        return _statsRecorder;
    }

    @Override
    public boolean isCreature() {
        return true;
    }

    /**
     * Внимание: цифры дамага отсылаются в displayReceiveDamageMessage() цели,
     * здесь только общие сообщения.
     */
    public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
        if (target.isPlayer()) {
            if (miss && !target.isDamageBlocked()) {
                target.sendPacket(new SystemMessage(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(this));
            } else if (shld) {
                if (damage > 1) {
                    target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                } else if (damage == 1) {
                    target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                }
            }
        }
    }

    /**
     * Здесь отсылка цифр дамага атакующему от цели.
     * Самой цели сообщения отсылаются в Playables (Player, Summon/Pet).
     * Вызов super обязателен.
     */
    public void displayReceiveDamageMessage(Creature attacker, int damage, int toPet, int reflected) {
        if (attacker != this && !isDead() && !isDamageBlocked() && attacker.isPlayable() && attacker.getPlayer() != null) {
            if (toPet > 0) {
                attacker.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR).addNumber(damage).addNumber(toPet));
            } else if (attacker.isPet()) {
                attacker.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOUR_PET_HIT_FOR_S1_DAMAGE).addNumber(damage));
            } else {
                attacker.getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(attacker).addName(this).addNumber(damage));
            }

            if (reflected > 0) {
                attacker.getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(attacker).addNumber(reflected));
            }
        }
    }

    public Collection<TimeStamp> getSkillReuses() {
        return skillReuses.values();
    }

    public TimeStamp getSkillReuse(SkillEntry skill) {
        return skillReuses.get(skill.hashCode());
    }

    public void enableSkillsByEntryType(SkillEntryType entryType) {
        if (entryType == SkillEntryType.NONE) {
            throw new IllegalArgumentException();
        }

        for (SkillEntry entry : getAllSkills()) {
            if (entry.getEntryType() != entryType || !entry.isDisabled()) {
                continue;
            }

            entry.setDisabled(false);

            addStatFuncs(entry.getStatFuncs());
            addTriggers(entry.getTemplate());
        }
    }

    public void disableSkillsByEntryType(SkillEntryType entryType) {
        if (entryType == SkillEntryType.NONE) {
            throw new IllegalArgumentException();
        }

        for (SkillEntry entry : getAllSkills()) {
            if (entry.getEntryType() != entryType || entry.isDisabled()) {
                continue;
            }

            entry.setDisabled(true);

            removeTriggers(entry.getTemplate());
            removeStatsByOwner(entry);
        }
    }

    public Servitor getFearRunningSummon() {
        return fearRunningSummon;
    }

    public void setFearRunningSummon(final Servitor fearRunningSummon) {
        this.fearRunningSummon = fearRunningSummon;
    }

    public Location getLastClientPosition() {
        return _lastClientPosition;
    }

    public void setLastClientPosition(final Location position) {
        _lastClientPosition = position;
    }

    public Location getLastServerPosition() {
        return _lastServerPosition;
    }

    public void setLastServerPosition(final Location position) {
        _lastServerPosition = position;
    }

    public boolean isDominionTransform() {
        return _dominionTransform;
    }

    public void setDominionTransform(boolean dominionTransform) {
        this._dominionTransform = dominionTransform;
    }

    public int getInteractDistance(final GameObject target) {
        return INTERACTION_DISTANCE + (int) getMinDistance(target);
    }

    public Listeners listeners() {
        return listeners;
    }

    public class MoveNextTask extends RunnableImpl {
        private double allDist;
        private double doneDist;

        public MoveNextTask setDist(final double dist) {
            allDist = dist;
            doneDist = 0.D;

            return this;
        }

        @Override
        public void runImpl() throws Exception {
            if (!isMoving) {
                return;
            }

            moveLock.lock();
            try {
                if (!isMoving) {
                    return;
                }

                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                final int speed = getMoveSpeed();
                if (speed <= 0) {
                    stopMove();
                    return;
                }

                final long now = System.currentTimeMillis();

                Creature follow = null;
                if (isFollow) {
                    follow = getFollowTarget();
                    if (follow == null) {
                        stopMove();
                        return;
                    }
                    if (isInRangeZ(follow, _offset) && GeoEngine.canSeeTarget(Creature.this, follow, false)) {
                        stopMove();
                        ThreadPoolManager.getInstance().execute(new NotifyAITask(Creature.this, CtrlEvent.EVT_ARRIVED_TARGET));
                        return;
                    }
                }

                if (allDist <= 0.D) {
                    moveNext(false);
                    return;
                }

                doneDist += (now - _startMoveTime) * _previousSpeed / 1000.D;

                double done = doneDist / allDist;

                if (done < 0) {
                    done = 0;
                }

                if (done >= 1.D) {
                    moveNext(false);
                    return;
                }

                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                int index = (int) (moveList.size() * done);
                if (index >= moveList.size()) {
                    index = moveList.size() - 1;
                }
                if (index < 0) {
                    index = 0;
                }

                final Location loc = moveList.get(index).clone().geo2world();

                if (!isFlying() && !isInBoat() && !isInWater() && !isBoat()) {
                    if (loc.z - getZ() > 256) {
                        String bug_text = "geo bug 1 at: " + getLoc() + " => " + loc.x + ',' + loc.y + ',' + loc.z + "\tAll path: " + moveList.get(0) + " => " + moveList
                                .get(moveList.size() - 1);
                        Log.add(bug_text, "geo");
                        stopMove();
                        return;
                    }
                }

                // Проверяем, на всякий случай
                if (loc == null || isMovementDisabled()) {
                    stopMove();
                    return;
                }

                setLoc(loc, true);
                //if(getLastClientPosition() != null) //TODO[K] - возможно фикс повышения дифа асинхрона координат
                //{
                //	setLoc(getLastClientPosition(), true);
                //	setLastServerPosition(getLastClientPosition());
                //}
                //else
                //{
                //	setLoc(loc, true);
                //}

                // В процессе изменения координат, мы остановились
                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                if (isFollow && now - _followTimestamp > (_forestalling ? 500 : 1000) && follow != null && !follow.isInRange(movingDestTempPos, Math.max(50, _offset))) {
                    if (Math.abs(getZ() - loc.z) > 1000 && !isFlying()) {
                        sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                        stopMove();
                        return;
                    }
                    if (buildPathTo(follow.getX(), follow.getY(), follow.getZ(), _offset, follow, true, true)) {
                        movingDestTempPos.set(follow.getX(), follow.getY(), follow.getZ());
                    } else {
                        stopMove();
                        return;
                    }
                    moveNext(true);
                    return;
                }

                _previousSpeed = speed;
                _startMoveTime = now;
                _moveTask = ThreadPoolManager.getInstance().schedule(this, getMoveTickInterval());
            } catch (RuntimeException e) {
                LOGGER.error("", e);
            } finally {
                moveLock.unlock();
            }
        }
    }

    private class AttackStanceTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (!isInCombat()) {
                stopAttackStanceTask();
            }
        }
    }

    private class RegenTask implements Runnable {
        @Override
        public void run() {
            if (isDead() || getRegenTick() == 0L) {
                return;
            }

            double hpStart = currentHp;

            double maxHp = getMaxHp();
            int maxMp = getMaxMp();
            int maxCp = isPlayer() ? getMaxCp() : 0;

            double addHp = 0.;
            double addMp = 0.;

            regenLock.lock();
            try {
                if (currentHp < maxHp) {
                    addHp += Formulas.calcHpRegen(Creature.this);
                }

                if (_currentMp < maxMp) {
                    addMp += Formulas.calcMpRegen(Creature.this);
                }

                // Added regen bonus when character is sitting
                if (isPlayer() && OtherConfig.REGEN_SIT_WAIT) {
                    Player pl = (Player) Creature.this;
                    if (pl.isSitting()) {
                        pl.updateWaitSitTime();
                        if (pl.getWaitSitTime() > 5) {
                            addHp += pl.getWaitSitTime();
                            addMp += pl.getWaitSitTime();
                        }
                    }
                } else if (isRaid()) {
                    addHp *= ServerConfig.RATE_RAID_REGEN;
                    addMp *= ServerConfig.RATE_RAID_REGEN;
                }

                currentHp += Math.max(0, Math.min(addHp, calcStat(Stats.HP_LIMIT, null, null) * maxHp / 100. - currentHp));
                _currentMp += Math.max(0, Math.min(addMp, calcStat(Stats.MP_LIMIT, null, null) * maxMp / 100. - _currentMp));

                currentHp = Math.min(maxHp, currentHp);
                _currentMp = Math.min(maxMp, _currentMp);

                if (isPlayer()) {
                    _currentCp += Math.max(0, Math.min(Formulas.calcCpRegen(Creature.this), calcStat(Stats.CP_LIMIT, null, null) * maxCp / 100. - _currentCp));
                    _currentCp = Math.min(maxCp, _currentCp);
                }

                //отрегенились, останавливаем задачу
                if (currentHp == maxHp && _currentMp == maxMp && _currentCp == maxCp) {
                    stopRegeneration();
                }
            } finally {
                regenLock.unlock();
            }

            sendChanges();
            broadcastStatusUpdate();

            checkHpMessages(hpStart, currentHp);
        }
    }
}
