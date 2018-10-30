package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.WarehouseType;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Log;

/**
 * Format: cdb, b - array of (dd)
 */
public class SendWareHouseDepositList extends L2GameClientPacket {
    private static final long _WAREHOUSE_FEE = 30; //TODO [G1ta0] hardcode price

    private int _count;
    private int[] _items;
    private long[] _itemQ;

    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[SendWareHouseDepositList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }

        _items = new int[_count];
        _itemQ = new long[_count];

        for (int i = 0; i < _count; i++) {
            _items[i] = readD();
            _itemQ[i] = readQ();
            if (_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i) {
                _count = 0;
                return;
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

        // Проверяем наличие npc и расстояние до него
        final NpcInstance whkeeper = activeChar.getLastNpc();
        if (whkeeper == null || !activeChar.isInRangeZ(whkeeper, activeChar.getInteractDistance(whkeeper))) {
            activeChar.sendPacket(SystemMsg.WAREHOUSE_IS_TOO_FAR);
            return;
        }

        final PcInventory inventory = activeChar.getInventory();
        final boolean privatewh = activeChar.getUsingWarehouseType() != WarehouseType.CLAN;
        final Warehouse warehouse;
        if (privatewh) {
            warehouse = activeChar.getWarehouse();
        } else {
            warehouse = activeChar.getClan().getWarehouse();
        }

        inventory.writeLock();
        warehouse.writeLock();
        try {
            int slotsleft = 0;
            long adenaDeposit = 0;

            if (privatewh) {
                slotsleft = activeChar.getWarehouseLimit() - warehouse.getSize();
            } else {
                slotsleft = activeChar.getClan().getWarehouseBonus() + OtherConfig.WAREHOUSE_SLOTS_CLAN - warehouse.getSize();
            }

            int items = 0;

            // Создаем новый список передаваемых предметов, на основе полученных данных
            for (int i = 0; i < _count; i++) {
                final ItemInstance item = inventory.getItemByObjectId(_items[i]);
                if (item == null || item.getCount() < _itemQ[i] || !item.canBeStored(activeChar, privatewh)) {
                    _items[i] = 0; // Обнуляем, вещь не будет передана
                    _itemQ[i] = 0L;
                    continue;
                }

                if (!item.isStackable() || warehouse.getItemByItemId(item.getItemId()) == null) // вещь требует слота
                {
                    if (slotsleft <= 0) // если слоты кончились нестекуемые вещи и отсутствующие стекуемые пропускаем
                    {
                        _items[i] = 0; // Обнуляем, вещь не будет передана
                        _itemQ[i] = 0L;
                        continue;
                    }
                    slotsleft--; // если слот есть то его уже нет
                }

                if (item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    adenaDeposit = _itemQ[i];
                }

                items++;
            }

            // Сообщаем о том, что слоты кончились
            if (slotsleft <= 0) {
                activeChar.sendPacket(SystemMsg.YOUR_WAREHOUSE_IS_FULL);
            }

            if (items == 0) {
                activeChar.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
                return;
            }

            // Проверяем, хватит ли у нас денег на уплату налога
            final long fee = Math.multiplyExact(items, _WAREHOUSE_FEE);

            if (fee + adenaDeposit > activeChar.getAdena()) {
                activeChar.sendPacket(SystemMsg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
                return;
            }

            if (!activeChar.reduceAdena(fee, true)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            for (int i = 0; i < _count; i++) {
                if (_items[i] == 0) {
                    continue;
                }

                final ItemInstance item = inventory.removeItemByObjectId(_items[i], _itemQ[i]);
                Log.items(activeChar, privatewh ? Log.WarehouseDeposit : Log.ClanWarehouseDeposit, item);
                warehouse.addItem(item);
            }
        } catch (ArithmeticException ae) {
            Log.audit("[SendWareHouseDepositList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        } finally {
            warehouse.writeUnlock();
            inventory.writeUnlock();
        }

        // Обновляем параметры персонажа
        activeChar.sendChanges();
        activeChar.sendPacket(SystemMsg.THE_TRANSACTION_IS_COMPLETE);
    }
}
