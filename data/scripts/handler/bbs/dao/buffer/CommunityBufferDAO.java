package handler.bbs.dao.buffer;
import org.mmocore.gameserver.object.components.player.community.Scheme;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Create by Mangol on 13.12.2015.
 */
public class CommunityBufferDAO extends AbstractGameServerDAO
{
	public static final String SELECT_SQL_QUERY = "SELECT name, buffs FROM bbs_buffer WHERE player_id=?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO bbs_buffer (name, player_id, buffs) VALUES (?,?,?)";
	public static final String DELETE_SQL_QUERY = "DELETE FROM bbs_buffer WHERE player_id=? AND name=?";

	private static final class LazyHolder
	{
		public static final CommunityBufferDAO INSTANCE = new CommunityBufferDAO();
	}

	public static CommunityBufferDAO getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	public void select(final Player player)
	{
		jdbcHelper.query(SELECT_SQL_QUERY, new ResultSetHandler()
		{
			@Override
			public void processRow(final ResultSet rs) throws SQLException
			{
				final String buffs = rs.getString("buffs");
				final String name = rs.getString("name");
				if(!buffs.isEmpty())
				{
					final Scheme scheme = new Scheme(name);
					for(final String str : buffs.split(";"))
					{
						final String[] arrayOfString2 = str.split(",");
						final int id = Integer.parseInt(arrayOfString2[0]);
						final int lvl = Integer.parseInt(arrayOfString2[1]);
						scheme.addBuff(id, lvl);
					}
					if(scheme.getBuffs().size() > 1)
					{
						player.getCommunityComponent().addBuffScheme(scheme);
					}
				}
				else
				{
					player.sendMessage("Schema " + name + " has been removed as it doesn't have buff!");
					delete(player, name);
				}
			}
		}, rs -> rs.setInt(1, player.getObjectId()));
	}

	public void insert(final Player player, final String buffs, final Scheme scheme)
	{
		jdbcHelper.execute(INSERT_SQL_QUERY, stmt -> {
			stmt.setString(1, scheme.getName());
			stmt.setInt(2, player.getObjectId());
			stmt.setString(3, buffs);
		});
		player.getCommunityComponent().addBuffScheme(scheme);
	}

	public void delete(final Player player, final String name)
	{
		jdbcHelper.execute(DELETE_SQL_QUERY, stmt ->
		{
			stmt.setInt(1, player.getObjectId());
			stmt.setString(2, name);
		});
		player.getCommunityComponent().deleteBuffScheme(name);
	}
}
