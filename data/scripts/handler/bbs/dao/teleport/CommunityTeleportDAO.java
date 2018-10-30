package handler.bbs.dao.teleport;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.components.player.community.TeleportPoint;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Create by Mangol on 12.12.2015.
 */
public class CommunityTeleportDAO extends AbstractGameServerDAO
{
	private static final String SQL_DELETE_TELEPORT_ID = "DELETE FROM bbs_teleport WHERE player_id=? AND id=?;";
	private static final String SQL_SELECT_TELEPORT_ID = "SELECT * FROM bbs_teleport WHERE player_id=?;";
	private static final String SQL_SELECT_TELEPORT_COUNT = "SELECT COUNT(*) FROM bbs_teleport WHERE player_id=?";
	private static final String SQL_SELECT_TELEPORT_NAME_COUNT = "SELECT COUNT(*) FROM bbs_teleport WHERE player_id=? AND name=?";
	private static final String SQL_INSERT_TELEPORT = "INSERT INTO bbs_teleport (player_id, x, y, z, name) VALUES(?,?,?,?,?)";
	private static final String SQL_UPDATE_TELEPORT = "UPDATE bbs_teleport SET x=?, y=?, z=? WHERE player_id=? AND name=?;";

	private static final class LazyHolder
	{
		public static final CommunityTeleportDAO INSTANCE = new CommunityTeleportDAO();
	}

	public static CommunityTeleportDAO getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	public void selectTeleportPoint(final Player player)
	{
		jdbcHelper.query(SQL_SELECT_TELEPORT_ID, new ResultSetHandler()
		{
			@Override
			public void processRow(final ResultSet rs) throws SQLException
			{
				player.getCommunityComponent().addTeleportId(new TeleportPoint(rs.getInt("id"), rs.getString("name"), 57, 0, 1, 85, false, false, 57, 0, new Location(rs.getInt("x"), rs.getInt("y"), rs.getInt("z")), false));
			}
		}, rs -> rs.setInt(1, player.getObjectId()));
	}

	public void deleteTeleportPoint(final Player player, final int id)
	{
		jdbcHelper.execute(SQL_DELETE_TELEPORT_ID, player.getObjectId(), id);
		player.getCommunityComponent().deleteTeleportId(id);
	}

	public int selectCountTeleportPoint(final int objectId)
	{
		return jdbcHelper.queryForInt(SQL_SELECT_TELEPORT_COUNT, objectId);
	}

	public void selectCountToInsert(final Player player, final String namePoint)
	{
		final int count = jdbcHelper.queryForInt(SQL_SELECT_TELEPORT_NAME_COUNT, player.getObjectId(), namePoint);
		if(count == 0)
		{
			Connection con = null;
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(SQL_INSERT_TELEPORT, Statement.RETURN_GENERATED_KEYS);
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, player.getX());
				statement.setInt(3, player.getY());
				statement.setInt(4, player.getZ());
				statement.setString(5, namePoint);
				statement.execute();
				resultSet = statement.getGeneratedKeys();
				resultSet.next();
				player.getCommunityComponent().addTeleportId(new TeleportPoint(resultSet.getInt(1), namePoint, 57, 0, 1, 85, false, false, 57, 0, new Location(player.getX(), player.getY(), player.getZ()), false));
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, resultSet);

			}
		}
		else if(count > 0)
		{
			jdbcHelper.execute(SQL_UPDATE_TELEPORT, stmt -> {
				stmt.setInt(1, player.getX());
				stmt.setInt(2, player.getY());
				stmt.setInt(3, player.getZ());
				stmt.setInt(4, player.getObjectId());
				stmt.setString(5, namePoint);
				final Optional<TeleportPoint> teleportPoint = player.getCommunityComponent().getTeleportPoints().values().stream().filter(teleport -> Objects.equals(teleport.getName(), namePoint)).findFirst();
				if(teleportPoint.isPresent())
				{
					player.getCommunityComponent().addTeleportId(new TeleportPoint(teleportPoint.get().getId(), namePoint, 57, 0, 1, 85, false, false, 57, 0, new Location(player.getX(), player.getY(), player.getZ()), false));
				}
			});
		}
	}
}
