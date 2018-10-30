package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listeners.impl.OnCastleDataLoaded;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Hack
 *
 * @author l2gw
 * Date: 24.04.2017 21:37
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _21003_Benom extends Quest {
    private static final Logger _log = LoggerFactory.getLogger(_21003_Benom.class);
    private static final int BENOM = 29054;
    private static final int RUNE = 8;
    private static final int DUNGEON_GK = 35506;
    private static final int TELEPORT = 29055;
    private static final Location benomLoc = new Location(11882, -49216, -3008, 43200);
    private static final Location dungeonLoc = new Location(12589, -49044, -2950);
    private static final Location castleLoc = new Location(11979, -49154, -530);
    private static final Map<String, Future> tasks = new HashMap<>();
    private static boolean isBenomSpawned = false;
    private static boolean isBenomInTrone = false;
    private static String prefix = "Benom-";
    private static NpcInstance lastSpawn;

    public _21003_Benom() {
        super(false);
        addStartNpc(DUNGEON_GK);
        addTalkId(DUNGEON_GK);
        addKillId(BENOM);
        onLoad();
    }

    public void onLoad() {
        GameServer.getInstance().globalListeners().add(OnCastleDataLoaded.class, new OnCastleDataLoaded() {
            @Override
            public void onLoad(int castleId) {
                if (castleId == RUNE) {
                    init();
                    GameServer.getInstance().globalListeners().remove(this);
                }
            }
        });
    }

    public void init() {
        long siegeDate = getSiegeDate();
        _log.info("_21003_Benom: siege date: " + new Date(siegeDate) + " / " + siegeDate);
        long spawnTime = siegeDate - 86100000L;
        _log.info("_21003_Benom: spawn time: " + new Date(spawnTime));

        if (spawnTime > System.currentTimeMillis()) {
            _log.info("_21003_Benom: start spawn timer");
            startQuestTimer("benom_spawn", spawnTime - System.currentTimeMillis());
        } else {
            long siegeEndTime = siegeDate + 2 * 60 * 60000;
            _log.info("_21003_Benom: spawn _21003_Benom");
            _log.info("_21003_Benom: despawn time: " + new Date(siegeEndTime));
            startQuestTimer("benom_despawn", siegeEndTime - System.currentTimeMillis() + 15000);
            spawnBenom();
            long tmp = siegeDate - System.currentTimeMillis() + 5 * 60000;
            if (tmp > 0) {
                _log.info("_21003_Benom: start benom task: " + (tmp / 60000) + " min.");
                startQuestTimer("benom_task", tmp);
            } else {
                _log.info("_21003_Benom: start benom task: 5 min.");
                startQuestTimer("benom_task", 5 * 60000);
            }

            isBenomSpawned = true;
        }
    }

    public String onEvent(String event) {
        if (event.equals("benom_spawn")) {
            _log.info("_21003_Benom: spawning benom");
            spawnBenom();
            isBenomSpawned = true;
            isBenomInTrone = false;
            long siegeDate = getSiegeDate();
            long siegeEndTime = siegeDate + 2 * 60 * 60000;
            _log.info("_21003_Benom: start despawn timer: " + new Date(siegeEndTime));
            startQuestTimer("benom_despawn", siegeEndTime - System.currentTimeMillis());
            _log.info("_21003_Benom: Start check timer: 24 hour");
            startQuestTimer("benom_task", siegeDate - System.currentTimeMillis() + 5 * 60000);
        } else if (event.equals("benom_task")) {
            _log.info("_21003_Benom: benom_task started");
            if (ResidenceHolder.getInstance().getResidence(Castle.class, RUNE).isInSiege()) {
                if (lastSpawn != null && !lastSpawn.isDead() && !isBenomInTrone) //TODO: CT killed > 1 check
                {
                    _log.info("_21003_Benom: teleport benom to throne");
                    lastSpawn.teleToLocation(castleLoc);
                    lastSpawn.setAggroRange(1000);
                    isBenomInTrone = true;
                }
                _log.info("_21003_Benom: reschedule benim_task");
                cancelQuestTimer("benom_task");
                startQuestTimer("benom_task", 5 * 60000);
            } else {
                _log.info("_21003_Benom: Rune siege is over, stop benom task, despawn _21003_Benom");
                deleteLastSpawn();
                updateSpawnTime();
                cancelQuestTimer("benom_task");
            }
        } else if (event.equals("benom_despawn")) {
            _log.info("_21003_Benom: despawn task started");
            deleteLastSpawn();
            if (!ResidenceHolder.getInstance().getResidence(Castle.class, RUNE).isInSiege())
                updateSpawnTime();
            else {
                _log.info("_21003_Benom: siege in progress start update task");
                startQuestTimer("benom_update", 15 * 60000);
            }
        } else if (event.equals("benom_update"))
            updateSpawnTime();

        return null;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == DUNGEON_GK)
            // Let's check that player is in the castle owner clan
            if (st.getPlayer().getClan() != null && st.getPlayer().getClan().getClanId() == RUNE && !ResidenceHolder.getInstance().getResidence(Castle.class, RUNE).getSiegeEvent().isInProgress()) {
                //st.getPlayer().setVar("InstanceRP", st.getPlayer().getX() + "," + st.getPlayer().getY() + "," + st.getPlayer().getZ());
                st.getPlayer()._stablePoint = st.getPlayer().getLoc();
                st.getPlayer().teleToLocation(dungeonLoc);
                return null;
            } else
                return prefix + "clan.htm";
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        cancelQuestTimer("benom_task");
        if (!isBenomInTrone)
            addSpawn(TELEPORT, new Location(12200, -49220, -3000), 0, 900000);
        return super.onKill(npc, qs);
    }

    private void updateSpawnTime() {
        _log.info("_21003_Benom: calculate spawn time");
        long siegeTime = getSiegeDate();
        long spawnTime = siegeTime - 86100000L;
        _log.info("_21003_Benom AI: spawn time: " + new Date(spawnTime));
        isBenomSpawned = false;
        isBenomInTrone = false;
        startQuestTimer("benom_spawn", spawnTime - System.currentTimeMillis());
    }

    private void startQuestTimer(String taskName, long delay) {
        if (tasks.get(taskName) != null)
            return;
        tasks.put(taskName, ThreadPoolManager.getInstance().schedule(() -> onEvent(taskName), delay));
    }

    private void cancelQuestTimer(String taskName) {
        Future task = tasks.get(taskName);
        if (task == null)
            return;
        task.cancel(false);
    }

    private void spawnBenom() {
        lastSpawn = addSpawn(BENOM, benomLoc, 0, 0);
    }

    private void deleteLastSpawn() {
        if (lastSpawn != null)
            lastSpawn.deleteMe();
    }

    private long getSiegeDate() {
        return ResidenceHolder.getInstance().getResidence(Castle.class, RUNE).getSiegeDate().toInstant().toEpochMilli();
    }
}
