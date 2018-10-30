package handler.bbs.custom.privateOffice.services.clan;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 05.03.2016
 */
public class BuyClanReputationService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.buy_clan_rep;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int countColumn = Math.min(3, getService().getCounts().length);
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/buyRep/buy_clan_rep.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/buyRep/button.htm", player);
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
		if(player.getClan() == null)
		{
			htm = htm.replace("<?correct_rep?>", new CustomMessage("bbs.service.clanRep.noClan").toString(player));
		}
		else if(player.getClan().getLevel() < 5)
		{
			htm = htm.replace("<?correct_rep?>", new CustomMessage("bbs.service.clanRep.clanNoFive").toString(player));
		}
		else
		{
			htm = htm.replace("<?correct_rep?>", String.valueOf(player.getClan().getReputationScore()));
		}
		htm = htm.replace("<?buttons?>", buttons.toString());
		separateAndSend(htm, player);
	}

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
			player.sendPacket(new CustomMessage("bbs.service.clanRep.error"));
			useSaveCommand(player);
			return;
		}
		if(player.getClan() == null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRep.noClan"));
			useSaveCommand(player);
			return;
		}
		if(player.getClan().getLevel() < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRep.clanNoFive"));
			useSaveCommand(player);
			return;
		}
		final int index = Converter.convert(Integer.class, str[4]);
		if(index < 0 || (index >= getService().getCounts().length ||
				index >= getService().getItemIds().length ||
				index >= getService().getItemCounts().length))
		{
			player.sendPacket(new CustomMessage("bbs.service.clanRep.error"));
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
		final int reputationCount = getService().getCounts()[index];
		final int oldReputationCount = player.getClan().getReputationScore();
		player.getClan().incReputation(reputationCount, false, "BuyClanReputationService");
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyClanReputationService", player, "buy reputation count - " + reputationCount + " old reputation - " + oldReputationCount);
	}

}
