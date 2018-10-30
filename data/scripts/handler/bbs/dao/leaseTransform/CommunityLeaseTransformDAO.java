package handler.bbs.dao.leaseTransform;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mangol
 * @since 04.02.2016
 */
public class CommunityLeaseTransformDAO extends AbstractGameServerDAO
{
	private static final String SQL_SELECT = "SELECT * FROM custom_lease_transform WHERE player_obj_id=?";
	private static final String SQL_DELETE = "DELETE FROM custom_lease_transform WHERE player_obj_id=?";
	private static final String SQL_INSERT = "INSERT INTO custom_lease_transform (player_obj_id, id_transform) VALUES(?,?)";

	private static final class LazyHolder
	{
		public static final CommunityLeaseTransformDAO INSTANCE = new CommunityLeaseTransformDAO();
	}

	public static CommunityLeaseTransformDAO getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	public int select(final Player player)
	{
		final int[] idTransform = new int[1];
		if(player != null)
		{
			jdbcHelper.query(SQL_SELECT, new ResultSetHandler()
			{
				@Override
				public void processRow(ResultSet rs) throws SQLException
				{
					idTransform[0] = rs.getInt("id_transform");
				}
			}, stmt -> stmt.setInt(1, player.getObjectId()));
		}
		return idTransform[0];
	}

	public void delete(final Player player)
	{
		jdbcHelper.execute(SQL_DELETE, stmt -> stmt.setInt(1, player.getObjectId()));
	}

	public void insert(final Player player, final int id)
	{
		jdbcHelper.execute(SQL_INSERT, stmt -> {
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, id);
		});
	}
}
