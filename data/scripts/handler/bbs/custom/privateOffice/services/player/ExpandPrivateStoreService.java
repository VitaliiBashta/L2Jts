package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStorageMaxCount;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 22.02.2016
 */
public class ExpandPrivateStoreService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int maxTrade = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_PVTSTORE_SLOTS_DWARF : OtherConfig.MAX_PVTSTORE_SLOTS_OTHER;
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/expand_private_store.htm", player);
		htm = htm.replace("<?correct_size_private_store?>", String.valueOf(player.getTradeLimit()));
		htm = htm.replace("<?max_size_private_store?>", String.valueOf(maxTrade));
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
			player.sendPacket(new CustomMessage("bbs.service.expandPrivateStore.error"));
			useSaveCommand(player);
			return;
		}
		final int tradeLimit = player.getTradeLimit();
		final int maxTrade = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_PVTSTORE_SLOTS_DWARF : OtherConfig.MAX_PVTSTORE_SLOTS_OTHER;
		if(tradeLimit >= maxTrade)
		{
			player.sendPacket(new CustomMessage("bbs.service.expandPrivateStore.maxPrivateStore"));
			useSaveCommand(player);
			return;
		}
		final String strP = str[4].trim();
		final int privateStoreAdd = Converter.convert(Integer.class, strP);
		final int privateStoreFinish = Math.min(maxTrade, tradeLimit + privateStoreAdd);
		final int privateStoreCorrectCount = (tradeLimit + privateStoreAdd) - privateStoreFinish;
		final int price = (int) (getService().getItemCount() * (privateStoreAdd - privateStoreCorrectCount));
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, privateStoreAdd - privateStoreCorrectCount);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int add = (int) params[0];
		player.getPremiumAccountComponent().setPrivateStore(player.getPremiumAccountComponent().getPrivateStore() + add);
		player.getPlayerVariables().set(PlayerVariables.EXPAND_PRIVATE_STORE, String.valueOf(player.getPremiumAccountComponent().getPrivateStore()), -1);
		player.sendPacket(new ExStorageMaxCount(player));
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ExpandPrivateStoreService", player, "buy private store count = " + add);
	}

	@Override
	public Services getService()
	{
		return Services.expand_private_store;
	}

}
