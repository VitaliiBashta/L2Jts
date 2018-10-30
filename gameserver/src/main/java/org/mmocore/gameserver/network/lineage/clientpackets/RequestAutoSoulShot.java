package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.handler.items.IItemHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAutoSoulShot;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

/**
 * format:		chdd
 *
 * @param decrypt
 */
public class RequestAutoSoulShot extends L2GameClientPacket {
    private int _itemId;
    private boolean _type; // 1 = on : 0 = off;

    @Override
    protected void readImpl() {
        _itemId = readD();
        _type = readD() == 1;
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || activeChar.isDead()) {
            return;
        }
        final ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);
        if (item == null) {
            return;
        }
        final ItemAction[] allow_action = new ItemAction[]{ItemAction.action_fishingshot, ItemAction.action_soulshot, ItemAction.action_spiritshot, ItemAction.action_summon_soulshot, ItemAction.action_summon_spiritshot};
        if (!ArrayUtils.contains(allow_action, item.getTemplate().getActionType())) {
            Log.audit("[RequestAutoSoulShot]:", " : used bug player name - " + activeChar.getName() + " ip - " + activeChar.getIP() + " login - " + activeChar.getAccountName());
            return;
        }
        if (_type) {
            activeChar.addAutoSoulShot(_itemId);
            activeChar.sendPacket(new ExAutoSoulShot(_itemId, true));
            activeChar.sendPacket(new SystemMessage(SystemMsg.THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED).addItemName(item.getItemId()));
            final IItemHandler handler = item.getTemplate().getHandler();
            handler.useItem(activeChar, item, false);
            return;
        }
        activeChar.removeAutoSoulShot(_itemId);
        activeChar.sendPacket(new ExAutoSoulShot(_itemId, false));
        activeChar.sendPacket(new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(item.getItemId()));
    }
}