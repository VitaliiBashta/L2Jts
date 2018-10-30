package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @author KilRoy
 */
public class ClanDataDAO extends AbstractGameServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanDataDAO.class);

    private static final ClanDataDAO INSTANCE = new ClanDataDAO();

    private static final String SELECT_CASTLE_OWNER = "SELECT clan_id FROM clan_data WHERE hasCastle = ? LIMIT 1";
    private static final String SELECT_FORTRESS_OWNER = "SELECT clan_id FROM clan_data WHERE hasFortress = ? LIMIT 1";
    private static final String SELECT_CLANHALL_OWNER = "SELECT clan_id FROM clan_data WHERE hasHideout = ? LIMIT 1";
    private static final String SELECT_CLAN_SUBPLEDGE_QUERY = "INSERT INTO clan_subpledges (clan_id, type, leader_id, name) VALUES (?,?,?,?)";
    private static final String SELECT_CLAN_QUERY = "SELECT clan_level,hasCastle,hasFortress,hasHideout,ally_id,reputation_score,expelled_member,leaved_ally,dissolved_ally,warehouse,airship,castle_defend_count FROM clan_data where clan_id=?";
    private static final String SELECT_CLAN_PRIVILEGES_QUERY = "SELECT privilleges,rank FROM clan_privs WHERE clan_id=?";

    private static final String UPDATE_WAREHOUSE_BONUS = "UPDATE `clan_data` SET `warehouse`= ? WHERE `clan_id`= ?";
    private static final String UPDATE_CLAN_QUERY = "UPDATE clan_data SET ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,airship=?,castle_defend_count=? WHERE clan_id=?";
    private static final String UPDATE_CHARACTERS_QUERY = "UPDATE characters SET clanid=?,pledge_type=? WHERE obj_Id=?";
    private static final String UPDATE_CLAN_SKILLS_QUERY = "UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?";

    private static final String INSERT_CLAN_QUERY = "INSERT INTO clan_data (clan_id,clan_level,hasCastle,hasFortress,hasHideout,ally_id,expelled_member,leaved_ally,dissolved_ally,airship) values (?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_CLAN_SKILLS_QUERY = "INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?)";

    private static final String REPLACE_CLAN_PRIVILEGES_QUERY = "REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)";

    public static ClanDataDAO getInstance() {
        return INSTANCE;
    }

    public Clan getOwner(final Castle c) {
        return getOwner(c, SELECT_CASTLE_OWNER);
    }

    public Clan getOwner(final Fortress f) {
        return getOwner(f, SELECT_FORTRESS_OWNER);
    }

    public Clan getOwner(final ClanHall c) {
        return getOwner(c, SELECT_CLANHALL_OWNER);
    }

    private Clan getOwner(final Residence residence, final String sql) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, residence.getId());
            rset = statement.executeQuery();
            if (rset.next()) {
                return ClanTable.getInstance().getClan(rset.getInt("clan_id"));
            }
        } catch (Exception e) {
            LOGGER.error("ClanDataDAO.getOwner(Residence, String)", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return null;
    }

    public void updateWarehouseBonus(final int clanId, final int bonus) {
        jdbcHelper.execute(UPDATE_WAREHOUSE_BONUS, bonus, clanId);
    }
}