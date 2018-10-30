package org.mmocore.gameserver.object.components.items.warehouse;

import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PetInventoryUpdate;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.Collection;

public class PetInventory extends Inventory {
    private final PetInstance _actor;

    public PetInventory(PetInstance actor) {
        super(actor.getPlayer().getObjectId());
        _actor = actor;
    }

    @Override
    public PetInstance getActor() {
        return _actor;
    }

    public Player getOwner() {
        return _actor.getPlayer();
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.PET_INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PET_PAPERDOLL;
    }

    @Override
    protected void onRefreshWeight() {
        getActor().sendPetInfo();
    }

    @Override
    protected void sendAddItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addNewItem(item));
    }

    @Override
    protected void sendModifyItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addModifiedItem(item));
    }

    @Override
    protected void sendRemoveItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addRemovedItem(item));
    }

    @Override
    public void restore() {
        final int ownerId = getOwnerId();

        writeLock();
        try {
            Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(ownerId, getBaseLocation());

            for (ItemInstance item : items) {
                _items.add(item);
                onRestoreItem(item);
            }

            items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

            for (ItemInstance item : items) {
                _items.add(item);
                onRestoreItem(item);
                if (ItemFunctions.checkIfCanEquip(getActor(), item) == null) {
                    setPaperdollItem(item.getEquipSlot(), item);
                }
            }
        } finally {
            writeUnlock();
        }

        refreshWeight();
    }

    @Override
    public void store() {
        writeLock();
        try {
            ItemsDAO.getInstance().update(_items);
        } finally {
            writeUnlock();
        }
    }

    public void validateItems() {
        for (ItemInstance item : _paperdoll) {
            if (item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item))) {
                unEquipItem(item);
            }
        }
    }
}