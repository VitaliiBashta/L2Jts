package org.mmocore.gameserver.model.entity.events.impl;

import org.apache.logging.log4j.util.Strings;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.listener.actor.OnDeathFromUndyingListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventInstance;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RTeamType;
import org.mmocore.gameserver.model.entity.events.impl.reflection.ResurrectTask;
import org.mmocore.gameserver.model.entity.events.impl.reflection.listener.RAnswerListener;
import org.mmocore.gameserver.model.entity.events.impl.reflection.listener.RPlayerListener;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RFlagObject;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RRaidBossObject;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RSnapshotObject;
import org.mmocore.gameserver.model.entity.events.impl.reflection.raid.RRaidBossInstance;
import org.mmocore.gameserver.model.reward.RewardItem;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class ReflectionEvent extends SingleMatchEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionEvent.class);
    private static final List<Location> location = Arrays.asList(new Location(140344, 140344, -15264), new Location(140312, 145034, -15264), new Location(145176, 145304, -15264), new Location(145064, 140328, -15264), new Location(142389, 142785, -15624));
    private static final String REGISTRATION = "registration";
    private final Map<Integer, ScheduledFuture<?>> deathMap;
    private final int stealFlagPoint;
    private final int maxPoint;
    private final int killPoint;
    private final Map<RTeamType, RBaseController> baseControllerMap;
    private final MultiValueSet<String> set;
    private final int minLevel;
    private final int maxLevel;
    private final CronExpression pattern;
    private Instant startTime;
    private REventState eventState = REventState.NONE;
    private final RPlayerListener<ReflectionEvent> playerListeners;
    //private RDeathListener<ReflectionEvent> deathListener;
    private final OnDeathFromUndyingListener onDeathFromUndyingListener;
    //private RReviveListener<ReflectionEvent> reviveListener;
    private RBaseController winner;
    private EventInstance reflection;
    private RRaidBossObject raidBossObject;
    private RFlagObject flag;
    private ScheduledFuture<?> endTimeTask;
    private final int endTime;

    public ReflectionEvent(final MultiValueSet<String> set) {
        super(set);
        this.set = set;
        this.pattern = QuartzUtils.createCronExpression(set.getString("pattern"));
        this.baseControllerMap = new HashMap<>();
        this.stealFlagPoint = getSet().getInteger("stealFlagPoint");
        this.maxPoint = getSet().getInteger("maxPoint");
        this.killPoint = getSet().getInteger("killPoint");
        this.playerListeners = new RPlayerListener<>(this);
        //this.deathListener = new RDeathListener<>(this);
        this.onDeathFromUndyingListener = new OnDeathFromUndyingListenerImpl();
        this.deathMap = new ConcurrentHashMap<>();
        this.minLevel = set.getInteger("min_level");
        this.maxLevel = set.getInteger("max_level");
        this.endTime = set.getInteger("end_time");
    }

    private static boolean checkTeleport(final Player player, final int minLevel, final int maxLevel) {
        if (!player.getReflection().isDefault()) {
            //LOGGER.info(" player = " + player.getName() + " !getReflection().isDefault");
            return false;
        }
        if (player.isMounted()) {
            //LOGGER.info(" player = " + player.getName() + " isMounted()");
            return false;
        }
        if (player.isDead()) {
            //LOGGER.info(" player = " + player.getName() + " isDead()");
            return false;
        }
        if (player.isInObserverMode()) {
            //LOGGER.info(" player = " + player.getName() + " player.isInObserverMode()");
            return false;
        }
        if (player.isTeleporting()) {
            //LOGGER.info(" player = " + player.getName() + " isTeleporting()");
            return false;
        }
        if (player.isInOfflineMode()) {
            //LOGGER.info(" player = " + player.getName() + " isInOfflineMode()");
            return false;
        }
        if (player.isTransformed()) {
            return false;
        }
        if (player.isCursedWeaponEquipped()) {
            return false;
        }
        if (player.getLevel() < minLevel || player.getLevel() > maxLevel) {
            return false;
        }
        return true;
    }

/*	public boolean fastStartAndStop(final byte state) {
		switch(state) {
			case 0: {
				if(isInProgress()) {
					stopEvent();
					return true;
				}
				break;
			}
			case 1: {
				if(!isInProgress()) {
					clearActions();
					registerActions(true);
					return true;
				}
				break;
			}
		}
		return false;
	}*/

    @Override
    public void reCalcNextTime(boolean onInit) {
        if (isInProgress()) {
            return;
        }
        clearActions();
        baseControllerMap.clear();
        final List<Location> loc = new ArrayList<>(location);
        Collections.shuffle(loc);
        Stream.of(RTeamType.values()).filter(t -> t != RTeamType.NONE).forEach(r -> baseControllerMap.put(r, new RBaseController(loc.get(r.ordinal() - 1), r)));
        startTime = Instant.ofEpochMilli(pattern.getNextValidTimeAfter(new Date()).getTime() + 60000L);
        registerActions();
    }

    @Override
    public EventType getType() {
        return EventType.PVP_EVENT;
    }

    @Override
    protected Instant startTime() {
        return startTime;
    }

    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION)) {
            eventState = REventState.REGISTRATION;
            inviteAskPlayers();
        } else {
            super.action(name, start);
        }
    }

    private void inviteAskPlayers() {
        GameObjectsStorage.getPlayers().stream().filter(p -> p != null && p.getReflection().isDefault() && !p.isInOlympiadMode() && !p.isInObserverMode() && !p.isOnSiegeField()).forEach(p -> {
            final String msg = new CustomMessage("event.r.register").toString(p);
            final ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 480000).addString(msg);
            p.ask(ask, new RAnswerListener<>(this, p, 480000));
        });
    }

    public synchronized void addPlayer(final Player player) {
        if (getEventState() != REventState.REGISTRATION)
            return;
        player.addEvent(this);
        int minPlayer = 0;
        RTeamType teamType = RTeamType.NONE;
        boolean noPlayers = baseControllerMap.values().stream().allMatch(sh -> sh.getPlayers().size() == 0);
        if (noPlayers) {
            baseControllerMap.get(Rnd.get(Stream.of(RTeamType.values()).filter(team -> team != RTeamType.NONE).collect(Collectors.toList()))).addPlayer(player);
        } else {
            for (final Map.Entry<RTeamType, RBaseController> team : baseControllerMap.entrySet()) {
                List<RSnapshotObject> snapshotObjects = team.getValue().getPlayers();
                if (minPlayer != 0 && snapshotObjects.size() < minPlayer || minPlayer == 0 && snapshotObjects.size() == 0) {
                    minPlayer = snapshotObjects.size();
                    teamType = team.getKey();
                }
            }
            if (teamType == RTeamType.NONE)
                baseControllerMap.get(Rnd.get(Stream.of(RTeamType.values()).filter(team -> team != RTeamType.NONE).collect(Collectors.toList()))).addPlayer(player);
            else
                baseControllerMap.get(teamType).addPlayer(player);
        }
    }

    public REventState getEventState() {
        return eventState;
    }

    @Override
    public void startEvent() {
        eventState = REventState.PROCESS;
        flag = new RFlagObject(this);
        flag.spawnObject();
        baseControllerMap.values().stream().filter(p -> !p.getPlayers().isEmpty()).forEach(b -> {
            b.getPlayers().stream().filter(p -> p != null && p.getPlayer() != null).forEach(p -> {
                if (p.getPlayer().isFrozen())
                    p.getPlayer().stopFrozen();
                p.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, 0, 0, 0));
                p.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, flag.getBaseLocation()));
            });
            b.spawnObject(this);
        });
        stopEndTime();
        endTimeTask = ThreadPoolManager.getInstance().schedule(this::stopEvent, endTime, TimeUnit.MINUTES);
        super.startEvent();
    }

    @Override
    public void stopEvent() {
        if (!isInProgress())
            return;
        deathMap.values().stream().forEach(t -> t.cancel(true));
        deathMap.clear();
        if (raidBossObject != null) {
            raidBossObject.despawnObject();
        }
        if (flag != null) {
            flag.despawnObject();
        }
        baseControllerMap.values().stream().filter(Objects::nonNull).forEach(c -> {
            c.getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
                p.restore(false);
                p.getPlayer().setReflection(ReflectionManager.DEFAULT);
                p.getPlayer().getCustomPlayerComponent().setName(Strings.EMPTY);
                p.getPlayer().getCustomPlayerComponent().setTitleName(Strings.EMPTY);
                p.getPlayer().getCustomPlayerComponent().setNameColor(CustomPlayerComponent.DEFAULT);
                p.getPlayer().getCustomPlayerComponent().setTitleColor(CustomPlayerComponent.DEFAULT);
                p.getPlayer().broadcastCharInfo();
                p.getPlayer().getEventComponent().removeTeam(this.getClass());
                p.getPlayer().setUndying(false);
                p.teleportBack();
                p.getPlayer().removeEvent(this);
            });
            if (c.getNpc() != null)
                c.despawnObject(this);
        });
        getBaseControllerMap().clear();
        if (reflection != null) {
            reflection.collapse();
        }
        if (_tasks != null) {
            for (final Future<?> f : _tasks) {
                f.cancel(true);
            }
            _tasks.clear();
        }
        stopEndTime();
        super.stopEvent();
        eventState = REventState.NONE;
    }

    @Override
    public void teleportPlayers(final String teleportWho, final ZoneType zoneType) {
        eventState = REventState.CLOSE_REGISTRATION;
        baseControllerMap.values().stream().forEach(b -> b.getPlayers().stream().forEach(o -> {
            final Player player = o.getPlayer();
            if (player != null) {
                if (!checkTeleport(player, minLevel, maxLevel)) {
                    player.sendMessage(new CustomMessage("event.r.ifNoCorrect"));
                    b.getPlayers().remove(o);
                }
            } else
                b.getPlayers().remove(o);
        }));
        reflection = new EventInstance();
        reflection.init(InstantZoneHolder.getInstance().getInstantZone(999));
        final boolean anySize = baseControllerMap.values().stream().anyMatch(o -> o.getPlayers().size() >= 1);
        if (anySize) {
            baseControllerMap.values().stream().forEach(c -> c.teleportPlayer(true, reflection));
        } else {
            announce(2);
            stopEvent();
        }
    }

    private void stopEndTime() {
        if (endTimeTask != null) {
            endTimeTask.cancel(true);
            endTimeTask = null;
        }
    }

    private void announce(final CustomMessage msg) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            final Say2 cs = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", new CustomMessage("event.r.name").toString(player) + " : " + msg.toString(player), player.getLanguage());
            player.sendPacket(cs);
        }
    }

    @Override
    public void announce(int a) {
        switch (a) {
            case 2: {
                announce(new CustomMessage("event.r.participants"));
                break;
            }
            case 1: {
                announce(new CustomMessage("event.r.register"));
                break;
            }
            case 0: {
                announce(new CustomMessage("event.r.reg_completed"));
                break;
            }
            default:
                break;
        }
    }

    public void exitPlayer(Player player) {
        final RTeamType playerType = player.getEventComponent().getTeam(ReflectionEvent.class);
        if (playerType == null)
            return;
        final RBaseController controller = getBaseController(playerType);
        final Optional<RSnapshotObject> optional = controller.getPlayers().stream().filter(o -> o.getPlayer().getObjectId() == player.getObjectId()).findFirst();
        if (optional.isPresent()) {
            optional.get().getPlayer().setReflection(ReflectionManager.DEFAULT);
            optional.get().getPlayer().getEventComponent().removeTeam(this.getClass());
            optional.get().getPlayer().removeEvent(this);
            optional.get().getPlayer().getCustomPlayerComponent().setName(Strings.EMPTY);
            optional.get().getPlayer().getCustomPlayerComponent().setTitleName(Strings.EMPTY);
            optional.get().getPlayer().getCustomPlayerComponent().setNameColor(CustomPlayerComponent.DEFAULT);
            optional.get().getPlayer().getCustomPlayerComponent().setTitleColor(CustomPlayerComponent.DEFAULT);
            optional.get().getPlayer().setUndying(false);
            optional.get().getPlayer().broadcastCharInfo();
            controller.getPlayers().remove(optional.get());
        }
        final boolean playerEmpty = getBaseControllerMap().values().stream().allMatch(p -> p != null && p.getPlayers().isEmpty());
        if (playerEmpty) {
            stopEvent();
        }
    }

    public synchronized void checkWinner() {
        if (eventState == REventState.RAID_BOSS) {
            return;
        }
        final List<RBaseController> playerEmpty = getBaseControllerMap().values().stream().filter(p -> p.getPlayers().size() >= 1).collect(Collectors.toList());
        if (playerEmpty.isEmpty()) {
            stopEvent();
        } else {
            final List<RBaseController> winners = getBaseControllerMap().values().stream().filter(p -> p.getPoint() >= getMaxPoint()).collect(Collectors.toList());
            if (!winners.isEmpty()) {
                eventState = REventState.RAID_BOSS;
                if (flag != null) {
                    flag.despawnObject();
                }
                final RBaseController controller = Rnd.get(winners);
                controller.setWinner(true);
                winner = controller;
                raidBossObject = new RRaidBossObject(winner);
                raidBossObject.spawnObject();
                final Location locRB = raidBossObject.getRaidBoss().getSpawnedLoc();
                getBaseControllerMap().values().stream().filter(b -> b != null && !b.getPlayers().isEmpty()).forEach(s -> s.getPlayers().stream().filter(p -> p != null).forEach(p -> {
                    if (s.getTeamType().ordinal() != winner.getTeamType().ordinal()) {
                        List<Serializable> list = controller.getEvent().getObjects("rewardLose");
                        if (list != null && !list.isEmpty()) {
                            list.stream().filter(o -> o instanceof RewardList).forEach(l -> {
                                RewardList rewardGroups = (RewardList) l;
                                List<RewardItem> rewardItems = rewardGroups.rollEvent();
                                rewardItems.forEach(i -> ItemFunctions.addItem(p.getPlayer(), i.itemId, i.count, true));
                            });
                        }
                        p.getPlayer().sendMessage(new CustomMessage("event.r.win").addString(winner.getTeamType().getNameTeam()));
                        p.getPlayer().setReflection(ReflectionManager.DEFAULT);
                        p.restore(false);
                        p.teleportBack();
                        p.getPlayer().getCustomPlayerComponent().setName(Strings.EMPTY);
                        p.getPlayer().getCustomPlayerComponent().setTitleName(Strings.EMPTY);
                        p.getPlayer().getCustomPlayerComponent().setNameColor(CustomPlayerComponent.DEFAULT);
                        p.getPlayer().getCustomPlayerComponent().setTitleColor(CustomPlayerComponent.DEFAULT);
                        p.getPlayer().getEventComponent().removeTeam(this.getClass());
                        p.getPlayer().broadcastCharInfo();
                        p.getPlayer().setUndying(false);
                        p.getPlayer().removeEvent(this);
                    } else {
                        List<Serializable> list = controller.getEvent().getObjects("rewardWin");
                        if (list != null && !list.isEmpty()) {
                            list.stream().filter(o -> o instanceof RewardList).forEach(l -> {
                                RewardList rewardGroups = (RewardList) l;
                                List<RewardItem> rewardItems = rewardGroups.rollEvent();
                                rewardItems.forEach(i -> ItemFunctions.addItem(p.getPlayer(), i.itemId, i.count, true));
                            });
                        }
                        p.getPlayer().sendMessage(new CustomMessage("event.r.win_next_raid"));
                        p.getPlayer().addRadar(locRB.getX(), locRB.getY(), locRB.getZ());
                    }
                }));
                Stream.of(RTeamType.values()).filter(race -> race.ordinal() != winner.getTeamType().ordinal()).forEach(b -> getBaseControllerMap().remove(b));
                reflection.openDoor(24220040);
            }
        }
    }

    @Override
    public void onAddEvent(final GameObject o) {
        super.onAddEvent(o);
        if (o.isPlayer()) {
            o.getPlayer().addListener(onDeathFromUndyingListener);
            //o.getPlayer().addListener(deathListener);
            o.getPlayer().addListener(playerListeners);
            //o.getPlayer().addListener(reviveListener);
        }
    }

    @Override
    public void onRemoveEvent(final GameObject o) {
        super.onRemoveEvent(o);
        if (o.isPlayer()) {
            o.getPlayer().removeListener(onDeathFromUndyingListener);
            //o.getPlayer().removeListener(deathListener);
            o.getPlayer().removeListener(playerListeners);
            //o.getPlayer().removeListener(reviveListener);
        }
    }

    @Override
    public Reflection getReflection() {
        return reflection;
    }

    public MultiValueSet<String> getSet() {
        return set;
    }

    public int getStealFlagPoint() {
        return stealFlagPoint;
    }

    public int getMaxPoint() {
        return maxPoint;
    }

    public int getKillPoint() {
        return killPoint;
    }

    public Map<RTeamType, RBaseController> getBaseControllerMap() {
        return baseControllerMap;
    }

    public RBaseController getBaseController(final RTeamType rTeamType) {
        return baseControllerMap.get(rTeamType);
    }

    public RBaseController getWinner() {
        return winner;
    }

    public Map<Integer, ScheduledFuture<?>> getDeathMap() {
        return deathMap;
    }

    public void addDeathPlayer(final Player player, final RBaseController controller) {
        final ResurrectTask task = new ResurrectTask(player, controller);
        task.sendMessage();
        final ScheduledFuture<?> schedule = ThreadPoolManager.getInstance().schedule(task, 15000);
        final ScheduledFuture<?> oldSchedule = getDeathMap().remove(player.getObjectId());
        if (oldSchedule != null) {
            oldSchedule.cancel(true);
        }
        deathMap.put(player.getObjectId(), schedule);
    }

    @Override
    public boolean isInProgress() {
        return eventState != REventState.NONE;
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (actor == null || killer == null) {
            return;
        }
        if (getEventState() == REventState.RAID_BOSS) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                final RTeamType teamType = player.getEventComponent().getTeam(ReflectionEvent.class);
                if (teamType != null) {
                    final RBaseController controllerActor = getBaseControllerMap().get(teamType);
                    if (controllerActor != null && actor.isPlayer() && killer.isMonster()) {
                        if (killer instanceof RRaidBossInstance) {
                            final RRaidBossInstance raid = (RRaidBossInstance) killer;
                            final List<Creature> nextTargets = actor.getAroundCharacters(200, 200).stream().filter(target -> !target.isDead()).filter(GameObject::isPlayable).collect(Collectors.toList());
                            if (nextTargets != null && !nextTargets.isEmpty()) {
                                Creature nextTarget = Rnd.get(nextTargets);
                                if (nextTarget != null && killer.isInRange(nextTarget, 200)) {
                                    raid.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, nextTarget);
                                    raid.getAggroList().addDamageHate(nextTarget, 100, 100);
                                }
                            }
                        }
                        player.stopAttackStanceTask();
                        player.startFrozen();
                        World.removeObjectFromPlayers(player);
                        addDeathPlayer(player, controllerActor);
                    }
                }
            }
        } else if (getEventState() == REventState.PROCESS) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
                if (flagItemAttachment != null && flagItemAttachment instanceof RFlagObject) {
                    RFlagObject flagObject = (RFlagObject) flagItemAttachment;
                    flagObject.onDeath(player, killer);
                }
                final RTeamType playerType = player.getEventComponent().getTeam(ReflectionEvent.class);
                if (playerType == null)
                    return;
                final RBaseController controllerActor = getBaseController(playerType);
                if (controllerActor == null)
                    return;
                if (killer.isPlayable()) {
                    final Player killers = killer.getPlayer();
                    final RTeamType killerType = killers.getEventComponent().getTeam(ReflectionEvent.class);
                    if (killerType == null)
                        return;
                    final RBaseController controllerKiller = getBaseController(killerType);
                    if (controllerKiller == null)
                        return;
                    if (playerType != killerType) {
                        final int point = controllerKiller.addAndGet(getKillPoint());
                        controllerKiller.addPlayerPoint(killers, getKillPoint());
                        killers.sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.newCount").addNumber(Math.max(0, getMaxPoint() - point)).toString(killers), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                        checkWinner();
                    }
                }
                player.stopAttackStanceTask();
                player.startFrozen();
                World.removeObjectFromPlayers(player);
                addDeathPlayer(player, controllerActor);
            }
        }
    }

    @Override
    public boolean canAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force, final boolean nextAttackCheck) {
        if (target.isPlayable() && attacker.isPlayable()) {
            final RTeamType playerType = target.getPlayer().getEventComponent().getTeam(ReflectionEvent.class);
            if (playerType == null)
                return true;
            final RTeamType attackerType = attacker.getPlayer().getEventComponent().getTeam(ReflectionEvent.class);
            if (attackerType == null)
                return true;
            if (playerType == attackerType)
                return false;
            if (getEventState() == REventState.FINISH)
                return false;
        }
        return true;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public ScheduledFuture<?> getEndTimeTask() {
        return endTimeTask;
    }
}
