package org.mmocore.gameserver.database.dao.impl;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 19:36/20.09.2011
 */
public class SummonDAO {
    private static final Logger _log = LoggerFactory.getLogger(SummonDAO.class);

    private static final SummonDAO _instance = new SummonDAO();

    private SummonDAO() {
    }

    public static SummonDAO getInstance() {
        return _instance;
    }

    public SummonInstance select(final Player owner, final int skillId) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM summons WHERE object_id=? AND call_skill_id=?");
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, skillId);
            rset = statement.executeQuery();

            if (rset.next()) {
                final NpcTemplate template = NpcHolder.getInstance().getTemplate(rset.getInt("npc_id"));
                if (template == null) {
                    return null;
                }

                final SummonInstance summon = new SummonInstance(IdFactory.getInstance().getNextId(), template, owner, rset.getInt("current_life"), rset.getInt("max_life"), rset.getInt("item_consume_id"), rset.getInt("item_consume_count"), rset.getInt("item_consume_delay"), skillId);
                summon.setExpPenalty(rset.getDouble("exp_penalty"));
                summon.setExp(ExpDataHolder.getInstance().getExpForLevel(Math.min(summon.getLevel(), ExpDataHolder.getInstance().getExpTableData().length - 1)));
                summon.setCurrentHpMp(rset.getDouble("current_hp"), rset.getInt("current_mp"), false);

                delete(owner.getObjectId(), skillId);

                return summon;
            }
        } catch (Exception e) {
            _log.warn("SummonDAO:select(Player,int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return null;
    }

    public void insert(final Player player, final SummonInstance summon) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO summons (current_hp,current_mp,current_life,max_life,item_consume_id, item_consume_count, item_consume_delay, npc_id, exp_penalty, object_id, call_skill_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            statement.setDouble(1, summon.getCurrentHp());
            statement.setDouble(2, summon.getCurrentMp());
            statement.setInt(3, summon.getCurrentFed());
            statement.setInt(4, summon.getMaxFed());
            statement.setInt(5, summon.getItemConsumeIdInTime());
            statement.setInt(6, summon.getItemConsumeCountInTime());
            statement.setInt(7, summon.getItemConsumeDelay());
            statement.setInt(8, summon.getNpcId());
            statement.setDouble(9, summon.getExpPenalty());
            statement.setInt(10, player.getObjectId());
            statement.setInt(11, summon.getCallSkillId());
            statement.executeUpdate();
        } catch (Exception e) {
            _log.error("SummonDAO.insert(SummonInstance): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final int objectId, final int skillId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM summons WHERE object_id = ? AND call_skill_id=?");
            statement.setInt(1, objectId);
            statement.setInt(2, skillId);
            statement.execute();
        } catch (final Exception e) {
            _log.error("SummonDAO.delete(int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}