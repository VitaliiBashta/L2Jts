package org.mmocore.gameserver.model.pledge;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.lineage.serverpackets.ExSubPledgeSkillAdd;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author VISTALL
 * @author KilRoy
 */
public class SubUnit {
    private static final Logger _log = LoggerFactory.getLogger(SubUnit.class);

    private final Map<Integer, SkillEntry> skills = new ConcurrentSkipListMap<>();
    private final Map<Integer, UnitMember> members = new ConcurrentHashMap<>();

    private final int _type;
    private final Clan _clan;
    private int _leaderObjectId;
    private int newLeaderObjectId;
    private UnitMember _leader;
    private String _name;

    public SubUnit(final Clan c, final int type, final UnitMember leader, final String name) {
        _clan = c;
        _type = type;
        _name = name;

        setLeader(leader, false);
    }

    public SubUnit(final Clan c, final int type, final int leader, final String name, final int newLeaderObjectId) {
        _clan = c;
        _type = type;
        _leaderObjectId = leader;
        this.newLeaderObjectId = newLeaderObjectId;
        _name = name;
    }

    private static void removeMemberInDatabase(final UnitMember member) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET clanid=0, pledge_type=?, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?");
            statement.setInt(1, Clan.SUBUNIT_NONE);
            statement.setLong(2, System.currentTimeMillis() / 1000);
            statement.setInt(3, member.getObjectId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public int getType() {
        return _type;
    }

    public String getName() {
        return _name;
    }

    public UnitMember getLeader() {
        return _leader;
    }

    public boolean isUnitMember(final int obj) {
        return members.containsKey(obj);
    }

    public void addUnitMember(final UnitMember member) {
        members.put(member.getObjectId(), member);
    }

    public UnitMember getUnitMember(final int obj) {
        if (obj == 0) {
            return null;
        }
        return members.get(obj);
    }

    public UnitMember getUnitMember(final String obj) {
        for (final UnitMember m : getUnitMembers()) {
            if (m.getName().equalsIgnoreCase(obj)) {
                return m;
            }
        }

        return null;
    }

    public void removeUnitMember(final int objectId) {
        final UnitMember m = members.remove(objectId);
        if (m == null) {
            return;
        }

        if (objectId == getLeaderObjectId()) // subpledge leader
        {
            setLeader(null, true); // clan leader has to assign another one, via villagemaster
        }

        if (m.hasSponsor()) {
            _clan.getAnyMember(m.getSponsor()).setApprentice(0);
        }

        removeMemberInDatabase(m);

        m.setPlayerInstance(null, true);
    }

    public void replace(final int objectId, final int newUnitId) {
        final SubUnit newUnit = _clan.getSubUnit(newUnitId);
        if (newUnit == null) {
            return;
        }

        final UnitMember m = members.remove(objectId);
        if (m == null) {
            return;
        }

        m.setPledgeType(newUnitId);
        newUnit.addUnitMember(m);

        if (m.getPowerGrade() > 5) {
            m.setPowerGrade(_clan.getAffiliationRank(m.getPledgeType()));
        }
    }

    public int getLeaderObjectId() {
        return _leader == null ? 0 : _leader.getObjectId();
    }

    public int size() {
        return members.size();
    }

    public Collection<UnitMember> getUnitMembers() {
        return members.values();
    }

    public void setLeader(final UnitMember newLeader, final boolean updateDB) {
        final UnitMember old = _leader;
        if (old != null)   // обновляем старого мембера
            old.setLeaderOf(Clan.SUBUNIT_NONE);

        _leader = newLeader;
        _leaderObjectId = newLeader == null ? 0 : newLeader.getObjectId();

        if (newLeader != null)
            newLeader.setLeaderOf(_type);

        if (updateDB) {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?");
                statement.setInt(1, getLeaderObjectId());
                statement.setInt(2, _clan.getClanId());
                statement.setInt(3, _type);
                statement.execute();
            } catch (Exception e) {
                _log.error("Exception: " + e, e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }

    public void changeLeader() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_subpledges SET new_leader_id=? WHERE clan_id=? and type=?");
            statement.setInt(1, getNewLeaderObjectId());
            statement.setInt(2, _clan.getClanId());
            statement.setInt(3, _type);
            statement.execute();
        } catch (Exception e) {
            _log.error("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void cancelChangeLeader() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=?, new_leader_id=? WHERE clan_id=? and type=?");
            statement.setInt(1, getLeaderObjectId());
            statement.setInt(2, getNewLeaderObjectId());
            statement.setInt(3, _clan.getClanId());
            statement.setInt(4, getType());
            statement.execute();
        } catch (Exception e) {
            _log.error("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public int getNewLeaderObjectId() {
        return newLeaderObjectId;
    }

    public void setNewLeaderObjectId(int newLeaderObjectId) {
        this.newLeaderObjectId = newLeaderObjectId;
    }

    public void setName(final String name, final boolean updateDB) {
        _name = name;
        if (updateDB) {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? and type=?");
                statement.setString(1, _name);
                statement.setInt(2, _clan.getClanId());
                statement.setInt(3, _type);
                statement.execute();
            } catch (Exception e) {
                _log.error("Exception: " + e, e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }

    public String getLeaderName() {
        return _leader == null ? StringUtils.EMPTY : _leader.getName();
    }

    public SkillEntry addSkill(final SkillEntry newSkill, final boolean store) {
        SkillEntry oldSkill = null;
        if (newSkill != null) {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = skills.put(newSkill.getId(), newSkill);

            if (store) {
                Connection con = null;
                PreparedStatement statement = null;

                try {
                    con = DatabaseFactory.getInstance().getConnection();

                    if (oldSkill != null) {
                        statement = con.prepareStatement("UPDATE clan_subpledges_skills SET skill_level=? WHERE skill_id=? AND clan_id=? AND type=?");
                        statement.setInt(1, newSkill.getLevel());
                        statement.setInt(2, oldSkill.getId());
                        statement.setInt(3, _clan.getClanId());
                        statement.setInt(4, _type);
                        statement.execute();
                    } else {
                        statement = con.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id,type,skill_id,skill_level) VALUES (?,?,?,?)");
                        statement.setInt(1, _clan.getClanId());
                        statement.setInt(2, _type);
                        statement.setInt(3, newSkill.getId());
                        statement.setInt(4, newSkill.getLevel());
                        statement.execute();
                    }
                } catch (Exception e) {
                    _log.warn("Exception: " + e, e);
                } finally {
                    DbUtils.closeQuietly(con, statement);
                }
            }

            final ExSubPledgeSkillAdd packet = new ExSubPledgeSkillAdd(_type, newSkill.getId(), newSkill.getLevel());
            for (final UnitMember temp : _clan) {
                if (temp.isOnline()) {
                    final Player player = temp.getPlayer();
                    if (player != null) {
                        player.sendPacket(packet);
                        if (player.getPledgeType() == _type) {
                            _clan.addSkill(player, newSkill);
                        }
                    }
                }
            }
        }

        return oldSkill;
    }

    public Collection<SkillEntry> getSkills() {
        return skills.values();
    }

    public void restore() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(//
                    "SELECT `c`.`char_name` AS `char_name`," + //
                            "`s`.`level` AS `level`," + //
                            "`s`.`class_id` AS `classid`," + //
                            "`c`.`obj_Id` AS `obj_id`," + //
                            "`c`.`title` AS `title`," + //
                            "`c`.`pledge_rank` AS `pledge_rank`," + //
                            "`c`.`apprentice` AS `apprentice`, " + //
                            "`c`.`sex` AS `sex`, " + //
                            "`c`.`race` AS `race` " + //
                            "FROM `characters` `c` " + //
                            "LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') " + //
                            "WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC");

            statement.setInt(1, _clan.getClanId());
            statement.setInt(2, _type);
            rset = statement.executeQuery();

            while (rset.next()) {
                final UnitMember member = new UnitMember(_clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"),
                        rset.getInt("classid"), rset.getInt("obj_Id"), _type, rset.getInt("pledge_rank"),
                        rset.getInt("apprentice"), rset.getInt("sex"), rset.getInt("race"), Clan.SUBUNIT_NONE);

                addUnitMember(member);
            }

            if (_type != Clan.SUBUNIT_ACADEMY) {
                final SubUnit mainClan = _clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
                final UnitMember leader = mainClan.getUnitMember(_leaderObjectId);
                if (leader != null) {
                    setLeader(leader, false);
                } else if (_type == Clan.SUBUNIT_MAIN_CLAN) {
                    _log.error("Clan " + _name + " have no leader!");
                }
            }
        } catch (Exception e) {
            _log.warn("Error while restoring clan members for clan: " + _clan.getClanId() + ' ' + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void restoreSkills() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_subpledges_skills WHERE clan_id=? AND type=?");
            statement.setInt(1, _clan.getClanId());
            statement.setInt(2, _type);
            rset = statement.executeQuery();

            while (rset.next()) {
                final int id = rset.getInt("skill_id");
                final int level = rset.getInt("skill_level");

                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);

                skills.put(skill.getId(), skill);
            }
        } catch (Exception e) {
            _log.warn("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public int getSkillLevel(final int id, final int def) {
        final SkillEntry skill = skills.get(id);
        return skill == null ? def : skill.getLevel();
    }

    public int getSkillLevel(final int id) {
        return getSkillLevel(id, -1);
    }
}
