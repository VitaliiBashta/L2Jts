package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.PlayerUtils;

/**
 * @author Mangol
 * @since 02.03.2016
 */
public class IncreaseLowerLevelService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.buy_level;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/buy_level.htm", player);
		htm = htm.replace("<?correct_level?>", String.valueOf(player.getLevel()));
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
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
			player.sendPacket(new CustomMessage("bbs.service.increaseLowerLevel.error"));
			useSaveCommand(player);
			return;
		}
		final int playerLevel = player.getLevel();
		final int minLevel = 1;
		final int maxLevel = player.getPlayerClassComponent().isSubClassActive() ? PlayerUtils.getMaxSubLevel() : PlayerUtils.getMaxLevel();
		final int buyLevel = Converter.convert(Integer.class, str[4].trim());
		final int buyLevelFinish = Math.min(maxLevel, buyLevel);
		if(buyLevelFinish == playerLevel)
		{
			player.sendPacket(new CustomMessage("bbs.service.increaseLowerLevel.thisLevel"));
			useSaveCommand(player);
			return;
		}
		if(buyLevelFinish < minLevel)
		{
			player.sendPacket(new CustomMessage("bbs.service.increaseLowerLevel.noCorrect"));
			useSaveCommand(player);
			return;
		}
		if(player.isTransformed())
		{
			player.sendPacket(new CustomMessage("bbs.service.increaseLowerLevel.isTransformed"));
			useSaveCommand(player);
			return;
		}
		if(buyLevelFinish > playerLevel)
		{
			final int numberLevels = buyLevelFinish - playerLevel;
			final long price = numberLevels * getService().getItemCount();
			if(!getCheckAndPick(player, getService().getItemId(), price, true))
			{
				useSaveCommand(player);
				return;
			}
			reply(player, buyLevelFinish);
		}
		else if(buyLevelFinish < playerLevel)
		{
			final int numberLevels = playerLevel - buyLevelFinish;
			final long price = numberLevels * getService().getItemCount();
			if(!getCheckAndPick(player, getService().getItemId(), price, true))
			{
				useSaveCommand(player);
				return;
			}
			reply(player, buyLevelFinish);
		}
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int oldPlayerLevel = player.getLevel();
		final int level = (int) params[0];
		final long expAdd = ExpDataHolder.getInstance().getExpForLevel(level) - player.getExp();
		player.addExpAndSp(expAdd, 0);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("IncreaseLowerLevelService", player, "buy level = " + level + " old level = " + oldPlayerLevel);
	}
}
