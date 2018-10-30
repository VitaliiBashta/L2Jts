package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class PET_DEPOSIT implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        final Servitor servitor = player.getServitor();
        if (servitor == null || !servitor.isPet()) {
            return;
        }
        final PetInstance pet = (PetInstance) servitor;
        if (pet.isDead()) {
            player.sendPacket(SystemMsg.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM, ActionFail.STATIC);
            return;
        }
        if (pet.isInCombat()) {
            player.sendPacket(SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE, ActionFail.STATIC);
            return;
        }
        if (!pet.getData().isSyncLevel() && pet.isHungryPet()) {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET, ActionFail.STATIC);
            return;
        }
/*		for(final ItemInstance items : ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(player.getObjectId(), ItemInstance.ItemLocation.PET_INVENTORY)) //FIXME[K] - возможно стоит выполнять в синхронизации с инвентарем.
		{
			if(items.getLocation() == ItemInstance.ItemLocation.PET_INVENTORY)
			{
				player.sendPacket(SystemMsg.THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELLTRADEDROP_PET_SUMMONING_ITEMS, SendTradeDone.FAIL, ActionFail.STATIC);
				return;
			}
		}*/
        pet.unSummon(true, false);
    }
}
