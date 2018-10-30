package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ResidenceConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterServitorDAO;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.database.dao.impl.SummonDAO;
import org.mmocore.gameserver.database.dao.impl.SummonEffectDAO;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.OnKillListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.ZoneObject;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.RelationChanged;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.DoorTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.TeleportUtils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @date 15:11/14.02.2011
 */
public abstract class SiegeEvent<R extends Residence, S extends SiegeClanObject> extends Event {
    public static final String OWNER = "owner";
    public static final String OLD_OWNER = "old_owner";
    public static final String ATTACKERS = "attackers";
    public static final String DEFENDERS = "defenders";
    public static final String SPECTATORS = "spectators";
    public static final String FROM_RESIDENCE_TO_TOWN = "from_residence_to_town";
    public static final String SIEGE_ZONES = "siege_zones";
    public static final String FLAG_ZONES = "flag_zones";
    public static final String DAY_OF_WEEK = "day_of_week";
    public static final String HOUR_OF_DAY = "hour_of_day";
    public static final String REGISTRATION = "registration";
    public static final String DOORS = "doors";
    // states
    public static final int PROGRESS_STATE = 1 << 0;
    public static final int REGISTRATION_STATE = 1 << 1;
    // block fame time
    public static final long BLOCK_FAME_TIME = 5 * 60 * 1000L;
    protected final int _dayOfWeek;
    protected final int _hourOfDay;
    protected final Map<Integer, SiegeSummonInfo> siegeSummons = new ConcurrentHashMap<>();
    protected final Map<Integer, Long> blockedFameOnKill = new ConcurrentHashMap<>();
    protected R _residence;
    protected Clan _oldOwner;
    protected OnKillListener killListener;
    protected OnDeathListener _doorDeathListener = new DoorDeathListener();
    private int _state;
    public SiegeEvent(final MultiValueSet<String> set) {
        super(set);
        _dayOfWeek = set.getInteger(DAY_OF_WEEK, 1);
        _hourOfDay = set.getInteger(HOUR_OF_DAY, 0);
    }

    @Override
    public void startEvent() {
        addState(PROGRESS_STATE);

        super.startEvent();
    }

    @Override
    public final void stopEvent() {
        stopEvent(false);
    }

    //========================================================================================================================================================================
    //                                                                   Start / Stop Siege
    //========================================================================================================================================================================

    public void stopEvent(final boolean step) {
        removeState(PROGRESS_STATE);

        despawnSiegeSummons();
        reCalcNextTime(false);

        super.stopEvent();
    }

    public void processStep(final Clan clan) {
        //
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();

        ZonedDateTime startSiegeDate = getResidence().getSiegeDate();
        if (onInit) {
            // дата ниже текущей
            if (!startSiegeDate.isAfter(ZonedDateTime.now())) {
                startSiegeDate = startSiegeDate.with(DayOfWeek.of(_dayOfWeek)).withHour(_hourOfDay);
                startSiegeDate = validateSiegeDate(startSiegeDate, 2);

                getResidence().setSiegeDate(startSiegeDate);
                getResidence().setJdbcState(JdbcEntityState.UPDATED);
            }
        } else {
            getResidence().setSiegeDate(startSiegeDate.plusWeeks(ResidenceConfig.CASTLE_SIEGE_PERIOD));
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
        }

        registerActions();

        getResidence().update();
    }

    protected ZonedDateTime validateSiegeDate(final ZonedDateTime dateTime, final int add) {
        ZonedDateTime newDateTime = dateTime.withMinute(0).withSecond(0).withNano(0);

        while (newDateTime.isBefore(ZonedDateTime.now()))
            newDateTime = newDateTime.plusWeeks(add);

        return newDateTime;
    }

    @Override
    protected Instant startTime() {
        return getResidence().getSiegeDate().toInstant();
    }

    @Override
    public void teleportPlayers(final String t, final ZoneType zoneType) {
        List<Player> players = new ArrayList<>();
        final Clan ownerClan = getResidence().getOwner();
        if (t.equalsIgnoreCase(OWNER)) {
            if (ownerClan != null) {
                for (final Player player : getPlayersInZone()) {
                    if (player.getClan() == ownerClan) {
                        players.add(player);
                    }
                }
            }
        } else if (t.equalsIgnoreCase(ATTACKERS)) {
            for (final Player player : getPlayersInZone()) {
                final S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
                if (siegeClan != null && siegeClan.isParticle(player)) {
                    players.add(player);
                }
            }
        } else if (t.equalsIgnoreCase(DEFENDERS)) {
            for (final Player player : getPlayersInZone()) {
                if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan) {
                    continue;
                }

                final S siegeClan = getSiegeClan(DEFENDERS, player.getClan());
                if (siegeClan != null && siegeClan.isParticle(player)) {
                    players.add(player);
                }
            }
        } else if (t.equalsIgnoreCase(SPECTATORS)) {
            for (final Player player : getPlayersInZone()) {
                if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan) {
                    continue;
                }

                if (player.getClan() == null || getSiegeClan(ATTACKERS, player.getClan()) == null && getSiegeClan(DEFENDERS, player.getClan()) == null) {
                    players.add(player);
                }
            }
        }
        // выносих всех с резиденции в город
        else if (t.equalsIgnoreCase(FROM_RESIDENCE_TO_TOWN)) {
            for (final Player player : getResidence().getZone().getInsidePlayers()) {
                if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan) {
                    continue;
                }

                players.add(player);
            }
        } else {
            players = getPlayersInZone();
        }

        for (final Player player : players) {
            Location loc;
            if (t.equalsIgnoreCase(OWNER) || t.equalsIgnoreCase(DEFENDERS)) {
                loc = getResidence().getOwnerRestartPoint();
            } else if (t.equalsIgnoreCase(FROM_RESIDENCE_TO_TOWN)) {
                loc = TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE);
            } else {
                loc = getResidence().getNotOwnerRestartPoint(player);
            }

            player.teleToLocation(loc, ReflectionManager.DEFAULT);
        }
    }

    public List<Player> getPlayersInZone() {
        final List<ZoneObject> zones = getObjects(SIEGE_ZONES);
        final List<Player> result = new ArrayList<>();
        for (final ZoneObject zone : zones) {
            result.addAll(zone.getInsidePlayers());
        }
        return result;
    }

    //========================================================================================================================================================================
    //                                                                   Zones
    //========================================================================================================================================================================

    public void broadcastInZone(final L2GameServerPacket... packet) {
        for (final Player player : getPlayersInZone()) {
            player.sendPacket(packet);
        }
    }

    public void broadcastInZone(final IBroadcastPacket... packet) {
        for (final Player player : getPlayersInZone()) {
            player.sendPacket(packet);
        }
    }

    public boolean checkIfInZone(final Creature character) {
        final List<ZoneObject> zones = getObjects(SIEGE_ZONES);
        for (final ZoneObject zone : zones) {
            if (zone.checkIfInZone(character)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastInZone2(final IBroadcastPacket... packet) {
        for (final Player player : getResidence().getZone().getInsidePlayers()) {
            player.sendPacket(packet);
        }
    }

    public void broadcastInZone2(final L2GameServerPacket... packet) {
        for (final Player player : getResidence().getZone().getInsidePlayers()) {
            player.sendPacket(packet);
        }
    }

    //========================================================================================================================================================================
    //                                                                   Siege Clans
    //========================================================================================================================================================================
    public void loadSiegeClans() {
        addObjects(ATTACKERS, SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS));
        addObjects(DEFENDERS, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS));
    }

    @SuppressWarnings("unchecked")
    public S newSiegeClan(final String type, final int clanId, final long param, final Instant date) {
        final Clan clan = ClanTable.getInstance().getClan(clanId);
        return clan == null ? null : (S) new SiegeClanObject(type, clan, param, date);
    }

    public void updateParticles(final boolean start, final String... arg) {
        for (final String a : arg) {
            final List<SiegeClanObject> siegeClans = getObjects(a);
            for (final SiegeClanObject s : siegeClans) {
                s.setEvent(start, this);
            }
        }
    }

    public S getSiegeClan(final String name, final Clan clan) {
        if (clan == null) {
            return null;
        }
        return getSiegeClan(name, clan.getClanId());
    }

    @SuppressWarnings("unchecked")
    public S getSiegeClan(final String name, final int objectId) {
        final List<SiegeClanObject> siegeClanList = getObjects(name);
        if (siegeClanList.isEmpty()) {
            return null;
        }
        for (final SiegeClanObject siegeClan : siegeClanList) {
            if (siegeClan.getObjectId() == objectId) {
                return (S) siegeClan;
            }
        }
        return null;
    }

    public void broadcastTo(final IBroadcastPacket packet, final String... types) {
        for (final String type : types) {
            final List<SiegeClanObject> siegeClans = getObjects(type);
            for (final SiegeClanObject siegeClan : siegeClans) {
                siegeClan.broadcast(packet);
            }
        }
    }

    public void broadcastTo(final L2GameServerPacket packet, final String... types) {
        for (final String type : types) {
            final List<SiegeClanObject> siegeClans = getObjects(type);
            for (final SiegeClanObject siegeClan : siegeClans) {
                siegeClan.broadcast(packet);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initEvent() {
        _residence = (R) ResidenceHolder.getInstance().getResidence(getId());

        loadSiegeClans();

        clearActions();

        super.initEvent();
    }

    @Override
    public boolean ifVar(final String name) {
        if (name.equals(OWNER)) {
            return getResidence().getOwner() != null;
        }
        if (name.equals(OLD_OWNER)) {
            return _oldOwner != null;
        }

        return false;
    }

    //========================================================================================================================================================================
    //                                                         Override Event
    //========================================================================================================================================================================

    @Override
    public void findEvent(final Player player) {
        if (!isInProgress() || player.getClan() == null) {
            return;
        }

        if (getSiegeClan(ATTACKERS, player.getClan()) != null || getSiegeClan(DEFENDERS, player.getClan()) != null) {
            player.addEvent(this);

            if (blockedFameOnKill.containsKey(player.getObjectId())) {
                final long val = blockedFameOnKill.get(player.getObjectId());
                if (val > 0) {
                    final long diff = val - System.currentTimeMillis();
                    if (diff > 0) {
                        player.startEnableUserRelationTask(diff, this);
                    }
                }
            }
        }
    }

    @Override
    public void checkRestartLocs(final Player player, final Map<RestartType, Boolean> r) {
        if (getObjects(FLAG_ZONES).isEmpty()) {
            return;
        }

        final S clan = getSiegeClan(ATTACKERS, player.getClan());
        if (clan != null) {
            if (clan.getFlag() != null) {
                r.put(RestartType.TO_FLAG, Boolean.TRUE);
            }
        }
    }

    @Override
    public Location getRestartLoc(final Player player, final RestartType type) {
        if (player.getReflection() != ReflectionManager.DEFAULT) {
            return null;
        }

        if (type == RestartType.TO_FLAG) {
            final S attackerClan = getSiegeClan(ATTACKERS, player.getClan());
            if (!getObjects(FLAG_ZONES).isEmpty() && attackerClan != null && attackerClan.getFlag() != null) {
                return Location.findPointToStay(attackerClan.getFlag(), 50, 75);
            } else {
                player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            }
        }

        return null;
    }

    @Override
    public int getRelation(final Player thisPlayer, final Player targetPlayer, int result) {
        final Clan clan1 = thisPlayer.getClan();
        final Clan clan2 = targetPlayer.getClan();
        if (clan1 == null || clan2 == null) {
            return result;
        }

        final SiegeEvent<?, ?> siegeEvent2 = targetPlayer.getEvent(SiegeEvent.class);
        if (this == siegeEvent2) {
            result |= RelationChanged.RELATION_IN_SIEGE;

            final SiegeClanObject siegeClan1 = getSiegeClan(ATTACKERS, clan1);
            final SiegeClanObject siegeClan2 = getSiegeClan(ATTACKERS, clan2);

            if (siegeClan1 == null && siegeClan2 == null || siegeClan1 == siegeClan2 || siegeClan1 != null && siegeClan2 != null && isAttackersInAlly()) {
                result |= RelationChanged.RELATION_ALLY;
            } else {
                result |= RelationChanged.RELATION_ENEMY;
            }
            if (siegeClan1 != null) {
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }

        return result;
    }

    @Override
    public int getUserRelation(final Player thisPlayer, int oldRelation) {
        oldRelation |= RelationChanged.USER_RELATION_IN_SIEGE;

        final SiegeClanObject siegeClan = getSiegeClan(ATTACKERS, thisPlayer.getClan());
        if (siegeClan != null) {
            oldRelation |= RelationChanged.USER_RELATION_ATTACKER;
        }

        return oldRelation;
    }

    @Override
    public SystemMsg checkForAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force) {
        if (!checkIfInZone(target) || !checkIfInZone(attacker)) {
            return null;
        }

        final SiegeEvent<?, ?> siegeEvent = target.getEvent(SiegeEvent.class);

        // или вообще не учасник, или учасники разных осад
        if (this != siegeEvent) {
            return null;
        }

        final Player player = target.getPlayer();
        if (player == null) {
            return null;
        }

        final SiegeClanObject siegeClan1 = getSiegeClan(ATTACKERS, player.getClan());
        if (siegeClan1 == null && attacker.isSiegeGuard()) {
            return SystemMsg.INVALID_TARGET;
        }
        final Player playerAttacker = attacker.getPlayer();
        if (playerAttacker == null) {
            return SystemMsg.INVALID_TARGET;
        }

        final SiegeClanObject siegeClan2 = getSiegeClan(ATTACKERS, playerAttacker.getClan());
        // если оба аттакеры, и в осаде, аттакеры в Алли, невозможно бить
        if (siegeClan1 != null && siegeClan2 != null && isAttackersInAlly()) {
            return SystemMsg.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE;
        }
        // если нету как Аттакры, это дефендеры, то невозможно бить
        if (siegeClan1 == null && siegeClan2 == null) {
            return SystemMsg.INVALID_TARGET;
        }

        // если из одного клана атаковать скилами нельзя
        if (attacker.getClan() == target.getClan()) {
            return SystemMsg.INVALID_TARGET;
        }

        return null;
    }

    @Override
    public boolean isInProgress() {
        return hasState(PROGRESS_STATE);
    }

    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION)) {
            if (start) {
                addState(REGISTRATION_STATE);
            } else {
                removeState(REGISTRATION_STATE);
            }
        } else {
            super.action(name, start);
        }
    }

    public boolean isAttackersInAlly() {
        return false;
    }

    @Override
    public void onAddEvent(final GameObject object) {
        if (killListener == null) {
            return;
        }

        if (object.isPlayer()) {
            ((Player) object).addListener(killListener);
        }
    }

    @Override
    public void onRemoveEvent(final GameObject object) {
        if (killListener == null) {
            return;
        }

        if (object.isPlayer()) {
            ((Player) object).removeListener(killListener);
        }
    }

    @Override
    public List<Player> broadcastPlayers(final int range) {
        return itemObtainPlayers();
    }

    @Override
    public EventType getType() {
        return EventType.SIEGE_EVENT;
    }

    @Override
    public List<Player> itemObtainPlayers() {
        return getPlayersInZone().stream().filter(player -> player.getEvent(getClass()) == this).collect(Collectors.toList());
    }

    @Override
    public void giveItem(final Player player, final int itemId, final long count) {
        if (AllSettingsConfig.ALT_NO_FAME_FOR_DEAD && itemId == ItemTemplate.ITEM_ID_FAME && player.isDead()) {
            return;
        }

        super.giveItem(player, itemId, count);
    }

    public Location getEnterLoc(final Player player, final Zone zone) // DS: в момент вызова игрок еще не вошел в игру и с него нельзя получить список зон, поэтому просто передаем найденную по локации
    {
        final S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
        if (siegeClan != null) {
            if (siegeClan.getFlag() != null) {
                return Location.findAroundPosition(siegeClan.getFlag(), 50, 75);
            } else {
                return getResidence().getNotOwnerRestartPoint(player);
            }
        } else {
            return getResidence().getOwnerRestartPoint();
        }
    }

    /**
     * Вызывается для эвента киллера и показывает может ли киллер стать ПК
     */
    public boolean canPK(Player target, Player killer) {
        if (!isInProgress()) {
            return true; // осада еще не началась
        }

        final SiegeEvent<?, ?> targetEvent = target.getEvent(SiegeEvent.class);
        if (targetEvent != this) {
            return true; // либо вообще не участник осад, либо разные осады
        }

        final S targetClan = getSiegeClan(SiegeEvent.ATTACKERS, target.getClan());
        final S killerClan = getSiegeClan(SiegeEvent.ATTACKERS, killer.getClan());
        if (targetClan != null && killerClan != null && isAttackersInAlly()) // оба атакующие и в альянсе
        {
            return true;
        }
        if (targetClan == null && killerClan == null) // оба защитники
        {
            return true;
        }

        return false;
    }

    //========================================================================================================================================================================
    // Getters & Setters
    //========================================================================================================================================================================
    public R getResidence() {
        return _residence;
    }

    public void addState(final int b) {
        _state |= b;
    }

    public void removeState(final int b) {
        _state &= ~b;
    }

    public boolean hasState(final int val) {
        return (_state & val) == val;
    }

    public boolean isRegistrationOver() {
        return !hasState(REGISTRATION_STATE);
    }

    //========================================================================================================================================================================
    public void addSiegeSummon(final Player player, final SummonInstance summon) {
        siegeSummons.put(player.getObjectId(), new SiegeSummonInfo(summon));
    }

    public boolean containsSiegeSummon(final Servitor cha) {
        final SiegeSummonInfo siegeSummonInfo = siegeSummons.get(cha.getPlayer().getObjectId());
        if (siegeSummonInfo == null) {
            return false;
        }
        return siegeSummonInfo.summonRef.get() == cha;
    }

    public void removeSiegeSummon(final Player player, final Servitor cha) {
        siegeSummons.remove(player.getObjectId());
    }

    public void updateSiegeSummon(final Player player, final SummonInstance summon) {
        final SiegeSummonInfo siegeSummonInfo = siegeSummons.get(player.getObjectId());
        if (siegeSummonInfo == null) {
            return;
        }

        if (siegeSummonInfo.getSkillId() == summon.getCallSkillId()) {
            summon.setSiegeSummon(true);
            siegeSummonInfo.summonRef = summon.getRef();
        }
    }

    public void despawnSiegeSummons() {
        for (final Map.Entry<Integer, SiegeSummonInfo> entry : siegeSummons.entrySet()) {
            final SiegeSummonInfo summonInfo = entry.getValue();

            final SummonInstance summon = summonInfo.summonRef.get();
            if (summon != null) {
                summon.unSummon(false, false);
            } else {
                CharacterServitorDAO.getInstance().delete(entry.getKey(), summonInfo.skillId, Servitor.SUMMON_TYPE);
                SummonDAO.getInstance().delete(entry.getKey(), summonInfo.skillId);
                SummonEffectDAO.getInstance().delete(entry.getKey(), summonInfo.skillId);
            }
        }

        siegeSummons.clear();
    }

    public void removeBlockFame(final Player player) {
        blockedFameOnKill.remove(player.getObjectId());
    }

    protected static class SiegeSummonInfo {
        private final int skillId;
        private final int ownerObjectId;

        private HardReference<SummonInstance> summonRef = HardReferences.emptyRef();

        SiegeSummonInfo(final SummonInstance summonInstance) {
            skillId = summonInstance.getCallSkillId();
            ownerObjectId = summonInstance.getPlayer().getObjectId();
            summonRef = summonInstance.getRef();
        }

        public int getSkillId() {
            return skillId;
        }

        public int getOwnerObjectId() {
            return ownerObjectId;
        }
    }

    public class DoorDeathListener implements OnDeathListener {
        @Override
        public void onDeath(final Creature actor, final Creature killer) {
            if (!isInProgress()) {
                return;
            }

            final DoorInstance door = (DoorInstance) actor;
            if (door.getDoorType() == DoorTemplate.DoorType.WALL) {
                return;
            }

            broadcastTo(SystemMsg.THE_CASTLE_GATE_HAS_BEEN_DESTROYED, SiegeEvent.ATTACKERS, SiegeEvent.DEFENDERS);
        }
    }
}