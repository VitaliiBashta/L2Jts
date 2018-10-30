package org.mmocore.gameserver.manager.games;

import gnu.trove.map.hash.TIntIntHashMap;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.BlockCheckerEngine;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBlockUpSetList;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BiggBoss, n0nam3
 */
public final class HandysBlockCheckerManager {
    /*
     * This class manage the player add/remove, team change and
     * event arena status, as the clearance of the participants
     * list or liberate the arena
     */

    // Arena votes to start the game
    private static final TIntIntHashMap _arenaVotes = new TIntIntHashMap();
    // Registration request penalty (10 seconds)
    private static final List<Integer> _registrationPenalty = new ArrayList<>();
    // All the participants and their team classifed by arena
    private static ArenaParticipantsHolder[] _arenaPlayers;
    // Arena Status, True = is being used, otherwise, False
    private static final Map<Integer, Boolean> _arenaStatus = new HashMap<>(4);

    private HandysBlockCheckerManager() {
        // Initialize arena status
        _arenaStatus.put(0, false);
        _arenaStatus.put(1, false);
        _arenaStatus.put(2, false);
        _arenaStatus.put(3, false);
    }

    public static HandysBlockCheckerManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static boolean isRegistered(final Player player) {
        for (int i = 0; i < 4; i++) {
            if (_arenaPlayers[i].getAllPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the number of event-start votes for the spcified
     * arena id
     *
     * @param arenaId
     * @return int (number of votes)
     */
    public synchronized int getArenaVotes(final int arenaId) {
        return _arenaVotes.get(arenaId);
    }

    /**
     * Add a new vote to start the event for the specified
     * arena id
     *
     * @param arena
     */
    public synchronized void increaseArenaVotes(final int arena) {
        final int newVotes = _arenaVotes.get(arena) + 1;
        final ArenaParticipantsHolder holder = _arenaPlayers[arena];

        if (newVotes > holder.getAllPlayers().size() / 2 && !holder.getEvent().isStarted()) {
            clearArenaVotes(arena);
            if (holder.getBlueTeamSize() == 0 || holder.getRedTeamSize() == 0) {
                return;
            }
            if (AllSettingsConfig.ALT_HBCE_FAIR_PLAY) {
                holder.checkAndShuffle();
            }
            ThreadPoolManager.getInstance().execute(holder.getEvent().new StartEvent());
        } else {
            _arenaVotes.put(arena, newVotes);
        }
    }

    /**
     * Will clear the votes queue (of event start) for the
     * specified arena id
     *
     * @param arena
     */
    public synchronized void clearArenaVotes(final int arena) {
        _arenaVotes.put(arena, 0);
    }

    /**
     * Returns the players holder
     *
     * @param arena
     * @return ArenaParticipantsHolder
     */
    public ArenaParticipantsHolder getHolder(final int arena) {
        return _arenaPlayers[arena];
    }

    /**
     * Initializes the participants holder
     */
    public void startUpParticipantsQueue() {
        _arenaPlayers = new ArenaParticipantsHolder[4];

        for (int i = 0; i < 4; ++i) {
            _arenaPlayers[i] = new ArenaParticipantsHolder(i);
        }
    }

    /**
     * Add the player to the specified arena (throught the specified
     * arena manager) and send the needed server ->  client packets
     *
     * @param player
     * @param arenaId
     */
    public boolean addPlayerToArena(final Player player, final int arenaId) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arenaId];

        synchronized (holder) {
            final boolean isRed;

            if (isRegistered(player)) {
                player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST).addName(player));
                return false;
            }

            if (player.isCursedWeaponEquipped()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
                return false;
            }

            final KrateisCubeRunnerEvent krateis = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
            if (krateis.isRegistered(player)) {
                player.sendPacket(SystemMsg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER);
                return false;
            }

            if (Olympiad.isRegistered(player)) {
                player.sendPacket(SystemMsg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER);
                return false;
            }

            final UndergroundColiseumBattleEvent battleEvent = player.getEvent(UndergroundColiseumBattleEvent.class);
            if (battleEvent != null) {
                player.sendPacket(SystemMsg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER);
                return false;
            }
            if (_registrationPenalty.contains(player.getObjectId())) {
                player.sendPacket(SystemMsg.YOU_CANNOT_MAKE_ANOTHER_REQUEST_FOR_10_SECONDS_AFTER_CANCELLING_A_MATCH_REGISTRATION);
                return false;
            }

            if (holder.getBlueTeamSize() < holder.getRedTeamSize()) {
                holder.addPlayer(player, 1);
                isRed = false;
            } else {
                holder.addPlayer(player, 0);
                isRed = true;
            }
            holder.broadCastPacketToTeam(new ExBlockUpSetList(player, isRed, 0x01));
            return true;
        }
    }

    /**
     * Will remove the specified player from the specified
     * team and arena and will send the needed packet to all
     * his team mates / enemy team mates
     *
     * @param player
     * @param arenaId
     */
    public void removePlayer(final Player player, final int arenaId, final int team) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arenaId];
        synchronized (holder) {
            final boolean isRed = team == 0;

            holder.removePlayer(player, team);
            holder.broadCastPacketToTeam(new ExBlockUpSetList(player, isRed, 0x02));

            // End event if theres an empty team
            final int teamSize = isRed ? holder.getRedTeamSize() : holder.getBlueTeamSize();
            if (teamSize == 0) {
                holder.getEvent().endEventAbnormally();
            }

            final Integer objId = player.getObjectId();
            if (!_registrationPenalty.contains(objId)) {
                _registrationPenalty.add(objId);
            }
            schedulePenaltyRemoval(objId);
        }
    }

    /**
     * Will change the player from one team to other (if possible)
     * and will send the needed packets
     *
     * @param player
     * @param arena
     * @param team
     */
    public void changePlayerToTeam(final Player player, final int arena, final int team) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arena];

        synchronized (holder) {
            final boolean isFromRed = holder._redPlayers.contains(player);

            if (isFromRed && holder.getBlueTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            } else if (!isFromRed && holder.getRedTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            }

            final int futureTeam = isFromRed ? 1 : 0;
            holder.addPlayer(player, futureTeam);

            if (isFromRed) {
                holder.removePlayer(player, 0);
            } else {
                holder.removePlayer(player, 1);
            }
            holder.broadCastPacketToTeam(new ExBlockUpSetList(player, isFromRed, 0x05));
        }
    }

    /**
     * Will erase all participants from the specified holder
     *
     * @param arenaId
     */
    public synchronized void clearPaticipantQueueByArenaId(final int arenaId) {
        _arenaPlayers[arenaId].clearPlayers();
    }

    /**
     * Returns true if arena is holding an event at this momment
     *
     * @param arenaId
     * @return Boolean
     */
    public boolean arenaIsBeingUsed(final int arenaId) {
        if (arenaId < 0 || arenaId > 3) {
            return false;
        }
        return _arenaStatus.get(arenaId);
    }

    /**
     * Set the specified arena as being used
     *
     * @param arenaId
     */
    public void setArenaBeingUsed(final int arenaId) {
        _arenaStatus.put(arenaId, true);
    }

    /**
     * Set as free the specified arena for future
     * events
     *
     * @param arenaId
     */
    public void setArenaFree(final int arenaId) {
        _arenaStatus.put(arenaId, false);
    }

    private void schedulePenaltyRemoval(final int objId) {
        ThreadPoolManager.getInstance().schedule(new PenaltyRemove(objId), 10000);
    }

    private static class PenaltyRemove extends RunnableImpl {
        final Integer objectId;

        public PenaltyRemove(final Integer id) {
            objectId = id;
        }

        @Override
        public void runImpl() {
            _registrationPenalty.remove(objectId);
        }
    }

    private static class LazyHolder {
        private static final HandysBlockCheckerManager INSTANCE = new HandysBlockCheckerManager();
    }

    public class ArenaParticipantsHolder {
        final int _arena;
        final List<Player> _redPlayers;
        final List<Player> _bluePlayers;
        final BlockCheckerEngine _engine;

        public ArenaParticipantsHolder(final int arena) {
            _arena = arena;
            _redPlayers = new ArrayList<>(6);
            _bluePlayers = new ArrayList<>(6);
            _engine = new BlockCheckerEngine(this, _arena);
        }

        public List<Player> getRedPlayers() {
            return _redPlayers;
        }

        public List<Player> getBluePlayers() {
            return _bluePlayers;
        }

        public ArrayList<Player> getAllPlayers() {
            final ArrayList<Player> all = new ArrayList<>(12);
            all.addAll(_redPlayers);
            all.addAll(_bluePlayers);
            return all;
        }

        public void addPlayer(final Player player, final int team) {
            if (team == 0) {
                _redPlayers.add(player);
            } else {
                _bluePlayers.add(player);
            }
        }

        public void removePlayer(final Player player, final int team) {
            if (team == 0) {
                _redPlayers.remove(player);
            } else {
                _bluePlayers.remove(player);
            }
        }

        public int getPlayerTeam(final Player player) {
            if (_redPlayers.contains(player)) {
                return 0;
            } else if (_bluePlayers.contains(player)) {
                return 1;
            } else {
                return -1;
            }
        }

        public int getRedTeamSize() {
            return _redPlayers.size();
        }

        public int getBlueTeamSize() {
            return _bluePlayers.size();
        }

        public void broadCastPacketToTeam(final L2GameServerPacket packet) {
            final ArrayList<Player> team = new ArrayList<>(12);
            team.addAll(_redPlayers);
            team.addAll(_bluePlayers);

            for (final Player p : team) {
                p.sendPacket(packet);
            }
        }

        public void clearPlayers() {
            _redPlayers.clear();
            _bluePlayers.clear();
        }

        public BlockCheckerEngine getEvent() {
            return _engine;
        }

        public void updateEvent() {
            _engine.updatePlayersOnStart(this);
        }

        private void checkAndShuffle() {
            final int redSize = _redPlayers.size();
            final int blueSize = _bluePlayers.size();
            if (redSize > blueSize + 1) {
                broadCastPacketToTeam(new SystemMessage(SystemMsg.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
                final int needed = redSize - (blueSize + 1);
                for (int i = 0; i < needed + 1; i++) {
                    final Player plr = _redPlayers.get(i);
                    if (plr == null) {
                        continue;
                    }
                    changePlayerToTeam(plr, _arena, 1);
                }
            } else if (blueSize > redSize + 1) {
                broadCastPacketToTeam(new SystemMessage(SystemMsg.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
                final int needed = blueSize - (redSize + 1);
                for (int i = 0; i < needed + 1; i++) {
                    final Player plr = _bluePlayers.get(i);
                    if (plr == null) {
                        continue;
                    }
                    changePlayerToTeam(plr, _arena, 0);
                }
            }
        }
    }
}
