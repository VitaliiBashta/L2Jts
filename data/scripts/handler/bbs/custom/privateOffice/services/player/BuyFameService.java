package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 18.02.2016
 */
public class BuyFameService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int countColumn = Math.min(3, getService().getCounts().length);
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/fame/buy_fame.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/fame/button.htm", player);
		htm = htm.replace("<?correct_fame?>", String.valueOf(player.getFame()));
		final StringBuilder buttons = new StringBuilder();
		for(int correctCount = 0; correctCount < countColumn; correctCount++)
		{
			String generateButton = button;
			generateButton = generateButton.replace("<?index?>", String.valueOf(correctCount));
			generateButton = generateButton.replace("<?counts?>", String.valueOf(getService().getCounts()[correctCount]));
			generateButton = generateButton.replace("<?price?>", String.valueOf(getService().getItemCounts()[correctCount]));
			generateButton = generateButton.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemIds()[correctCount]));
			buttons.append(generateButton);
		}
		htm = htm.replace("<?buttons?>", buttons.toString());
		separateAndSend(htm, player);	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.buyFame.error"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.buyFame.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(player.getFame() >= FormulasConfig.LIM_FAME)
		{
			player.sendPacket(new CustomMessage("bbs.service.buyFame.maxFame"));
			useSaveCommand(player);
			return;
		}
		final int index = Converter.convert(Integer.class, str[4]);
		if(index < 0 || (index >= getService().getCounts().length ||
				index >= getService().getItemIds().length ||
				index >= getService().getItemCounts().length))
		{
			player.sendPacket(new CustomMessage("bbs.service.buyFame.error"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemIds()[index], getService().getItemCounts()[index], true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, index);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int index = (int) params[0];
		final int fame = getService().getCounts()[index];
		player.setFame(player.getFame() + fame, "");
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyFameService", player, "buy fame count = " + fame);
	}

	@Override
	public Services getService()
	{
		return Services.buy_fame;
	}
}
