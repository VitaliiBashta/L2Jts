package org.mmocore.gameserver.database.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.jdbchelper.NoResultException;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CharacterDAO extends AbstractGameServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterDAO.class);

    private static final CharacterDAO INSTANCE = new CharacterDAO();

    private static final String UPDATE_ONLINE_STATUS = "UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?";
    private static final String UPDATE_ACCESS_LEVEL = "UPDATE characters SET accesslevel=? WHERE obj_Id=?";

    public static CharacterDAO getInstance() {
        return INSTANCE;
    }

    public void deleteCharByObjId(final int objid) {
        if (objid < 0) {
            return;
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
            statement.setInt(1, objid);
            statement.execute();
        } catch (final Exception e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public boolean insert(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO `characters` (account_name, obj_Id, char_name, face, race, hairStyle, hairColor, sex, karma, pvpkills, pkkills, clanid, createtime, deletetime, title, accesslevel, online, leaveclan, deleteclan, nochannel, pledge_type, pledge_rank, lvl_joined_academy, apprentice) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, player.getAccountName());
            statement.setInt(2, player.getObjectId());
            statement.setString(3, player.getName());
            statement.setInt(4, player.getAppearanceComponent().getFace());
            statement.setInt(5, player.getPlayerTemplateComponent().getPlayerRace().ordinal());
            statement.setInt(6, player.getAppearanceComponent().getHairStyle());
            statement.setInt(7, player.getAppearanceComponent().getHairColor());
            statement.setInt(8, player.getSex());
            statement.setInt(9, player.getKarma());
            statement.setInt(10, player.getPvpKills());
            statement.setInt(11, player.getPkKills());
            statement.setInt(12, player.getClanId());
            statement.setLong(13, player.getCreateTime() / 1000);
            statement.setInt(14, player.getDeleteTimer());
            statement.setString(15, player.getTitle());
            statement.setInt(16, player.getAccessLevel());
            statement.setInt(17, player.isOnline() ? 1 : 0);
            statement.setLong(18, player.getLeaveClanTime() / 1000);
            statement.setLong(19, player.getDeleteClanTime() / 1000);
            statement.setLong(20, player.getNoChannel() > 0 ? player.getNoChannel() / 1000 : player.getNoChannel());
            statement.setInt(21, player.getPledgeType());
            statement.setInt(22, player.getPowerGrade());
            statement.setInt(23, player.getLvlJoinedAcademy());
            statement.setInt(24, player.getApprentice());
            statement.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("", e);
            return false;
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return true;
    }

    public boolean insertSub(final int objId, final int classId, final double curHp, final double curMp, final double curCp, final double maxHp, final double maxMp, final double maxCp) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, objId);
            statement.setInt(2, classId);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setDouble(5, curHp);
            statement.setDouble(6, curMp);
            statement.setDouble(7, curCp);
            statement.setDouble(8, maxHp);
            statement.setDouble(9, maxMp);
            statement.setDouble(10, maxCp);
            statement.setInt(11, 1);
            statement.setInt(12, 1);
            statement.setInt(13, 1);
            statement.setInt(14, 0);
            statement.setInt(15, 0);
            statement.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("", e);
            return false;
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return true;
    }

    public int getObjectIdByName(final String name) {
        int result = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
            statement.setString(1, name);
            rset = statement.executeQuery();
            if (rset.next()) {
                result = rset.getInt(1);
            }
        } catch (final Exception e) {
            LOGGER.error("CharNameTable.getObjectIdByName(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return result;
    }

    public int getPlayersCountByName(final String name) {
        int result = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
            statement.setString(1, name);
            rset = statement.executeQuery();
            if (rset.next()) {
                result++;
            }
        } catch (final Exception e) {
            LOGGER.error("CharNameTable.getPlayersCountByName(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return result;
    }

    public String getNameByObjectId(final int objectId) {
        String result = StringUtils.EMPTY;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id=?");
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
            if (rset.next()) {
                result = rset.getString(1);
            }
        } catch (final Exception e) {
            LOGGER.error("CharNameTable.getObjectIdByName(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return result;
    }

    /**
     * @param objectId - obj id of player
     * @return clanId of player, or 0 if is not player (or info not found)
     */
    public int getPlayerClanId(final int objectId) {
        int clanId;
        try {
            clanId = jdbcHelper.queryForInt("SELECT clanid FROM characters WHERE obj_Id=?", objectId);
        } catch (NoResultException e) {
            clanId = 0;
        }
        return clanId;
    }

    public int accountCharNumber(final String account) {
        int number = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT COUNT(char_name) FROM characters WHERE account_name=?");
            statement.setString(1, account);
            rset = statement.executeQuery();
            if (rset.next()) {
                number = rset.getInt(1);
            }
        } catch (final Exception e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return number;
    }

    public void updateName(final int objectId, final String name) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
            st.setString(1, name);
            st.setInt(2, objectId);
            st.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, st);
        }
    }

    public void updateCreateTime(final int objectId, final String date) {
        jdbcHelper.execute("update characters set createtime = UNIX_TIMESTAMP('?') where obj_Id = ?", date, objectId);
    }

    public Collection<StatsSet> restoreCharSubClasses(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT class_id,exp,sp,curHp,curCp,curMp,active,isBase,death_penalty,certification FROM character_subclasses WHERE char_obj_id=?");
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();

            final Collection<StatsSet> result = new ArrayList<>();

            while (rset.next()) {
                final StatsSet subClass = new StatsSet();
                subClass.set("isBase", rset.getInt("isBase") != 0);
                subClass.set("class_id", rset.getInt("class_id"));
                subClass.set("exp", rset.getLong("exp"));
                subClass.set("sp", rset.getInt("sp"));
                subClass.set("curHp", rset.getDouble("curHp"));
                subClass.set("curMp", rset.getDouble("curMp"));
                subClass.set("curCp", rset.getDouble("curCp"));
                subClass.set("death_penalty", rset.getInt("death_penalty"));
                subClass.set("certification", rset.getInt("certification"));
                subClass.set("active", rset.getInt("active") != 0);

                result.add(subClass);
            }

            return result;
        } catch (final Exception e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return Collections.emptyList();
    }

    public void storeCharSubClasses(final Player player) {
        try (final Connection con = DatabaseFactory.getInstance().getConnection();
             final PreparedStatement statement = con.prepareStatement(
                     "UPDATE character_subclasses SET exp=?, sp=?, curHp=?, curMp=?, curCp=?, level=?, active=?, isBase=?, death_penalty=?, certification=?" +
                             " WHERE char_obj_id=? AND class_id=? LIMIT 1"
             );
             final PreparedStatement statement2 = con.prepareStatement(
                     "UPDATE character_subclasses SET maxHp=?, maxMp=?, maxCp=? WHERE char_obj_id=? AND active=1 LIMIT 1")) {
            final Collection<SubClass> subClasses = player.getPlayerClassComponent().getSubClasses().values();
            for (final SubClass subClass : subClasses) {
                statement.setLong(1, subClass.getExp());
                statement.setLong(2, subClass.getSp());
                statement.setDouble(3, subClass.getHp());
                statement.setDouble(4, subClass.getMp());
                statement.setDouble(5, subClass.getCp());
                statement.setInt(6, subClass.getLevel());
                statement.setInt(7, subClass.isActive() ? 1 : 0);
                statement.setInt(8, subClass.isBase() ? 1 : 0);
                statement.setInt(9, subClass.getDeathPenalty(player).getLevelOnSaveDB());
                statement.setInt(10, subClass.getCertification());
                statement.setLong(11, player.getObjectId());
                statement.setInt(12, subClass.getClassId());

                statement.addBatch();
            }

            statement2.setDouble(1, player.getMaxHp());
            statement2.setDouble(2, player.getMaxMp());
            statement2.setDouble(3, player.getMaxCp());
            statement2.setLong(4, player.getObjectId());

            statement.executeBatch();

            statement2.executeUpdate();
        } catch (final Exception e) {
            LOGGER.warn("Could not store char sub data.", e);
        }
    }

    public boolean addSubClass(final Player player, final int certification, final SubClass newClass) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            // Store the basic info about this new sub-class.
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newClass.getClassId());
            statement.setLong(3, ExpDataHolder.getInstance().getExpForLevel(40));
            statement.setInt(4, 0);
            statement.setDouble(5, player.getCurrentHp());
            statement.setDouble(6, player.getCurrentMp());
            statement.setDouble(7, player.getCurrentCp());
            statement.setDouble(8, player.getCurrentHp());
            statement.setDouble(9, player.getCurrentMp());
            statement.setDouble(10, player.getCurrentCp());
            statement.setInt(11, 40);
            statement.setInt(12, 0);
            statement.setInt(13, 0);
            statement.setInt(14, 0);
            statement.setInt(15, certification);
            statement.execute();
        } catch (final Exception e) {
            LOGGER.error("", e);
            return false;
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return true;
    }

    public boolean modifySubClass(final Player player, final int oldClassId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            // Remove all basic info stored about this sub-class.
            statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND isBase = 0");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all skill info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all saved skills info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all saved effects stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_effects WHERE object_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all dyes info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_dyes WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all shortcuts info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);
        } catch (final Exception e) {
            LOGGER.error("", e);
            return false;
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return true;
    }

    public void changeClassInDb(final Player player, final int oldclass, final int newclass) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_dyes WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_dyes SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_effects WHERE object_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_effects SET class_index=? WHERE object_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);
        } catch (final SQLException e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void updateAccessLevel(final int playerObjectId, final int newAccessLevel) {
        jdbcHelper.execute(UPDATE_ACCESS_LEVEL, stmt -> {
            stmt.setInt(1, playerObjectId);
            stmt.setInt(2, newAccessLevel);
        });
    }
}