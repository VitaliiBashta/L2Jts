package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

/**
 * packet type id 0x37
 * format:		cddb, b - array if (ddd)
 */
public class RequestSellItem extends L2GameClientPacket {
    private int _count;
    private int[] _items; // object id
    private long[] _itemQ; // count

    @Override
    protected void readImpl() {
        int _listId = readD();
        _count = readD();
        if (_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _items = new int[_count];
        _itemQ = new long[_count];

        for (int i = 0; i < _count; i++) {
            _items[i] = readD(); // object id
            readD(); //item id
            _itemQ[i] = readQ(); // count
            if (_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i) {
                _count = 0;
                break;
            }
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0) {
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
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

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        final NpcInstance merchant = activeChar.getLastNpc();
        final boolean isValidMerchant = merchant != null && merchant.isMerchantNpc() || merchant != null && merchant.isTeleportNpc();
        if (!activeChar.isGM() && (merchant == null || !isValidMerchant || !activeChar.isInRangeZ(merchant, activeChar.getInteractDistance(merchant)))) {
            Log.audit("[SellItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " isInRange > 200 || null");
            activeChar.sendActionFailed();
            return;
        }

        activeChar.getInventory().writeLock();
        try {

            for (int i = 0; i < _count; i++) {
                final int objectId = _items[i];
                final long count = _itemQ[i];

                final ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
                if (item == null || item.getCount() < count || !item.canBeSold(activeChar)) {
                    continue;
                }

                final long price = Math.multiplyExact(item.getReferencePrice(), count) / 2;

                final ItemInstance refund = activeChar.getInventory().removeItemByObjectId(objectId, count);

                Log.items(activeChar, Log.RefundSell, refund);

                activeChar.addAdena(price);
                activeChar.getRefund().addItem(refund);
            }
        } catch (ArithmeticException ae) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.sendPacket(new ExBuySellList(activeChar, true));
        activeChar.sendChanges();
    }
}
