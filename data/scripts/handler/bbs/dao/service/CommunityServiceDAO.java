package handler.bbs.dao.service;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 18.02.2016
 */
public class CommunityServiceDAO extends AbstractGameServerDAO
{
	private static final String SQL_CHANGE_SEX = "UPDATE characters SET sex = ? WHERE obj_Id = ?";
	private static final String SQL_CHANGE_RACE = "UPDATE characters SET race = ? WHERE obj_Id = ?";

	private static final class LazyHolder
	{
		public static final CommunityServiceDAO INSTANCE = new CommunityServiceDAO();
	}

	public static CommunityServiceDAO getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	public void changeSex(final Player player, final PlayerSex sex)
	{
		jdbcHelper.execute(SQL_CHANGE_SEX, stmt -> {
			stmt.setInt(1, sex.ordinal());
			stmt.setInt(2, player.getObjectId());
		});
	}

	public void changeRace(final Player player, final PlayerRace race)
	{
		jdbcHelper.execute(SQL_CHANGE_RACE, stmt -> {
			stmt.setInt(1, race.ordinal());
			stmt.setInt(2, player.getObjectId());
		});
	}
}
