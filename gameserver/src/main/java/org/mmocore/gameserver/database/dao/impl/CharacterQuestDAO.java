package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 18:10/27.08.2011
 */
public class CharacterQuestDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterQuestDAO.class);
    private static final CharacterQuestDAO _instance = new CharacterQuestDAO();

    public static CharacterQuestDAO getInstance() {
        return _instance;
    }

    public void select(final Player player) {
        if (ServerConfig.DONTLOADQUEST) {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement invalidQuestData = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and quest_id=?");
            statement = con.prepareStatement("SELECT quest_id,value FROM character_quests WHERE char_id=? AND var=?");
            statement.setInt(1, player.getObjectId());
            statement.setString(2, QuestState.VAR_STATE);
            rset = statement.executeQuery();
            while (rset.next()) {
                final int questId = rset.getInt("quest_id");
                final String state = rset.getString("value");

                if ("Start".equalsIgnoreCase(state)) // невзятый квест
                {
                    invalidQuestData.setInt(1, player.getObjectId());
                    invalidQuestData.setInt(2, questId);
                    invalidQuestData.executeUpdate();
                    continue;
                }

                final Quest q = QuestManager.getQuest(questId);
                if (q == null) {
                    _log.warn("Unknown quest " + questId + " for player " + player.getName());
                    continue;
                }

                new QuestState(q, player, Quest.getStateId(state));
            }

            DbUtils.close(statement, rset);

            // Get list of quests owned by the player from the DB in order to add variables used in the quest.
            statement = con.prepareStatement("SELECT quest_id,var,value FROM character_quests WHERE char_id=?");
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                final int questId = rset.getInt("quest_id");
                final String var = rset.getString("var");
                String value = rset.getString("value");

                final QuestState qs = player.getQuestState(questId);
                if (qs == null) {
                    continue;
                }

                // затычка на пропущенный первый конд
                if ("cond".equals(var) && Integer.parseInt(value) < 0) {
                    value = String.valueOf(Integer.parseInt(value) | 1);
                }

                qs.setMemoState(var, value, false);
            }
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(invalidQuestData);
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void replace(final QuestState qs) {
        replace(qs, QuestState.VAR_STATE, qs.getStateName());
    }

    public void replace(final QuestState qs, final String var, final String value) {
        final Player player = qs.getPlayer();
        if (player == null) {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_quests (char_id,quest_id,var,value) VALUES (?,?,?,?)");
            statement.setInt(1, qs.getPlayer().getObjectId());
            statement.setInt(2, qs.getQuest().getId());
            statement.setString(3, var);
            statement.setString(4, value);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.replace(QuestState, String, String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final int objectId, final int questId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND quest_id=?");
            statement.setInt(1, objectId);
            statement.setInt(2, questId);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.delete(int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final int objectId, final int questId, final String var) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND quest_id=? AND var=?");
            statement.setInt(1, objectId);
            statement.setInt(2, questId);
            statement.setString(3, var);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.delete(int, int, String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    // Quest: 501
    public void Q501_removeQuestFromOfflineMembers(final QuestState st) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return;
        }

        final int clan = st.getPlayer().getClan().getClanId();

        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("DELETE FROM character_quests WHERE quest_id = ? AND char_id IN (SELECT obj_id FROM characters WHERE clanId = ? AND online = 0)");
            offline.setInt(1, st.getQuest().getId());
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    // Quest: 503
    public int Q503_getLeaderVar(final QuestState st, final String var, final Player leader) {
        final boolean cond = QuestState.VAR_COND.equalsIgnoreCase(var);
        try {
            if (leader != null) {
                final QuestState qs2 = leader.getQuestState(st.getQuest().getId());
                if (qs2 == null) {
                    return -1;
                }

                return cond ? qs2.getCond() : qs2.getInt(var);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        final Clan clan = st.getPlayer().getClan();

        if (clan == null) {
            return -1;
        }

        final int leaderId = clan.getLeaderId();

        Connection con = null;
        PreparedStatement offline = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND quest_id=?");
            offline.setInt(1, leaderId);
            offline.setString(2, var);
            offline.setInt(3, st.getQuest().getId());
            int val = -1;
            rs = offline.executeQuery();
            if (rs.next()) {
                val = rs.getInt("value");
                if (cond && (val & 0x80000000) != 0) {
                    val &= 0x7fffffff;
                    for (int i = 1; i < 32; i++) {
                        val >>= 1;
                        if (val == 0) {
                            return i;
                        }
                    }
                }
            }
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            DbUtils.closeQuietly(con, offline, rs);
        }
    }

    // Quest : 503
    public void Q503_offlineMemberExit(final QuestState st) {
        final int clan = st.getPlayer().getClan().getClanId();

        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("DELETE FROM character_quests WHERE quest_id=? AND char_id IN (SELECT obj_id FROM characters WHERE clanId=? AND online=0)");
            offline.setInt(1, st.getQuest().getId());
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    // Quest : 503
    public void Q503_setLeaderVar(final int questId, final int leaderId, final String var, final String value) {
        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND quest_id=?");
            offline.setString(1, value);
            offline.setInt(2, leaderId);
            offline.setString(3, var);
            offline.setInt(4, questId);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    // Quest : 503
    public void Q503_suscribe_members(final QuestState st) {
        final int clan = st.getPlayer().getClan().getClanId();
        Connection con = null;
        PreparedStatement offline = null, insertion = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0");
            insertion = con.prepareStatement("REPLACE INTO character_quests (char_id,quest_id,var,value) VALUES (?,?,?,?)");
            offline.setInt(1, clan);
            rs = offline.executeQuery();
            while (rs.next()) {
                final int char_id = rs.getInt("obj_Id");
                try {
                    insertion.setInt(1, char_id);
                    insertion.setInt(2, st.getQuest().getId());
                    insertion.setString(3, QuestState.VAR_STATE);
                    insertion.setString(4, "Started");
                    insertion.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(insertion);
            DbUtils.closeQuietly(con, offline, rs);
        }
    }
}
