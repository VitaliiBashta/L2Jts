package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
public class ExpandInventoryService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final int maxInventory = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.INVENTORY_MAX_FOR_DWARF : OtherConfig.INVENTORY_MAX_NO_DWARF;
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/expand_inventory.htm", player);
		htm = htm.replace("<?correct_size_inventory?>", String.valueOf(player.getInventoryLimit()));
		htm = htm.replace("<?max_size_inventory?>", String.valueOf(maxInventory));
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
			player.sendPacket(new CustomMessage("bbs.service.expandInventory.error"));
			useSaveCommand(player);
			return;
		}
		final int maxInventory = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.INVENTORY_MAX_FOR_DWARF : OtherConfig.INVENTORY_MAX_NO_DWARF;
		if(player.getInventoryLimit() >= maxInventory)
		{
			player.sendPacket(new CustomMessage("bbs.service.expandInventory.maxInventory"));
			useSaveCommand(player);
			return;
		}
		final String strInv = str[4].trim();
		final int inventoryLimit = player.getInventoryLimit();
		final int inventoryAdd = convert(Integer.class, strInv);
		final int inventorySizeFinish = Math.min(maxInventory,  inventoryLimit + inventoryAdd);
		final int inventoryCorrectCount = inventorySizeFinish - inventoryLimit;
		final int price = (int) (getService().getItemCount() * inventoryCorrectCount);
		if(!getCheckAndPick(player, getService().getItemId(), price, true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, inventoryCorrectCount);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final int add = (int) params[0];
		player.getPremiumAccountComponent().setExpandInventory(player.getPremiumAccountComponent().getExpandInventory() + add);
		player.getPlayerVariables().set(PlayerVariables.EXPAND_INVENTORY, String.valueOf(player.getPremiumAccountComponent().getExpandInventory()), -1);
		player.sendPacket(new ExStorageMaxCount(player));
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ExpandInventoryService", player, "buy inventory count = " + add);
	}

	@Override
	public Services getService()
	{
		return Services.expand_inventory;
	}

}
