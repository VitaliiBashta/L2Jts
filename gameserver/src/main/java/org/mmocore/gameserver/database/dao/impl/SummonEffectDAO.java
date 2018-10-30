package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author VISTALL
 * @date 19:35/20.09.2011
 */
public class SummonEffectDAO {
    private static final Logger _log = LoggerFactory.getLogger(SummonEffectDAO.class);

    private static final SummonEffectDAO _instance = new SummonEffectDAO();

    private SummonEffectDAO() {
    }

    public static SummonEffectDAO getInstance() {
        return _instance;
    }

    public void select(final SummonInstance summon) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `summon_effects` WHERE `object_id`=? AND call_skill_id=? ORDER BY `order` ASC");
            statement.setInt(1, summon.getPlayer().getObjectId());
            statement.setInt(2, summon.getCallSkillId());
            rset = statement.executeQuery();
            int size = rset.getFetchSize();
            while (rset.next()) {
                final int skillId = rset.getInt("skill_id");
                final int skillLvl = rset.getInt("skill_level");
                final int effectCount = rset.getInt("effect_count");
                final long effectCurTime = rset.getLong("effect_cur_time");
                final long duration = rset.getLong("duration");

                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skillId, skillLvl);
                if (skill == null) {
                    continue;
                }

                for (final EffectTemplate et : skill.getTemplate().getEffectTemplates()) {
                    if (et == null) {
                        continue;
                    }

                    final Effect effect = et.getEffect(summon, summon, skill);
                    if (effect == null || effect.isOneTime()) {
                        continue;
                    }

                    effect.setCount(effectCount);
                    effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);

                    summon.getEffectList().addEffect(effect);
                    effect.fixStartTime(size--);
                }
            }

            delete(summon.getPlayer().getObjectId(), summon.getCallSkillId());
        } catch (final Exception e) {
            _log.error("SummonEffectDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void delete(final int objectId, final int callSkillId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM summon_effects WHERE object_id = ? AND call_skill_id=?");
            statement.setInt(1, objectId);
            statement.setInt(2, callSkillId);
            statement.execute();
        } catch (final Exception e) {
            _log.error("SummonEffectDAO.delete(int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void insert(final SummonInstance summon) {
        final List<Effect> effects = summon.getEffectList().getAllEffects();
        if (effects.isEmpty()) {
            return;
        }

        Connection con = null;
        Statement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();

            int order = 0;
            final SqlBatch b = new SqlBatch("INSERT IGNORE INTO `summon_effects` (`object_id`,`call_skill_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`) VALUES");

            StringBuilder sb;
            for (Effect effect : effects) {
                if (effect.isInUse() && !effect.getSkill().getTemplate().isToggle() && effect.getEffectType() != EffectType.HealOverTime && effect.getEffectType() != EffectType.CombatPointHealOverTime) {
                    if (effect.isSaveable()) {
                        sb = new StringBuilder("(");
                        sb.append(summon.getPlayer().getObjectId()).append(',');
                        sb.append(summon.getCallSkillId()).append(',');
                        sb.append(effect.getSkill().getId()).append(',');
                        sb.append(effect.getSkill().getLevel()).append(',');
                        sb.append(effect.getCount()).append(',');
                        sb.append(effect.getTime()).append(',');
                        sb.append(effect.getPeriod()).append(',');
                        sb.append(order).append(')');
                        b.write(sb.toString());
                    }
                    while ((effect = effect.getNext()) != null && effect.isSaveable()) {
                        sb = new StringBuilder("(");
                        sb.append(summon.getPlayer().getObjectId()).append(',');
                        sb.append(summon.getCallSkillId()).append(',');
                        sb.append(effect.getSkill().getId()).append(',');
                        sb.append(effect.getSkill().getLevel()).append(',');
                        sb.append(effect.getCount()).append(',');
                        sb.append(effect.getTime()).append(',');
                        sb.append(effect.getPeriod()).append(',');
                        sb.append(order).append(')');
                        b.write(sb.toString());
                    }
                    order++;
                }
            }

            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            _log.error("Could not store active effects data!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}