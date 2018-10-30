package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.WarehouseType;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendWareHouseWithDrawList extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);

    private int _count;
    private int[] _items;
    private long[] _itemQ;

    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[SendWareHouseWithDrawList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }
        _items = new int[_count];
        _itemQ = new long[_count];
        for (int i = 0; i < _count; i++) {
            _items[i] = readD(); // item object id
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

        if (!activeChar.getPlayerAccess().UseWarehouse) {
            activeChar.sendActionFailed();
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

        final NpcInstance whkeeper = activeChar.getLastNpc();
        if (whkeeper == null || !activeChar.isInRange(whkeeper, activeChar.getInteractDistance(whkeeper))) {
            activeChar.sendPacket(SystemMsg.WAREHOUSE_IS_TOO_FAR);
            return;
        }

        final Warehouse warehouse;
        final String logType;

        if (activeChar.getUsingWarehouseType() == WarehouseType.PRIVATE) {
            warehouse = activeChar.getWarehouse();
            logType = Log.WarehouseWithdraw;
        } else if (activeChar.getUsingWarehouseType() == WarehouseType.CLAN) {
            logType = Log.ClanWarehouseWithdraw;
            boolean canWithdrawCWH = false;
            if (activeChar.getClan() != null) {
                if ((activeChar.getClanPrivileges() & Clan.CP_CL_WAREHOUSE_SEARCH) == Clan.CP_CL_WAREHOUSE_SEARCH
                        && (AllSettingsConfig.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || activeChar.isClanLeader()
                        || activeChar.getPlayerVariables().getBoolean(PlayerVariables.CAN_WAREHOUSE_WITHDRAW))) {
                    canWithdrawCWH = true;
                }
            }
            if (!canWithdrawCWH) {
                return;
            }

            warehouse = activeChar.getClan().getWarehouse();
        } else if (activeChar.getUsingWarehouseType() == WarehouseType.FREIGHT) {
            warehouse = activeChar.getFreight();
            logType = Log.FreightWithdraw;
        } else {
            _log.warn("Error retrieving a warehouse object for char " + activeChar.getName() + " - using warehouse type: " + activeChar.getUsingWarehouseType());
            return;
        }

        final PcInventory inventory = activeChar.getInventory();

        inventory.writeLock();
        warehouse.writeLock();
        try {
            long weight = 0;
            int slots = 0;

            for (int i = 0; i < _count; i++) {
                final ItemInstance item = warehouse.getItemByObjectId(_items[i]);
                if (item == null || item.getCount() < _itemQ[i]) {
                    activeChar.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(item.getTemplate().getWeight(), _itemQ[i]));
                if (!item.isStackable() || inventory.getItemByItemId(item.getItemId()) == null) // вещь требует слота
                {
                    slots++;
                }
            }

            if (!activeChar.getInventory().validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            if (!activeChar.getInventory().validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }

            for (int i = 0; i < _count; i++) {
                final ItemInstance item = warehouse.removeItemByObjectId(_items[i], _itemQ[i]);
                Log.items(activeChar, logType, item);
                activeChar.getInventory().addItem(item);
            }
        } catch (ArithmeticException ae) {
            Log.audit("[SendWareHouseWithDrawList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        } finally {
            warehouse.writeUnlock();
            inventory.writeUnlock();
        }

        activeChar.sendChanges();
        activeChar.sendPacket(SystemMsg.THE_TRANSACTION_IS_COMPLETE);
    }
}
