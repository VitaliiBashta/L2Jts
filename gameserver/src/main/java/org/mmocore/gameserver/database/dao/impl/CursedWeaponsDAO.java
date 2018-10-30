package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.items.etcitems.CursedWeapon;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author KilRoy
 */
public class CursedWeaponsDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CursedWeaponsDAO.class);

    private static final CursedWeaponsDAO INSTANCE = new CursedWeaponsDAO();

    public static CursedWeaponsDAO getInstance() {
        return INSTANCE;
    }

    public void restore() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM cursed_weapons");
            rset = statement.executeQuery();

            while (rset.next()) {
                final int itemId = rset.getInt("item_id");
                final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(itemId);
                if (cw != null) {
                    cw.setPlayerId(rset.getInt("player_id"));
                    cw.setPlayerKarma(rset.getInt("player_karma"));
                    cw.setPlayerPkKills(rset.getInt("player_pkkills"));
                    cw.setNbKills(rset.getInt("nb_kills"));
                    cw.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
                    cw.setEndTime(rset.getLong("end_time") * 1000L);

                    if (!cw.reActivate()) {
                        endOfLife(cw);
                    }
                } else {
                    removeFromDb(itemId);
                    LOGGER.warn("CursedWeaponDAO: Unknown cursed weapon " + itemId + ", deleted");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("CursedWeaponDAO: Could not restore cursed_weapons data: " + e);
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void checkConditions() {
        Connection con = null;
        PreparedStatement statement1 = null, statement2 = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement1 = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=?");
            statement2 = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?");
            for (final CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons()) {
                final int itemId = cw.getItemId();
                // Do an item check to be sure that the cursed weapon and/or skill isn't hold by someone
                final int skillId = cw.getSkillId();
                boolean foundedInItems = false;
                // Delete all cursed weapons skills (we don`t care about same skill on multiply weapons, when player back, skill will appears again)
                statement1.setInt(1, skillId);
                statement1.executeUpdate();
                statement2.setInt(1, itemId);
                rset = statement2.executeQuery();

                while (rset.next()) {
                    // A player has the cursed weapon in his inventory ...
                    final int playerId = rset.getInt("owner_id");

                    if (!foundedInItems) {
                        if (playerId != cw.getPlayerId() || cw.getPlayerId() == 0) {
                            emptyPlayerCursedWeapon(playerId, itemId, cw);
                            LOGGER.info("CursedWeaponDAO: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
                        } else {
                            foundedInItems = true;
                        }
                    } else {
                        emptyPlayerCursedWeapon(playerId, itemId, cw);
                        LOGGER.info("CursedWeaponDAO: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
                    }
                }
                if (!foundedInItems && cw.getPlayerId() != 0) {
                    removeFromDb(cw.getItemId());

                    LOGGER.info("CursedWeaponDAO: Unownered weapon, removing from table...");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("CursedWeaponDAO: Could not check cursed_weapons data: " + e);
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(statement1);
            DbUtils.closeQuietly(con, statement2, rset);
        }
    }

    private void emptyPlayerCursedWeapon(final int playerId, final int itemId, final CursedWeapon cw) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            // Delete the item
            statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
            statement.setInt(1, playerId);
            statement.setInt(2, itemId);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_id=?");
            statement.setInt(1, cw.getPlayerKarma());
            statement.setInt(2, cw.getPlayerPkKills());
            statement.setInt(3, playerId);
            if (statement.executeUpdate() != 1) {
                LOGGER.warn("CursedWeaponDAO: Error while updating karma & pkkills for userId " + cw.getPlayerId());
            }
            // clean up the cursedweapons table.
            removeFromDb(itemId);
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void removeFromDb(final int itemId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
            statement.setInt(1, itemId);
            statement.executeUpdate();

            if (CursedWeaponsManager.getInstance().getCursedWeapon(itemId) != null) {
                CursedWeaponsManager.getInstance().getCursedWeapon(itemId).initWeapon();
            }
        } catch (SQLException e) {
            LOGGER.error("CursedWeaponDAO: Failed to remove data: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void endOfLife(final CursedWeapon cw) {
        if (cw.isActivated()) {
            Player player = cw.getOnlineOwner();
            if (player == null) {
                LOGGER.info("CursedWeaponDAO: " + cw.getName() + " not find player. Avaliable ID: " + cw.getPlayerId());
                player = GameObjectsStorage.getPlayer(cw.getPlayerId());
            }
            if (player != null) {
                // Remove from player
                LOGGER.info("CursedWeaponDAO: " + cw.getName() + " being removed online from " + player + '.');

                player.abortAttack(true, true);

                player.setKarma(cw.getPlayerKarma());
                player.setPkKills(cw.getPlayerPkKills());
                player.setCursedWeaponEquippedId(0);
                player.stopTransformation();
                //player.removeSkill(SkillTable.getInstance().getSkillEntry(cw.getSkillId(), player.getSkillLevel(cw.getSkillId())), false);
                player.getInventory().destroyItemByItemId(cw.getItemId(), 1L);
                player.broadcastCharInfo();
            } else {
                // Remove from Db
                LOGGER.info("CursedWeaponDAO: " + cw.getName() + " being removed offline.");
                if (cw.getPlayerId() != 0) {
                    Connection con = null;
                    PreparedStatement statement = null;
                    try {
                        con = DatabaseFactory.getInstance().getConnection();

                        // Delete the item
                        statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
                        statement.setInt(1, cw.getPlayerId());
                        statement.setInt(2, cw.getItemId());
                        statement.executeUpdate();
                        DbUtils.close(statement);

                        // Delete the skill
                        statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?");
                        statement.setInt(1, cw.getPlayerId());
                        statement.setInt(2, cw.getSkillId());
                        statement.executeUpdate();
                        DbUtils.close(statement);

                        // Restore the karma
                        statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_Id=?");
                        statement.setInt(1, cw.getPlayerKarma());
                        statement.setInt(2, cw.getPlayerPkKills());
                        statement.setInt(3, cw.getPlayerId());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        LOGGER.warn("CursedWeaponDAO: Could not delete : " + e);
                    } finally {
                        DbUtils.closeQuietly(con, statement);
                    }
                } else {
                    LOGGER.warn("CursedWeaponDAO: Could not delete: " + cw.getName() + " playerID nulled.");
                }
            }
        } else // either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
            // OR this cursed weapon is on the ground.
            if (cw.getPlayer() != null && cw.getPlayer().getInventory().getItemByItemId(cw.getItemId()) != null) {
                final Player player = cw.getPlayer();
                if (!cw.getPlayer().getInventory().destroyItemByItemId(cw.getItemId(), 1)) {
                    LOGGER.info("CursedWeaponDAO: Error! Cursed weapon not found!!!");
                }

                player.sendChanges();
                player.broadcastUserInfo(true);
            }
            // is dropped on the ground
            else if (cw.getItem() != null) {
                cw.getItem().deleteMe();
                cw.getItem().delete();
                LOGGER.info("CursedWeaponDAO: " + cw.getName() + " item has been removed from World.");
            }

        cw.initWeapon();
        removeFromDb(cw.getItemId());

        CursedWeaponsManager.getInstance().announce(new SystemMessage(SystemMsg.S1_HAS_DISAPPEARED_).addItemName(cw.getItemId()));
    }

    public void saveData(final CursedWeapon cw) {
        Connection con = null;
        PreparedStatement statement = null;
        synchronized (cw)//FIXME [G1ta0] зачем синхронизация если она только на сохранении
        {
            try {
                con = DatabaseFactory.getInstance().getConnection();

                // Delete previous datas
                statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
                statement.setInt(1, cw.getItemId());
                statement.executeUpdate();

                if (cw.isActive()) {
                    DbUtils.close(statement);
                    statement = con.prepareStatement("REPLACE INTO cursed_weapons (item_id, player_id, player_karma, player_pkkills, nb_kills, x, y, z, end_time) VALUES (?,?,?,?,?,?,?,?,?)");
                    statement.setInt(1, cw.getItemId());
                    statement.setInt(2, cw.getPlayerId());
                    statement.setInt(3, cw.getPlayerKarma());
                    statement.setInt(4, cw.getPlayerPkKills());
                    statement.setInt(5, cw.getNbKills());
                    statement.setInt(6, cw.getLoc().x);
                    statement.setInt(7, cw.getLoc().y);
                    statement.setInt(8, cw.getLoc().z);
                    statement.setLong(9, cw.getEndTime() / 1000);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                LOGGER.error("CursedWeaponDAO: Failed to save data: " + e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }
}