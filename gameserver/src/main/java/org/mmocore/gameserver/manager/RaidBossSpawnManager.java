package org.mmocore.gameserver.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.mysql;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.SqlBatch;
import org.mmocore.gameserver.utils.TimeUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class RaidBossSpawnManager {
    public static final Integer KEY_RANK = -1;
    public static final Integer KEY_TOTAL_POINTS = 0;
    protected static final Map<Integer, Spawner> _spawntable = new ConcurrentHashMap<>();
    protected static final Map<Integer, StatsSet> _storedInfo = new ConcurrentHashMap<>();
    protected static final Map<Integer, Map<Integer, Integer>> _points = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(RaidBossSpawnManager.class);

    private RaidBossSpawnManager() {
        if (!ServerConfig.DONTLOADSPAWN)
            reloadBosses();
    }

    public static RaidBossSpawnManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void reloadBosses() {
        loadStatus();
        restorePointsTable();
        calculateRanking();
    }

    public void cleanUp() {
        updateAllStatusDb();
        updatePointsDb();

        _storedInfo.clear();
        _spawntable.clear();
        _points.clear();
    }

    private void loadStatus() {
        Connection con = null;
        final Statement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            rset = con.createStatement().executeQuery("SELECT * FROM `raidboss_status`");
            while (rset.next()) {
                final int id = rset.getInt("id");
                final StatsSet info = new StatsSet();
                info.set("current_hp", rset.getDouble("current_hp"));
                info.set("current_mp", rset.getDouble("current_mp"));
                info.set("respawn_delay", rset.getInt("respawn_delay"));
                _storedInfo.put(id, info);
            }
        } catch (Exception e) {
            LOGGER.warn("RaidBossSpawnManager: Couldnt load raidboss statuses");
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        LOGGER.info("RaidBossSpawnManager: Loaded " + _storedInfo.size() + " Statuses");
    }

    private void updateAllStatusDb() {
        for (final int id : _storedInfo.keySet()) {
            updateStatusDb(id);
        }
    }

    private int updateStatusDb(final int id) // возвращает время респа
    {
        final Spawner spawner = _spawntable.get(id);
        if (spawner == null) {
            return -1;
        }

        StatsSet info = _storedInfo.get(id);
        if (info == null) {
            _storedInfo.put(id, info = new StatsSet());
        }

        final NpcInstance raidboss = spawner.getFirstSpawned();
        if (raidboss instanceof ReflectionBossInstance) {
            return -1;
        }

        int respawn = 0;
        if (raidboss != null && !raidboss.isDead()) {
            info.set("current_hp", raidboss.getCurrentHp());
            info.set("current_mp", raidboss.getCurrentMp());
            info.set("respawn_delay", 0);
        } else {
            respawn = spawner.getRespawnTime();
            info.set("current_hp", 0);
            info.set("current_mp", 0);
            info.set("respawn_delay", respawn);
        }

        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO `raidboss_status` (id, current_hp, current_mp, respawn_delay) VALUES (?,?,?,?)");
            statement.setInt(1, id);
            statement.setDouble(2, info.getDouble("current_hp"));
            statement.setDouble(3, info.getDouble("current_mp"));
            statement.setInt(4, respawn);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.warn("RaidBossSpawnManager: Couldnt update raidboss_status table");
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return respawn;
    }

    public void addNewSpawn(final int npcId, final Spawner spawnDat) {
        if (_spawntable.containsKey(npcId)) {
            return;
        }

        _spawntable.put(npcId, spawnDat);

        final StatsSet info = _storedInfo.get(npcId);
        if (info != null) {
            spawnDat.setRespawnTime(info.getInteger("respawn_delay", 0));
        }
    }

    public void onBossSpawned(final NpcInstance raidboss) {
        final int bossId = raidboss.getNpcId();
        if (!_spawntable.containsKey(bossId)) {
            return;
        }

        final StatsSet info = _storedInfo.get(bossId);
        if (info != null && info.getDouble("current_hp") > 1) {
            raidboss.setCurrentHp(info.getDouble("current_hp"), false);
            raidboss.setCurrentMp(info.getDouble("current_mp"));
        }

        GmManager.broadcastMessageToGMs("Spawning RaidBoss " + raidboss.getName());
        if (AllSettingsConfig.allowBossSpawnAnnounce
                && !ArrayUtils.contains(AllSettingsConfig.ignoredBossSpawnAnnounceIds, raidboss.getNpcId())) {
            AnnouncementUtils.announceMultilang("Появился Рейдовый Босс -> [" + raidboss.getName() + "]",
                    "Spawning RaidBoss -> [" + raidboss.getName() + "]");
        }

        //LOGGER.info("Spawned RaidBoss " + raidboss.getName());
    }

    public void onBossDespawned(final NpcInstance raidboss) {
        final int respawn = updateStatusDb(raidboss.getNpcId());
        if (respawn >= 0) // 0 оставлен для проверки
        {
            final Instant respawnTime = Instant.ofEpochSecond(respawn);
            LOGGER.info("Killed RaidBoss " + raidboss.getName() + ", respawn in " + TimeUtils.dateTimeFormat(respawnTime));
        }
    }

    public Status getRaidBossStatusId(final int bossId) {
        final Spawner spawner = _spawntable.get(bossId);
        if (spawner == null) {
            return Status.UNDEFINED;
        }

        final NpcInstance npc = spawner.getFirstSpawned();
        return npc == null ? Status.DEAD : Status.ALIVE;
    }

    public boolean isDefined(final int bossId) {
        return _spawntable.containsKey(bossId);
    }

    // ----------- Points & Ranking -----------

    public Map<Integer, Spawner> getSpawnTable() {
        return _spawntable;
    }

    private void restorePointsTable() {
        Connection con = null;
        Statement statement = null;
        ResultSet rset = null;

        try {
            //read raidboss points
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            rset = statement.executeQuery("SELECT owner_id, boss_id, points FROM `raidboss_points` ORDER BY owner_id ASC");
            int currentOwner = 0;
            Map<Integer, Integer> score = null;
            while (rset.next()) {
                if (currentOwner != rset.getInt("owner_id")) {
                    currentOwner = rset.getInt("owner_id");
                    score = new HashMap<>();
                    _points.put(currentOwner, score);
                }

                assert score != null;
                final int bossId = rset.getInt("boss_id");
                final NpcTemplate template = NpcHolder.getInstance().getTemplate(bossId);
                if (bossId != KEY_RANK && bossId != KEY_TOTAL_POINTS && template != null && template.rewardRp > 0) {
                    score.put(bossId, rset.getInt("points"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("RaidBossSpawnManager: Couldnt load raidboss points", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void updatePointsDb() {
        if (!mysql.set("DELETE FROM `raidboss_points`")) {
            LOGGER.warn("RaidBossSpawnManager: Couldnt empty raidboss_points table");
        }

        if (_points.isEmpty())
            return;

        Connection con = null;
        Statement statement = null;
        StringBuilder sb;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            final SqlBatch b = new SqlBatch("INSERT INTO `raidboss_points` (owner_id, boss_id, points) VALUES");

            for (final Entry<Integer, Map<Integer, Integer>> pointEntry : _points.entrySet()) {
                final Map<Integer, Integer> tmpPoint = pointEntry.getValue();
                if (tmpPoint == null || tmpPoint.isEmpty()) {
                    continue;
                }

                for (final Entry<Integer, Integer> pointListEntry : tmpPoint.entrySet()) {
                    if (KEY_RANK.equals(pointListEntry.getKey()) || KEY_TOTAL_POINTS.equals(pointListEntry.getKey()) || pointListEntry.getValue() == null || pointListEntry.getValue() == 0) {
                        continue;
                    }

                    sb = new StringBuilder("(");
                    sb.append(pointEntry.getKey()).append(','); // игрок
                    sb.append(pointListEntry.getKey()).append(','); // босс
                    sb.append(pointListEntry.getValue()).append(')'); // количество очков
                    b.write(sb.toString());
                }
            }

            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (SQLException e) {
            LOGGER.warn("RaidBossSpawnManager: Couldnt update raidboss_points table");
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void addPoints(final int ownerId, final int bossId, final int points) {
        if (points <= 0 || ownerId <= 0 || bossId <= 0)
            return;

        // ищем этого игрока в таблице рейтинга
        Map<Integer, Integer> pointsTable = _points.get(ownerId);

        // не нашли? добавляем
        if (pointsTable == null) {
            pointsTable = new HashMap<>();
            _points.put(ownerId, pointsTable);
        }

        // его таблица пуста? добавляем новую запись
        if (pointsTable.isEmpty())
            pointsTable.put(bossId, points);
        else
        // нет? сперва ищем старую
        {
            final Integer currentPoins = pointsTable.get(bossId);
            pointsTable.put(bossId, currentPoins == null ? points : currentPoins + points);
        }
    }

    public TreeMap<Integer, Integer> calculateRanking() {
        // таблица PlayerId - Rank для внутреннего пользования
        final TreeMap<Integer, Integer> tmpRanking = new TreeMap<>();

        // берем существующую таблицу с информацией о поинтах и перебираем по строкам
        for (final Entry<Integer, Map<Integer, Integer>> point : _points.entrySet()) {
            // получаем таблицу пар <BossId - Points>
            final Map<Integer, Integer> tmpPoint = point.getValue();

            // ранг и сумма нам тут не нужны, мы их пересчитываем
            tmpPoint.remove(KEY_RANK);
            tmpPoint.remove(KEY_TOTAL_POINTS);
            int totalPoints = 0;

            // собираем всю сумму для игрока
            for (final Entry<Integer, Integer> e : tmpPoint.entrySet()) {
                totalPoints += e.getValue();
            }

            // вдруг кто левый затесался
            if (totalPoints != 0) {
                // кладем в кучу сумму
                tmpPoint.put(KEY_TOTAL_POINTS, totalPoints);
                // а это пригодится чуть позже
                tmpRanking.put(totalPoints, point.getKey());
            }
        }

        // перебираем таблицу рангов и сливаем ее с общей таблицей
        int ranking = 1;
        for (final Entry<Integer, Integer> entry : tmpRanking.descendingMap().entrySet()) {
            // ищем соответствующую строку из основной таблицы
            final Map<Integer, Integer> tmpPoint = _points.get(entry.getValue());

            // и добавляем туда ранг
            tmpPoint.put(KEY_RANK, ranking);
            ranking++;
        }

        return tmpRanking;
    }

    /*
    Rank 1 = 2,500 Clan Reputation Points
    Rank 2 = 1,800 Clan Reputation Points
    Rank 3 = 1,400 Clan Reputation Points
    Rank 4 = 1,200 Clan Reputation Points
    Rank 5 = 900 Clan Reputation Points
    Rank 6 = 700 Clan Reputation Points
    Rank 7 = 600 Clan Reputation Points
    Rank 8 = 400 Clan Reputation Points
    Rank 9 = 300 Clan Reputation Points
    Rank 10 = 200 Clan Reputation Points
    Rank 11~50 = 50 Clan Reputation Points
    Rank 51~100 = 25 Clan Reputation Points
     */
    public void distributeRewards() {
        final TreeMap<Integer, Integer> ranking = calculateRanking();
        final Iterator<Integer> e = ranking.descendingMap().values().iterator();
        int counter = 1;
        while (e.hasNext() && counter <= 100) {
            int reward = 0;
            final int playerId = e.next();
            if (counter == 1) {
                reward = 2500;
            } else if (counter == 2) {
                reward = 1800;
            } else if (counter == 3) {
                reward = 1400;
            } else if (counter == 4) {
                reward = 1200;
            } else if (counter == 5) {
                reward = 900;
            } else if (counter == 6) {
                reward = 700;
            } else if (counter == 7) {
                reward = 600;
            } else if (counter == 8) {
                reward = 400;
            } else if (counter == 9) {
                reward = 300;
            } else if (counter == 10) {
                reward = 200;
            } else if (counter <= 50) {
                reward = 50;
            } else if (counter <= 100) {
                reward = 25;
            }
            final Player player = GameObjectsStorage.getPlayer(playerId);
            Clan clan = null;
            if (player != null) {
                clan = player.getClan();
            } else {
                final int clanId = CharacterDAO.getInstance().getPlayerClanId(playerId);
                clan = ClanTable.getInstance().getClan(clanId);
            }
            if (clan != null) {
                clan.incReputation(reward, true, "RaidPoints");
            }
            counter++;
        }
        _points.clear();
        updatePointsDb();
    }

    public Map<Integer, Map<Integer, Integer>> getPoints() {
        return _points;
    }

    public Map<Integer, Integer> getPointsForOwnerId(final int ownerId) {
        return _points.get(ownerId);
    }

    public enum Status {
        ALIVE,
        DEAD,
        UNDEFINED
    }

    private static class LazyHolder {
        private static final RaidBossSpawnManager INSTANCE = new RaidBossSpawnManager();
    }
}