package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

import java.util.Objects;

/**
 * @author Mangol
 * @since 22.02.2016
 */
public class RestorePetNameService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/restore_pet_name.htm", player);
		final String petName;
		final String defaultPetName;
		final PetInstance pet = player.getServitor() != null && player.getServitor().isPet() ? (PetInstance) player.getServitor() : null;
		if(pet != null)
		{
			petName = pet.getName();
			defaultPetName = pet.getTemplate().getName();
		}
		else
		{
			final String message = new CustomMessage("bbs.service.restorePetName.noSummonPet").toString(player);
			petName = message;
			defaultPetName = message;
		}
		htm = htm.replace("<?correct_pet_name?>", String.valueOf(petName));
		htm = htm.replace("<?default_pet_name?>", String.valueOf(defaultPetName));
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		final PetInstance pet = player.getServitor() != null && player.getServitor().isPet() ? (PetInstance) player.getServitor() : null;
		if(pet == null)
		{
			player.sendPacket(new CustomMessage("bbs.service.restorePetName.noSummonPet"));
			useSaveCommand(player);
			return;
		}
		if(Objects.equals(pet.getName(), pet.getTemplate().getName()))
		{
			player.sendPacket(new CustomMessage("bbs.service.restorePetName.equalsPetName"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.restorePetName.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.restorePetName.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final PetInstance pet = (PetInstance) player.getServitor();
		final String oldPetName = pet.getName();
		pet.setName(pet.getTemplate().getName());
		pet.broadcastCharInfo();
		final ItemInstance controlItem = pet.getControlItem();
		if(controlItem != null)
		{
			controlItem.setCustomType2(1);
			controlItem.setJdbcState(JdbcEntityState.UPDATED);
			controlItem.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(controlItem));
		}
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("RestorePetNameService", player, "rename pet name = " + oldPetName + " to name = " + pet.getName());
	}

	@Override
	public Services getService()
	{
		return Services.restore_pet_name;
	}

}
