package handler.bbs.custom.rating;
import handler.bbs.ScriptBbsHandler;
import handler.bbs.dao.rating.CommunityRatingDAO;
import infinispan.com.google.common.cache.CacheBuilder;
import infinispan.com.google.common.cache.CacheLoader;
import infinispan.com.google.common.cache.LoadingCache;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CRatingConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.custom.community.RatingTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommunityRating extends ScriptBbsHandler
{
	private static final LoadingCache<Rating, List<RatingTemplate>> ratingsCache = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.HOURS).build(new RefreshStatisticCacheLoader());
	private static final CommunityRatingDAO DAO = CommunityRatingDAO.getInstance();
	private static final String[] COMMANDS = new String[] { "_bbsrating" };
	private static final int MAX_PLAYERS = 10;

	@Override
	public String[] getBypassCommands()
	{
		return COMMANDS;
	}

	@Override
	public void onBypassCommand(final Player player, final String bypass)
	{
		final String[] args = bypass.split(":");
		if(args.length == 0)
		{
			player.sendMessage(new CustomMessage("common.Disabled"));
			useCommand(player, "_bbshome");
			return;
		}

		final Rating rating = Rating.valueOf(args[1]);
		if(rating == null || !rating.isEnabled())
		{
			player.sendMessage(new CustomMessage("common.Disabled"));
			useCommand(player, "_bbshome");
			return;
		}

		final String html = getHtm(rating, player);
		separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{

	}

	private enum Rating
	{
		top_pvp
				{
					@Override
					public boolean isEnabled()
					{
						return CRatingConfig.AllowTopPvp;
					}

					@Override
					public String getHtml(Player player)
					{
						return getCache().getHtml(CBasicConfig.BBS_PATH + "/rating/pvp.htm", player);
					}

					@Override
					public List<RatingTemplate> getResult()
					{
						return DAO.selectTopPvp();
					}
				},
		top_pk
				{
					@Override
					public boolean isEnabled()
					{
						return CRatingConfig.AllowTopPk;
					}

					@Override
					public String getHtml(Player player)
					{
						return getCache().getHtml(CBasicConfig.BBS_PATH + "/rating/pk.htm", player);
					}

					@Override
					public List<RatingTemplate> getResult()
					{
						return DAO.selectTopPk();
					}
				},
		top_rich
				{
					@Override
					public boolean isEnabled()
					{
						return CRatingConfig.AllowTopFortune;
					}

					@Override
					public String getHtml(Player player)
					{
						return getCache().getHtml(CBasicConfig.BBS_PATH + "/rating/rich.htm", player);
					}

					@Override
					public List<RatingTemplate> getResult()
					{
						return DAO.selectTopFortune();
					}
				},
		top_online
				{
					@Override
					public boolean isEnabled()
					{
						return CRatingConfig.AllowTopOnline;
					}

					@Override
					public String getHtml(Player player)
					{
						return getCache().getHtml(CBasicConfig.BBS_PATH + "/rating/online.htm", player);
					}

					@Override
					public List<RatingTemplate> getResult()
					{
						return DAO.selectTopOnline();
					}
				};

		public abstract boolean isEnabled();

		public abstract String getHtml(final Player player);

		public abstract List<RatingTemplate> getResult();
	}

	private static String getHtm(final Rating type, final Player player)
	{
		String content = type.getHtml(player);
		final String menu = getCache().getHtml(CBasicConfig.BBS_PATH + "/rating/menu.htm", player);
		final List<RatingTemplate> players = ratingsCache.getUnchecked(type);
		if(type == Rating.top_rich)
		{
			content = content.replace("<?item_name?>", getItemName(player.getLanguage(), 57));
		}
		for(int i = 0; i < MAX_PLAYERS; i++)
		{
			if(type == Rating.top_rich)
			{
				content = content.replace("<?name_" + i + "?>", i < players.size() ? players.get(i).getName() : "-");
				content = content.replace("<?sex_" + i + "?>", i < players.size() ? (players.get(i).getSex() == 1 ? new CustomMessage("bbs.rating.female").toString(player) : new CustomMessage("bbs.rating.male").toString(player)) : "-");
				content = content.replace("<?on_" + i + "?>", i < players.size() ? (players.get(i).isOnline() ? new CustomMessage("bbs.rating.online").toString(player) : new CustomMessage("bbs.rating.offline").toString(player)) : "-");
				content = content.replace("<?online_" + i + "?>", i < players.size() ? getHours(players.get(i).getTimeGame()) : "-");
				content = content.replace("<?count_" + i + "?>", i < players.size() ? String.valueOf(players.get(i).getAdenaCount()) : "-");
			}
			else
			{
				content = content.replace("<?name_" + i + "?>", i < players.size() ? players.get(i).getName() : "-");
				content = content.replace("<?sex_" + i + "?>", i < players.size() ? (players.get(i).getSex() == 1 ? new CustomMessage("bbs.rating.female").toString(player) : new CustomMessage("bbs.rating.male").toString(player)) : "-");
				content = content.replace("<?on_" + i + "?>", i < players.size() ? (players.get(i).isOnline() ? new CustomMessage("bbs.rating.online").toString(player) : new CustomMessage("bbs.rating.offline").toString(player)) : "-");
				content = content.replace("<?online_" + i + "?>", i < players.size() ? getHours(players.get(i).getTimeGame()) : "-");
				content = content.replace("<?pvp_count_" + i + "?>", i < players.size() ? String.valueOf(players.get(i).getPvp()) : "-");
				content = content.replace("<?pk_count_" + i + "?>", i < players.size() ? String.valueOf(players.get(i).getPk()) : "-");
			}
		}
		content = content.replace("<?rating_menu?>", menu);
		return content;
	}

	private static class RefreshStatisticCacheLoader extends CacheLoader<Rating, List<RatingTemplate>>
	{
		public List<RatingTemplate> load(final Rating rating) throws Exception
		{
			return rating.getResult();
		}
	}

	private static String getHours(final int time)
	{
		final int hours = time / 3600;
		return String.format("%dh", hours);
	}
}
