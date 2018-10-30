package org.mmocore.gameserver.manager.naia;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.BossConfig;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author pchayka
 */
public final class NaiaTowerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaiaTowerManager.class);
    private final static AtomicIntegerArray _roomStatus = new AtomicIntegerArray(12);
    private final static Map<Integer, List<NpcInstance>> _roomMobs = new HashMap<>();

    // TODO перенести ID дверей и номера комнат для нпс которые за них отвечают в параметры NPC
    // TODO исправить открытие/закрытие дверей - должны открывать и закрывать по дной, а не обе двери
    // TODO проверить освобождение команты если пати вышла или её выгнали по таймеру
    //
    private final static Map<Long, PartyHolder> _parties = new ConcurrentHashMap<>();
    private final static Set<Integer> _allowerPlayers = new CopyOnWriteArraySet<>();
    //
    private static final Location ENTRANCE_LOCATION = new Location(-47271, 246098, -9120);
    private static final Location TOPROOF = new Location(16110, 243841, 11616);
    private final static long ROOM_TIME_LIMIT = TimeUnit.MINUTES.toMillis(5); // 5 минут
    private final static long ENTRANCE_TIME_LIMIT = TimeUnit.MINUTES.toMillis(20); // 20 минут
    private static long _towerAccessible = 0;
    private NaiaTowerManager() {
        for (int i = 0; i < 12; i++)
            _roomMobs.put(i, new CopyOnWriteArrayList<>());

        LOGGER.info("Naia Tower Manager: Loaded 12 rooms");
        if (!BossConfig.disableBelethQuestChecks)
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new GroupTowerTimer(), 30 * 1000L, 30 * 1000L);
    }

    public static NaiaTowerManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void enter(final Player leader) {
        if (_towerAccessible > System.currentTimeMillis())
            return;

        final Party party = leader.getParty();
        if (party == null || party.getMemberCount() < 2)
            return;

        for (Player member : party)
            if (!member.isInRange(leader, 600)) {
                leader.sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                return;
            }

        _towerAccessible = System.currentTimeMillis() + ENTRANCE_TIME_LIMIT;

        if (party.getCommandChannel() != null && AllSettingsConfig.naiaTeleportWholeCc)
            for (Party pt : party.getCommandChannel().getParties())
                teleport(pt);
        else
            teleport(party);

        ReflectionUtils.getDoor(18250001).openMe();
    }

    private static void teleport(Party party) {
        final PartyHolder holder = new PartyHolder(party);
        _parties.put(holder.getPartyId(), holder);
        holder.updateTimer();
        holder.teleport(ENTRANCE_LOCATION);
    }

    public static boolean isValidParty(final Party party) {
        return _parties.containsKey(party.getId());
    }

    public static void updateGroupTimer(final Party party) {
        final PartyHolder h = _parties.get(party.getId());
        h.updateTimer();
    }

    public static void removeGroupTimer(final Party party) {
        final PartyHolder h = _parties.remove(party.getId());
        h.finish();

        for (Player player : h.getPlayers()) {
            player.addListener(ExistListener.STATIC_INSTNCE);
            _allowerPlayers.add(player.getObjectId());
        }
    }

    public static boolean isLockedRoom(final int roomId) {
        return _roomStatus.get(roomId) == 1;
    }

    public static boolean lockRoom(final int roomId) {
        return _roomStatus.compareAndSet(roomId, 0, 1);
    }

    public static boolean unlockRoom(final int roomId) {
        return _roomStatus.compareAndSet(roomId, 1, 0);
    }

    public static boolean isRoomDone(final int roomId, final Party party) {
        final PartyHolder h = _parties.get(party.getId());
        if (h == null)
            return true;

        return h.getCurrentRoom() >= roomId;
    }

    public static void setCurrentRoom(final int roomId, final Party party) {
        final PartyHolder h = _parties.get(party.getId());
        h.setRoom(roomId);
    }

    public static void addMobToRoom(final int roomId, final NpcInstance mob) {
        final List<NpcInstance> monsters = _roomMobs.get(roomId);
        monsters.add(mob);
    }

    public static List<NpcInstance> getRoomMobs(final int roomId) {
        return _roomMobs.get(roomId);
    }

    public static void clearRoom(final int roomId) {
        final List<NpcInstance> monsters = _roomMobs.get(roomId);
        for (NpcInstance mob : monsters) {
            if (mob != null)
                mob.deleteMe();
        }

        monsters.clear();
    }

    public static boolean isValidPlayer(final Player player) {
        return player != null && _allowerPlayers.contains(player.getObjectId()) || BossConfig.disableBelethQuestChecks;
    }

    private static final class PartyHolder implements OnPlayerPartyLeaveListener {
        private final long _partyId;
        private final List<HardReference<Player>> _playerRefs;
        private volatile long _kickTime;

        private int _room = -1;

        PartyHolder(final Party party) {
            _partyId = party.getId();
            //
            final List<Player> members = party.getPartyMembers();
            _playerRefs = new ArrayList<>(members.size());
            for (Player member : members) {
                member.getListeners().add(this);
                _playerRefs.add(member.getRef());
            }
        }

        long getPartyId() {
            return _partyId;
        }

        int getCurrentRoom() {
            return _room;
        }

        void setRoom(final int roomId) {
            _room = roomId;
        }

        Collection<Player> getPlayers() {
            return HardReferences.unwrap(_playerRefs);
        }

        void teleport(final Location location) {
            for (final Player player : getPlayers())
                if (player != null)
                    player.teleToLocation(location);

        }

        void updateTimer() {
            _kickTime = System.currentTimeMillis() + ROOM_TIME_LIMIT;
        }

        long getKickTime() {
            return _kickTime;
        }

        void kickOut() {
            for (final Player player : getPlayers())
                if (player != null) {
                    player.getListeners().remove(this);
                    player.teleToLocation(TOPROOF);
                    // нет на оффе такого сообщения player.sendMessage("The time has expired. You cannot stay in Tower of Naia any longer");
                }
        }

        void finish() {
            for (final Player player : getPlayers())
                if (player != null) {
                    player.getListeners().remove(this);
                }
        }

        @Override
        public void onPartyLeave(Player player) {
            final Party party = player.getParty();
            if (party.getMemberCount() < 2)
                _parties.remove(_partyId);
            //
            player.getListeners().remove(this);
            player.teleToLocation(TOPROOF);
        }
    }

    private static final class ExistListener implements OnPlayerExitListener {
        final static ExistListener STATIC_INSTNCE = new ExistListener();

        @Override
        public void onPlayerExit(Player player) {
            _allowerPlayers.remove(player.getObjectId());
        }
    }

    private static class GroupTowerTimer extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (!_parties.isEmpty()) {
                final Collection<PartyHolder> parties = _parties.values();
                for (PartyHolder h : parties) {
                    if (h.getKickTime() < System.currentTimeMillis()) {
                        h.kickOut();
                        _parties.remove(h.getPartyId());
                    }
                }
            }
        }
    }

    private static class LazyHolder {
        private static final NaiaTowerManager INSTANCE = new NaiaTowerManager();
    }
}
