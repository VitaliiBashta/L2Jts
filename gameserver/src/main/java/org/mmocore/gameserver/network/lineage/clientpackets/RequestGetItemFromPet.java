package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.PetInventory;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;

public class RequestGetItemFromPet extends L2GameClientPacket {
    //private static final Logger LOGGER = LoggerFactory.getLogger(RequestGetItemFromPet.class);

    private int _objectId;
    private long _amount;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _amount = readQ();
        int _unknown = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _amount < 1) {
            return;
        }

        final PetInstance pet = (PetInstance) activeChar.getServitor();
        if (pet == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isInTrade() || activeChar.isProcessingRequest()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        final PetInventory petInventory = pet.getInventory();
        final PcInventory playerInventory = activeChar.getInventory();

        final ItemInstance item = petInventory.getItemByObjectId(_objectId);
        if (item == null || item.getCount() < _amount || item.isEquipped()) {
            activeChar.sendActionFailed();
            return;
        }

        int slots = 0;
        final long weight = item.getTemplate().getWeight() * _amount;
        if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
            slots = 1;
        }

        if (!activeChar.getInventory().validateWeight(weight)) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }

        if (!activeChar.getInventory().validateCapacity(slots)) {
            activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            return;
        }

        playerInventory.addItem(petInventory.removeItemByObjectId(_objectId, _amount));

        pet.sendChanges();
        activeChar.sendChanges();
    }
}
