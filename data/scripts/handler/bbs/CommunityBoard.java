package handler.bbs;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bypass.BypassHolder;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.entity.events.impl.UndyingMatchEvent;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowBoard;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.ThymeleafJob;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.StringTokenizer;

public class CommunityBoard extends ScriptBbsHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);
	private static final ThymeleafJob thymeleaf = ThymeleafJob.getInstance();

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbshome", "_bbslink", "_bbsmultisell", "_bbspage", "_bbsscripts","_bbsopen" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(player.getEvent(UndyingMatchEvent.class) != null)
		{
			player.sendMessage("Community disabled for players on events.");
		}

		if(player.isInStoreMode())
		{
			player.sendMessage("Community disabled for players in Private Store.");
		}

		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";
		if("bbshome".equals(cmd))
		{
			StringTokenizer p = new StringTokenizer(CBasicConfig.BBS_DEFAULT, "_");
			String dafault = p.nextToken();
			if(dafault.equals(cmd))
			{
				html = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/bbs_top.htm", player);

				int favCount = 0;
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rset = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_favorites` WHERE `object_id` = ?");
					statement.setInt(1, player.getObjectId());
					rset = statement.executeQuery();
					if(rset.next())
					{
						favCount = rset.getInt("cnt");
					}
				}
				catch(Exception e)
				{
				}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
				}
				final ZonedDateTime time = ZonedDateTime.now().withZoneSameInstant(CBasicConfig.timeZoneId);
				final Context contex = new Context();
				contex.setVariable("rate_xp", ServerConfig.RATE_XP);
				contex.setVariable("rate_sp", ServerConfig.RATE_SP);
				contex.setVariable("rate_adena", ServerConfig.RATE_DROP_ADENA);
				contex.setVariable("rate_items", ServerConfig.RATE_DROP_ITEMS);
				contex.setVariable("rate_spoil", ServerConfig.RATE_DROP_SPOIL);
				contex.setVariable("rate_raid", ServerConfig.RATE_DROP_RAIDBOSS);
				contex.setVariable("rate_siege", ServerConfig.RATE_DROP_SIEGE_GUARD);
				contex.setVariable("rate_quest", ServerConfig.RATE_QUESTS_DROP);
				contex.setVariable("rate_manor", ServerConfig.RATE_MANOR);
				contex.setVariable("rate_clanrep", ServerConfig.RATE_CLAN_REP_SCORE);
				contex.setVariable("rate_hellbound", ServerConfig.RATE_HELLBOUND_CONFIDENCE);
				contex.setVariable("time", time.getHour() + ":" + time.getMinute());
				contex.setVariable("player_name", player.getName());
				contex.setVariable("online", (int)(GameObjectsStorage.getAllPlayersSize() * CBasicConfig.increaseOnline));
				contex.setVariable("offtrade", (int)(GameObjectsStorage.getPlayers().stream().filter(Player::isInOfflineMode).count() * CBasicConfig.increaseOffline));
				contex.setVariable("player_class", player.getPlayerClassComponent().getClassId().name());
				contex.setVariable("player_clan", player.getClan() != null ? player.getClan().getName() : "Не состоит");
				contex.setVariable("player_noblesse", player.isNoble() ? "Приобретено" : "Отсутствует");
				contex.setVariable("player_ingame", player.getOnlineTime() / (60 * 60 * 1000) + " ч");
				contex.setVariable("player_premium", player.getPremiumAccountComponent().hasBonus() ? "Приобретено" : "Отсутствует");
				contex.setVariable("player_hwid", player.isHwidLockVisual() ? "Присутствует" : "Отсутствует");

				html = html.replace("<?fav_count?>", String.valueOf(favCount));
				html = html.replace("<?clan_count?>", String.valueOf(ClanTable.getInstance().getClans().length));
				html = html.replace("<?market_count?>", String.valueOf(BbsHandlerHolder.getInstance().getIntProperty("col_count")));
				html = thymeleaf.process(html, contex);
			}
			else
			{
				onBypassCommand(player, CBasicConfig.BBS_DEFAULT);
				return;
			}
			saveCommand(player, bypass, true);
		}
		else if("bbslink".equals(cmd))
		{
			html = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/bbs_homepage.htm", player);
		}
		else if(bypass.startsWith("_bbsopen"))
		{
			final String[] b = bypass.split(":");
			final String folder = b[1];
			final String page = b[2];
			html = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/" + folder + "/" + page + ".htm", player);
			if(html == null)
			{
				return;
			}
		}
		else if(bypass.startsWith("_bbspage"))
		{
			//Example: "bypass _bbspage:index".
			String[] b = bypass.split(":");
			String page = b[1];
			html = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/pages/" + page + ".htm", player);
			if (page.equalsIgnoreCase("security")) {
				html = html.replaceFirst("%player_hwid_button%", player.isHwidLockVisual() ? "<button action=\"bypass htmbypass_services.HwidLock:unlock\" width=\"15\" height=\"15\" back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">" : "<button action=\"bypass htmbypass_services.HwidLock:lock\" width=\"15\" height=\"15\" back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">");
				html = html.replaceFirst("%player_hwid_colored%", player.isHwidLockVisual() ? "<font color=00e600>on</font>" : "<font color=ff0000>off</font>");
			}
		}
		else if(bypass.startsWith("_bbsmultisell"))
		{
			//Example: "_bbsmultisell:10000;_bbspage:index" or "_bbsmultisell:10000;_bbshome" or "_bbsmultisell:10000"...
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				onBypassCommand(player, pBypass);
			}

			int listId = Integer.parseInt(mBypass[1]);
			MultiSellHolder.getInstance().SeparateAndSend(listId, player, -1, 0);
			return;
		}
		else if(bypass.startsWith("_bbsscripts"))
		{
			//Example: "_bbsscripts:events.GvG.GvG:addGroup;_bbspage:index" or "_bbsscripts:events.GvG.GvG:addGroup;_bbshome" or "_bbsscripts:events.GvG.GvG:addGroup"...
			/*StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String sBypass = st2.nextToken().substring(12);
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
				onBypassCommand(player, pBypass);

			String[] word = sBypass.split("\\s+");
			String[] args = sBypass.substring(word[0].length()).trim().split("\\s+");
			String[] path = word[0].split(":");
			if(path.length != 2)
				return;

			Scripts.getInstance().callScripts(player, path[0], path[1], word.length == 1 ? new Object[] {} : new Object[] { args });
			return;*/
			_log.error("Trying to call script bypass: " + bypass + ' ' + player);
		}
		else if(bypass.startsWith("_bbshtmbypass"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String command = st2.nextToken().substring(14);
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				onBypassCommand(player, pBypass);
			}

			String word = command.split("\\s+")[0];

			Pair<Object, Method> b = BypassHolder.getInstance().getBypass(word);
			if(b != null)
			{
				try
				{
					b.getValue().invoke(b.getKey(), player, null, command.substring(word.length()).trim().split("\\s+"));
				}
				catch(Exception e)
				{
					_log.info("Exception: " + e, e);
				}
			}
			return;
		}

		ShowBoard.separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}
}
