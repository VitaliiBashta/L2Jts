package org.mmocore.gameserver.model.entity;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.HeroDAO;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.database.dao.impl.variables.PlayerVariablesDAO;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.custom.HeroRewardTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: перенести работу с бд в дао
 */
public class Hero {
    public static final String COUNT = "count";
    public static final String PLAYED = "played";
    public static final String CLAN_NAME = "clan_name";
    public static final String CLAN_CREST = "clan_crest";
    public static final String ALLY_NAME = "ally_name";
    public static final String ALLY_CREST = "ally_crest";
    public static final String ACTIVE = "active";
    public static final String MESSAGE = "message";
    private static final Logger _log = LoggerFactory.getLogger(Hero.class);
    private static final String GET_HEROES = "SELECT * FROM heroes WHERE played = 1";
    private static final String GET_ALL_HEROES = "SELECT * FROM heroes";
    private static Map<Integer, StatsSet> _heroes;
    private static Map<Integer, StatsSet> _completeHeroes;
    private static Map<Integer, List<HeroDiary>> _herodiary;
    private static List<HeroRewardTemplate> heroRewards;

    public Hero() {
        init();
    }

    public static Hero getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static void HeroSetClanAndAlly(final int charId, final StatsSet hero) {
        Entry<Clan, Alliance> e = ClanTable.getInstance().getClanAndAllianceByCharId(charId);
        hero.set(CLAN_CREST, e.getKey() == null ? 0 : e.getKey().getCrestId());
        hero.set(CLAN_NAME, e.getKey() == null ? "" : e.getKey().getName());
        hero.set(ALLY_CREST, e.getValue() == null ? 0 : e.getValue().getAllyCrestId());
        hero.set(ALLY_NAME, e.getValue() == null ? "" : e.getValue().getAllyName());
        e = null;
    }

    public static void addSkills(final Player player) {
        player.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HEROIC_MIRACLE, 1));
        player.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HEROIC_BERSERKER, 1));
        player.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HEROIC_VALOR, 1));
        player.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HEROIC_GRANDEUR, 1));
        player.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HEROIC_DREAD, 1));
    }

    public static void removeSkills(final Player player) {
        player.removeSkillById(Skill.SKILL_HEROIC_MIRACLE);
        player.removeSkillById(Skill.SKILL_HEROIC_BERSERKER);
        player.removeSkillById(Skill.SKILL_HEROIC_VALOR);
        player.removeSkillById(Skill.SKILL_HEROIC_GRANDEUR);
        player.removeSkillById(Skill.SKILL_HEROIC_DREAD);
    }

    private void init() {
        _heroes = new ConcurrentHashMap<>();
        _completeHeroes = new ConcurrentHashMap<>();
        _herodiary = new ConcurrentHashMap<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(GET_HEROES);
            rset = statement.executeQuery();
            while (rset.next()) {
                final StatsSet hero = new StatsSet();
                final int charId = rset.getInt(Olympiad.CHAR_ID);
                hero.set(Olympiad.CHAR_ID, charId);
                hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
                hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
                hero.set(COUNT, rset.getInt(COUNT));
                hero.set(PLAYED, rset.getInt(PLAYED));
                hero.set(ACTIVE, rset.getInt(ACTIVE));
                HeroSetClanAndAlly(charId, hero);
                loadDiary(charId);
                loadMessage(charId);
                _heroes.put(charId, hero);
            }
            DbUtils.close(statement, rset);

            statement = con.prepareStatement(GET_ALL_HEROES);
            rset = statement.executeQuery();
            while (rset.next()) {
                final StatsSet hero = new StatsSet();
                final int charId = rset.getInt(Olympiad.CHAR_ID);
                hero.set(Olympiad.CHAR_ID, charId);
                hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
                hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
                hero.set(COUNT, rset.getInt(COUNT));
                hero.set(PLAYED, rset.getInt(PLAYED));
                hero.set(ACTIVE, rset.getInt(ACTIVE));
                HeroSetClanAndAlly(charId, hero);
                _completeHeroes.put(charId, hero);
            }
        } catch (SQLException e) {
            _log.warn("Hero System: Couldnt load Heroes", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        _log.info("Hero System: Loaded " + _heroes.size() + " Heroes.");
        _log.info("Hero System: Loaded " + _completeHeroes.size() + " all time Heroes.");
        try {
            heroRewards = Util.parseTemplateConfig(LostDreamCustom.heroRewards, HeroRewardTemplate.class);
        } catch (Exception e) {
//			_log.warn("Error while parse additional rewards for heroes!");
            heroRewards = new ArrayList<>();
        }

    }

    public Map<Integer, StatsSet> getHeroes() {
        return _heroes;
    }

    public synchronized void clearHeroes() {
        HeroDAO.getInstance().clearHeroes();

        if (!_heroes.isEmpty()) {
            for (final StatsSet hero : _heroes.values()) {
                if (hero.getInteger(ACTIVE) == 0) {
                    continue;
                }

                final int objectId = hero.getInteger(Olympiad.CHAR_ID);

                final Player player = GameObjectsStorage.getPlayer(objectId);
                if (player != null) {
                    if (player.getCustomPlayerComponent().isTemporalHero())
                        continue;
                    final PcInventory inventory = player.getInventory();
                    inventory.writeLock();
                    try {
                        for (final ItemInstance item : player.getInventory().getItems()) {
                            if (item.isHeroWeapon()) {
                                player.getInventory().destroyItem(item);
                            }
                        }
                    } finally {
                        inventory.writeUnlock();
                    }

                    player.setHero(false);
                    player.updatePledgeClass();
                    player.broadcastUserInfo(true);
                } else {
                    String value = PlayerVariablesDAO.getInstance().getValue(objectId, PlayerVariables.TEMPORAL_HERO.name());
                    if (value != null && !value.isEmpty()) {
                        long time = Long.parseLong(value);
                        final ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
                        final ZonedDateTime endHeroTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
                        if (endHeroTime.toInstant().toEpochMilli() > dateTime.toInstant().toEpochMilli()) {
                            continue;
                        }
                    }

                    Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objectId, ItemInstance.ItemLocation.PAPERDOLL);
                    for (final ItemInstance item : items) {
                        if (item.isHeroWeapon()) {
                            item.delete();
                        }
                    }

                    items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objectId, ItemInstance.ItemLocation.INVENTORY);
                    for (final ItemInstance item : items) {
                        if (item.isHeroWeapon()) {
                            item.delete();
                        }
                    }
                }
            }
        }

        _heroes.clear();
        _herodiary.clear();
    }

    public synchronized boolean computeNewHeroes(final List<StatsSet> newHeroes) {
        if (newHeroes.isEmpty()) {
            return true;
        }

        final Map<Integer, StatsSet> heroes = new ConcurrentHashMap<>();
        final boolean error = false;

        for (final StatsSet hero : newHeroes) {
            final int charId = hero.getInteger(Olympiad.CHAR_ID);

            if (_completeHeroes != null && _completeHeroes.containsKey(charId)) {
                final StatsSet oldHero = _completeHeroes.get(charId);
                final int count = oldHero.getInteger(COUNT);
                oldHero.set(COUNT, count + 1);
                oldHero.set(PLAYED, 1);
                oldHero.set(ACTIVE, 0);

                heroes.put(charId, oldHero);
            } else {
                final StatsSet newHero = new StatsSet();
                newHero.set(Olympiad.CHAR_NAME, hero.getString(Olympiad.CHAR_NAME));
                newHero.set(Olympiad.CLASS_ID, hero.getInteger(Olympiad.CLASS_ID));
                newHero.set(COUNT, 1);
                newHero.set(PLAYED, 1);
                newHero.set(ACTIVE, 0);

                heroes.put(charId, newHero);
            }

            addHeroDiary(charId, HeroDiary.ACTION_HERO_GAINED, 0);
            loadDiary(charId);
            addHeroBonusItems(charId);
        }

        _heroes.putAll(heroes);
        heroes.clear();

        updateHeroes(0);

        return error;
    }

    private void addHeroBonusItems(int obj_id) {
        if (heroRewards == null || heroRewards.size() == 0)
            return;
        Player player = World.getPlayer(obj_id);
        if (player != null) {
            for (HeroRewardTemplate template : heroRewards)
                if (Rnd.chance(template.getChance()))
                    ItemFunctions.addItem(player, template.getItemId(), template.getItemCount());
        } else {
            for (HeroRewardTemplate template : heroRewards)
                if (Rnd.chance(template.getChance()))
                    Util.addOfflineItem(obj_id, template.getItemId(), template.getItemCount(), "Hero bonus");
        }
    }

    public void updateHeroes(final int id) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO heroes (char_id, count, played, active) VALUES (?,?,?,?)");

            for (final Integer heroId : _heroes.keySet()) {
                if (id > 0 && heroId != id) {
                    continue;
                }
                final StatsSet hero = _heroes.get(heroId);
                statement.setInt(1, heroId);
                statement.setInt(2, hero.getInteger(COUNT));
                statement.setInt(3, hero.getInteger(PLAYED));
                statement.setInt(4, hero.getInteger(ACTIVE));
                statement.execute();
                if (_completeHeroes != null && !_completeHeroes.containsKey(heroId)) {
                    HeroSetClanAndAlly(heroId, hero);
                    _completeHeroes.put(heroId, hero);
                }
            }
        } catch (SQLException e) {
            _log.warn("Hero System: Couldnt update Heroes");
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public boolean isHero(final int id) {
        if (_heroes == null || _heroes.isEmpty()) {
            return false;
        }
        if (_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 1) {
            return true;
        }
        return false;
    }

    public boolean isInactiveHero(final int id) {
        if (_heroes == null || _heroes.isEmpty()) {
            return false;
        }
        if (_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 0) {
            return true;
        }
        return false;
    }

    public void activateHero(final Player player) {
        final StatsSet hero = _heroes.get(player.getObjectId());
        hero.set(ACTIVE, 1);
        _heroes.replace(player.getObjectId(), hero);

        if (player.getPlayerClassComponent().isBaseExactlyActiveId()) {
            addSkills(player);
        }

        player.setHero(true);
        player.updatePledgeClass();
        player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.GIVE_HERO));
        if (player.getClan() != null && player.getClan().getLevel() >= 5) {
            player.getClan().incReputation(1000, true, "Hero:activateHero:" + player);
            player.getClan().broadcastToOtherOnlineMembers(new SystemMessage(SystemMsg.CLAN_MEMBER_S1_WAS_NAMED_A_HERO_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addString(player.getName()).addNumber(Math.round(1000 * ServerConfig.RATE_CLAN_REP_SCORE)), player);
        }
        player.broadcastUserInfo(true);
        updateHeroes(player.getObjectId());
    }

    public void loadDiary(final int charId) {
        final List<HeroDiary> diary = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM  heroes_diary WHERE charId=? ORDER BY time ASC");
            statement.setInt(1, charId);
            rset = statement.executeQuery();

            while (rset.next()) {
                final Instant time = Instant.ofEpochMilli(rset.getLong("time"));
                final int action = rset.getInt("action");
                final int param = rset.getInt("param");

                final HeroDiary d = new HeroDiary(action, time, param);
                diary.add(d);
            }

            _herodiary.put(charId, diary);

            if (OtherConfig.DEBUG) {
                _log.info("Hero System: Loaded " + diary.size() + " diary entries for Hero(object id: #" + charId + ')');
            }
        } catch (SQLException e) {
            _log.warn("Hero System: Couldnt load Hero Diary for CharId: " + charId, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void showHeroDiary(final Player activeChar, final int heroclass, final int charid, final int page) {
        final int perpage = 10;

        final List<HeroDiary> mainlist = _herodiary.get(charid);

        if (mainlist != null) {
            final HtmlMessage html = new HtmlMessage(5);
            html.setFile("olympiad/monument_hero_info.htm");
            html.replace("%heroname%", Olympiad.getNobleName(charid));
            // FIXME хер знает че тут должно быть если сообщение пустое
            html.replace("%message%", _heroes.get(charid).getString(MESSAGE, StringUtils.EMPTY));

            final List<HeroDiary> list = new ArrayList<>(mainlist);

            Collections.reverse(list);

            boolean color = true;
            final StringBuilder fList = new StringBuilder(500);
            int counter = 0;
            int breakat = 0;
            for (int i = (page - 1) * perpage; i < list.size(); i++) {
                breakat = i;
                final HeroDiary diary = list.get(i);
                final Entry<String, String> entry = diary.toString(activeChar);

                fList.append("<tr><td>");
                if (color) {
                    fList.append("<table width=270 bgcolor=\"131210\">");
                } else {
                    fList.append("<table width=270>");
                }
                fList.append("<tr><td width=270><font color=\"LEVEL\">").append(entry.getKey()).append("</font></td></tr>");
                fList.append("<tr><td width=270>").append(entry.getValue()).append("</td></tr>");
                fList.append("<tr><td>&nbsp;</td></tr></table>");
                fList.append("</td></tr>");
                color = !color;
                counter++;
                if (counter >= perpage) {
                    break;
                }
            }

            if (breakat < list.size() - 1) {
                html.replace("%buttprev%", HtmlUtils.PREV_BUTTON);
                html.replace("%prev_bypass%", "_diary?class=" + heroclass + "&page=" + (page + 1));
            } else {
                html.replace("%buttprev%", StringUtils.EMPTY);
            }

            if (page > 1) {
                html.replace("%buttnext%", HtmlUtils.NEXT_BUTTON);
                html.replace("%next_bypass%", "_diary?class=" + heroclass + "&page=" + (page - 1));
            } else {
                html.replace("%buttnext%", StringUtils.EMPTY);
            }

            html.replace("%list%", fList.toString());

            activeChar.sendPacket(html);
        }
    }

    public void addHeroDiary(final int playerId, final int id, final int param) {
        insertHeroDiary(playerId, id, param);

        final List<HeroDiary> list = _herodiary.get(playerId);
        if (list != null) {
            list.add(new HeroDiary(id, Instant.now(), param));
        }
    }

    private void insertHeroDiary(final int charId, final int action, final int param) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO heroes_diary (charId, time, action, param) values(?,?,?,?)");
            statement.setInt(1, charId);
            statement.setLong(2, System.currentTimeMillis());
            statement.setInt(3, action);
            statement.setInt(4, param);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.error("SQL exception while saving DiaryData.", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void loadMessage(final int charId) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            String message = null;
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT message FROM heroes WHERE char_id=?");
            statement.setInt(1, charId);
            rset = statement.executeQuery();
            rset.next();
            message = rset.getString("message");
            setHeroMessage(charId, message);
        } catch (SQLException e) {
            _log.error("Hero System: Couldnt load Hero Message for CharId: " + charId, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void setHeroMessage(final int charId, final String message) {
        final StatsSet hero = _heroes.get(charId);
        if (hero != null && message != null && !message.equals(StringUtils.EMPTY))
            hero.set(MESSAGE, message);
    }

    public void saveHeroMessage(final int charId) {
        if (!_heroes.get(charId).containsKey(MESSAGE))
            return;

        Connection con = null;
        PreparedStatement statement = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE heroes SET message=? WHERE char_id=?;");
            statement.setString(1, _heroes.get(charId).getString(MESSAGE));
            statement.setInt(2, charId);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.error("SQL exception while saving HeroMessage.", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void shutdown() {
        for (final int charId : _heroes.keySet())
            saveHeroMessage(charId);
    }

    public int getHeroByClass(final int classid) {
        if (!_heroes.isEmpty()) {
            for (final Integer heroId : _heroes.keySet()) {
                final StatsSet hero = _heroes.get(heroId);
                if (hero.getInteger(Olympiad.CLASS_ID) == classid) {
                    return heroId;
                }
            }
        }
        return 0;
    }

    public Entry<Integer, StatsSet> getHeroStats(final int classId) {
        if (!_heroes.isEmpty()) {
            for (final Entry<Integer, StatsSet> entry : _heroes.entrySet()) {
                if (entry.getValue().getInteger(Olympiad.CLASS_ID) == classId) {
                    return entry;
                }
            }
        }
        return null;
    }

    private static class LazyHolder {
        private static final Hero INSTANCE = new Hero();
    }
}