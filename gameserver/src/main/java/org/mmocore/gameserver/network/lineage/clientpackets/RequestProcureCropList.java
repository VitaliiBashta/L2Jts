package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
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

/**
 * Format: (ch) d [dddd]
 * d: size
 * [
 * d  obj id
 * d  item id
 * d  manor id
 * d  count
 * ]
 */
public class RequestProcureCropList extends L2GameClientPacket {
    private int _count;
    private int[] _items;
    private int[] _crop;
    private int[] _manor;
    private long[] _itemQ;

    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 20 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _items = new int[_count];
        _crop = new int[_count];
        _manor = new int[_count];
        _itemQ = new long[_count];
        for (int i = 0; i < _count; i++) {
            _items[i] = readD();
            _crop[i] = readD();
            _manor[i] = readD();
            _itemQ[i] = readQ();
            if (_crop[i] < 1 || _manor[i] < 1 || _itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i) {
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

        final int currentManorId = manor == null ? 0 : manor.getCastle().getId();

        long totalFee = 0;
        int slots = 0;
        long weight = 0;

        try {
            for (int i = 0; i < _count; i++) {
                final int objId = _items[i];
                final int cropId = _crop[i];
                final int manorId = _manor[i];
                final long count = _itemQ[i];

                final ItemInstance item = activeChar.getInventory().getItemByObjectId(objId);
                if (item == null || item.getCount() < count || item.getItemId() != cropId) {
                    return;
                }

                final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, manorId);
                if (castle == null) {
                    return;
                }

                final CropProcure crop = castle.getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
                if (crop == null || crop.getId() == 0 || crop.getPrice() == 0) {
                    return;
                }

                if (count > crop.getAmount()) {
                    return;
                }

                final long price = Math.multiplyExact(count, crop.getPrice());
                long fee = 0;
                if (currentManorId != 0 && manorId != currentManorId) {
                    fee = price * 5 / 100; // 5% fee for selling to other manor
                }

                totalFee = Math.addExact(totalFee, fee);

                final int rewardItemId = Manor.getInstance().getRewardItem(cropId, crop.getReward());

                final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(rewardItemId);
                if (template == null) {
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(count, template.getWeight()));
                if (!template.isStackable() || activeChar.getInventory().getItemByItemId(cropId) == null) {
                    slots++;
                }
            }
        } catch (ArithmeticException ae) {
            Log.audit("[ProcureCropList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
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

            if (activeChar.getInventory().getAdena() < totalFee) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            // Proceed the purchase
            for (int i = 0; i < _count; i++) {
                final int objId = _items[i];
                final int cropId = _crop[i];
                final int manorId = _manor[i];
                final long count = _itemQ[i];

                // check if player have correct items count
                final ItemInstance item = activeChar.getInventory().getItemByObjectId(objId);
                if (item == null || item.getCount() < count || item.getItemId() != cropId) {
                    continue;
                }

                final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, manorId);
                if (castle == null) {
                    continue;
                }

                final CropProcure crop = castle.getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
                if (crop == null || crop.getId() == 0 || crop.getPrice() == 0) {
                    continue;
                }

                if (count > crop.getAmount()) {
                    continue;
                }

                final int rewardItemId = Manor.getInstance().getRewardItem(cropId, crop.getReward());
                final long sellPrice = count * crop.getPrice();
                final long rewardPrice = ItemTemplateHolder.getInstance().getTemplate(rewardItemId).getReferencePrice();

                if (rewardPrice == 0) {
                    continue;
                }

                final double reward = (double) sellPrice / rewardPrice;
                final long rewardItemCount = (long) reward + (Rnd.nextDouble() <= reward % 1 ? 1 : 0); // дробную часть округляем с шансом пропорционально размеру дробной части

                if (rewardItemCount < 1) {
                    final SystemMessage sm = new SystemMessage(SystemMsg.FAILED_IN_TRADING_S2_OF_S1_CROPS);
                    sm.addItemName(cropId);
                    sm.addNumber(count);
                    activeChar.sendPacket(sm);
                    continue;
                }

                long fee = 0;
                if (currentManorId != 0 && manorId != currentManorId) {
                    fee = sellPrice * 5 / 100; // 5% fee for selling to other manor
                }

                if (!activeChar.getInventory().destroyItemByObjectId(objId, count)) {
                    continue;
                }

                if (!activeChar.reduceAdena(fee, false)) {
                    final SystemMessage sm = new SystemMessage(SystemMsg.FAILED_IN_TRADING_S2_OF_S1_CROPS);
                    sm.addItemName(cropId);
                    sm.addNumber(count);
                    activeChar.sendPacket(sm, SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    continue;
                }

                crop.setAmount(crop.getAmount() - count);
                castle.updateCrop(crop.getId(), crop.getAmount(), CastleManorManager.PERIOD_CURRENT);
                castle.addToTreasuryNoTax(fee, false, false);

                if (activeChar.getInventory().addItem(rewardItemId, rewardItemCount) == null) {
                    continue;
                }

                activeChar.sendPacket(new SystemMessage(SystemMsg.TRADED_S2_OF_S1_CROPS).addItemName(cropId).addNumber(count), SystemMessage.removeItems(cropId, count), SystemMessage.obtainItems(rewardItemId, rewardItemCount, 0));
                if (fee > 0L) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES).addNumber(fee));
                }
            }
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.sendChanges();
    }
}