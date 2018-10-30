package org.mmocore.gameserver.model.entity.olympiad;

import gnu.trove.map.TIntIntMap;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.database.dao.impl.OlympiadNobleDAO;
import org.mmocore.gameserver.manager.OlympiadHistoryManager;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

public class Olympiad {
    public static final ConcurrentLinkedQueue<Integer> nonClassBasedRegisters = new ConcurrentLinkedQueue<>();
    public static final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Integer>> classBasedRegisters = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, Collection<Integer>> teamBasedRegisters = new ConcurrentHashMap<>();
    public static final ConcurrentLinkedQueue<RegisteredPlayerInfo> registeredPlayersInfo = new ConcurrentLinkedQueue<>();
    //public static final int DEFAULT_POINTS = 50;
    //private static final int WEEKLY_POINTS = 10;
    public static final int TEAM_PARTY_SIZE = 3;
    public static final String OLYMPIAD_HTML_PATH = "olympiad/";
    public static final String CHAR_ID = "char_id";
    public static final String CLASS_ID = "class_id";
    public static final String CHAR_NAME = "char_name";
    public static final String POINTS = "olympiad_points";
    public static final String POINTS_PAST = "olympiad_points_past";
    public static final String POINTS_PAST_STATIC = "olympiad_points_past_static";
    public static final String COMP_DONE = "competitions_done";
    public static final String COMP_WIN = "competitions_win";
    public static final String COMP_LOOSE = "competitions_loose";
    public static final String GAME_CLASSES_COUNT = "game_classes_count";
    public static final String GAME_NOCLASSES_COUNT = "game_noclasses_count";
    public static final String GAME_TEAM_COUNT = "game_team_count";
    public static final Stadia[] STADIUMS = new Stadia[OlympiadConfig.OLYMPIAD_STADIAS_COUNT];
    private static final Logger _log = LoggerFactory.getLogger(Olympiad.class);
    private static final List<NpcInstance> _npcs = new ArrayList<>();
    public static Map<Integer, StatsSet> nobles;
    public static TIntIntMap noblesRank;
    public static List<StatsSet> heroesToBe;
    public static long _olympiadEnd;
    public static long _validationEnd;
    public static int _period;
    public static long _nextWeeklyChange;
    public static int _currentCycle;
    public static boolean _inCompPeriod;
    public static boolean _isOlympiadEnd;
    public static boolean _isDoubleLoad = false;
    public static ScheduledFuture<?> _scheduledManagerTask;
    public static ScheduledFuture<?> _scheduledWeeklyTask;
    public static ScheduledFuture<?> _scheduledValdationTask;
    public static OlympiadManager _manager;
    private static long _compEnd;
    private static Calendar _compStart;
    private static ScheduledFuture<?> _scheduledOlympiadEnd;

    public static void load() {
        nobles = new ConcurrentHashMap<>();
        _currentCycle = ServerVariables.getInt("Olympiad_CurrentCycle", -1);
        _period = ServerVariables.getInt("Olympiad_Period", -1);
        _olympiadEnd = ServerVariables.getLong("Olympiad_End", -1);
        _validationEnd = ServerVariables.getLong("Olympiad_ValdationEnd", -1);
        _nextWeeklyChange = ServerVariables.getLong("Olympiad_NextWeeklyChange", -1);

        if (_currentCycle == -1) {
            //_currentCycle = olympiadProperties.getProperty("CurrentCycle", 1);
            _currentCycle = 1;
        }
        if (_period == -1) {
            //_period = olympiadProperties.getProperty("Period", 0);
            _period = 0;
        }
        if (_olympiadEnd == -1) {
            //_olympiadEnd = olympiadProperties.getProperty("OlympiadEnd", 0L);
            _olympiadEnd = 0L;
        }
        if (_validationEnd == -1) {
            //_validationEnd = olympiadProperties.getProperty("ValdationEnd", 0L);
            _validationEnd = 0L;
        }
        if (_nextWeeklyChange == -1) {
            //_nextWeeklyChange = olympiadProperties.getProperty("NextWeeklyChange", 0L);
            _nextWeeklyChange = 0L;
        }

        initStadiums();

        OlympiadHistoryManager.getInstance();
        OlympiadNobleDAO.getInstance().select();
        OlympiadDatabase.loadNoblesRank();

        switch (_period) {
            case 0:
                if (_olympiadEnd == 0 || _olympiadEnd < Calendar.getInstance().getTimeInMillis()) {
                    OlympiadDatabase.setNewOlympiadEnd();
                } else {
                    _isOlympiadEnd = false;
                }
                break;
            case 1:
                _isOlympiadEnd = true;
                _scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), getMillisToValidationEnd());
                break;
            default:
                _log.warn("Olympiad System: Omg something went wrong in loading!! Period = " + _period);
                return;
        }

        _log.info("Olympiad System: Loading Olympiad System....");
        if (_period == 0) {
            _log.info("Olympiad System: Currently in Olympiad Period");
        } else {
            _log.info("Olympiad System: Currently in Validation Period");
        }

        _log.info("Olympiad System: Period Ends....");

        long milliToEnd;
        if (_period == 0) {
            milliToEnd = getMillisToOlympiadEnd();
        } else {
            milliToEnd = getMillisToValidationEnd();
        }

        final double numSecs = milliToEnd / 1000.0d % 60;
        double countDown = (milliToEnd / 1000.0d - numSecs) / 60;
        final int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        final int numHours = (int) Math.floor(countDown % 24);
        final int numDays = (int) Math.floor((countDown - numHours) / 24);

        _log.info("Olympiad System: In " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

        if (_period == 0) {
            _log.info("Olympiad System: Next Weekly Change is in....");

            milliToEnd = getMillisToWeekChange();

            final double numSecs2 = milliToEnd / 1000.0d % 60;
            double countDown2 = (milliToEnd / 1000.0d - numSecs2) / 60;
            final int numMins2 = (int) Math.floor(countDown2 % 60);
            countDown2 = (countDown2 - numMins2) / 60;
            final int numHours2 = (int) Math.floor(countDown2 % 24);
            final int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);

            _log.info("Olympiad System: In " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");
        }

        _log.info("Olympiad System: Loaded " + nobles.size() + " Noblesses");

        if (_period == 0) {
            init();
        }
    }

    private static void initStadiums() {
        for (int i = 0; i < STADIUMS.length; i++) {
            if (STADIUMS[i] == null) {
                STADIUMS[i] = new Stadia();
            }
        }
    }

    public static void init() {
        if (_period == 1) {
            return;
        }

        _compStart = Calendar.getInstance();
        _compStart.set(Calendar.HOUR_OF_DAY, OlympiadConfig.ALT_OLY_START_TIME);
        _compStart.set(Calendar.MINUTE, OlympiadConfig.ALT_OLY_MIN);
        _compEnd = _compStart.getTimeInMillis() + OlympiadConfig.ALT_OLY_CPERIOD;

        if (_scheduledOlympiadEnd != null) {
            _scheduledOlympiadEnd.cancel(false);
        }
        _scheduledOlympiadEnd = ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), getMillisToOlympiadEnd());

        updateCompStatus();

        if (_scheduledWeeklyTask != null) {
            _scheduledWeeklyTask.cancel(false);
        }
        _scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WeeklyTask(), getMillisToWeekChange(), OlympiadConfig.ALT_OLY_WPERIOD);
    }

    public static synchronized boolean registerNoble(final Player noble, final CompType type) {
        if (noble.getPlayerClassComponent().getBaseClassId() != noble.getPlayerClassComponent().getClassId().getId()) {
            noble.sendPacket(SystemMsg.DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
            return false;
        }

        if (!noble.isNoble()) {
            noble.sendPacket(SystemMsg.DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_NOBLESSE_CHARACTERS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
            return false;
        }

        if (!noble.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Fourth)) {
            noble.sendPacket(SystemMsg.DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
            return false;
        }

        if (!_inCompPeriod || _isOlympiadEnd) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (getMillisToOlympiadEnd() <= 600 * 1000) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (getMillisToCompEnd() <= 600 * 1000) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (noble.isCursedWeaponEquipped()) {
            noble.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
            return false;
        }

        final StatsSet nobleInfo = nobles.get(noble.getObjectId());

        if (!validPlayer(noble, noble, type)) {
            return false;
        }

        if (getNoblePoints(noble.getObjectId()) < 1) {
            noble.sendMessage(new CustomMessage("org.mmocore.gameserver.model.entity.Olympiad.LessPoints"));
            return false;
        }

        if (noble.getOlympiadGame() != null) {
            //
            return false;
        }

        int classId = nobleInfo.getInteger(CLASS_ID, 0);

        // SoulHound hack
        if (classId == 133) {
            classId = 132;
        }

        if (OlympiadConfig.OLYMPIAD_CHECK_IP) {
            for (RegisteredPlayerInfo info : registeredPlayersInfo)
                if (info.getIp().equals(noble.getNetConnection().getIpAddr())) {
                    noble.sendMessage("You are already registered at the Olympiad Games!");
                    return false;
                }
        }

        if (OlympiadConfig.OLYMPIAD_CHECK_HWID) {
            for (RegisteredPlayerInfo info : registeredPlayersInfo)
                if (info.getHwid().equals(noble.getNetConnection().getHWID())) {
                    noble.sendMessage("You are already registered at the Olympiad Games!");
                    return false;
                }
        }

        switch (type) {
            case CLASSED: {
                if (!classBasedRegisters.containsKey(classId))
                    classBasedRegisters.put(classId, new ConcurrentLinkedQueue<>());
                classBasedRegisters.get(classId).add(noble.getObjectId());
                RegisteredPlayerInfo nfo = new RegisteredPlayerInfo(noble.getObjectId(),
                        noble.getNetConnection().getIpAddr(), noble.getNetConnection().getHWID());
                registeredPlayersInfo.add(nfo);
                noble.sendPacket(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_THE_GRAND_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_SPECIFIC_MATCH);
                break;
            }
            case NON_CLASSED: {
                nonClassBasedRegisters.add(noble.getObjectId());
                RegisteredPlayerInfo nfo = new RegisteredPlayerInfo(noble.getObjectId(),
                        noble.getNetConnection().getIpAddr(), noble.getNetConnection().getHWID());
                registeredPlayersInfo.add(nfo);
                noble.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH);
                break;
            }
            case TEAM: {
                final Party party = noble.getParty();
                if (party == null) {
                    noble.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH);
                    return false;
                }

                if (party.getMemberCount() != TEAM_PARTY_SIZE) {
                    noble.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET);
                    return false;
                }

                int score = 0;

                for (Player member : party)
                    score += getNoblePoints(member.getObjectId());

                if (score > 0)
                    score = (int) Math.min(Math.ceil((double) score / 15), 10);

                for (final Player member : party.getPartyMembers()) {
                    if (getNoblePoints(member.getObjectId()) < score) {
                        noble.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_COMPLETED_BACAUSE_THE_REQUIREMENTS_ARE_NOT_MET_IN_ORDER_TO_PARTICIPATE_IN_A_TEAM_MATCH_ALL_TEAM_MEMBERS_MUST_HAVE_AN_OLYMPIAD_SCORE_OF_1_OR_MORE);
                        return false;
                    }

                    if (!validPlayer(noble, member, type)) {
                        return false;
                    }
                    RegisteredPlayerInfo info = new RegisteredPlayerInfo(noble.getObjectId(),
                            noble.getNetConnection().getIpAddr(), noble.getNetConnection().getHWID());
                    registeredPlayersInfo.add(info);
                }

                teamBasedRegisters.put(noble.getObjectId(), party.getPartyMembersObjIds());
                noble.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH);
                break;
            }
        }

        return true;
    }

    private static boolean validPlayer(final Player sendPlayer, final Player validPlayer, final CompType type) {
        if (validPlayer.isDead()) {
            sendPlayer.sendPacket(new SystemMessage(SystemMsg.C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addName(validPlayer));
            return false;
        }
        if (!validPlayer.isNoble()) {
            sendPlayer.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGE_TO_YOUR_SUB_CLASS).addName(validPlayer));
            return false;
        }

        if (validPlayer.getPlayerClassComponent().getBaseClassId() != validPlayer.getPlayerClassComponent().getClassId().getId()) {
            sendPlayer.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGE_TO_YOUR_SUB_CLASS).addName(validPlayer));
            return false;
        }

        if (!validPlayer.isWeightLimit(false) || !validPlayer.isInventoryLimit(false)) {
            sendPlayer.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOUR_INVENTORY_SLOT_EXCEEDS_80_PERCENT).addName(validPlayer));
            return false;
        }

        if (validPlayer.getEvent(SingleMatchEvent.class) != null) {
            sendPlayer.sendPacket(SystemMsg.YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS);
            return false;
        }

        if (isRegistered(validPlayer))
            return false;

        final int[] ar = getWeekGameCounts(validPlayer.getObjectId());

        switch (type) {
            case CLASSED:
                if (ar[1] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
            case NON_CLASSED:
                if (ar[2] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
            case TEAM:
                if (ar[3] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
        }

        if (ar[0] == 0) {
            validPlayer.sendPacket(SystemMsg.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70);
            return false;
        }

        if (isRegisteredInComp(validPlayer)) {
            sendPlayer.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST).addName(validPlayer));
            return false;
        }

        return true;
    }

    public static synchronized void logoutPlayer(final Player player) {
        removePlayerFromLists(player);

        final OlympiadGame game = player.getOlympiadGame();
        if (game != null) {
            try {
                if (!game.logoutPlayer(player) && !game.validated) {
                    game.endGame(20, true);
                }
            } catch (Exception e) {
                _log.error("", e);
            }
        }
    }

    static void removePlayerFromLists(Player player) {
        classBasedRegisters.entrySet().forEach(entry -> {
            if (entry.getValue().contains(player.getObjectId())) {
                classBasedRegisters.remove(entry.getKey(), entry.getValue());
            }
        });
        nonClassBasedRegisters.remove(player.getObjectId());
        teamBasedRegisters.entrySet().forEach(entry -> {
            if (entry.getValue().contains(player.getObjectId())) {
                teamBasedRegisters.remove(entry.getKey(), entry.getValue());
            }
        });
        RegisteredPlayerInfo info = getRegisteredPlayerInfo(player);
        if (info != null) {
            registeredPlayersInfo.remove(info);
        }
    }

    public static synchronized boolean unRegisterNoble(final Player noble) {
        if (!_inCompPeriod || _isOlympiadEnd) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (!noble.isNoble()) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (!isRegistered(noble)) {
            noble.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_GRAND_OLYMPIAD);
            return false;
        }

        final OlympiadGame game = noble.getOlympiadGame();
        if (game != null) {
            if (game.getStatus() == BattleStatus.Begin_Countdown) {
                // TODO: System Message
                //TODO [VISTALL] узнать ли прерывается бой и если так ли это та мессага SystemMsg.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED
                noble.sendMessage("Now you can't cancel participation in the Grand Olympiad.");
                return false;
            }

            try {
                if (!game.logoutPlayer(noble) && !game.validated) {
                    game.endGame(20, true);
                }
            } catch (Exception e) {
                _log.error("", e);
            }
        }

        if (noble.isInParty()) {
            Player partyLeader = noble.getParty().getGroupLeader();

            if (partyLeader != null && teamBasedRegisters.containsKey(partyLeader.getObjectId()))
                if (teamBasedRegisters.get(partyLeader.getObjectId()).contains(noble.getObjectId())) {
                    if (partyLeader != noble) {
                        noble.sendMessage("Only party leader can unregister party.");
                        return false;
                    }

                    for (Player member : noble.getParty().getPartyMembers()) {
                        removePlayerFromLists(member);

                        member.sendPacket(SystemMsg.YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST);
                    }
                    return true;
                }
        }


        removePlayerFromLists(noble);

        noble.sendPacket(SystemMsg.YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST);

        return true;
    }

    private static synchronized void updateCompStatus() {
        final long milliToStart = getMillisToCompBegin();
        final double numSecs = milliToStart / 1000.0d % 60;
        double countDown = (milliToStart / 1000.0d - numSecs) / 60;
        final int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        final int numHours = (int) Math.floor(countDown % 24);
        final int numDays = (int) Math.floor((countDown - numHours) / 24);

        _log.info("Olympiad System: Competition Period Starts in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
        _log.info("Olympiad System: Event starts/started: " + _compStart.getTime());

        ThreadPoolManager.getInstance().schedule(new CompStartTask(), getMillisToCompBegin());
    }

    private static long getMillisToOlympiadEnd() {
        return _olympiadEnd - System.currentTimeMillis();
    }

    static long getMillisToValidationEnd() {
        if (_validationEnd > System.currentTimeMillis()) {
            return _validationEnd - System.currentTimeMillis();
        }
        return 10L;
    }

    public static boolean isOlympiadEnd() {
        return _isOlympiadEnd;
    }

    public static boolean inCompPeriod() {
        return _inCompPeriod;
    }

    private static long getMillisToCompBegin() {
        if (_compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && _compEnd > Calendar.getInstance().getTimeInMillis()) {
            return 10L;
        }
        if (_compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        }
        return setNewCompBegin();
    }

    private static long setNewCompBegin() {
        _compStart = Calendar.getInstance();
        _compStart.set(Calendar.HOUR_OF_DAY, OlympiadConfig.ALT_OLY_START_TIME);
        _compStart.set(Calendar.MINUTE, OlympiadConfig.ALT_OLY_MIN);
        _compStart.add(Calendar.HOUR_OF_DAY, 24);
        _compEnd = _compStart.getTimeInMillis() + OlympiadConfig.ALT_OLY_CPERIOD;

        _log.info("Olympiad System: New Schedule @ " + _compStart.getTime());

        return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    public static long getMillisToCompEnd() {
        return _compEnd - Calendar.getInstance().getTimeInMillis();
    }

    private static long getMillisToWeekChange() {
        if (_nextWeeklyChange > Calendar.getInstance().getTimeInMillis()) {
            return _nextWeeklyChange - Calendar.getInstance().getTimeInMillis();
        }
        return 10L;
    }

    public static synchronized void doWeekTasks() {
        if (_period == 1) {
            return;
        }
        for (final Map.Entry<Integer, StatsSet> entry : nobles.entrySet()) {
            final StatsSet set = entry.getValue();
            final Player player = GameObjectsStorage.getPlayer(entry.getKey());

            if (_period != 1) {
                set.set(POINTS, set.getInteger(POINTS) + OlympiadConfig.OLYMPIAD_POINTS_WEEKLY);
            }
            set.set(GAME_CLASSES_COUNT, 0);
            set.set(GAME_NOCLASSES_COUNT, 0);
            set.set(GAME_TEAM_COUNT, 0);

            if (player != null) {
                player.sendPacket(new SystemMessage(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addName(player).addNumber(OlympiadConfig.OLYMPIAD_POINTS_WEEKLY));
            }
        }
    }

    public static int getCurrentCycle() {
        return _currentCycle;
    }

    public static synchronized void addSpectator(final int id, final Player spectator) {
        if (spectator.getOlympiadGame() != null || isRegistered(spectator) || isRegisteredInComp(spectator)) {
            spectator.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
            return;
        }

        if (_manager == null || _manager.getOlympiadInstance(id) == null || _manager.getOlympiadInstance(id).getStatus() == BattleStatus.Begining || _manager.getOlympiadInstance(id).getStatus() == BattleStatus.Begin_Countdown) {
            spectator.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        if (spectator.isInCombat() || spectator.getPvpFlag() > 0 || spectator.getEvent(SingleMatchEvent.class) != null) {
            spectator.sendPacket(SystemMsg.YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT);
            return;
        }

        if (spectator.getServitor() != null) {
            spectator.getServitor().unSummon(false, false);
        }

        if (spectator.isInParty()) {
            spectator.leaveParty();
        }

        final OlympiadGame game = getOlympiadGame(id);
        final List<Location> spawns = game.getReflection().getInstancedZone().getTeleportCoords();
        if (spawns.size() < 3) {
            final Location c1 = spawns.get(0);
            final Location c2 = spawns.get(1);
            spectator.enterOlympiadObserverMode(new Location((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (c1.z + c2.z) / 2), game, game.getReflection());
        } else {
            spectator.enterOlympiadObserverMode(spawns.get(2), game, game.getReflection());
        }
    }

    public static synchronized void removeSpectator(final int id, final Player spectator) {
        if (_manager == null || _manager.getOlympiadInstance(id) == null) {
            return;
        }

        _manager.getOlympiadInstance(id).removeSpectator(spectator);
    }

    public static List<Player> getSpectators(final int id) {
        if (_manager == null || _manager.getOlympiadInstance(id) == null) {
            return null;
        }
        return _manager.getOlympiadInstance(id).getSpectators();
    }

    public static OlympiadGame getOlympiadGame(final int gameId) {
        if (_manager == null || gameId < 0) {
            return null;
        }
        return _manager.getOlympiadGames().get(gameId);
    }

    public static synchronized int[] getWaitingList() {
        if (!inCompPeriod()) {
            return null;
        }

        final int[] array = new int[3];
        array[0] = classBasedRegisters.size();
        array[1] = nonClassBasedRegisters.size();
        array[2] = teamBasedRegisters.size();

        return array;
    }

    public static synchronized int getParticipants() {
        return classBasedRegisters.size() + nonClassBasedRegisters.size() + teamBasedRegisters.size();
    }

    public static synchronized int getNoblessePasses(final Player player) {
        final int objId = player.getObjectId();

        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }

        int points = noble.getInteger(POINTS_PAST, 0);
        if (points == 0) // Уже получил бонус
        {
            return 0;
        }

        final int rank = noblesRank.get(objId);
        switch (rank) {
            case 1:
                points = OlympiadConfig.ALT_OLY_RANK1_POINTS;
                break;
            case 2:
                points = OlympiadConfig.ALT_OLY_RANK2_POINTS;
                break;
            case 3:
                points = OlympiadConfig.ALT_OLY_RANK3_POINTS;
                break;
            case 4:
                points = OlympiadConfig.ALT_OLY_RANK4_POINTS;
                break;
            default:
                points = OlympiadConfig.ALT_OLY_RANK5_POINTS;
        }

        if (player.isHero() || Hero.getInstance().isInactiveHero(player.getObjectId())) {
            points += OlympiadConfig.ALT_OLY_HERO_POINTS;
        }

        noble.set(POINTS_PAST, 0);
        OlympiadDatabase.saveNobleData(objId);

        return points * OlympiadConfig.ALT_OLY_GP_PER_POINT;
    }

    public static synchronized boolean isRegistered(final Player noble) {
        if (nonClassBasedRegisters.contains(noble.getObjectId())) {
            noble.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST).addName(noble));
            return true;
        }
        for (Collection<Integer> group : classBasedRegisters.values())
            if (group.contains(noble.getObjectId())) {
                noble.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_CLASS_IRRELEVANT_INDIVIDUAL_MATCH).addName(noble));
                return true;
            }
        for (Collection<Integer> group : teamBasedRegisters.values())
            if (group.contains(noble.getObjectId())) {
                noble.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH).addName(noble));
                return true;
            }
        return false;
    }

    public static synchronized boolean isRegisteredInComp(final Player player) {
        if (isRegistered(player)) {
            return true;
        }
        if (_manager == null || _manager.getOlympiadGames() == null) {
            return false;
        }
        for (final OlympiadGame g : _manager.getOlympiadGames().values()) {
            if (g != null && g.isRegistered(player.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает олимпийские очки за текущий период
     */
    public static synchronized int getNoblePoints(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(POINTS, 0);
    }

    /**
     * Возвращает олимпийские очки за прошлый период
     */
    public static synchronized int getNoblePointsPast(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(POINTS_PAST, 0);
    }

    public static synchronized int getCompetitionDone(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(COMP_DONE, 0);
    }

    public static synchronized int getCompetitionWin(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(COMP_WIN, 0);
    }

    public static synchronized int getCompetitionLoose(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(COMP_LOOSE, 0);
    }

    public static synchronized int[] getWeekGameCounts(final int objId) {
        final int[] ar = new int[4];

        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return ar;
        }

        ar[0] = OlympiadConfig.GAME_MAX_LIMIT - noble.getInteger(GAME_CLASSES_COUNT, 0) - noble.getInteger(GAME_NOCLASSES_COUNT) - noble.getInteger(GAME_TEAM_COUNT, 0);
        ar[1] = OlympiadConfig.GAME_CLASSES_COUNT_LIMIT - noble.getInteger(GAME_CLASSES_COUNT, 0);
        ar[2] = OlympiadConfig.GAME_NOCLASSES_COUNT_LIMIT - noble.getInteger(GAME_NOCLASSES_COUNT, 0);
        ar[3] = OlympiadConfig.GAME_TEAM_COUNT_LIMIT - noble.getInteger(GAME_TEAM_COUNT, 0);

        return ar;
    }

    public static Stadia[] getStadiums() {
        return STADIUMS;
    }

    public static List<NpcInstance> getNpcs() {
        return _npcs;
    }

    public static void addOlympiadNpc(final NpcInstance npc) {
        _npcs.add(npc);
    }

    public static void changeNobleClass(final int objId, final int newClass) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return;
        }
        noble.set(CLASS_ID, newClass);
        OlympiadDatabase.saveNobleData(objId);
    }

    public static void changeNobleName(final int objId, final String newName) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return;
        }
        noble.set(CHAR_NAME, newName);
        OlympiadDatabase.saveNobleData(objId);
    }

    public static String getNobleName(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return null;
        }
        return noble.getString(CHAR_NAME, "");
    }

    public static int getNobleClass(final int objId) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return 0;
        }
        return noble.getInteger(CLASS_ID, 0);
    }

    public static void manualSetNoblePoints(final int objId, final int points) {
        final StatsSet noble = nobles.get(objId);
        if (noble == null) {
            return;
        }
        noble.set(POINTS, points);
        OlympiadDatabase.saveNobleData(objId);
    }

    public static synchronized boolean isNoble(final int objId) {
        return nobles.get(objId) != null;
    }

    public static synchronized void addNoble(final Player noble) {
        if (!nobles.containsKey(noble.getObjectId())) {
            int classId = noble.getPlayerClassComponent().getBaseClassId();
            if (classId < 88 || (classId > 122 && classId < 131) || classId == 135) // Если это не 3-я профа, то исправляем со 2-й на 3-ю.
            {
                for (final ClassId id : ClassId.VALUES) {
                    if (id.level() == 3 && id.getParent(0).getId() == classId) {
                        classId = id.getId();
                        break;
                    }
                }
            }

            final StatsSet statDat = new StatsSet();
            statDat.set(CLASS_ID, classId);
            statDat.set(CHAR_NAME, noble.getName());
            statDat.set(POINTS, OlympiadConfig.OLYMPIAD_POINTS_DEFAULT);
            statDat.set(POINTS_PAST, 0);
            statDat.set(POINTS_PAST_STATIC, 0);
            statDat.set(COMP_DONE, 0);
            statDat.set(COMP_WIN, 0);
            statDat.set(COMP_LOOSE, 0);
            statDat.set(GAME_CLASSES_COUNT, 0);
            statDat.set(GAME_NOCLASSES_COUNT, 0);
            statDat.set(GAME_TEAM_COUNT, 0);

            nobles.put(noble.getObjectId(), statDat);
            OlympiadDatabase.saveNobleData(noble.getObjectId());
        }
    }

    public static synchronized void removeNoble(final Player noble) {
        nobles.remove(noble.getObjectId());
        OlympiadDatabase.saveNobleData();
    }

    public static RegisteredPlayerInfo getRegisteredPlayerInfo(int objId) {
        for (RegisteredPlayerInfo info : registeredPlayersInfo)
            if (info.getObjId() == objId)
                return info;
        return null;
    }

    public static RegisteredPlayerInfo getRegisteredPlayerInfo(Player player) {
        return getRegisteredPlayerInfo(player.getObjectId());
    }
}