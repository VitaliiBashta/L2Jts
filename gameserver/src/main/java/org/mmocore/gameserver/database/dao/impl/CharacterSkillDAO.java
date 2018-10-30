package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.SiegeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 17:49/13.09.2011
 */
public class CharacterSkillDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterSkillDAO.class);

    private static final CharacterSkillDAO _instance = new CharacterSkillDAO();

    private CharacterSkillDAO() {
    }

    public static CharacterSkillDAO getInstance() {
        return _instance;
    }

    public void select(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT skill_id,skill_level,entry_type FROM character_skills WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getPlayerClassComponent().getActiveClassId());
            rset = statement.executeQuery();

            while (rset.next()) {
                final int id = rset.getInt("skill_id");
                final int level = rset.getInt("skill_level");
                final SkillEntryType entryType = SkillEntryType.VALUES[rset.getInt("entry_type")];

                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);

                if (skill == null) {
                    continue;
                }

                // Remove skill if not possible
                if (!player.isGM() && !SkillAcquireHolder.getInstance().isSkillPossible(player, skill)) {
                    //int ReturnSP = SkillTreeTable.getInstance().getSkillCost(this, skill);
                    //if(ReturnSP == Integer.MAX_VALUE || ReturnSP < 0)
                    //		ReturnSP = 0;
                    //removeSkill(skill, true);
                    //removeSkillFromShortCut(skill.getId());
                    //if(ReturnSP > 0)
                    //		setSp(getSp() + ReturnSP);
                    Log.audit("[CharactedSkillDAO]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " removed not possible skill: " + skill.getId());
                    continue;
                }

                player.addSkill(entryType == SkillEntryType.NONE ? skill : skill.copyTo(entryType));
            }

            // Restore noble skills
            player.updateNobleSkills();


            // Restore Hero skills at main class only
            if (player.isHero() && player.getPlayerClassComponent().isBaseExactlyActiveId()) {
                Hero.addSkills(player);
            }

            // Даем скилы фейк хиро
            else if (player.getCustomPlayerComponent().isTemporalHero() && player.getPlayerClassComponent().isBaseExactlyActiveId()) {
                Hero.addSkills(player);
            }

            final Clan clan = player.getClan();
            if (clan != null) {
                clan.addSkillsQuietly(player);

                // Restore clan leader siege skills
                if (clan.getLeaderId() == player.getObjectId() && clan.getLevel() >= 4) {
                    SiegeUtils.addSiegeSkills(player);
                }
            }

            // Give dwarven craft skill
            if (player.getPlayerClassComponent().getActiveClassId() >= 53 && player.getPlayerClassComponent().getActiveClassId() <= 57 || player.getPlayerClassComponent().getActiveClassId() == 117 || player.getPlayerClassComponent().getActiveClassId() == 118) {
                player.addSkill(SkillTable.getInstance().getSkillEntry(1321, 1));
            }

            player.addSkill(SkillTable.getInstance().getSkillEntry(1322, 1));

            if (OtherConfig.UNSTUCK_SKILL && player.getSkillLevel(1050) < 0) {
                player.addSkill(SkillTable.getInstance().getSkillEntry(2099, 1));
            }
        } catch (final Exception e) {
            _log.warn("Could not restore skills for player objId: " + player.getObjectId());
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void replace(final Player player, final SkillEntry newSkill) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,entry_type,class_index) VALUES(?,?,?,?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newSkill.getId());
            statement.setInt(3, newSkill.getLevel());
            statement.setInt(4, newSkill.getEntryType().ordinal());
            statement.setInt(5, player.getPlayerClassComponent().getActiveClassId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("Error could not store skills!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Player player, final SkillEntry oldSkill) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?");
            statement.setInt(1, oldSkill.getId());
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, player.getPlayerClassComponent().getActiveClassId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("Could not delete skill!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}