package org.mmocore.gameserver.scripts.events.LettersCollection;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.reward.RewardData;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mmocore.gameserver.utils.ItemFunctions.*;

public abstract class LettersCollection implements OnInitScriptListener {
    public static final Logger _log = LoggerFactory.getLogger(LettersCollection.class);
    private static final OnPlayerEnterListener _eventAnnouncer = new OnPlayerEnterListenerImpl();
    private static final RewardList DROP_DATA;
    // Переменные, определять
    protected static boolean _active;
    protected static int[][] letters;
    protected static int EVENT_MANAGERS[][] = null;
    protected static String _msgStarted;
    protected static String _msgEnded;
    // Буквы, статика
    protected static int A = 3875;
    protected static int C = 3876;
    protected static int E = 3877;
    protected static int F = 3878;
    protected static int G = 3879;
    protected static int H = 3880;
    protected static int I = 3881;
    protected static int L = 3882;
    protected static int N = 3883;
    protected static int O = 3884;
    protected static int R = 3885;
    protected static int S = 3886;
    protected static int T = 3887;
    protected static int II = 3888;
    protected static int Y = 13417;
    protected static int _5 = 13418;
    protected static int EVENT_MANAGER_ID = 31230;
    // Контейнеры, не трогать
    protected static Map<String, Integer[][]> _words = new HashMap<String, Integer[][]>();
    protected static Map<String, RewardData[]> _rewards = new HashMap<String, RewardData[]>();
    protected static List<SimpleSpawner> _spawns = new ArrayList<>();

    static {
        final RewardGroup eventDrop = new RewardGroup(150000.);
        eventDrop.setNotRate(true);
        eventDrop.addData(new RewardData(A, 1, 1, 200000.));
        eventDrop.addData(new RewardData(C, 1, 1, 200000.));
        eventDrop.addData(new RewardData(E, 1, 1, 450000.));
        eventDrop.addData(new RewardData(F, 1, 1, 50000.));
        eventDrop.addData(new RewardData(G, 1, 1, 20000.));
        eventDrop.addData(new RewardData(H, 1, 1, 40000.));
        eventDrop.addData(new RewardData(I, 1, 1, 20000.));
        eventDrop.addData(new RewardData(L, 1, 1, 20000.));
        eventDrop.addData(new RewardData(N, 1, 1, 20000.));
        eventDrop.addData(new RewardData(O, 1, 1, 20000.));
        eventDrop.addData(new RewardData(R, 1, 1, 20000.));
        eventDrop.addData(new RewardData(S, 1, 1, 20000.));
        eventDrop.addData(new RewardData(T, 1, 1, 20000.));
        eventDrop.addData(new RewardData(II, 1, 1, 20000.));
        eventDrop.addData(new RewardData(Y, 1, 1, 20000.));

        DROP_DATA = new RewardList(RewardType.EVENT, true);
        DROP_DATA.add(eventDrop);
    }

    /**
     * Читает статус эвента из базы.
     */
    protected static boolean isActive() {
        return ServerVariables.getString("letter", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            _active = true;
            startEvent();
            _log.info("Loaded Event: Letter Collections [state: activated]");
        } else {
            _log.info("Loaded Event: Letter Collections [state: deactivated]");
        }
    }

    /**
     * Спавнит эвент менеджеров
     */
    protected void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
    }

    private void SpawnNPCs(final int npcId, final int[][] locations, final List<SimpleSpawner> list) {
        final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        if (template == null) {
            System.out.println("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
            Thread.dumpStack();
            return;
        }
        for (final int[] location : locations) {
            final SimpleSpawner sp = new SimpleSpawner(template);
            sp.setLoc(new Location(location[0], location[1], location[2]));
            sp.setAmount(1);
            sp.setRespawnDelay(0);
            sp.init();
            list.add(sp);
        }
    }

    /**
     * Удаляет спавн эвент менеджеров
     */
    protected void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
    }

    private void deSpawnNPCs(List<SimpleSpawner> spawns) {
        spawns.stream().forEach(Spawner::deleteAll);
        spawns.clear();
    }

    @Bypass("events.LettersCollection:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!isActive()) {
            startEvent();
            ServerVariables.set("LettersCollection", "on");
            _log.info("Event 'Letters Collection' activated.");
            _active = true;
        } else
            player.sendAdminMessage("Event 'Letters Collection' already started.");
    }

    @Bypass("events.LettersCollection:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (isActive()) {
            stopEvent();
            ServerVariables.set("LettersCollection", "off");
            _log.info("Event 'Letters Collection' stoped.");
            _active = false;
        } else
            player.sendAdminMessage("Event 'Letters Collection' not started.");
    }

    private boolean SimpleCheckDrop(Creature mob, Creature killer) {
        return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        PlayerListenerList.addGlobal(_eventAnnouncer);
        spawnEventManagers();
        AnnouncementUtils.announceToAll(new CustomMessage(_msgStarted));
        NpcHolder.getInstance().addEventDrop(DROP_DATA);
        System.out.println("Event Letter Collections started.");
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        unSpawnEventManagers();
        NpcHolder.getInstance().removeEventDrop();
        AnnouncementUtils.announceToAll(new CustomMessage(_msgEnded));
        PlayerListenerList.removeGlobal(_eventAnnouncer);
        System.out.println("Event Letter Collections stopped.");
    }

    /**
     * Обмен эвентовых вещей, где var[0] - слово.
     */
    @Bypass("events.LettersCollection.LettersCollection:exchange")
    public void exchange(Player player, NpcInstance lastNpc, String[] var) {
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }

        Integer[][] mss = _words.get(var[0]);

        for (Integer[] l : mss) {
            if (getItemCount(player, l[0]) < l[1]) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return;
            }
        }

        for (Integer[] l : mss) {
            removeItem(player, l[0], l[1]);
        }

        RewardData[] rewards = _rewards.get(var[0]);
        int sum = 0;
        for (RewardData r : rewards) {
            sum += r.getChance();
        }
        int random = Rnd.get(sum);
        sum = 0;
        for (RewardData r : rewards) {
            sum += r.getChance();
            if (sum > random) {
                addItem(player, r.getItemId(), Rnd.get(r.getMinDrop(), r.getMaxDrop()));
                return;
            }
        }
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (_active) {
                AnnouncementUtils.announceToAll(new CustomMessage(_msgStarted));
            }
        }
    }
}