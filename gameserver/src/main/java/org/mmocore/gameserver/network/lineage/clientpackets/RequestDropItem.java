package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.PetDataHolder;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.utils.Location;

public class RequestDropItem extends L2GameClientPacket {
    private int _objectId;
    private long _count;
    private Location _loc;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _count = readQ();
        _loc = new Location(readD(), readD(), readD());
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_count < 1 || _loc.isNull()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!ServerConfig.ALLOW_DISCARDITEM) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestDropItem.Disallowed"));
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isSitting() || activeChar.isDropDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!activeChar.isInRangeSq(_loc, 22500) || Math.abs(_loc.z - activeChar.getZ()) > 50) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }

        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (PetDataHolder.getInstance().isPetControlItem(item.getItemId())) {
            for (final ItemInstance items : ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(activeChar.getObjectId(), ItemLocation.PET_INVENTORY)) //FIXME[K] - возможно стоит выполнять в синхронизации с инвентарем.
            {
                if (items.getLocation() == ItemLocation.PET_INVENTORY) {
                    activeChar.sendPacket(SystemMsg.THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELLTRADEDROP_PET_SUMMONING_ITEMS);
                    return;
                }
            }
        }

        if (!item.canBeDropped(activeChar, false)) {
            activeChar.sendPacket(SystemMsg.THAT_ITEM_CANNOT_BE_DISCARDED);
            return;
        }

        item.getTemplate().getHandler().dropItem(activeChar, item, _count, _loc);
    }
}
