package org.mmocore.gameserver.database.dao.impl;

import org.jts.dataparser.data.holder.DyeDataHolder;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 17:49/13.09.2011
 */
public class CharacterDyeDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterDyeDAO.class);

    private static final CharacterDyeDAO _instance = new CharacterDyeDAO();

    private CharacterDyeDAO() {
    }

    public static CharacterDyeDAO getInstance() {
        return _instance;
    }

    public void select(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("select slot, symbol_id from character_dyes where char_obj_id=? AND class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getPlayerClassComponent().getActiveClassId());
            rset = statement.executeQuery();

            while (rset.next()) {
                final int slot = rset.getInt("slot");
                if (slot < 1 || slot > 3) {
                    continue;
                }

                final int symbol_id = rset.getInt("symbol_id");

                if (symbol_id != 0) {
                    final DyeData tpl = DyeDataHolder.getInstance().getDye(symbol_id);
                    if (tpl != null) {
                        player.getDyes()[slot - 1] = tpl;
                    }
                }
            }
        } catch (final Exception e) {
            _log.warn("could not restore dye: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void insert(final Player player, final int index, final int symbolId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO `character_dyes` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, symbolId);
            statement.setInt(3, index);
            statement.setInt(4, player.getPlayerClassComponent().getActiveClassId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("could not save char dye: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Player player, final int index) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_dyes where char_obj_id=? and slot=? and class_index=?");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, index);
            statement.setInt(3, player.getPlayerClassComponent().getActiveClassId());
            statement.execute();
        } catch (final Exception e) {
            _log.warn("could not remove char dye: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}