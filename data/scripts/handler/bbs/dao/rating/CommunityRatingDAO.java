package handler.bbs.dao.rating;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.templates.custom.community.RatingTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mangol
 * @since 30.01.2016
 */
public class CommunityRatingDAO extends AbstractGameServerDAO
{
	private static final String SQL_SELECT_TOP_PK = "SELECT char_name, pvpkills, pkkills, sex, onlinetime, online FROM characters ORDER BY pkkills DESC LIMIT 10";
	private static final String SQL_SELECT_TOP_PVP = "SELECT char_name, pvpkills, pkkills, sex, onlinetime, online FROM characters ORDER BY pvpkills DESC LIMIT 10";
	private static final String SQL_SELECT_TOP_ONLINE = "SELECT c.char_name, c.pvpkills, c.pkkills, c.sex, c.onlinetime, c.online FROM characters c ORDER BY c.onlinetime DESC LIMIT 10";
	private static final String SQL_SELECT_TOP_FORTUNE = "SELECT c.char_name, c.pvpkills, c.pkkills, c.sex, c.onlinetime, c.online, sum(i.count) as adenas FROM items i INNER JOIN characters c ON c.obj_id = i.owner_id WHERE i.item_id=57 and i.loc IN ('INVENTORY', 'WAREHOUSE') GROUP BY i.owner_id ORDER BY adenas DESC LIMIT 10";

	private static final class LazyHolder
	{
		public static final CommunityRatingDAO INSTANCE = new CommunityRatingDAO();
	}

	public static CommunityRatingDAO getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	public List<RatingTemplate> selectTopPk()
	{
		final List<RatingTemplate> topPk = new ArrayList<>(10);
		jdbcHelper.query(SQL_SELECT_TOP_PK, new ResultSetHandler()
		{
			@Override
			public void processRow(final ResultSet resultSet) throws SQLException
			{
				String name = resultSet.getString(1);
				int pvp = resultSet.getInt(2);
				int pk = resultSet.getInt(3);
				int sex = resultSet.getInt(4);
				int onlineTime = resultSet.getInt(5);
				boolean online = resultSet.getBoolean(6);
				final RatingTemplate template = new RatingTemplate(name, pvp, pk, sex, online, onlineTime);
				if (!org.apache.commons.lang3.ArrayUtils.contains(CServiceConfig.ratingIgnoreNicks, name))
					topPk.add(template);
			}
		});

		return topPk;
	}

	public List<RatingTemplate> selectTopPvp()
	{
		final List<RatingTemplate> topPvp = new ArrayList<>(10);
		jdbcHelper.query(SQL_SELECT_TOP_PVP, new ResultSetHandler()
		{
			@Override
			public void processRow(ResultSet resultSet) throws SQLException
			{
				String name = resultSet.getString(1);
				int pvp = resultSet.getInt(2);
				int pk = resultSet.getInt(3);
				int sex = resultSet.getInt(4);
				int onlineTime = resultSet.getInt(5);
				boolean online = resultSet.getBoolean(6);
				RatingTemplate template = new RatingTemplate(name, pvp, pk, sex, online, onlineTime);
				if (!org.apache.commons.lang3.ArrayUtils.contains(CServiceConfig.ratingIgnoreNicks, name))
					topPvp.add(template);
			}
		});
		return topPvp;
	}

	public List<RatingTemplate> selectTopOnline()
	{
		final List<RatingTemplate> topOnline = new ArrayList<>(10);
		jdbcHelper.query(SQL_SELECT_TOP_ONLINE, new ResultSetHandler()
		{
			@Override
			public void processRow(final ResultSet resultSet) throws SQLException
			{
				String name = resultSet.getString(1);
				int pvp = resultSet.getInt(2);
				int pk = resultSet.getInt(3);
				int sex = resultSet.getInt(4);
				int onlineTime = resultSet.getInt(5);
				boolean online = resultSet.getBoolean(6);
				RatingTemplate template = new RatingTemplate(name, pvp, pk, sex, online, onlineTime);
				if (!org.apache.commons.lang3.ArrayUtils.contains(CServiceConfig.ratingIgnoreNicks, name))
					topOnline.add(template);
			}
		});
		return topOnline;
	}

	public List<RatingTemplate> selectTopFortune()
	{
		final List<RatingTemplate> topFortune = new ArrayList<>(10);
		jdbcHelper.query(SQL_SELECT_TOP_FORTUNE, new ResultSetHandler()
		{
			@Override
			public void processRow(final ResultSet resultSet) throws SQLException
			{
				String name = resultSet.getString(1);
				int pvp = resultSet.getInt(2);
				int pk = resultSet.getInt(3);
				int sex = resultSet.getInt(4);
				int onlineTime = resultSet.getInt(5);
				boolean online = resultSet.getBoolean(6);
				long adenaCount = resultSet.getLong(7);
				RatingTemplate template = new RatingTemplate(name, pvp, pk, sex, online, onlineTime, adenaCount);
				if (!org.apache.commons.lang3.ArrayUtils.contains(CServiceConfig.ratingIgnoreNicks, name))
					topFortune.add(template);
			}
		});
		return topFortune;
	}


}
