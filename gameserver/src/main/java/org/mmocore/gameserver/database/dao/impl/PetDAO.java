package org.mmocore.gameserver.database.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.instances.PetBabyInstance;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 15:16/19.09.2011
 */
public class PetDAO {
    private static final Logger _log = LoggerFactory.getLogger(PetDAO.class);

    private static final PetDAO _instance = new PetDAO();

    private PetDAO() {
    }

    public static PetDAO getInstance() {
        return _instance;
    }

    public PetInstance select(final Player owner, final ItemInstance item, final NpcTemplate template) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM pets WHERE object_id=?");
            statement.setInt(1, item.getObjectId());
            rset = statement.executeQuery();

            PetInstance pet = null;
            if (!rset.next()) {
                if (PetUtils.isBabyPet(template.getNpcId()) || PetUtils.isImprovedBabyPet(template.getNpcId())) {
                    pet = new PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, item);
                } else {
                    pet = new PetInstance(IdFactory.getInstance().getNextId(), template, owner, item);
                }
            } else {
                if (PetUtils.isBabyPet(template.getNpcId()) || PetUtils.isImprovedBabyPet(template.getNpcId())) {
                    pet = new PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, item, rset.getLong("exp"));
                } else {
                    pet = new PetInstance(IdFactory.getInstance().getNextId(), template, owner, item, rset.getLong("exp"));
                }

                pet.setExistsInDatabase(true);
                String name = rset.getString("name");
                if (StringUtils.isEmpty(name) || NpcNameLineHolder.getInstance().isBlackListContainsName(name)) {
                    name = template.name;
                }
                pet.setName(name);
                pet.setCurrentHpMp(rset.getDouble("current_hp"), rset.getInt("current_mp"), true);
                pet.setCurrentCp(pet.getMaxCp());
                pet.setSp(rset.getInt("sp"));
                pet.setCurrentFed(rset.getInt("current_life"));
            }
            return pet;
        } catch (Exception e) {
            _log.warn("PetDAO:select(Player,ItemInstance,NpcTemplate): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return null;
    }

    public void insert(final PetInstance pet) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("INSERT INTO pets (name,current_hp,current_mp,exp,sp,current_life,object_id) VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, pet.isDefaultName() ? StringUtils.EMPTY : pet.getName());
            statement.setDouble(2, pet.getCurrentHp());
            statement.setDouble(3, pet.getCurrentMp());
            statement.setLong(4, pet.getExp());
            statement.setLong(5, pet.getSp());
            statement.setInt(6, pet.getCurrentFed());
            statement.setInt(7, pet.getControlItemObjId());
            statement.executeUpdate();
        } catch (Exception e) {
            _log.error("PetDAO.insert(PetInstance): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update(final PetInstance pet) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("UPDATE pets SET name=?,current_hp=?,current_mp=?,exp=?,sp=?,current_life=? WHERE object_id = ?");
            statement.setString(1, pet.isDefaultName() ? StringUtils.EMPTY : pet.getName());
            statement.setDouble(2, pet.getCurrentHp());
            statement.setDouble(3, pet.getCurrentMp());
            statement.setLong(4, pet.getExp());
            statement.setLong(5, pet.getSp());
            statement.setInt(6, pet.getCurrentFed());
            statement.setInt(7, pet.getControlItemObjId());
            statement.executeUpdate();
        } catch (Exception e) {
            _log.error("PetDAO.update(pet): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void updateMount(final int controlItemObjId, final int currentFed) {
        if (currentFed < 0 || controlItemObjId <= 0) {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("UPDATE pets SET current_life=? WHERE object_id = ?");
            statement.setInt(1, currentFed);
            statement.setInt(2, controlItemObjId);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.error("PetDAO.updateMount(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final int objectId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM pets WHERE object_id = ?");
            statement.setInt(1, objectId);
            statement.execute();
        } catch (final Exception e) {
            _log.error("PetDAO.delete(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}