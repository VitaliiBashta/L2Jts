package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.ManorManagerInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.mmocore.gameserver.utils.Log;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
// TODO
public class RequestProcureCrop extends L2GameClientPacket {
    // format: cddb
    private int _manorId;
    private int _count;
    private int[] _items;
    private long[] _itemQ;
    private List<CropProcure> _procureList = Collections.emptyList();

    @Override
    protected void readImpl() {
        _manorId = readD();
        _count = readD();
        if (_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[ProcureCrop]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }
        _items = new int[_count];
        _itemQ = new long[_count];
        for (int i = 0; i < _count; i++) {
            readD(); // service
            _items[i] = readD();
            _itemQ[i] = readQ();
            if (_itemQ[i] < 1) {
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

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        final GameObject target = activeChar.getTarget();

        final ManorManagerInstance manor = target instanceof ManorManagerInstance ? (ManorManagerInstance) target : null;
        if (!activeChar.isGM() && (manor == null || !activeChar.isInRangeZ(manor, activeChar.getInteractDistance(manor)))) {
            activeChar.sendActionFailed();
            return;
        }

        final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
        if (castle == null) {
            return;
        }

        int slots = 0;
        long weight = 0;

        try {
            for (int i = 0; i < _count; i++) {
                final int itemId = _items[i];
                final long count = _itemQ[i];

                final CropProcure crop = castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
                if (crop == null) {
                    return;
                }

                final int rewradItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
                long rewradItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));

                rewradItemCount = Math.multiplyExact(count, rewradItemCount);

                final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(rewradItemId);
                if (template == null) {
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(count, template.getWeight()));
                if (!template.isStackable() || activeChar.getInventory().getItemByItemId(itemId) == null) {
                    slots++;
                }
            }
        } catch (ArithmeticException ae) {
            Log.audit("[ProcureCrop]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        activeChar.getInventory().writeLock();
        try {
            if (!activeChar.getInventory().validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }

            if (!activeChar.getInventory().validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            _procureList = castle.getCropProcure(CastleManorManager.PERIOD_CURRENT);

            for (int i = 0; i < _count; i++) {
                final int itemId = _items[i];
                final long count = _itemQ[i];

                final int rewradItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
                long rewradItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));

                rewradItemCount = Math.multiplyExact(count, rewradItemCount);

                if (!activeChar.getInventory().destroyItemByItemId(itemId, count)) {
                    continue;
                }

                final ItemInstance item = activeChar.getInventory().addItem(rewradItemId, rewradItemCount);
                if (item == null) {
                    continue;
                }

                // Send Char Buy Messages
                activeChar.sendPacket(SystemMessage.obtainItems(rewradItemId, rewradItemCount, 0));
            }
        } catch (ArithmeticException ae) {
            Log.audit("[ProcureCrop]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            _count = 0;
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.sendChanges();
    }
}