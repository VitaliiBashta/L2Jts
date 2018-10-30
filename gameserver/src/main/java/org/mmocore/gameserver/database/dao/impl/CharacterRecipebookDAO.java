package org.mmocore.gameserver.database.dao.impl;

import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CharacterRecipebookDAO {
    private static final Logger log = LoggerFactory.getLogger(CharacterRecipebookDAO.class);
    private static final String SELECT_QUERY = "SELECT id FROM character_recipebook WHERE char_id=?";
    private static final String REPLACE_QUERY = "REPLACE INTO character_recipebook (id,char_id) VALUES(?,?)";
    private static final String DELETE_QUERY = "DELETE FROM character_recipebook WHERE char_id=? AND id=? LIMIT 1";
    private static final RecipeHolder recipeHolder = RecipeHolder.getInstance();
    private static final CharacterRecipebookDAO ourInstance = new CharacterRecipebookDAO();

    private CharacterRecipebookDAO() {
    }

    public static CharacterRecipebookDAO getInstance() {
        return ourInstance;
    }

    public void restoreRecipeBook(final int objectId, final RecipeComponent component) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_QUERY);
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
            while (rset.next()) {
                final int id = rset.getInt("id");
                final Recipe recipe = recipeHolder.getRecipeId(id);
                if (recipe.isCommonRecipe()) {
                    component.addCommonRecipe(recipe);
                } else {
                    component.addDwarfRecipe(recipe);
                }
            }
        } catch (Exception e) {
            log.warn("count not recipe skills:" + e);
            log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void registerRecipe(int playerObjId, int recipeId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(REPLACE_QUERY);
            statement.setInt(1, recipeId);
            statement.setInt(2, playerObjId);
            statement.executeUpdate();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void unregisterRecipe(int playerObjId, int recipeId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_QUERY);
            statement.setInt(1, playerObjId);
            statement.setInt(2, recipeId);
            statement.executeUpdate();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
