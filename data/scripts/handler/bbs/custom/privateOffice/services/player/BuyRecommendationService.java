package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 18.02.2016
 */
public class BuyRecommendationService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/buy_rec.htm", player);
		htm = htm.replace("<?correct_recommendation?>", String.valueOf(player.getRecommendationComponent().getRecomHave()));
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
			player.sendPacket(new CustomMessage("bbs.service.buyFame.error"));
			useSaveCommand(player);
			return;
		}
		if(player.getRecommendationComponent().getRecomHave() >= 255)
		{
			player.sendPacket(new CustomMessage("bbs.service.buyRec.maxRec"));
			useSaveCommand(player);
			return;
		}
		final String strRec = str[4].trim();
		final int rec = Converter.convert(Integer.class, strRec);
		final int recFinish = Math.min(255, player.getRecommendationComponent().getRecomHave() + rec);
		final int currectCount = (player.getRecommendationComponent().getRecomHave() + rec) - recFinish;
		final int price = (int) (getService().getItemCount() * (rec - currectCount));
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, rec - currectCount);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int rec = (int) params[0];
		player.getRecommendationComponent().addRecomHave(rec);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyRecommendationService", player, "buy rec count = " + rec);
	}

	@Override
	public Services getService()
	{
		return Services.buy_recommendation;
	}

}
