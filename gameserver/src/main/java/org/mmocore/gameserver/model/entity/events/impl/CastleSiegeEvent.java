package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ResidenceConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.CastleDamageZoneDAO;
import org.mmocore.gameserver.database.dao.impl.CastleDoorUpgradeDAO;
import org.mmocore.gameserver.database.dao.impl.CastleHiredGuardDAO;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.listener.actor.OnKillListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.HeroDiary;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.*;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.ArtefactInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.custom.CastleRewardTemplate;
import org.mmocore.gameserver.templates.item.support.MerchantGuard;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author VISTALL
 * @date 15:12/14.02.2011
 */
public class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject> {
    public static final int MAX_SIEGE_CLANS = 500;
    public static final int BASE_SIEGE_FAME = 72;
    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    public static final String DEFENDERS_WAITING = "defenders_waiting";
    public static final String DEFENDERS_REFUSED = "defenders_refused";
    public static final String CONTROL_TOWERS = "control_towers";
    public static final String FLAME_TOWERS = "flame_towers";
    public static final String BOUGHT_ZONES = "bought_zones";
    public static final String GUARDS = "guards";
    public static final String HIRED_GUARDS = "hired_guards";
    private static final Logger _log = LoggerFactory.getLogger(CastleSiegeEvent.class);
    private static Map<Integer, List<CastleRewardTemplate>> additionalRewards;
    private static Map<Integer, List<CastleRewardTemplate>> leaderAdditionalRewards;

    static {
        try {
            if (LostDreamCustom.allowAdditionalCastleRewards) {
                parseAdditionalRewards();
                parseLeaderAdditionalRewards();
            }
        } catch (Exception e) {
            _log.warn("Can't parse additional rewards for castle siege victory!");
        }
    }

    private Set<Integer> nextSiegeTimes = Collections.emptySet();
    private Future<?> nextSiegeDateSetTask;
    private boolean firstStep;
    public CastleSiegeEvent(final MultiValueSet<String> set) {
        super(set);
        killListener = new KillListener();
    }

    //TODO[Hack]: надо бы написать лоадер под такую хрень и вынести это все в него
    private static void parseAdditionalRewards() throws Exception {
        additionalRewards = new HashMap<>();
        int i = 1;
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.gludioCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.dionCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.giranCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.orenCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.adenCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.innadrilCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.goddardCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.runeCastleRewards, CastleRewardTemplate.class));
        additionalRewards.put(i, Util.parseTemplateConfig(LostDreamCustom.shuttgartCastleRewards, CastleRewardTemplate.class));
    }

    private static void parseLeaderAdditionalRewards() throws Exception {
        leaderAdditionalRewards = new HashMap<>();
        int i = 1;
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.gludioLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.dionLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.giranLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.orenLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.adenLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.innadrilLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.goddardLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i++, Util.parseTemplateConfig(LostDreamCustom.runeLeaderCastleRewards, CastleRewardTemplate.class));
        leaderAdditionalRewards.put(i, Util.parseTemplateConfig(LostDreamCustom.shuttgartLeaderCastleRewards, CastleRewardTemplate.class));
    }

    //========================================================================================================================================================================
    //                                                    Главные методы осады
    //========================================================================================================================================================================
    @Override
    public void initEvent() {
        super.initEvent();

        if (getResidence().getOwner() != null) {
            final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.registerDominion(getResidence().getDominion(), true);
        }

        final List<DoorObject> doorObjects = getObjects(DOORS);

        addObjects(BOUGHT_ZONES, CastleDamageZoneDAO.getInstance().load(getResidence()));

        for (final DoorObject doorObject : doorObjects) {
            doorObject.setUpgradeValue(this, CastleDoorUpgradeDAO.getInstance().load(doorObject.getUId()));
            doorObject.getDoor().addListener(_doorDeathListener);
        }
    }

    public synchronized void processCastArtefact(final Clan clan, final ArtefactInstance artefact) {
        if (clan == null)
            return;
        SpawnExObject spawnExObject = (SpawnExObject) getObjects("artefact").get(0);
        if (spawnExObject == null) {
            return;
        }
        artefact.clanCastArtefact(clan);
        boolean win = true;
        for (NpcInstance npc : spawnExObject.getAllSpawned()) {
            ArtefactInstance art = ArtefactInstance.class.cast(npc);
            if (!art.isEqualsClanCast(clan)) {
                win = false;
                break;
            }
        }
        if (win)
            processStep(clan);

    }

    @Override
    public void processStep(final Clan newOwnerClan) {
        final Clan oldOwnerClan = getResidence().getOwner();

        getResidence().changeOwner(newOwnerClan);

        // если есть овнер в резиденции, делаем его аттакером
        if (oldOwnerClan != null) {
            final SiegeClanObject ownerSiegeClan = getSiegeClan(DEFENDERS, oldOwnerClan);
            removeObject(DEFENDERS, ownerSiegeClan);

            ownerSiegeClan.setType(ATTACKERS);
            addObject(ATTACKERS, ownerSiegeClan);
        } else {
            // Если атакуется замок, принадлежащий NPC, и только 1 атакующий - закончить осаду
            if (getObjects(ATTACKERS).size() == 1) {
                stopEvent();
                return;
            }

            // Если атакуется замок, принадлежащий NPC, и все атакующие в одном альянсе - закончить осаду
            final int allianceObjectId = newOwnerClan.getAllyId();
            if (allianceObjectId > 0) {
                final List<SiegeClanObject> attackers = getObjects(ATTACKERS);
                boolean sameAlliance = true;
                for (final SiegeClanObject sc : attackers) {
                    if (sc != null && sc.getClan().getAllyId() != allianceObjectId) {
                        sameAlliance = false;
                    }
                }
                if (sameAlliance) {
                    stopEvent();
                    return;
                }
            }
        }

        // ставим нового овнера защитником
        final SiegeClanObject newOwnerSiegeClan = getSiegeClan(ATTACKERS, newOwnerClan);
        newOwnerSiegeClan.deleteFlag();
        newOwnerSiegeClan.setType(DEFENDERS);

        removeObject(ATTACKERS, newOwnerSiegeClan);

        // у нас защитник ток овнер
        final List<SiegeClanObject> defenders = removeObjects(DEFENDERS);
        for (final SiegeClanObject siegeClan : defenders) {
            siegeClan.setType(ATTACKERS);
        }

        // новый овнер это защитник
        addObject(DEFENDERS, newOwnerSiegeClan);

        // все дефендеры, стают аттакующими
        addObjects(ATTACKERS, defenders);

        updateParticles(true, ATTACKERS, DEFENDERS);

        // Респавним Control Towers, при смене защитника.
        spawnAction(CONTROL_TOWERS, true);

        teleportPlayers(FROM_RESIDENCE_TO_TOWN, null);
        //teleportPlayers(ATTACKERS, ZoneType.SIEGE);
        //teleportPlayers(SPECTATORS, ZoneType.SIEGE);
        // ток при первом захвате обнуляем мерчант гвардов и убираем апгрейд дверей
        if (!firstStep) {
            firstStep = true;

            broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, ATTACKERS, DEFENDERS);

            if (_oldOwner != null) {
                spawnAction(HIRED_GUARDS, false);
                damageZoneAction(false);
                removeObjects(HIRED_GUARDS);
                removeObjects(BOUGHT_ZONES);

                CastleDamageZoneDAO.getInstance().delete(getResidence());
            } else {
                spawnAction(GUARDS, false);
            }

            final List<DoorObject> doorObjects = getObjects(DOORS);
            for (final DoorObject doorObject : doorObjects) {
                doorObject.setWeak(true);
                doorObject.setUpgradeValue(this, 0);

                CastleDoorUpgradeDAO.getInstance().delete(doorObject.getUId());
            }
        }

        spawnAction(DOORS, true);
        despawnSiegeSummons();
    }

    @Override
    public void startEvent() {
        final List<SiegeClanObject> attackers = getObjects(ATTACKERS);
        if (attackers.isEmpty()) {
            if (getResidence().getOwner() == null) {
                broadcastToWorld(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            } else {
                broadcastToWorld(new SystemMessage(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));

                getResidence().getOwner().setCastleDefendCount(getResidence().getOwner().getCastleDefendCount() + 1);
                getResidence().getOwner().updateClanInDB();
            }

            getResidence().setOwnDate(getResidence().getOwner() == null ? Residence.MIN_SIEGE_DATE : ZonedDateTime.now());

            reCalcNextTime(false);
            return;
        }

        _oldOwner = getResidence().getOwner();
        if (_oldOwner != null) {
            addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));

            if (!getResidence().getSpawnMerchantTickets().isEmpty()) {
                for (final ItemInstance item : getResidence().getSpawnMerchantTickets()) {
                    final MerchantGuard guard = getResidence().getMerchantGuard(item.getItemId());

                    addObject(HIRED_GUARDS, new SpawnSimpleObject(guard.getNpcId(), item.getLoc()));

                    item.deleteMe();
                }

                CastleHiredGuardDAO.getInstance().delete(getResidence());

                spawnAction(HIRED_GUARDS, true);
            }
        }

        SiegeClanDAO.getInstance().delete(getResidence());

        updateParticles(true, ATTACKERS, DEFENDERS);

        broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, ATTACKERS);
        broadcastTo(new SystemMessage(SystemMsg.YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS)
                .addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        super.startEvent();

        if (_oldOwner == null) {
            initControlTowers();
        } else {
            damageZoneAction(true);
        }
    }

    @Override
    public void stopEvent(final boolean step) {
        final List<DoorObject> doorObjects = getObjects(DOORS);
        for (final DoorObject doorObject : doorObjects) {
            doorObject.setWeak(false);
        }

        damageZoneAction(false);

        blockedFameOnKill.clear();

        updateParticles(false, ATTACKERS, DEFENDERS);

        final List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
        for (final SiegeClanObject siegeClan : attackers) {
            siegeClan.deleteFlag();
        }

        broadcastToWorld(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));

        removeObjects(DEFENDERS);
        removeObjects(DEFENDERS_WAITING);
        removeObjects(DEFENDERS_REFUSED);

        final Clan ownerClan = getResidence().getOwner();
        if (ownerClan != null) {
            if (_oldOwner == ownerClan) {
                getResidence().getOwner().setCastleDefendCount(getResidence().getOwner().getCastleDefendCount() + 1);
                getResidence().getOwner().updateClanInDB();

                ownerClan
                        .broadcastToOnlineMembers(new SystemMessage(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
                                .addNumber(ownerClan.incReputation(1500, false, toString())));
            } else {
                broadcastToWorld(new SystemMessage(SystemMsg.CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE).addString(ownerClan.getName()).addResidenceName(getResidence()));

                ownerClan
                        .broadcastToOnlineMembers(new SystemMessage(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
                                .addNumber(ownerClan.incReputation(3000, false, toString())));

                if (_oldOwner != null) {
                    _oldOwner
                            .broadcastToOnlineMembers(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOU_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS)
                                    .addNumber(-_oldOwner.incReputation(-3000, false, toString())));
                }

                for (final UnitMember member : ownerClan) {
                    final Player player = member.getPlayer();
                    if (player != null) {
                        player.sendPacket(PlaySound.SIEGE_VICTORY);
                        if (player.isOnline() && player.isNoble()) {
                            Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_CASTLE_TAKEN, getResidence().getId());
                        }
                    }
                }
            }

            cancelNextSieges(ownerClan);

            getResidence().setOwnDate(ZonedDateTime.now());
            getResidence().setLastSiegeDate(getResidence().getSiegeDate());

            final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.registerDominion(getResidence().getDominion(), false);

            getResidence().getDominion().rewardSkills();
        } else {
            broadcastToWorld(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

            getResidence().setOwnDate(Residence.MIN_SIEGE_DATE);
            getResidence().setLastSiegeDate(getResidence().getSiegeDate());

            final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
            runnerEvent.unRegisterDominion(getResidence().getDominion());
        }

        despawnSiegeSummons();

        if (_oldOwner != null) {
            spawnAction(HIRED_GUARDS, false);
            removeObjects(HIRED_GUARDS);
        }

        super.stopEvent(step);
        if (LostDreamCustom.allowAdditionalCastleRewards)
            manageAdditionalRewards();
    }

    private void manageAdditionalRewards() {
        Clan winner = getResidence().getOwner();
        if (winner == null)
            return;

//		if (winner.getLeader().getPlayer() != null)
//			for (CastleRewardTemplate template : leaderAdditionalRewards.get(getResidence().getId()))
//				manageAddReward(winner.getLeader().getPlayer(), template);

        if (winner.getLeader().getObjectId() > 0)
            for (CastleRewardTemplate template : leaderAdditionalRewards.get(getResidence().getId()))
                manageOfflineReward(winner.getLeader().getObjectId(), template);

        for (Player player : winner.getOnlineMembers())
            for (CastleRewardTemplate template : additionalRewards.get(getResidence().getId()))
                manageAddReward(player, template);
    }

    private void manageAddReward(Player player, CastleRewardTemplate template) {
        if (player == null || template == null)
            return;
        if (Rnd.chance(template.getChance()))
            player.getInventory().addItem(template.getItemId(), Rnd.get(template.getMinCount(), template.getMaxCount()));
    }

    private void manageOfflineReward(int objId, CastleRewardTemplate template) {
        if (template == null)
            return;
        Player player = World.getPlayer(objId);
        if (Rnd.chance(template.getChance())) {
            if (player != null)
                player.getInventory().addItem(template.getItemId(), Rnd.get(template.getMinCount(), template.getMaxCount()));
            else
                Util.addOfflineItem(objId, template.getItemId(), Rnd.get(template.getMinCount(),
                        template.getMaxCount()), "siege leader reward");
        }
    }

    private void cancelNextSieges(final Clan ownerClan) {
        for (final Castle r : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
            if (r == getResidence()) {
                continue;
            }

            final SiegeEvent<?, ?> siegeEvent = r.getSiegeEvent();

            SiegeClanObject siegeClan = siegeEvent.getSiegeClan(ATTACKERS, ownerClan);
            if (siegeClan == null) {
                siegeClan = siegeEvent.getSiegeClan(DEFENDERS, ownerClan);
            }
            if (siegeClan == null) {
                siegeClan = siegeEvent.getSiegeClan(DEFENDERS_WAITING, ownerClan);
            }

            if (siegeClan != null) {
                siegeEvent.getObjects(siegeClan.getType()).remove(siegeClan);

                SiegeClanDAO.getInstance().delete(r, siegeClan);
            }
        }
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();

        final ZonedDateTime currentTime = ZonedDateTime.now();
        final ZonedDateTime startSiegeDate = getResidence().getSiegeDate();
        final ZonedDateTime ownSiegeDate = getResidence().getOwnDate();
        if (onInit) {
            if (startSiegeDate.isAfter(currentTime)) {
                addState(REGISTRATION_STATE);

                registerActions();
            } else if (startSiegeDate.isEqual(Residence.MIN_SIEGE_DATE)) {
                //final long diff = Duration.between(currentTime, ownSiegeDate.plusDays(1)).toMillis();
                //if(diff > DAY_IN_MILLIS) // прошёл день после осады
                if (currentTime.isAfter(ownSiegeDate.plusDays(1))) {
                    setNextSiegeTime();
                } else {
                    generateNextSiegeDates();
                }
            } else {
                setNextSiegeTime();
            }
        } else {
            if (getResidence().getOwner() != null) {
                getResidence().setSiegeDate(Residence.MIN_SIEGE_DATE);
                getResidence().setJdbcState(JdbcEntityState.UPDATED);
                getResidence().update();

                generateNextSiegeDates();
            } else {
                setNextSiegeTime();
            }
        }
    }

    @Override
    public void loadSiegeClans() {
        super.loadSiegeClans();

        addObjects(DEFENDERS_WAITING, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_WAITING));
        addObjects(DEFENDERS_REFUSED, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_REFUSED));
    }
    //========================================================================================================================================================================

    @Override
    public void removeState(final int val) {
        super.removeState(val);

        if (val == REGISTRATION_STATE) {
            broadcastToWorld(new SystemMessage(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(getResidence()));
        }
    }

    @Override
    public void announce(final int val) {
        final SystemMessage msg;
        final int min = val / 60;
        final int hour = min / 60;

        if (hour > 0) {
            msg = new SystemMessage(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION).addNumber(hour);
        } else if (min > 0) {
            msg = new SystemMessage(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION).addNumber(min);
        } else {
            msg = new SystemMessage(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS).addNumber(val);
        }

        broadcastTo(msg, ATTACKERS, DEFENDERS);
    }

    //========================================================================================================================================================================
    //                                                   Control Tower Support
    //========================================================================================================================================================================
    private void initControlTowers() {
        final List<SpawnExObject> objects = getObjects(GUARDS);
        final List<Spawner> spawns = new ArrayList<>();
        for (final SpawnExObject o : objects) {
            spawns.addAll(o.getSpawns());
        }

        final List<SiegeToggleNpcObject> ct = getObjects(CONTROL_TOWERS);

        SiegeToggleNpcInstance closestCt;
        double distance, distanceClosest;

        for (final Spawner spawn : spawns) {
            final Location spawnLoc = spawn.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());

            closestCt = null;
            distanceClosest = 0;

            for (final SiegeToggleNpcObject c : ct) {
                final SiegeToggleNpcInstance npcTower = c.getToggleNpc();
                distance = npcTower.getDistance(spawnLoc);

                if (closestCt == null || distance < distanceClosest) {
                    closestCt = npcTower;
                    distanceClosest = distance;
                }

                closestCt.register(spawn);
            }
        }
    }

    //========================================================================================================================================================================
    //                                                    Damage Zone Actions
    //========================================================================================================================================================================
    private void damageZoneAction(final boolean active) {
        zoneAction(BOUGHT_ZONES, active);
    }

    /**
     * Генерирует даты для следующей осады замка, и запускает таймер на автоматическую установку даты
     */
    public void generateNextSiegeDates() {
        if (!getResidence().getSiegeDate().isEqual(Residence.MIN_SIEGE_DATE))
            return;

        ZonedDateTime nextSiegeDate = ResidenceConfig.CASTLE_VALIDATION_DATE.with(DayOfWeek.SUNDAY);

        if (nextSiegeDate.isBefore(ResidenceConfig.CASTLE_VALIDATION_DATE))
            nextSiegeDate = nextSiegeDate.plusWeeks(1);

        nextSiegeDate = validateSiegeDate(nextSiegeDate, ResidenceConfig.CASTLE_SIEGE_PERIOD);

        nextSiegeTimes = new TreeSet<>();

        for (final int h : ResidenceConfig.CASTLE_SELECT_HOURS) {
            nextSiegeTimes.add((int) nextSiegeDate.withHour(h).toEpochSecond());
        }

        final long diff = Duration.between(ZonedDateTime.now(), getResidence().getOwnDate().plusDays(1)).toMillis();
        nextSiegeDateSetTask = ThreadPoolManager.getInstance().schedule(new NextSiegeDateSet(), diff);
    }

    /**
     * Ставит осадное время вручную, вызывается с пакета {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestSetCastleSiegeTime}
     *
     * @param id
     */
    public void setNextSiegeTime(final int id) {
        if (!nextSiegeTimes.contains(id) || nextSiegeDateSetTask == null) {
            return;
        }

        nextSiegeTimes = Collections.emptySet();
        nextSiegeDateSetTask.cancel(false);
        nextSiegeDateSetTask = null;

        final ZonedDateTime nextSiegeTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(id), ZoneId.systemDefault());
        setNextSiegeTime(nextSiegeTime);
    }
    //========================================================================================================================================================================
    //                                                    Суппорт Методы для установки времени осады
    //========================================================================================================================================================================

    /**
     * Автоматически генерит и устанавливает дату осады
     */


    private void setNextSiegeTime() {
        ZonedDateTime nextSiegeTime = ResidenceConfig.CASTLE_VALIDATION_DATE.with(DayOfWeek.SUNDAY)
                .withHour(getResidence().getLastSiegeDate().isEqual(Residence.MIN_SIEGE_DATE)
                        ? Rnd.get(ResidenceConfig.CASTLE_SELECT_HOURS) : getResidence().getLastSiegeDate().getHour());

        if (nextSiegeTime.isBefore(ResidenceConfig.CASTLE_VALIDATION_DATE))
            nextSiegeTime = nextSiegeTime.plusWeeks(1);

        nextSiegeTime = validateSiegeDate(nextSiegeTime, ResidenceConfig.CASTLE_SIEGE_PERIOD);

        setNextSiegeTime(nextSiegeTime);
    }

    /**
     * Ставит дату осады, запускает действия, аннонсирует по миру
     */
    private void setNextSiegeTime(final ZonedDateTime nextSiegeTime) {
        broadcastToWorld(new SystemMessage(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(getResidence()));

        clearActions();

        getResidence().setSiegeDate(nextSiegeTime);
        getResidence().setJdbcState(JdbcEntityState.UPDATED);
        getResidence().update();

        registerActions();

        addState(REGISTRATION_STATE);
    }

    @Override
    public boolean isAttackersInAlly() {
        return !firstStep || !AllSettingsConfig.allowAttackTemporalAlly;
    }

    public Integer[] getNextSiegeTimes() {
        return nextSiegeTimes.toArray(new Integer[nextSiegeTimes.size()]);
    }

    @Override
    public boolean canAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force, final boolean nextAttackCheck) {
        final Player playerTarget = target.getPlayer();
        final Player playerAttacker = attacker.getPlayer();
        final SiegeEvent siegeEvent1 = playerTarget.getEvent(CastleSiegeEvent.class);
        final SiegeEvent siegeEvent2 = playerAttacker.getEvent(CastleSiegeEvent.class);
        if (siegeEvent1 == null || siegeEvent2 == null || (siegeEvent1 != siegeEvent2)) {
            return false;
        }
        final SiegeClanObject siegeClan1 = siegeEvent1.getSiegeClan(ATTACKERS, playerTarget.getClan());
        final SiegeClanObject siegeClan2 = siegeEvent2.getSiegeClan(ATTACKERS, playerAttacker.getClan());
        //атакующие защитников (и наоборот) могут бить везде
        return (siegeClan1 != null && siegeClan2 == null) || (siegeClan1 == null && siegeClan2 != null);

    }

    @Override
    public boolean canResurrect(final Creature active, final Creature target, final boolean force, final boolean quiet) {
        final boolean playerInZone = checkIfInZone(active);
        final boolean targetInZone = checkIfInZone(target);
        // если оба вне зоны - рес разрешен
        // если таргет вне осадный зоны - рес разрешен
        if (!playerInZone && !targetInZone || !targetInZone) {
            return true;
        }

        final Player resurectPlayer = active.getPlayer();
        final Player targetPlayer = target.getPlayer();

        // если оба незареганы - невозможно ресать
        // если таргет незареган - невозможно ресать
        final CastleSiegeEvent siegeEvent1 = resurectPlayer.getEvent(CastleSiegeEvent.class);
        final CastleSiegeEvent siegeEvent2 = targetPlayer.getEvent(CastleSiegeEvent.class);
        if (siegeEvent1 == null && siegeEvent2 == null || siegeEvent2 != this) {
            if (!quiet) {
                if (force) {
                    targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                }
                active.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            }
            return false;
        }

        SiegeClanObject targetSiegeClan = siegeEvent2.getSiegeClan(ATTACKERS, targetPlayer.getClan());
        if (targetSiegeClan == null) {
            targetSiegeClan = siegeEvent2.getSiegeClan(DEFENDERS, targetPlayer.getClan());
        }

        if (targetSiegeClan == null || targetSiegeClan.getType() == ATTACKERS) {
            // если нету флага - рес запрещен
            if (targetSiegeClan == null || targetSiegeClan.getFlag() == null) {
                if (!quiet) {
                    if (force) {
                        targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                    }
                    active.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
                }
                return false;
            }
        } else {
            final List<SiegeToggleNpcObject> towers = getObjects(CONTROL_TOWERS);

            boolean canRes = true;
            for (final SiegeToggleNpcObject t : towers) {
                if (!t.isAlive()) {
                    canRes = false;
                }
            }

            if (!canRes) {
                if (!quiet) {
                    if (force) {
                        targetPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
                    }
                    active.sendPacket(force ? SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
                }
                return false;
            }
        }

        if (force) {
            return true;
        } else {
            if (!quiet) {
                active.sendPacket(SystemMsg.INVALID_TARGET);
            }
            return false;
        }
    }

    @Override
    public Location getRestartLoc(final Player player, final RestartType type) {
        switch (type) {
            case TO_VILLAGE:
                // Если печатью владеют лорды Рассвета (Dawn), и в данном городе идет осада, то телепортирует во 2-й по счету город.
                if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN) {
                    return _residence.getNotOwnerRestartPoint(player);
                }
        }

        return super.getRestartLoc(player, type);
    }

    public class KillListener implements OnKillListener {
        @Override
        public void onKill(final Creature actor, final Creature victim) {
            final Player winner = actor.getPlayer();
            if (winner == null || !victim.isPlayer() || winner == victim || !checkIfInZone(victim) || !((Player) victim)
                    .isUserRelationActive() || victim.getEvent(CastleSiegeEvent.class) != CastleSiegeEvent.this) {
                return;
            }

            final List<Player> players;
            if (winner.getParty() == null) {
                players = Collections.singletonList(winner);
            } else {
                players = winner.getParty().getPartyMembers();
            }

            double bonus = AllSettingsConfig.ALT_PARTY_BONUS[players.size() - 1];
            int value = (int) (Math.round(BASE_SIEGE_FAME * bonus) / players.size());
            for (final Player $player : players) {
                if ($player.getEvent(CastleSiegeEvent.class) == null || $player.getLevel() < 40 || !$player.isInRange(winner, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    continue;
                }

                $player.setFame($player.getFame() + value, CastleSiegeEvent.this.toString());
            }

            ((Player) victim).startEnableUserRelationTask(BLOCK_FAME_TIME, CastleSiegeEvent.this);
            blockedFameOnKill.put(victim.getObjectId(), System.currentTimeMillis() + BLOCK_FAME_TIME);
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }

    private class NextSiegeDateSet extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            setNextSiegeTime();
        }
    }
}