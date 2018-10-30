package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 2:47/09.09.2011
 */
public class CharacterAccessDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterAccessDAO.class);

    private static final CharacterAccessDAO _instance = new CharacterAccessDAO();

    public static CharacterAccessDAO getInstance() {
        return _instance;
    }

    public void select(final CharSelectInfoPackage info) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM character_access WHERE object_id=?");
            statement.setInt(1, info.getObjectId());
            rset = statement.executeQuery();
            if (rset.next()) {
                info.setPasswordEnable(rset.getInt("password_enable") == 1);
                info.setPassword(rset.getString("password"));
            }
        } catch (Exception e) {
            _log.error("CharacterAccessDAO.select(CharSelectInfoPackage): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void update(final int objectId, final String password) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE character_access SET password=? WHERE object_id=?");
            statement.setString(1, password);
            statement.setInt(2, objectId);
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterAccessDAO.update(int,String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
