package org.mmocore.gameserver.scripts.events.LastHero;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.Revive;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LastHero extends Functions implements OnInitScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(LastHero.class);
    private static final Location _enter = new Location(149505, 46719, -3417);
    private static ScheduledFuture<?> _startTask;
    private static List<Integer> players_list = new CopyOnWriteArrayList<Integer>();
    private static List<Integer> live_list = new CopyOnWriteArrayList<Integer>();
    private static List<String> players_hwid_list = new CopyOnWriteArrayList<String>();
    private static boolean _isRegistrationActive = false;
    private static int _status = 0;
    private static int _time_to_start;
    private static int _category;
    private static int _minLevel;
    private static int _maxLevel;
    private static int _autoContinue = 0;
    private static List<Player> winnersList = new ArrayList<>();
    private static ScheduledFuture<?> _endTask;
    private static Zone _zone = ReflectionUtils.getZone("[colosseum_battle]");
    private static ZoneListener _zoneListener = new ZoneListener();
    private Player player;
    private NpcInstance lastNpc;
    private String[] args;

    private static boolean isActive() {
        return EventsConfig.LhIsActive;
    }

    public static boolean isRunned() {
        return _isRegistrationActive || _status > 0;
    }

    public static int getMinLevelForCategory(int category) {
        switch (category) {
            case 1:
                return 76;
        }
        return 0;
    }

    public static int getMaxLevelForCategory(int category) {
        switch (category) {
            case 1:
                return 85;
        }
        return 0;
    }

    public static int getCategory(int level) {
        if (level >= 76) {
            return 1;
        }
        return 0;
    }

    public static boolean checkPlayer(Player player, boolean first) {
        if (first && (!_isRegistrationActive || player.isDead())) {
            player.sendMessage(new CustomMessage("scripts.events.Late"));
            return false;
        }

        if (first && players_list.contains(player.getObjectId())) {
            removePlayer(player);
            player.sendMessage(new CustomMessage("scripts.events.LastHero.Cancelled"));
            return false;
        }

        if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledLevel"));
            return false;
        }

        if (!player.getReflection().isDefault()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledLevel"));
            return false;
        }

        if (player.isMounted()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.Cancelled"));
            return false;
        }

        if (player.isInDuel()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledDuel"));
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent"));
            return false;
        }

        if (player.getOlympiadGame() != null || first && Olympiad.isRegistered(player)) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOlympiad"));
            return false;
        }

        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent"));
            return false;
        }

        if (player.isTeleporting()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledTeleport"));
            return false;
        }

        if (player.isCursedWeaponEquipped()) {
            player.sendMessage("Регистрация не доступна");
            return false;
        }

        if (player.isBlocked()) {
            return false;
        }
        if (player.isInOlympiadMode())
            return false;
        //player.sendMessage(new CustomMessage("scripts.events.LastHero.Registered"));
        return true;
    }

    public static void removeState() {
        getPlayers(players_list).forEach(p -> p.setInLastHero(false));
    }

    public static void setHero(Player player) {
        if (player == null || player.isHero() || player.getCustomPlayerComponent().isTemporalHero())
            return;
        long period = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EventsConfig.LhHeroStatusDurationMin);
        player.getPlayerVariables().set(PlayerVariables.TEMPORAL_HERO, period, period);
        player.getCustomPlayerComponent().startTemporalHero();
//		player.setHero(true);
        player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.GIVE_HERO));
        player.broadcastUserInfo(true);
        winnersList.add(player);
    }

    public static boolean isWinner(Player player) {
        return winnersList.contains(player);
    }

    public static void removeWinner(Player player) {
        winnersList.remove(player);
    }

    private static void preparePlayer(Player player) {
        if (player == null)
            return;
        Servitor servitor = player.getServitor();
        if (!EventsConfig.LhAllowSummonTeleport) {
            if (servitor != null)
                servitor.unSummon(true, true);
        }
        if (EventsConfig.LhRemoveEffects) {
            player.getEffectList().stopAllEffects();
            if (servitor != null)
                servitor.getEffectList().stopAllEffects();
        }
        if (EventsConfig.LhAllowCustomBuffs) {
            if (player.isMageClass()) {
                EventsConfig.LhMageBuffs.forEach((id, lvl) -> buffCreature(player, id, lvl));
                if (servitor != null)
                    EventsConfig.LhMageBuffs.forEach((id, lvl) -> buffCreature(servitor, id, lvl));
            } else {
                EventsConfig.LhFighterBuffs.forEach((id, lvl) -> buffCreature(player, id, lvl));
                if (servitor != null)
                    EventsConfig.LhFighterBuffs.forEach((id, lvl) -> buffCreature(servitor, id, lvl));
            }
        }
        if (EventsConfig.LhForbidTransformations)
            player.stopTransformation();

    }

    private static void buffCreature(Creature creature, int id, int lvl) {
        SkillTable.getInstance().getSkillEntry(id, lvl).getEffects(creature, creature, false, false);
    }

    public static void upParalyzePlayers() {
        for (Player player : getPlayers(players_list)) {
            player.stopFrozen();
            if (player.getServitor() != null) {
                player.getServitor().stopFrozen();
            }
            player.leaveParty();
        }
    }

    public static void cleanPlayers() {
        for (Player player : getPlayers(players_list)) {
            if (!checkPlayer(player, false)) {
                removePlayer(player);
            }
        }
    }

    public static void removeAura() {
        for (Player player : getPlayers(live_list)) {
            player.setTeam(TeamType.NONE);
        }
    }

    public static void clearArena() {
        for (GameObject obj : _zone.getObjects()) {
            if (obj != null) {
                Player player = obj.getPlayer();
                if (player != null && !live_list.contains(player.getObjectId())) {
                    player.teleToLocation(147451, 46728, -3410);
                }
            }
        }
    }

    private static void removePlayer(Player player) {
        if (player != null) {
            live_list.remove(new Integer(player.getObjectId()));
            players_list.remove(new Integer(player.getObjectId()));
            player.setTeam(TeamType.NONE);
            player.setInLastHero(false);
            player.broadcastUserInfo(true);
        }
    }

    private static List<Player> getPlayers(List<Integer> list) {
        List<Player> result = new ArrayList<Player>(list.size());
        for (Integer storeId : list) {
            Player player = GameObjectsStorage.getPlayer((Integer) storeId);
            if (player != null) {
                result.add(player);
            }
        }
        return result;
    }

    public static boolean containsActiveParticipant(int objId) {
        return live_list.contains(objId);
    }

    @Override
    public void onInit() {

        _zone.addListener(_zoneListener);

        CharListenerList.addGlobal(new OnDeathListenerImpl());
        CharListenerList.addGlobal(new OnPlayerExitListenerImpl());
        CharListenerList.addGlobal(new OnTeleportImpl());
        if (isActive()) {
            scheduleEventStart();
            _log.info("Loaded Event: Last Hero [state: activated]");
        } else {
            _log.info("Loaded Event: Last Hero [state: deactivated]");
        }
    }

    @Bypass("events.LastHero:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {

        if (!isActive()) {
            if (_startTask == null) {
                scheduleEventStart();
            }
            _log.info("Event 'Last Hero' activated.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceEventStarted");
        }
    }

    @Bypass("events.LastHero:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {

        if (isActive()) {
            if (_startTask != null) {
                _startTask.cancel(false);
                _startTask = null;
            }
            _log.info("Event 'Last Hero' deactivated.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceEventStoped");
        }
    }

    @Bypass("events.LastHero:start")
    public void start(Player player, NpcInstance lastNpc, String[] args) {

//		if(isPvPEventStarted())
//		{
//			_log.info("Last Hero not started: another event is already running");
//			return;
//		}

        if (args.length != 2) {
            System.out.println("common.Error");
            return;
        }

        Integer category;
        Integer autoContinue;
        try {
            category = Integer.valueOf(args[0]);
            autoContinue = Integer.valueOf(args[1]);
        } catch (Exception e) {
            System.out.println("common.Error");
            return;
        }

        _category = category;
        _autoContinue = autoContinue;

        if (_category == -1) {
            _minLevel = 1;
            _maxLevel = 85;
        } else {
            _minLevel = getMinLevelForCategory(_category);
            _maxLevel = getMaxLevelForCategory(_category);
        }

        if (_endTask != null) {
            System.out.println("common.TryLater");
            return;
        }

        _status = 0;
        _isRegistrationActive = true;
        _time_to_start = EventsConfig.EVENT_LastHeroTime;

        players_list = new CopyOnWriteArrayList<Integer>();
        live_list = new CopyOnWriteArrayList<Integer>();

        AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnouncePreStart", _time_to_start, _minLevel, _maxLevel);

        ThreadPoolManager.getInstance().schedule(new question(), 10000);
        ThreadPoolManager.getInstance().schedule(new announce(), 60000);
    }

    @Bypass("events.LastHero:addPlayer")
    public void addPlayer(Player player, NpcInstance lastNpc, String[] args) {
        if (player == null)
            return;

        if (!checkPlayer(player, true)) {
            removePlayer(player);
            return;
        }

        if (players_list.size() > EventsConfig.LhMaxParticipants) {
            player.sendMessage("Players limit already reached!");
            return;
        }

        players_list.add(player.getObjectId());
        live_list.add(player.getObjectId());
        players_hwid_list.add(player.getHwid());
        player.setInLastHero(true);
        player.sendMessage("You have been successfully added as a member of the event.");
    }

    public void endDuel() {
        for (Player player : getPlayers(players_list)) {

            if (player.isInDuel()) {
                removePlayer(player);
                loosePlayer(player);
            }

        }
    }

    public void breakDuel() {
        for (Player player : getPlayers(players_list)) {
            if (player.isInDuel()) {
                System.out.println("игрок в дуэли на LH " + player.getName());
                DuelEvent duelEvent = player.getEvent(DuelEvent.class);

                if (duelEvent == null) {
                    return;
                }

                duelEvent.packetSurrender(player);
            }
        }
    }

    public void removeBuffs(boolean fromSummon) {
        for (Player player : getPlayers(players_list)) {
            if (player == null) {
                return;
            }

            player.abortAttack(true, false);
            if (player.isCastingNow()) {
                player.abortCast(true, true);
            }

            for (Effect e : player.getEffectList().getAllEffects()) {
                if (e == null) {
                    continue;
                }
                if (e.getSkill().getTemplate().isToggle()) {
                    continue;
                }
                player.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill()));
                e.exit();
            }

            Servitor summon = player.getServitor();
            if (summon != null) {
                summon.abortAttack(true, false);
                if (summon.isCastingNow()) {
                    summon.abortCast(true, true);
                }

                if (fromSummon) {
                    if (summon.isPet()) {
                        summon.unSummon(false, false);
                    } else {
                        summon.getEffectList().stopAllEffects();
                    }
                }
            }
        }
    }

    public void checkLive() {
        List<Integer> new_live_list = new CopyOnWriteArrayList<Integer>();
        for (Integer storeId : live_list) {
            Player player = GameObjectsStorage.getPlayer(storeId);
            if (player != null) {
                new_live_list.add(storeId);
            }
        }

        live_list = new_live_list;
        for (Player player : getPlayers(live_list)) {
            if (player.isInZone(_zone) && !player.isDead() && !player.isLogoutStarted()) {
                player.setTeam(TeamType.RED);
            } else {
                loosePlayer(player);
            }
        }
        if (live_list.size() <= 1) {
            ThreadPoolManager.getInstance().schedule(new endBattle(), 1000);
        }
    }

    private void loosePlayer(Player player) {
        if (player != null) {
            live_list.remove(new Integer(player.getObjectId()));
            player.setTeam(TeamType.NONE);
            player.teleToLocation(147512, 46744, -3400);
            player.restoreExp();
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), true);
            player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
            player.sendMessage(new CustomMessage("scripts.events.LastHero.YouLose"));
            player.setInLastHero(false);
        }
    }

    public void scheduleEventStart() {
        try {
            long day = 24 * 60 * 60 * 1000L;
            Calendar ci = Calendar.getInstance();
            for (String timeOfDay : EventsConfig.EVENT_LH_StartTime) {
                String[] splitTimeOfDay = timeOfDay.split(":");
                _log.info("EVENT_LH_StartTime " + timeOfDay + " splitTimeOfDay[0] " + splitTimeOfDay[0] + " splitTimeOfDay[1] " + splitTimeOfDay[1]);

                ci.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
                ci.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
                ci.set(Calendar.SECOND, 0);

                long delay = ci.getTimeInMillis() - System.currentTimeMillis();
                if (delay < 0) {
                    delay = delay + day;
                }

                _startTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), delay, day);
            }
        } catch (Exception e) {
            _log.info("Last Hero: Error figuring out a start time. Check CtFEventInterval in config file.");
        }
    }

    public static class saveBackCoords extends RunnableImpl {
        @Override
        public void runImpl() {
            for (Player player : getPlayers(players_list)) {
                player.getPlayerVariables().set(PlayerVariables.LAST_HERO_BACK_COORDINATES, player.getX() + " " + player.getY() + ' ' + player.getZ() + ' ' + player.getReflectionId(), -1);
            }
        }
    }

    public static class teleportPlayersToColiseum extends RunnableImpl {
        @Override
        public void runImpl() {
            for (Player player : getPlayers(players_list)) {
                preparePlayer(player);
                player.teleToLocation(Location.findPointToStay(_enter, 150, 500, ReflectionManager.DEFAULT.getGeoIndex()), ReflectionManager.DEFAULT);
            }
        }
    }

    public static class teleportPlayersToSavedCoords extends RunnableImpl {
        @Override
        public void runImpl() {

            for (Player player : getPlayers(players_list)) {
                try {
                    String var = player.getPlayerVariables().get(PlayerVariables.LAST_HERO_BACK_COORDINATES);
                    if (var == null || var.isEmpty()) {
                        continue;
                    }
                    String[] coords = var.split(" ");
                    if (coords.length != 4) {
                        continue;
                    }
                    player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
                    player.getPlayerVariables().remove(PlayerVariables.LAST_HERO_BACK_COORDINATES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class paralyzePlayers extends RunnableImpl {
        @Override
        public void runImpl() {
            for (Player player : getPlayers(players_list)) {
                player.startFrozen();
                if (player.getServitor() != null) {
                    player.getServitor().startFrozen();
                }
            }

        }
    }

    public static class ressurectPlayers extends RunnableImpl {
        @Override
        public void runImpl() {

            for (Player player : getPlayers(players_list)) {
                if (player.isDead()) {
                    player.restoreExp();
                    player.setCurrentCp(player.getMaxCp());
                    player.setCurrentHp(player.getMaxHp(), true);
                    player.setCurrentMp(player.getMaxMp());
                    player.broadcastPacket(new Revive(player));
                }
            }
        }
    }

    public static class healPlayers extends RunnableImpl {
        @Override
        public void runImpl() {

            for (Player player : getPlayers(players_list)) {
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
            }
        }
    }

    private static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            if (cha == null) {
                return;
            }
            Player player = cha.getPlayer();
            if (_status > 0 && player != null && !live_list.contains(player.getObjectId())) {
                ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, new Location(147451, 46728, -3410)), 3000);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
            if (cha == null) {
                return;
            }
            Player player = cha.getPlayer();
            if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && live_list.contains(player.getObjectId())) {
                double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
                double radian = Math.toRadians(angle - 90); // угол в радианах
                int x = (int) (cha.getX() + 50 * Math.sin(radian));
                int y = (int) (cha.getY() - 50 * Math.cos(radian));
                int z = cha.getZ();
                ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, new Location(x, y, z)), 3000);
            }
        }
    }

    private static class TeleportTask extends RunnableImpl {
        Location loc;
        Creature target;

        public TeleportTask(Creature target, Location loc) {
            this.target = target;
            this.loc = loc;
            target.block();
        }

        @Override
        public void runImpl() throws Exception {
            target.unblock();
            target.teleToLocation(loc);
        }
    }

    public class StartTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!isActive()) {
                LOGGER.info("Last Hero not started: event inactive.");
                return;
            }

            DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
            if (runnerEvent.isInProgress()) {
                LOGGER.info("LastHero not started: TerritorySiege in progress");
                return;
            }

            for (Residence c : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                if (c.getSiegeEvent() != null && c.getSiegeEvent().isInProgress()) {
                    LOGGER.info("LastHero not started: CastleSiege in progress");
                    return;
                }
            }

            LOGGER.info("Last Hero : running");
            start(player, lastNpc, new String[]{
                    "1",
                    "1"
            });
        }
    }

    public class question extends RunnableImpl {
        @Override
        public void runImpl() {
            GameObjectsStorage.getPlayers().stream().filter(player -> player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault() && !player.isInOlympiadMode() && !player.isInObserverMode() && !player.isOnSiegeField()).forEach(player -> {
                ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString(new CustomMessage("scripts.events.LastHero.AskPlayer").toString(player));
                player.ask(packet, new CoupleAnswerListener(player, 60000));
            });
        }
    }

    private class CoupleAnswerListener implements OnAnswerListener {
        private final long _expireTime;
        private HardReference<Player> _playerRef1;

        public CoupleAnswerListener(final Player player1, final long expireTime) {
            _playerRef1 = player1.getRef();
            _expireTime = expireTime > 0L ? System.currentTimeMillis() + expireTime : 0L;
        }

        @Override
        public void sayYes() {
            Player player1;
            if ((player1 = _playerRef1.get()) == null
                    || EventsConfig.EVENT_LastHeroHwidProtect && players_hwid_list.contains(player1.getHwid())) {
                return;
            }
            player1.sendMessage(new CustomMessage("scripts.events.LastHero.AnswerYes"));
            addPlayer(player1, lastNpc, args);
        }

        @Override
        public void sayNo() {
            Player player1;
            if ((player1 = _playerRef1.get()) == null) {
                return;
            }
            player1.sendMessage(new CustomMessage("scripts.events.LastHero.AnswerNo"));
        }

        @Override
        public long expireTime() {
            return _expireTime;
        }
    }

    public class announce extends RunnableImpl {
        @Override
        public void runImpl() {
            if (players_list.size() < EventsConfig.LhMinParticipants) {
                AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceEventCancelled");
                _isRegistrationActive = false;
                _status = 0;
                ThreadPoolManager.getInstance().schedule(new autoContinue(), 10000);
                return;
            }

            if (_time_to_start > 1) {
                _time_to_start--;
                AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnouncePreStart", _time_to_start, _minLevel, _maxLevel);
                ThreadPoolManager.getInstance().schedule(new announce(), 60000);
            } else {
                _status = 1;
                _isRegistrationActive = false;
                AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceEventStarting");
                ThreadPoolManager.getInstance().schedule(new prepare(), 5000);
            }
        }
    }

    public class prepare extends RunnableImpl {
        @Override
        public void runImpl() {
            ReflectionUtils.getDoor(24190002).closeMe();
            ReflectionUtils.getDoor(24190003).closeMe();
            cleanPlayers();
            clearArena();
            ThreadPoolManager.getInstance().schedule(new ressurectPlayers(), 100);
            ThreadPoolManager.getInstance().schedule(new healPlayers(), 200);
            ThreadPoolManager.getInstance().schedule(new paralyzePlayers(), 400);
            ThreadPoolManager.getInstance().schedule(new saveBackCoords(), 500);
            ThreadPoolManager.getInstance().schedule(new teleportPlayersToColiseum(), 600);
            ThreadPoolManager.getInstance().schedule(new go(), 60000);

            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceFinalCountdown");
        }
    }

    public class go extends RunnableImpl {
        @Override
        public void runImpl() {
            _status = 2;
            breakDuel();
            upParalyzePlayers();
            checkLive();
            clearArena();
//			removeBuffs(true);
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceFight");
            _endTask = ThreadPoolManager.getInstance().schedule(new endBattle(),
                    TimeUnit.MINUTES.toMillis(EventsConfig.LhEventDurationMin));
        }
    }

    public class endBattle extends RunnableImpl {
        @Override
        public void runImpl() {

            ReflectionUtils.getDoor(24190002).openMe();
            ReflectionUtils.getDoor(24190003).openMe();

            _status = 0;
            removeAura();
            removeState();
            if (live_list.size() == 1) {
                for (Player player : getPlayers(live_list)) {
                    AnnouncementUtils.announceToAll(new CustomMessage("scripts.events.LastHero.AnnounceWiner").addString(player.getName()));
                    ItemFunctions.addItem(player, EventsConfig.EVENT_LastHeroItemID, Math.round(EventsConfig.EVENT_LastHeroRateFinal ? player.getLevel() * EventsConfig.EVENT_LastHeroItemCOUNTFinal : 1 * EventsConfig.EVENT_LastHeroItemCOUNTFinal));
                    setHero(player);
                    break;
                }
            }
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.LastHero.AnnounceEnd");
            ThreadPoolManager.getInstance().schedule(new end(), 30000);
            _isRegistrationActive = false;
            if (_endTask != null) {
                _endTask.cancel(false);
                _endTask = null;
            }
            players_hwid_list.clear();
        }
    }

    public class end extends RunnableImpl {
        @Override
        public void runImpl() {
            ThreadPoolManager.getInstance().schedule(new ressurectPlayers(), 1000);
            ThreadPoolManager.getInstance().schedule(new healPlayers(), 2000);
            ThreadPoolManager.getInstance().schedule(new teleportPlayersToSavedCoords(), 3000);
            ThreadPoolManager.getInstance().schedule(new autoContinue(), 10000);
        }
    }

    public class autoContinue extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_autoContinue > 0) {
                if (_autoContinue >= 1) {
                    _autoContinue = 0;
                    return;
                }
                start(player, lastNpc, new String[]{
                        String.valueOf(_autoContinue + 1),
                        String.valueOf(_autoContinue + 1)
                });
            }
        }
    }

    private class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (_status > 1 && self.isPlayer() && self.getTeam() != TeamType.NONE && live_list.contains(self.getObjectId())) {
                Player player = (Player) self;
                loosePlayer(player);
                checkLive();
                if (killer != null && killer.isPlayer()) {
                    ItemFunctions.addItem((Player) killer, EventsConfig.EVENT_LastHeroItemID, Math.round(EventsConfig.EVENT_LastHeroRate ? player.getLevel() * EventsConfig.EVENT_LastHeroItemCOUNT : 1 * EventsConfig.EVENT_LastHeroItemCOUNT));
                }
            }
        }
    }

    private class OnTeleportImpl implements OnTeleportListener {
        @Override
        public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
            if (_zone.checkIfInZone(x, y, z, reflection)) {
                return;
            }

            if (_status > 1 && player.getTeam() != TeamType.NONE && live_list.contains(player.getObjectId())) {
                removePlayer(player);
                checkLive();
            }
        }
    }

    private class OnPlayerExitListenerImpl implements OnPlayerExitListener {
        @Override
        public void onPlayerExit(Player player) {
            if (player.getTeam() == TeamType.NONE) {
                return;
            }

            // Вышел или вылетел во время регистрации
            if (_status == 0 && _isRegistrationActive && live_list.contains(player.getObjectId())) {
                removePlayer(player);
                return;
            }

            // Вышел или вылетел во время телепортации
            if (_status == 1 && live_list.contains(player.getObjectId())) {
                removePlayer(player);

                try {
                    String var = player.getPlayerVariables().get(PlayerVariables.LAST_HERO_BACK_COORDINATES);
                    if (var == null || var.isEmpty()) {
                        return;
                    }
                    String[] coords = var.split(" ");
                    if (coords.length != 4) {
                        return;
                    }
                    player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
                    player.getPlayerVariables().remove(PlayerVariables.LAST_HERO_BACK_COORDINATES);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            // Вышел или вылетел во время эвента
            if (_status > 1 && player.getTeam() != TeamType.NONE && live_list.contains(player.getObjectId())) {
                removePlayer(player);
                checkLive();
            }
        }
    }
}