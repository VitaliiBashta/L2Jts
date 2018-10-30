package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author KilRoy
 */
public class CharacterProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(CharacterProductDAO.class);

    private static final CharacterProductDAO instance = new CharacterProductDAO();

    private static final String SQL_QUERY_SELECT = "SELECT product_Id FROM character_products WHERE char_Id=?";
    private static final String SQL_QUERY_INSERT = "INSERT character_products(char_Id, product_Id) VALUES (?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE character_products SET product_Id = ? WHERE char_Id = ? AND product_Id = ?";
    private static final String SQL_QUERY_SELECT_BOUGHT = "SELECT product_Id FROM character_products";

    public static CharacterProductDAO getInstance() {
        return instance;
    }

    public void select(final Connection con, final Player player) {
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            statement = con.prepareStatement(SQL_QUERY_SELECT);
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();

            while (rset.next()) {
                player.getPremiumAccountComponent().addBoughtProduct(ProductItemHolder.getInstance().getItem(rset.getInt("product_Id")), false);
            }
        } catch (final Exception e) {
            logger.info("CharacterProductDAO: select" + e, e);
        } finally {
            DbUtils.closeQuietly(statement, rset);
        }
    }

    public void insert(final Player player, final int productId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_INSERT);
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, productId);
            statement.execute();
        } catch (final Exception e) {
            logger.info("CharacterProductDAO: insert " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update(final Player player, final int oldId, final int newId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_UPDATE);
            statement.setInt(1, newId);
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, oldId);
            statement.execute();
        } catch (final Exception e) {
            logger.info("CharacterProductDAO: update " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void loadBoughtProducts() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            stmt = con.prepareStatement(SQL_QUERY_SELECT_BOUGHT);
            rset = stmt.executeQuery();
            while (rset.next()) {
                ProductItemHolder.getInstance().loadBestProduct(rset.getInt("product_Id"));
            }
        } catch (final Exception e) {
            logger.info("CharacterProductDAO: loadBoughtProducts() " + e, e);
        } finally {
            DbUtils.closeQuietly(con, stmt, rset);
        }
    }
}