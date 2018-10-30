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
public class PKClearService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/clear_pk.htm", player);
		final int pkKills = (player.getPkKills() > getPKMax() ? getPKMax() : player.getPkKills());
		final long price = getService().getItemCount() * pkKills;
		htm = htm.replace("<?correct_pk?>", String.valueOf(player.getPkKills()));
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?max_clear_pk?>", String.valueOf(getPKMax()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		htm = htm.replace("<?pick_up_price?>", String.valueOf(price));
		htm = htm.replace("<?price_clear_pk?>", String.valueOf(getService().getItemCount()));
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
		if(player.getPkKills() < 1)
		{
			player.sendPacket(new CustomMessage("bbs.service.clearPK.noPKKills"));
			useSaveCommand(player);
			return;
		}
		final int pkKillsFinal = Math.min(getPKMax(), player.getPkKills());
		final long price = getService().getItemCount() * pkKillsFinal;
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, pkKillsFinal);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int clearPKCount = (int) params[0];
		player.setPkKills(Math.max(0, player.getPkKills() - clearPKCount));
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("PKCleanService", player, "reduced PK kills to " + player.getPkKills() + ".");
	}

	private int getPKMax()
	{
		return CServiceConfig.clearPKMax;
	}

	@Override
	public Services getService()
	{
		return Services.clear_pk;
	}

}
