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
public class ExpandWarehouseService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int maxWarehouse = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_WAREHOUSE_SLOTS_DWARF : OtherConfig.MAX_WAREHOUSE_SLOTS_NO_DWARF;
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/expand_warehouse.htm", player);
		htm = htm.replace("<?correct_size_warehouse?>", String.valueOf(player.getWarehouseLimit()));
		htm = htm.replace("<?max_size_warehouse?>", String.valueOf(maxWarehouse));
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
			player.sendPacket(new CustomMessage("bbs.service.expandWarehouse.error"));
			useSaveCommand(player);
			return;
		}
		final int warehouseLimit = player.getWarehouseLimit();
		final int maxWarehouse = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_WAREHOUSE_SLOTS_DWARF : OtherConfig.MAX_WAREHOUSE_SLOTS_NO_DWARF;
		if(player.getWarehouseLimit() >= maxWarehouse)
		{
			player.sendPacket(new CustomMessage("bbs.service.expandWarehouse.maxWarehouse"));
			useSaveCommand(player);
			return;
		}
		final String strWar = str[4].trim();
		final int warehouseAdd = Converter.convert(Integer.class, strWar);
		final int warehouseFinish = Math.min(maxWarehouse, warehouseLimit + warehouseAdd);
		final int warehouseCorrectCount = (warehouseLimit + warehouseAdd) - warehouseFinish;
		final int price = (int) (getService().getItemCount() * (warehouseAdd - warehouseCorrectCount));
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, warehouseAdd - warehouseCorrectCount);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int add = (int) params[0];
		player.getPremiumAccountComponent().setExpandWarehouse(player.getPremiumAccountComponent().getExpandWarehouse() + add);
		player.getPlayerVariables().set(PlayerVariables.EXPAND_WAHEHOUSE, String.valueOf(player.getPremiumAccountComponent().getExpandWarehouse()), -1);
		player.sendPacket(new ExStorageMaxCount(player));
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ExpandWarehouseService", player, "buy warehouse count = " + add);
	}

	@Override
	public Services getService()
	{
		return Services.expand_warehouse;
	}
}
