package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class RequestExRefundItem extends L2GameClientPacket {
    private int _count;
    private int[] _items;

    @Override
    protected void readImpl() {
        int _listId = readD();
        _count = readD();
        if (_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _items = new int[_count];
        for (int i = 0; i < _count; i++) {
            _items[i] = readD();
            if (ArrayUtils.indexOf(_items, _items[i]) < i) {
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

        final NpcInstance npc = activeChar.getLastNpc();

        final boolean isValidMerchant = npc != null && npc.isMerchantNpc() || npc != null && npc.isTeleportNpc();
        if (!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRangeZ(npc, activeChar.getInteractDistance(npc)))) {
            Log.audit("[ExRefundItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " isInRange > 200 || null");
            activeChar.sendActionFailed();
            return;
        }

        activeChar.getInventory().writeLock();
        try {
            int slots = 0;
            long weight = 0;
            long totalPrice = 0;

            final List<ItemInstance> refundList = new ArrayList<>();
            for (final int objId : _items) {
                final ItemInstance item = activeChar.getRefund().getItemByObjectId(objId);
                if (item == null) {
                    continue;
                }

                totalPrice = Math.addExact(totalPrice, Math.multiplyExact(item.getCount(), item.getReferencePrice()) / 2);
                weight = Math.addExact(weight, Math.multiplyExact(item.getCount(), item.getTemplate().getWeight()));

                if (!item.isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
                    slots++;
                }

                refundList.add(item);
            }

            if (refundList.isEmpty()) {
                activeChar.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
                activeChar.sendActionFailed();
                return;
            }

            if (!activeChar.getInventory().validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                activeChar.sendActionFailed();
                return;
            }

            if (!activeChar.getInventory().validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                activeChar.sendActionFailed();
                return;
            }

            if (!activeChar.reduceAdena(totalPrice)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                activeChar.sendActionFailed();
                return;
            }

            for (final ItemInstance item : refundList) {
                final ItemInstance refund = activeChar.getRefund().removeItem(item);
                Log.items(activeChar, Log.RefundReturn, refund);
                activeChar.getInventory().addItem(refund);
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
