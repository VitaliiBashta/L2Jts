package handler.bbs.custom;
import handler.bbs.ScriptBbsHandler;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

public class CommunityShop extends ScriptBbsHandler
{
	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbsshop" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(!CBasicConfig.COMMUNITYBOARD_ENABLED)
		{
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		final String html;
		if(bypass.startsWith("_bbsshop"))
		{
			final String[] link = bypass.split(":");
			if(link[1].equals("open"))
			{
				final String[] data = bypass.split(";");
				final int listId = Integer.parseInt(link[2].split(";")[0]);
				if(data.length > 1)
				{
					useCommand(player, data[1]);
				}
				MultiSellHolder.getInstance().SeparateAndSend(listId, player, -1, 0);
				return;
			}
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/shop/" + link[1] + ".htm", player);
		}
		else
		{
			html = "";
		}
		separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
