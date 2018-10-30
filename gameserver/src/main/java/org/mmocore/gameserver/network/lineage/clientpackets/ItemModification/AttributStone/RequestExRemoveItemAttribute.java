package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.AttributStone;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem.ExBaseAttributeCancelResult;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem.ExShowBaseAttributeCancelWindow;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemAttributes;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.utils.Log;

/**
 * @author SYS
 */
public class RequestExRemoveItemAttribute extends L2GameClientPacket {
    // Format: chd
    private int objectId;
    private int attributeId;

    @Override
    protected void readImpl() {
        objectId = readD();
        attributeId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isActionsDisabled() || activeChar.isInStoreMode() || activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        final PcInventory inventory = activeChar.getInventory();
        final ItemInstance itemToUnnchant = inventory.getItemByObjectId(objectId);

        if (itemToUnnchant == null) {
            Log.audit("[RemoveItemAttribute]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            activeChar.sendActionFailed();
            return;
        }

        final ItemAttributes set = itemToUnnchant.getAttributes();
        final Element element = Element.getElementById(attributeId);

        if (element == Element.NONE || set.getValue(element) <= 0) {
            activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, element), ActionFail.STATIC);
            return;
        }

        // проверка делается клиентом, если зашло в эту проверку знач чит
        if (!activeChar.reduceAdena(ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemToUnnchant), true)) {
            activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, element), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, ActionFail.STATIC);
            return;
        }

        boolean equipped = false;
        if (equipped = itemToUnnchant.isEquipped())
            activeChar.getInventory().unEquipItem(itemToUnnchant);

        itemToUnnchant.setAttributeElement(element, 0);
        itemToUnnchant.setJdbcState(JdbcEntityState.UPDATED);
        itemToUnnchant.update();

        if (equipped)
            activeChar.getInventory().equipItem(itemToUnnchant);

        activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToUnnchant));
        activeChar.sendPacket(new ExBaseAttributeCancelResult(true, itemToUnnchant, element));

        activeChar.updateStats();
    }
}