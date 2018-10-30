package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 17.02.2016
 */
public class KarmaClearService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/clear_karma.htm", player);
		final int karma = (player.getKarma() > getKarmaMax() ? getKarmaMax() : player.getKarma());
		final long price = getService().getItemCount() * karma;
		htm = htm.replace("<?correct_karma?>", String.valueOf(player.getKarma()));
		htm = htm.replace("<?price_clear_karma?>", String.valueOf(price));
		htm = htm.replace("<?max_karma_points?>", String.valueOf(getKarmaMax()));
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		htm = htm.replace("<?full_item_name?>", getItemName(player.getLanguage(), CServiceConfig.clearKarmaFullItemId));
		htm = htm.replace("<?full_price?>", String.valueOf(CServiceConfig.clearKarmaFullPrice));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.clearPK.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new CustomMessage("bbs.service.clearKarma.isCursedWeapon"));
			useSaveCommand(player);
			return;
		}
		if(player.getKarma() < 1)
		{
			player.sendPacket(new CustomMessage("bbs.service.clearKarma.noKarma"));
			useSaveCommand(player);
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length == 5)
		{
			if(str[4].equalsIgnoreCase("full"))
			{
				if(!getCheckAndPick(player, CServiceConfig.clearKarmaFullItemId, CServiceConfig.clearKarmaFullPrice, true))
				{
					useSaveCommand(player);
					return;
				}
				reply(player, player.getKarma());
			}
			else {
				useSaveCommand(player);
				return;
			}
		}
		final int karmaFinal = Math.min(getKarmaMax(), player.getKarma());
		final long price = getService().getItemCount() * karmaFinal;
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, karmaFinal);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int karmaFinal = (int) params[0];
		player.setKarma(Math.max(0, player.getKarma() - karmaFinal));
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("KarmaClearService", player, "reduced karma points to " + player.getKarma() + ".");
	}

	private int getKarmaMax()
	{
		return CServiceConfig.clearKarmaMax;
	}

	@Override
	public Services getService()
	{
		return Services.clear_karma;
	}

}
