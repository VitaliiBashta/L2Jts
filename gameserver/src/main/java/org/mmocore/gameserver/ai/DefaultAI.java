package org.mmocore.gameserver.ai;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.math.random.RndSelector;
import org.mmocore.commons.math.random.RndSelector.RndNode;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.*;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList.AggroInfo;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoinCoordinate;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.taskmanager.AiTaskManager;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.World;
import org.mmocore.gameserver.world.WorldRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class DefaultAI extends CharacterAI implements Runnable {
    public static final int TaskDefaultWeight = 10000;
    protected static final Logger _log = LoggerFactory.getLogger(DefaultAI.class);
    private static final int[] check_skill_id = {28, 680, 51, 511, 15, 254, 1069, 1097, 1042, 1072, 1170, 352, 358, 1394, 695, 115, 1083, 1160, 1164, 1201, 1206, 1222, 1223, 1224, 1092, 65, 106, 122, 127, 1049, 1064, 1071, 1074, 1169, 1263, 1269, 352, 353, 1336, 1337, 1338, 1358, 1359, 402, 403, 412, 1386, 1394, 1396, 485, 501, 1445, 1446, 1447, 522, 531, 1481, 1482, 1483, 1484, 1485, 1486, 695, 696, 716, 775, 1511, 792, 1524, 1529};
    private static final int[] s_npc_ultimate_defence3 = {5044, 3};
    /**
     * Список заданий
     */
    protected final NavigableSet<Task> _tasks = new ConcurrentSkipListSet<>(TaskComparator.getInstance());
    protected final SkillEntry[] _damSkills, _dotSkills, _debuffSkills, _healSkills, _buffSkills, _stunSkills;
    /**
     * Сортирует от большего к меньшему
     * 23.3  1.2  0.2  0.1
     */
    protected final Comparator<TargetContains> _nearestTargetComparator;
    protected long AI_TASK_ATTACK_DELAY = AiConfig.AI_TASK_ATTACK_DELAY;
    protected long AI_TASK_ACTIVE_DELAY = AiConfig.AI_TASK_ACTIVE_DELAY;
    protected long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
    protected int MAX_PURSUE_RANGE;
    protected ScheduledFuture<?> aiTask;
    protected AtomicBoolean haveAiTask = new AtomicBoolean();
    protected ScheduledFuture<?> _runningTask;
    protected ScheduledFuture<?> _madnessTask;
    /**
     * Показывает, есть ли задания
     */
    protected boolean _def_think = false;
    /**
     * The L2NpcInstance aggro counter
     */
    protected long _globalAggro;
    protected long _randomAnimationEnd;
    protected int _pathfindFails;
    protected long _lastActiveCheck;
    protected long _checkAggroTimestamp = 0;
    /**
     * Время актуальности состояния атаки
     */
    protected long _attackTimeout;
    protected long _lastFactionNotifyTime = 0;
    protected long _minFactionNotifyInterval = 10000;
    protected boolean _isGlobal;
    /**
     * The flag used to indicate that a thinking action is in progress
     */
    private boolean _thinking = false;
    // superpoint
    private int currentPointCounter = 0;
    private boolean returningBack, superpointWait, canDelete = false;
    private long superpointTimeoutAfterServerStartup;
    private SuperPoint superPoint;
    public DefaultAI(final NpcInstance actor) {
        super(actor);

        setAttackTimeout(Long.MAX_VALUE);

        _damSkills = actor.getTemplate().getDamageSkills();
        _dotSkills = actor.getTemplate().getDotSkills();
        _debuffSkills = actor.getTemplate().getDebuffSkills();
        _buffSkills = actor.getTemplate().getBuffSkills();
        _stunSkills = actor.getTemplate().getStunSkills();
        _healSkills = actor.getTemplate().getHealSkills();

        _nearestTargetComparator = new NearestTargetComparator();

        // Preload some AI params
        MAX_PURSUE_RANGE = actor.getParameter("MaxPursueRange", actor.isRaid() ? AiConfig.MAX_PURSUE_RANGE_RAID : actor.isUnderground() ? AiConfig.MAX_PURSUE_UNDERGROUND_RANGE : AiConfig.MAX_PURSUE_RANGE);
        _minFactionNotifyInterval = actor.getParameter("FactionNotifyInterval", 10000);
        _isGlobal = actor.getParameter("GlobalAI", false);
    }

    protected static SkillEntry selectTopSkillByDamage(final Creature actor, final Creature target, final double distance, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0) {
            return null;
        }

        if (skills.length == 1) {
            return skills[0];
        }

        final RndNode<SkillEntry>[] nodes = Stream.of(skills).map(skill -> RndNode.of(skill,
                (int) Math.max(skill.getTemplate().getSimpleDamage(skill, actor, target) * skill.getTemplate().getAOECastRange() / distance, 1)))
                .toArray(RndNode[]::new);

        final RndSelector<SkillEntry> rndSelector = RndSelector.of(nodes);
        return rndSelector.select();
    }

    protected static SkillEntry selectTopSkillByDebuff(final Creature actor, final Creature target, final double distance, final SkillEntry[] skills) //FIXME
    {
        if (skills == null || skills.length == 0) {
            return null;
        }

        if (skills.length == 1) {
            return skills[0];
        }

        final RndNode[] nodes = Stream.of(skills).filter(skill -> skill.getTemplate().getSameByStackType(target) == null)
                .map(skill -> RndNode.of(skill, (int) Math.max(100. * skill.getTemplate().getAOECastRange() / distance, 1)))
                .toArray(RndNode[]::new);
        final RndSelector<SkillEntry> rndSelector = RndSelector.of(nodes);
        return rndSelector.select();
    }

    protected static SkillEntry selectTopSkillByBuff(final Creature target, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0) {
            return null;
        }

        if (skills.length == 1) {
            return skills[0];
        }

        final RndNode[] nodes = Stream.of(skills).filter(skill -> skill.getTemplate().getSameByStackType(target) == null)
                .map(skill -> {
                    double weight = skill.getTemplate().getPower();
                    return RndNode.of(skill, (int) Math.max(weight, 1));
                }).toArray(RndNode[]::new);
        final RndSelector<SkillEntry> rndSelector = RndSelector.of(nodes);
        return rndSelector.select();
    }

    protected static SkillEntry selectTopSkillByHeal(final Creature target, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0) {
            return null;
        }

        final double hpReduced = target.getMaxHp() - target.getCurrentHp();
        if (hpReduced < 1) {
            return null;
        }

        if (skills.length == 1) {
            return skills[0];
        }

        double nearest = Double.MIN_VALUE;
        int index = 0;
        for (int c = 1; c < skills.length; c++) {
            final double temp = Math.abs(skills[c].getTemplate().getPower() - hpReduced);
            if (temp < nearest) {
                index = c;
                nearest = temp;
            }
        }
        final SkillEntry topHealSkill = skills[index];
        return topHealSkill;
    }

    public void addTaskCast(final Creature target, final SkillEntry skill) {
        final Task task = new Task();
        task.type = TaskType.CAST;
        task.target = target.getRef();
        task.skill = skill;
        _tasks.add(task);
        _def_think = true;
    }

    public void addTaskBuff(final Creature target, final SkillEntry skill) {
        final Task task = new Task();
        task.type = TaskType.BUFF;
        task.target = target.getRef();
        task.skill = skill;
        _tasks.add(task);
        _def_think = true;
    }

    public void addTaskAttack(final Creature target) {
        final Task task = new Task();
        task.type = TaskType.ATTACK;
        task.target = target.getRef();
        _tasks.add(task);
        _def_think = true;
    }

    public void addTaskAttack(final Creature target, final SkillEntry skill, final int weight) {
        final Task task = new Task();
        task.type = skill.getTemplate().isOffensive() ? TaskType.CAST : TaskType.BUFF;
        task.target = target.getRef();
        task.skill = skill;
        task.weight = weight;
        _tasks.add(task);
        _def_think = true;
    }

    public void addTaskMove(final Location loc, final boolean pathfind) {
        final Task task = new Task();
        task.type = TaskType.MOVE;
        task.loc = loc;
        task.pathfind = pathfind;
        _tasks.add(task);
        _def_think = true;
    }

    protected void addTaskMove(final int locX, final int locY, final int locZ, final boolean pathfind) {
        addTaskMove(new Location(locX, locY, locZ), pathfind);
    }

    @Override
    public void runImpl() {
        if (!haveAiTask.get())
            return;

        // проверяем, если NPC вышел в неактивный регион, отключаем AI
        if (!isGlobalAI() && System.currentTimeMillis() - _lastActiveCheck > 60000L) {
            _lastActiveCheck = System.currentTimeMillis();
            final NpcInstance actor = getActor();
            final WorldRegion region = actor == null ? null : actor.getCurrentRegion();
            if (region == null || !region.isActive()) {
                stopAITask();
                return;
            }
        }
        onEvtThink();
    }

    @Override
    public void startAITask() {
        if (haveAiTask.compareAndSet(false, true)) {
            AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
            aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
        }
    }

    protected void switchAITask(final long NEW_DELAY) {
        if (haveAiTask.get()) {
            if (AI_TASK_DELAY_CURRENT == NEW_DELAY)
                return;

            aiTask.cancel(false);

            haveAiTask.set(false);
        }

        AI_TASK_DELAY_CURRENT = NEW_DELAY;
        aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);

        haveAiTask.set(true);
    }

    @Override
    public final void stopAITask() {
        if (haveAiTask.compareAndSet(true, false)) {
            aiTask.cancel(false);
            aiTask = null;
        }
    }

    @Override
    public boolean isGlobalAI() {
        return _isGlobal;
    }

    /**
     * Определяет, может ли этот тип АИ видеть персонажей в режиме Silent Move.
     *
     * @param target L2Playable цель
     * @return true если цель видна в режиме Silent Move
     */
    protected boolean canSeeInSilentMove(final Playable target) {
        if (getActor().getParameter("canSeeInSilentMove", false)) {
            return true;
        }
        return !target.isSilentMoving();
    }

    protected boolean canSeeInHide(final Playable target) {
        if (getActor().getParameter("canSeeInHide", false)) {
            return true;
        }

        return !target.isInvisible();
    }

    protected boolean checkAggression(final Creature target) {
        if (target == null)
            return false;
        final NpcInstance actor = getActor();
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro()) {
            return false;
        }
        if (target.isAlikeDead()) {
            return false;
        }

        if (target.isNpc() && target.isInvul()) {
            return false;
        }

        if (target.isPlayable()) {
            if (!canSeeInSilentMove((Playable) target)) {
                return false;
            }
            if (!canSeeInHide((Playable) target)) {
                return false;
            }
            if ("varka_silenos_clan".equalsIgnoreCase(actor.getFaction().getName()) && target.getPlayer().getVarka() > 0) {
                return false;
            }
            if ("ketra_orc_clan".equalsIgnoreCase(actor.getFaction().getName()) && target.getPlayer().getKetra() > 0) {
                return false;
            }
			/*if(target.isFollow && !target.isPlayer() && target.getFollowTarget() != null && target.getFollowTarget().isPlayer())
					return;*/
            if (target.isPlayer() && ((Player) target).isGM() && target.isInvisible()) {
                return false;
            }
            if (((Playable) target).getNonAggroTime() > System.currentTimeMillis()) {
                return false;
            }
            if (target.isPlayer() && !target.getPlayer().isActive()) {
                return false;
            }
            if (actor.isMonster() && target.isInZonePeace()) {
                return false;
            }
        }

        if (!isInAggroRange(target)) {
            return false;
        }
        if (!canAttackCharacter(target)) {
            return false;
        }
        if (!GeoEngine.canSeeTarget(actor, target, false)) {
            return false;
        }

        actor.getAggroList().addDamageHate(target, 0, 2);

        if (target.isSummon() || target.isPet()) {
            actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
        }

        startRunningTask(AI_TASK_ATTACK_DELAY);
        setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

        return true;
    }

    protected boolean isInAggroRange(Creature target) {
        NpcInstance actor = getActor();
        AggroInfo ai = actor.getAggroList().get(target);
        if (ai != null && ai.hate > 0) {
            if (!target.isInRangeZ(actor.getLoc(), MAX_PURSUE_RANGE)) {
                return false;
            }
        } else if (!isAggressive() || !target.isInRangeZ(actor.getLoc(), actor.getAggroRange())) {
            return false;
        }

        return true;
    }

    protected void setIsInRandomAnimation(final long time) {
        _randomAnimationEnd = System.currentTimeMillis() + time;
    }

    protected boolean randomAnimation() {
        final NpcInstance actor = getActor();

        if (actor.getParameter("noRandomAnimation", false)) {
            return false;
        }

        if (actor.hasRandomAnimation() && !actor.isActionsDisabled() && !actor.isMoving && !actor.isInCombat() && Rnd.chance(AiConfig.RND_ANIMATION_RATE)) {
            setIsInRandomAnimation(3000);
            actor.onRandomAnimation();
            return true;
        }
        return false;
    }

    protected boolean randomWalk() {
        final NpcInstance actor = getActor();

        if (actor.getParameter("noRandomWalk", false)) {
            return false;
        }

        return !actor.isMoving && maybeMoveToHome();
    }

    protected boolean findAggressionTarget() {
        final long now = System.currentTimeMillis();
        if (now - _checkAggroTimestamp > AiConfig.AGGRO_CHECK_INTERVAL) {
            _checkAggroTimestamp = now;
            NpcInstance actor = getActor();

            if (!actor.getAggroList().isEmpty()) {
                final List<Creature> hateList = actor.getAggroList().getHateList(actor.getAggroRange());
                for (final Creature cha : hateList) {
                    if (checkAggression(cha))
                        return true;
                }
            }

            boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", isAggressive() ? 100 : 0));
            if (aggressive) {
                List<TargetContains> targets = new ArrayList<>();
                List<Creature> chars = World.getAroundCharacters(actor, actor.getAggroRange(), actor.getAggroRange());
                for (Creature creature : chars) {
                    if (creature == null) {
                        continue;
                    }
                    double distance = actor.getDistance3D(creature);
                    TargetContains target = new TargetContains(distance, creature);
                    targets.add(target);
                }
                Collections.sort(targets, _nearestTargetComparator);
                for (TargetContains cha : targets) {
                    if (cha.getCreature() != null && checkAggression(cha.getCreature())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return true если действие выполнено, false если нет
     */
    protected boolean thinkActive() {
        final NpcInstance actor = getActor();
        if (actor.isActionsDisabled()) {
            return true;
        }

        if (_randomAnimationEnd > System.currentTimeMillis()) {
            return true;
        }

        if (_def_think) {
            if (doTask()) {
                clearTasks();
            }
            return true;
        }
        if (findAggressionTarget())
            return true;
/*		final long now = System.currentTimeMillis();
		if(now - _checkAggroTimestamp > AiConfig.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			if(!actor.getAggroList().isEmpty())
			{
				final List<Creature> hateList = actor.getAggroList().getHateList(actor.getAggroRange());
				for(final Creature cha : hateList)
				{
					if(checkAggression(cha))
						return true;
				}
			}

			final boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", isAggressive() ? 100 : 0));
*//*			if(aggressive)
			{
				final List<Creature> chars = World.getAroundCharacters(actor, actor.getAggroRange(), actor.getAggroRange());
				Collections.sort(chars, _nearestTargetComparator);
				for(final Creature cha : chars)
				{
					if(checkAggression(cha))
					{
						return true;
					}
				}
			}*//*
			if(!actor.getAggroList().isEmpty() || aggressive)
			{
				int count = 0;
				List<Creature> targets = World.getAroundCharacters(actor);
				while(!targets.isEmpty())
				{
					count++;
					if (count > 1000)
					{
						Log.debug("AI loop count exceeded, " + getActor() + " " + getActor().getLoc() + " " + targets);
						return false;
					}

					Creature target = getNearestTarget(targets);
					if(target == null)
						break;

					if(aggressive || actor.getAggroList().get(target) != null)
						if(checkAggression(target))
						{
							actor.getAggroList().addDamageHate(target, 0, 2);

							if((target.isSummon() || target.isPet()))
								actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);

							startRunningTask(AI_TASK_ATTACK_DELAY);
							setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

							return true;
						}

					targets.remove(target);
				}
			}
		}*/
        if (getSuperPoint() != null && !actor.isBlocked() && !actor.isAttackingNow() && !actor.isCastingNow() && !actor.isMoving() && System.currentTimeMillis() > superpointTimeoutAfterServerStartup) {
            final List<SuperPoinCoordinate> coords = getSuperPoint().getCoordinats();
            final SuperPoinCoordinate currentPointCoordinate = coords.get(currentPointCounter);
            if (currentPointCoordinate.getDelayInSec() > 0 && !superpointWait && System.currentTimeMillis() - superpointTimeoutAfterServerStartup > currentPointCoordinate.getDelayInSec()) {
                superpointTimeoutAfterServerStartup = System.currentTimeMillis() + (currentPointCoordinate.getDelayInSec() * 1000);
                superpointWait = true;
                return true;
            }
            if (currentPointCoordinate.getSocialId() > 0 && System.currentTimeMillis() - superpointTimeoutAfterServerStartup > currentPointCoordinate.getDelayInSec()) {
                actor.broadcastPacket(new SocialAction(actor.getObjectId(), currentPointCoordinate.getSocialId()));
            }
            if (currentPointCoordinate.getMsgId() > 0 && System.currentTimeMillis() - superpointTimeoutAfterServerStartup > currentPointCoordinate.getDelayInSec()) {
                ChatUtils.say(actor, currentPointCoordinate.getMsgRadius(), currentPointCoordinate.getMsgChatType(), NpcString.valueOf(currentPointCoordinate.getMsgId()));
            }

            superpointWait = false;

            if (getSuperPoint().isRunning())
                actor.setRunning();
            else
                actor.setWalking();

            switch (getSuperPoint().getType()) {
                case LENGTH: {
                    addTaskMove(currentPointCoordinate, true);
                    if (returningBack)
                        currentPointCounter--;
                    else
                        currentPointCounter++;

                    if (currentPointCounter >= coords.size() - 1)
                        returningBack = true;

                    if (currentPointCounter == 0)
                        returningBack = false;
                    break;
                }
                case ROUND: {
                    addTaskMove(currentPointCoordinate, true);
                    currentPointCounter++;

                    if (currentPointCounter >= coords.size() - 1)
                        currentPointCounter = 0;
                    break;
                }
                case RANDOM: {
                    currentPointCounter = Rnd.get(coords.size() - 1);

                    addTaskMove(currentPointCoordinate, true);
                    break;
                }
                case DELETE: {
                    if (canDelete) {
                        actor.deleteMe();
                        return true;
                    }

                    addTaskMove(currentPointCoordinate, true);
                    currentPointCounter++;

                    if (currentPointCounter >= coords.size() - 1)
                        canDelete = true;
                    break;
                }
                case FINISH: {
                    currentPointCounter++;

                    addTaskMove(currentPointCoordinate, true);

                    if (currentPointCounter >= coords.size() - 1)
                        actor.stopMove();
                    break;
                }
            }

            doTask();
            return true;
        }

        if (actor.isMinion()) {
            final NpcInstance leader = actor.getLeader();
            if (leader != null && !getActor().isIgnoreLeaderAction()) {
                final double distance = actor.getDistance(leader.getX(), leader.getY());
                if (distance > 1000) {
                    actor.teleToLocation(leader.getMinionPosition());
                } else if (distance > 200) {
                    addTaskMove(leader.getMinionPosition(), false);
                }
                return true;
            }
        }

        if (randomAnimation()) {
            return true;
        }

        if (randomWalk()) {
            return true;
        }

        return false;
    }

    @Override
    protected void onIntentionIdle() {
        final NpcInstance actor = getActor();

        // Удаляем все задания
        clearTasks();

        actor.stopMove();
        actor.getAggroList().clear(true);
        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);

        changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
    }

    @Override
    protected void onIntentionActive() {
        final NpcInstance actor = getActor();

        actor.stopMove();
        setAttackTimeout(Long.MAX_VALUE);

        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
            switchAITask(AI_TASK_ACTIVE_DELAY);
            changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        }

        onEvtThink();
    }

    @Override
    protected void onIntentionAttack(final Creature target) {
        final NpcInstance actor = getActor();

        // Удаляем все задания
        clearTasks();

        actor.stopMove();
        setAttackTarget(target);
        setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
        setGlobalAggro(0);

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
            switchAITask(AI_TASK_ATTACK_DELAY);
        }

        onEvtThink();
    }

    protected boolean canAttackCharacter(final Creature target) {
        return target.isPlayable();
    }

    protected boolean isAggressive() {
        return getActor().isAggressive();
    }

    protected boolean checkTarget(final Creature target, final int range) {
        final NpcInstance actor = getActor();
        if (target == null || target.isAlikeDead() || !actor.isInRangeZ(target, range)) {
            return false;
        }

        // если не видим чаров в хайде - не атакуем их
        final boolean hided = target.isPlayable() && !canSeeInHide((Playable) target);

        if (!hided && actor.isConfused()) {
            return true;
        }

        //В состоянии атаки атакуем всех, на кого у нас есть хейт
        if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            final AggroInfo ai = actor.getAggroList().get(target);
            if (ai != null) {
                if (hided) {
                    ai.hate = 0; // очищаем хейт
                    return false;
                }
                return ai.hate > 0;
            }
            return false;
        }

        return canAttackCharacter(target);
    }

    protected long getAttackTimeout() {
        return _attackTimeout;
    }

    public void setAttackTimeout(final long time) {
        _attackTimeout = time;
    }

    protected void thinkAttack() {
        final NpcInstance actor = getActor();
        if (actor.isDead()) {
            return;
        }

        final Location loc = actor.getSpawnedLoc();
        if (!actor.isInRange(loc, MAX_PURSUE_RANGE) && getSuperPoint() == null) {
            teleportHome();
            return;
        }

        if (doTask() && !actor.isAttackingNow() && !actor.isCastingNow()) {
            if (!createNewTask()) {
                if (System.currentTimeMillis() > getAttackTimeout()) {
                    returnHome();
                }
            }
        }
    }

    @Override
    protected void onEvtSpawn() {
        final int aggroDelayTime = getActor().getParameter("globalAggro", Rnd.get(2000, 3000));
        setGlobalAggro(System.currentTimeMillis() + aggroDelayTime);
        setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

        if (getActor().isMinion() && getActor().getLeader() != null) {
            _isGlobal = getActor().getLeader().getAI().isGlobalAI();
        }
    }

    @Override
    protected void onEvtReadyToAct() {
        onEvtThink();
    }

    @Override
    protected void onEvtArrivedTarget() {
        onEvtThink();
    }

    @Override
    protected void onEvtArrived() {
        onEvtThink();
    }

    protected boolean tryMoveToTarget(final Creature target) {
        return tryMoveToTarget(target, 0);
    }

    protected boolean tryMoveToTarget(final Creature target, final int range) {
        final NpcInstance actor = getActor();

        if (!actor.followToCharacter(target, actor.getPhysicalAttackRange(), true)) {
            _pathfindFails++;
        }

        if (_pathfindFails >= getMaxPathfindFails() && (System.currentTimeMillis() > getAttackTimeout() - getMaxAttackTimeout() + getTeleportTimeout()) && actor.isInRange(target, MAX_PURSUE_RANGE)) {
            _pathfindFails = 0;

            if (target.isPlayable()) {
                final AggroInfo hate = actor.getAggroList().get(target);
                if (hate == null || hate.hate < 100 || (actor.getReflection() != ReflectionManager.DEFAULT && actor instanceof RaidBossInstance)) // bless freya
                {
                    returnHome();
                    return false;
                }
            }
            Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getGeoIndex());
            if (!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex())) // Для подстраховки
            {
                loc = target.getLoc();
            }
            actor.teleToLocation(loc);
        }

        return true;
    }

    protected boolean maybeNextTask(final Task currentTask) {
        // Следующее задание
        _tasks.remove(currentTask);
        // Если заданий больше нет - определить новое
        if (_tasks.isEmpty()) {
            return true;
        }
        return false;
    }

    protected boolean doTask() {
        final NpcInstance actor = getActor();

        if (!_def_think) {
            return true;
        }

        final Task currentTask = _tasks.pollFirst();
        if (currentTask == null) {
            clearTasks();
            return true;
        }

        if (actor.isDead() || actor.isAttackingNow() || actor.isCastingNow()) {
            return false;
        }

        switch (currentTask.type) {
            // Задание "прибежать в заданные координаты"
            case MOVE: {
                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                if (actor.isInRange(currentTask.loc, 100)) {
                    return maybeNextTask(currentTask);
                }

                if (actor.isMoving) {
                    return false;
                }

                if (!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    actor.teleToLocation(currentTask.loc);
                    //actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
                    //ThreadPoolManager.getInstance().scheduleAi(new Teleport(currentTask.loc), 500, false);
                    return maybeNextTask(currentTask);
                }
            }
            break;
            // Задание "добежать - ударить"
            case ATTACK: {
                final Creature target = currentTask.target.get();

                if (!checkTarget(target, MAX_PURSUE_RANGE)) {
                    return true;
                }

                setAttackTarget(target);

                if (actor.isMoving) {
                    return Rnd.chance(25);
                }

                if (actor.getRealDistance3D(target) <= actor.getPhysicalAttackRange() + 40 && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
                    actor.doAttack(target);
                    return maybeNextTask(currentTask);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target);
            }
            break;
            // Задание "добежать - атаковать скиллом"
            case CAST: {
                final Creature target = currentTask.target.get();

                if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || currentTask.skill.isDisabled()) {
                    return true;
                }

                final Skill skill = currentTask.skill.getTemplate();
                final boolean isAoE = skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
                final int castRange = skill.getAOECastRange();

                if (!checkTarget(target, MAX_PURSUE_RANGE + castRange)) {
                    return true;
                }

                setAttackTarget(target);

                if (actor.getRealDistance3D(target) <= castRange + 60 && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
                    actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
                    return maybeNextTask(currentTask);
                }

                if (actor.isMoving) {
                    return Rnd.chance(10);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target, castRange);
            }
            break;
            // Задание "добежать - применить скилл"
            case BUFF: {
                final Creature target = currentTask.target.get();

                if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || currentTask.skill.isDisabled()) {
                    return true;
                }

                final Skill skill = currentTask.skill.getTemplate();
                if (skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF) {
                    actor.doCast(currentTask.skill, actor, false);
                    return maybeNextTask(currentTask);
                }

                if (target == null || target.isAlikeDead() || !actor.isInRange(target, 2000)) {
                    return true;
                }

                final boolean isAoE = skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
                final int castRange = skill.getAOECastRange();

                if (actor.isMoving) {
                    return Rnd.chance(10);
                }

                if (actor.getRealDistance3D(target) <= castRange + 60 && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
                    return maybeNextTask(currentTask);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target);
            }
            break;
        }

        return false;
    }

    protected boolean createNewTask() {
        return false;
    }

    protected boolean defaultNewTask() {
        clearTasks();

        final NpcInstance actor = getActor();
        final Creature target;
        if (actor == null || (target = prepareTarget()) == null) {
            return false;
        }

        final double distance = actor.getDistance(target);
        return chooseTaskAndTargets(null, target, distance);
    }

    @Override
    protected void onEvtThink() {
        final NpcInstance actor = getActor();
        if (_thinking || actor == null || actor.isActionsDisabled() || actor.isAfraid()) {
            return;
        }

        if (_randomAnimationEnd > System.currentTimeMillis()) {
            return;
        }

        if (actor.isRaid() && (actor.isInZonePeace() || actor.isInZoneBattle() || actor.isInZone(ZoneType.SIEGE))) {
            teleportHome();
            return;
        }

        _thinking = true;
        try {
            if (!AiConfig.BLOCK_ACTIVE_TASKS && getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                thinkActive();
            } else if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                thinkAttack();
            }
        } finally {
            _thinking = false;
        }
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        final NpcInstance actor = getActor();

        final int transformer = actor.getParameter("transformOnDead", 0);
        final int chance = actor.getParameter("transformChance", 100);
        if (transformer > 0 && Rnd.chance(chance)) {
            final NpcInstance npc = NpcUtils.spawnSingle(transformer, actor.getLoc(), actor.getReflection());

            if (killer != null && killer.isPlayable()) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
                killer.setTarget(npc);
                killer.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            }
        }

        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtClanAttacked(final Creature attacked, final Creature attacker, final int damage) {
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro()) {
            return;
        }

        notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final NpcInstance actor = getActor();
        if (attacker == null || actor.isDead()) {
            return;
        }

        final int transformer = actor.getParameter("transformOnUnderAttack", 0);
        if (transformer > 0) {
            final int chance = actor.getParameter("transformChance", 5);
            if (chance == 100 || ((MonsterInstance) actor).getChampion() == 0 && actor.getCurrentHpPercents() > 50 && Rnd.chance(chance)) {
                final MonsterInstance npc = (MonsterInstance) NpcHolder.getInstance().getTemplate(transformer).getNewInstance();
                npc.setSpawnedLoc(actor.getLoc());
                npc.setReflection(actor.getReflection());
                npc.setChampion(((MonsterInstance) actor).getChampion());
                npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                npc.spawnMe(npc.getSpawnedLoc());
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
                actor.doDie(actor);
                actor.decayMe();
                attacker.setTarget(npc);
                attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
                return;
            }
        }

        final Player player = attacker.getPlayer();

        if (player != null) {
            final List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
            if (quests != null) {
                for (final QuestState qs : quests) {
                    qs.getQuest().notifyAttack(actor, qs);
                }
            }
        }

        if (getSuperPoint() != null && !actor.isAttackable(attacker))
            return;

        //Добавляем только хейт, урон, если атакующий - игровой персонаж, будет добавлен в L2NpcInstance.onReduceCurrentHp
        actor.getAggroList().addDamageHate(attacker, 0, damage);

        // Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
        if (damage > 0 && (attacker.isSummon() || attacker.isPet())) {
            actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
        }

        if (!actor.getParameter("noCheckAggression", false)) {
            if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
                if (!actor.isRunning()) {
                    startRunningTask(AI_TASK_ATTACK_DELAY);
                }
                setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
            }

            notifyFriends(attacker, damage);
        }
        if (getActor().isMonster()) {
            checkRangeGuard(attacker, skill);
        }
    }

    @Override
    protected void onEvtAggression(final Creature attacker, final int aggro) {
        final NpcInstance actor = getActor();
        if (attacker == null || actor.isDead() || actor.getParameter("noCheckAggression", false)) {
            return;
        }

        if (getSuperPoint() != null && !actor.isAttackable(attacker))
            return;

        actor.getAggroList().addDamageHate(attacker, 0, aggro);

        // Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
        if (aggro > 0 && (attacker.isSummon() || attacker.isPet())) {
            actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? aggro : 1);
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            if (!actor.isRunning()) {
                startRunningTask(AI_TASK_ATTACK_DELAY);
            }
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
        }
    }

    protected boolean maybeMoveToHome() {
        final NpcInstance actor = getActor();
        if (actor.isDead() || getSuperPoint() != null) {
            return false;
        }

        final boolean randomWalk = actor.hasRandomWalk();
        final Location sloc = actor.getSpawnedLoc();

        // Random walk or not?
        if (randomWalk && (!AiConfig.RND_WALK || !Rnd.chance(AiConfig.RND_WALK_RATE))) {
            return false;
        }

        final boolean isInRange = actor.isInRangeZ(sloc, AiConfig.MAX_DRIFT_RANGE);

        if (!randomWalk && isInRange) {
            return false;
        }

        final Location pos = Location.findPointToStay(actor, sloc, 0, AiConfig.MAX_DRIFT_RANGE);

        actor.setWalking();

        // Телепортируемся домой, только если далеко от дома
        if (!actor.moveToLocation(pos.x, pos.y, pos.z, 0, true) && !isInRange) {
            teleportHome();
        }

        return true;
    }

    protected void returnHome() {
        returnHome(true, AiConfig.ALWAYS_TELEPORT_HOME);
    }

    protected void teleportHome() {
        returnHome(true, true);
    }

    protected void returnHome(final boolean clearAggro, final boolean teleport) {
        final NpcInstance actor = getActor();
        final Location sloc = actor.getSpawnedLoc();

        // Удаляем все задания
        clearTasks();
        actor.stopMove();

        if (clearAggro) {
            actor.getAggroList().clear(true);
        }

        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);

        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);

        if (getSuperPoint() != null) {
            final List<SuperPoinCoordinate> coords = getSuperPoint().getCoordinats();
            if (!actor.isRunning())
                actor.setWalking();

            addTaskMove(coords.get(currentPointCounter), true);
            return;
        }

        if (teleport) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 0));
            actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
        } else {
            if (!clearAggro) {
                actor.setRunning();
            } else {
                actor.setWalking();
            }

            addTaskMove(sloc, false);
        }
    }

    protected Creature prepareTarget() {
        final NpcInstance actor = getActor();

        if (actor.isConfused()) {
            return getAttackTarget();
        }

        // Для "двинутых" боссов, иногда, выбираем случайную цель
        if (Rnd.chance(actor.getParameter("isMadness", 0))) {
            final Creature randomHated = actor.getAggroList().getRandomHated();
            if (randomHated != null && Math.abs(actor.getZ() - randomHated.getZ()) < 1000) // Не прыгаем к случайной цели если слишком большая разница Z.
            {
                setAttackTarget(randomHated);
                if (_madnessTask == null && !actor.isConfused()) {
                    actor.startConfused();
                    _madnessTask = ThreadPoolManager.getInstance().schedule(new MadnessTask(), 10000);
                }
                return randomHated;
            }
        }

        // Новая цель исходя из агрессивности
        final List<Creature> hateList = actor.getAggroList().getHateList(MAX_PURSUE_RANGE);
        Creature hated = null;
        for (final Creature cha : hateList) {
            //Не подходит, очищаем хейт
            if (!checkTarget(cha, MAX_PURSUE_RANGE)) {
                actor.getAggroList().remove(cha, true);
                continue;
            }
            hated = cha;
            break;
        }

        if (hated != null) {
            setAttackTarget(hated);
            return hated;
        }

        return null;
    }

    protected boolean canUseSkill(final SkillEntry skill, final Creature target, final double distance) {
        final NpcInstance actor = getActor();
        if (skill == null || skill.getTemplate().isNotUsedByAI()) {
            return false;
        }

        if (skill.getTemplate().getTargetType() == Skill.SkillTargetType.TARGET_SELF && target != actor) {
            return false;
        }

        final int castRange = skill.getTemplate().getAOECastRange();
        if (castRange <= 200 && distance > 200) {
            return false;
        }

        if (actor.isSkillDisabled(skill) || actor.isMuted(skill) || skill.isDisabled()) {
            return false;
        }

        double mpConsume2 = skill.getTemplate().getMpConsume2();
        if (skill.getTemplate().isMagic()) {
            mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
        } else {
            mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
        }
        if (actor.getCurrentMp() < mpConsume2) {
            return false;
        }

        if (target.getEffectList().getEffectsCountForSkill(skill.getId()) != 0) {
            return false;
        }

        return true;
    }

    protected boolean canUseSkill(final SkillEntry sk, final Creature target) {
        return canUseSkill(sk, target, 0);
    }

    protected SkillEntry[] selectUsableSkills(final Creature target, final double distance, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0 || target == null) {
            return null;
        }

        SkillEntry[] ret = null;
        int usable = 0;

        for (final SkillEntry skill : skills) {
            if (canUseSkill(skill, target, distance)) {
                if (ret == null) {
                    ret = new SkillEntry[skills.length];
                }
                ret[usable++] = skill;
            }
        }

        if (ret == null || usable == skills.length) {
            return ret;
        }

        if (usable == 0) {
            return null;
        }

        return Arrays.copyOf(ret, usable);
    }

    protected void addDesiredSkill(final Map<SkillEntry, Integer> skillMap, final Creature target, final double distance, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0 || target == null) {
            return;
        }
        for (final SkillEntry sk : skills) {
            addDesiredSkill(skillMap, target, distance, sk);
        }
    }

    protected void addDesiredSkill(final Map<SkillEntry, Integer> skillMap, final Creature target, final double distance, final SkillEntry skill) {
        if (skill == null || target == null || !canUseSkill(skill, target)) {
            return;
        }
        int weight = (int) -Math.abs(skill.getTemplate().getAOECastRange() - distance);
        if (skill.getTemplate().getAOECastRange() >= distance) {
            weight += 1000000;
        } else if (skill.getTemplate().isNotTargetAoE() && skill.getTemplate().getTargets(getActor(), target, false).isEmpty()) {
            return;
        }
        skillMap.put(skill, weight);
    }

    protected void addDesiredHeal(final Map<SkillEntry, Integer> skillMap, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0) {
            return;
        }
        final NpcInstance actor = getActor();
        final double hpReduced = actor.getMaxHp() - actor.getCurrentHp();
        final double hpPercent = actor.getCurrentHpPercents();
        if (hpReduced < 1) {
            return;
        }
        int weight;
        for (final SkillEntry sk : skills) {
            if (canUseSkill(sk, actor) && sk.getTemplate().getPower() <= hpReduced) {
                weight = (int) sk.getTemplate().getPower();
                if (hpPercent < 50) {
                    weight += 1000000;
                }
                skillMap.put(sk, weight);
            }
        }
    }

    protected void addDesiredBuff(final Map<SkillEntry, Integer> skillMap, final SkillEntry[] skills) {
        if (skills == null || skills.length == 0) {
            return;
        }
        final NpcInstance actor = getActor();
        for (final SkillEntry sk : skills) {
            if (canUseSkill(sk, actor)) {
                skillMap.put(sk, 1000000);
            }
        }
    }

    protected SkillEntry selectTopSkill(final Map<SkillEntry, Integer> skillMap) {
        if (skillMap == null || skillMap.isEmpty()) {
            return null;
        }
        int nWeight, topWeight = Integer.MIN_VALUE;
        for (final int weight : skillMap.values()) {
            if (weight > topWeight) {
                topWeight = weight;
            }
        }
        if (topWeight == Integer.MIN_VALUE) {
            return null;
        }

        final SkillEntry[] skills = new SkillEntry[skillMap.size()];
        nWeight = 0;
        for (final Map.Entry<SkillEntry, Integer> e : skillMap.entrySet()) {
            if (e.getValue() < topWeight) {
                continue;
            }
            skills[nWeight++] = e.getKey();
        }
        return skills[Rnd.get(nWeight)];
    }

    protected boolean chooseTaskAndTargets(final SkillEntry skillEntry, Creature target, final double distance) {
        final NpcInstance actor = getActor();

        final Skill skill = skillEntry == null ? null : skillEntry.getTemplate();
        // Использовать скилл если можно, иначе атаковать
        if (skill != null) {
            // Проверка цели, и смена если необходимо
            if (actor.isMovementDisabled() && distance > skill.getAOECastRange() + 60) {
                target = null;
                if (skill.isOffensive()) {
                    final List<Creature> targets = new LazyArrayList<>(5);

                    for (final Creature cha : actor.getAggroList().getHateList(MAX_PURSUE_RANGE)) {
                        if (!checkTarget(cha, skill.getAOECastRange() + 60) || !canUseSkill(skillEntry, cha)) {
                            continue;
                        }
                        targets.add(cha);
                    }
                    if (!targets.isEmpty()) {
                        target = Rnd.get(targets);
                    }

                    targets.clear();
                }
            }

            if (target == null) {
                return false;
            }

            // Добавить новое задание
            if (skill.isOffensive()) {
                addTaskCast(target, skillEntry);
            } else {
                addTaskBuff(target, skillEntry);
            }
            return true;
        }

        // Смена цели, если необходимо
        if (actor.isMovementDisabled() && distance > actor.getPhysicalAttackRange() + 40) {
            target = null;
            final List<Creature> targets = new LazyArrayList<>(5);

            for (final Creature cha : actor.getAggroList().getHateList(MAX_PURSUE_RANGE)) {
                if (!checkTarget(cha, actor.getPhysicalAttackRange() + 40)) {
                    continue;
                }
                targets.add(cha);
            }
            if (!targets.isEmpty()) {
                target = targets.get(Rnd.get(targets.size()));
            }

            targets.clear();
        }

        if (target == null) {
            return false;
        }

        // Добавить новое задание
        addTaskAttack(target);
        return true;
    }

    @Override
    public boolean isActive() {
        return haveAiTask.get();
    }

    protected void clearTasks() {
        _def_think = false;
        _tasks.clear();
    }

    /**
     * переход в режим бега через определенный интервал времени
     */
    protected void startRunningTask(final long interval) {
        final NpcInstance actor = getActor();
        if (actor != null && _runningTask == null && !actor.isRunning()) {
            _runningTask = ThreadPoolManager.getInstance().schedule(new RunningTask(), interval);
        }
    }

    protected boolean isGlobalAggro() {
        if (_globalAggro == 0) {
            return true;
        }
        if (_globalAggro <= System.currentTimeMillis()) {
            _globalAggro = 0;
            return true;
        }
        return false;
    }

    public void setGlobalAggro(final long value) {
        _globalAggro = value;
    }

    @Override
    public NpcInstance getActor() {
        return (NpcInstance) super.getActor();
    }

    protected boolean defaultThinkBuff(final int rateSelf) {
        return defaultThinkBuff(rateSelf, 0);
    }

    /**
     * Оповестить дружественные цели об атаке.
     *
     * @param attacker
     * @param damage
     */
    protected void notifyFriends(final Creature attacker, final int damage) {
        final NpcInstance actor = getActor();
        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();
            if (actor.isMinion()) {
                //Оповестить лидера об атаке
                final NpcInstance master = actor.getLeader();
                if (master != null && !actor.isIgnoreLeaderAction()) {
                    if (!master.isDead() && master.isVisible()) {
                        master.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
                    }

                    //Оповестить минионов лидера об атаке
                    final List<NpcInstance> minionList = master.getPrivatesList().getAlivePrivates();
                    if (minionList != null) {
                        minionList.stream().filter(minion -> minion != actor).forEach(minion -> minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage));
                    }
                }
            }

            //Оповестить своих минионов об атаке
            if (actor.hasPrivates()) {
                if (actor.getPrivatesList().hasAlivePrivates()) {
                    for (final NpcInstance minion : actor.getPrivatesList().getAlivePrivates()) {
                        minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
                    }
                }
            }

            //Оповестить социальных мобов
            for (final NpcInstance npc : activeFactionTargets()) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_CLAN_ATTACKED, new Object[]{actor, attacker, damage});
            }
        }
    }

    protected List<NpcInstance> activeFactionTargets() {
        final NpcInstance actor = getActor();
        if (actor.getFaction().isNone()) {
            return Collections.emptyList();
        }

        final int range = actor.getFaction().getRange();
        final List<NpcInstance> npcFriends = new ArrayList<>();
        for (final NpcInstance npc : World.getAroundNpc(actor)) {
            if (!npc.isDead()) {
                if (npc.isInFaction(actor)) {
                    if (npc.isInRangeZ(actor, range)) {
                        if (npc.isInFaction(actor)) {
                            npcFriends.add(npc);
                        }
                    }
                }
            }
        }
        return npcFriends;
    }

    protected boolean defaultThinkBuff(final int rateSelf, final int rateFriends) {
        final NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        //TODO сделать более разумный выбор баффа, сначала выбирать подходящие а потом уже рандомно 1 из них
        if (Rnd.chance(rateSelf)) {
            final double actorHp = actor.getCurrentHpPercents();

            final SkillEntry[] skills = actorHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
            if (skills == null || skills.length == 0) {
                return false;
            }

            final SkillEntry skill = skills[Rnd.get(skills.length)];
            addTaskBuff(actor, skill);
            return true;
        }

        if (Rnd.chance(rateFriends)) {
            for (final NpcInstance npc : activeFactionTargets()) {
                final double targetHp = npc.getCurrentHpPercents();

                final SkillEntry[] skills = targetHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
                if (skills == null || skills.length == 0) {
                    continue;
                }

                final SkillEntry skill = skills[Rnd.get(skills.length)];
                addTaskBuff(actor, skill);
                return true;
            }
        }

        return false;
    }

    protected boolean defaultFightTask() {
        clearTasks();

        final NpcInstance actor = getActor();
        if (actor.isDead() || actor.isAMuted()) {
            return false;
        }

        final Creature target;
        if ((target = prepareTarget()) == null) {
            return false;
        }

        final double distance = actor.getDistance(target);
        final double targetHp = target.getCurrentHpPercents();
        final double actorHp = actor.getCurrentHpPercents();

        final SkillEntry[] dam = Rnd.chance(getRateDAM()) ? selectUsableSkills(target, distance, _damSkills) : null;
        final SkillEntry[] dot = Rnd.chance(getRateDOT()) ? selectUsableSkills(target, distance, _dotSkills) : null;
        final SkillEntry[] debuff = targetHp > 10 ? Rnd.chance(getRateDEBUFF()) ? selectUsableSkills(target, distance, _debuffSkills) : null : null;
        final SkillEntry[] stun = Rnd.chance(getRateSTUN()) ? selectUsableSkills(target, distance, _stunSkills) : null;
        final SkillEntry[] heal = actorHp < 50 ? Rnd.chance(getRateHEAL()) ? selectUsableSkills(actor, 0, _healSkills) : null : null;
        final SkillEntry[] buff = Rnd.chance(getRateBUFF()) ? selectUsableSkills(actor, 0, _buffSkills) : null;

        final RndSelector<SkillEntry[]> rndSelector = actor.isAMuted()
                ? RndSelector.of(RndNode.of(dam, getRateDAM()), RndNode.of(dot, getRateDOT()),
                RndNode.of(debuff, getRateDEBUFF()), RndNode.of(heal, getRateHEAL()), RndNode.of(buff, getRateBUFF()),
                RndNode.of(stun, getRateSTUN()))
                : RndSelector.of(RndNode.of(null, getRatePHYS()), RndNode.of(dam, getRateDAM()), RndNode.of(dot, getRateDOT()),
                RndNode.of(debuff, getRateDEBUFF()), RndNode.of(heal, getRateHEAL()), RndNode.of(buff, getRateBUFF()),
                RndNode.of(stun, getRateSTUN()));

        final SkillEntry[] selected = rndSelector.select();
        if (selected != null) {
            if (Arrays.equals(selected, dam) || Arrays.equals(selected, dot)) {
                return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);
            }

            if (Arrays.equals(selected, debuff) || Arrays.equals(selected, stun)) {
                return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);
            }

            if (Arrays.equals(selected, buff)) {
                if (Rnd.chance(40))
                    return chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);

                // бафаем братанов
                final List<NpcInstance> friendNpcs = activeFactionTargets();
                if (!friendNpcs.isEmpty()) {
                    final NpcInstance friendNpc = friendNpcs.get(Rnd.get(friendNpcs.size()));
                    return chooseTaskAndTargets(selectTopSkillByBuff(friendNpc, selected), friendNpc, distance);
                }
            }

            if (Arrays.equals(selected, heal)) {
                // если у актора меньше 70% хп, то он хилит только себя
                if (actor.getCurrentHpPercents() < 70)
                    return chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);

                // дальше хилим лидера
                if (actor.getLeader() != null && actor.getLeader().getCurrentHpPercents() < 60)
                    return chooseTaskAndTargets(selectTopSkillByHeal(actor.getLeader(), selected), actor.getLeader(), distance);

                // если у лидера больше 60% хп, то хилим братанов
                final List<NpcInstance> friendNpcs = activeFactionTargets();
                if (!friendNpcs.isEmpty()) {
                    final Ordering<NpcInstance> ordering = new Ordering<NpcInstance>() {
                        @Override
                        public int compare(final NpcInstance o1, final NpcInstance o2) {
                            return Double.compare(o1.getCurrentHpPercents(), o2.getCurrentHpPercents());
                        }
                    };
                    final NpcInstance friendNpcWithLessHp = ordering.min(friendNpcs);

                    return chooseTaskAndTargets(selectTopSkillByHeal(friendNpcWithLessHp, selected), friendNpcWithLessHp, distance);
                }
            }
        }

        return chooseTaskAndTargets(null, target, distance);
    }

    public int getRatePHYS() {
        return 100;
    }

    public int getRateDOT() {
        return 0;
    }

    public int getRateDEBUFF() {
        return 0;
    }

    public int getRateDAM() {
        return 0;
    }

    public int getRateSTUN() {
        return 0;
    }

    public int getRateBUFF() {
        return 0;
    }

    public int getRateHEAL() {
        return 0;
    }

    public boolean getIsMobile() {
        return !getActor().getParameter("isImmobilized", false);
    }

    public int getMaxPathfindFails() {
        return 3;
    }

    /**
     * Задержка, перед переключением в активный режим после атаки, если цель не найдена (вне зоны досягаемости, убита, очищен хейт)
     *
     * @return
     */
    public int getMaxAttackTimeout() {
        return 15000;
    }

    /**
     * Задержка, перед телепортом к цели, если не удается дойти
     *
     * @return
     */
    public int getTeleportTimeout() {
        return 10000;
    }

    protected SuperPoint getSuperPoint() {
        if (superPoint != null) {
            return superPoint;
        }
        final NpcInstance actor = getActor();
        final Spawner spawn = actor.getSpawn();
        if (spawn == null)
            return null;

        superPoint = spawn.getSuperPoint();

        return superPoint;
    }

    public void setSuperPoint(final SuperPoint superPoint) {
        this.superPoint = superPoint;
    }

    /**
     * При атаке моба ставим в УД
     *
     * @param attacker - атакующий
     * @param skill    - каким скилом был атакован
     */
    protected void checkRangeGuard(final Creature attacker, final SkillEntry skill) {
        final NpcInstance actor = getActor();
        final int LongRangeGuardRate = actor.getParameter("LongRangeGuardRate", -1);
        if (LongRangeGuardRate == -1) {
            return;
        }
        if (actor.isMinion() || actor.hasPrivates() || actor.isRaid() || actor instanceof ReflectionBossInstance || actor instanceof ChestInstance) {
            return;
        }
        final int skill_id = skill == null ? 0 : skill.getId();
        if (ArrayUtils.contains(check_skill_id, skill_id)) {
            return;
        } else if (LongRangeGuardRate > 0) {
            final int i11 = actor.getEffectList().getStackOrder(s_npc_ultimate_defence3[0]);
            if (actor.getDistance(attacker) > 150) {
                if (i11 <= 0 && Rnd.get(100) < LongRangeGuardRate) {
                    final SkillEntry skills = SkillTable.getInstance().getSkillEntry(s_npc_ultimate_defence3[0], s_npc_ultimate_defence3[1]);
                    if (skills != null) {
                        skills.getEffects(actor, actor, false, false);
                    }
                }
            } else if (i11 <= 0) {
                return;
            } else {
                actor.getEffectList().stopEffect(s_npc_ultimate_defence3[0]);
            }
        }
    }

    protected Creature getNearestTarget(List<Creature> targets) {
        NpcInstance actor = getActor();

        Creature nextTarget = null;
        long minDist = Long.MAX_VALUE;

        Creature target;
        for (Creature target1 : targets) {
            target = target1;
            long dist = actor.getXYZDeltaSq(target.getX(), target.getY(), target.getZ());
            if (dist < minDist)
                nextTarget = target;
        }

        return nextTarget;
    }

    public enum TaskType {
        MOVE,
        ATTACK,
        CAST,
        BUFF
    }

    public static class Task {
        public TaskType type;
        public SkillEntry skill;
        public HardReference<? extends Creature> target;
        public Location loc;
        public boolean pathfind;
        public int weight = TaskDefaultWeight;
    }

    private static class TaskComparator implements Comparator<Task> {
        private static final Comparator<Task> instance = new TaskComparator();

        public static Comparator<Task> getInstance() {
            return instance;
        }

        @Override
        public int compare(final Task o1, final Task o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return o2.weight - o1.weight;
        }
    }

    protected static class NearestTargetComparator implements Comparator<TargetContains> {
        @Override
        public int compare(final TargetContains o1, final TargetContains o2) {
            double diff = o1.getDistance() - o2.getDistance();
            if (diff < 0D) {
                return -1;
            }
            return diff > 0 ? 1 : 0;
        }
    }

    protected class Teleport extends RunnableImpl {
        final Location _destination;

        public Teleport(final Location destination) {
            _destination = destination;
        }

        @Override
        protected void runImpl() throws Exception {
            final NpcInstance actor = getActor();
            if (actor != null) {
                actor.teleToLocation(_destination);
            }
        }
    }

    protected class RunningTask extends RunnableImpl {
        @Override
        protected void runImpl() throws Exception {
            final NpcInstance actor = getActor();
            if (actor != null) {
                actor.setRunning();
            }
            _runningTask = null;
        }
    }

    public class MadnessTask extends RunnableImpl {
        @Override
        protected void runImpl() throws Exception {
            final NpcInstance actor = getActor();
            if (actor != null) {
                actor.stopConfused();
            }
            _madnessTask = null;
        }
    }

    protected class TargetContains {
        private final double distance;
        private final Creature creature;

        public TargetContains(double distance, Creature creature) {
            this.distance = distance;
            this.creature = creature;
        }

        public double getDistance() {
            return distance;
        }

        public Creature getCreature() {
            return creature;
        }
    }
}