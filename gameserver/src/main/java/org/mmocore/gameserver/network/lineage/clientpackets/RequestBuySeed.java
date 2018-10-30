package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.ManorManagerInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.manor.SeedProduction;

/**
 * Format: cdd[dd]
 * c    // id (0xC5)
 * <p/>
 * d    // manor id
 * d    // seeds to buy
 * [
 * d    // seed id
 * d    // count
 * ]
 */
public class RequestBuySeed extends L2GameClientPacket {
    private int _count, _manorId;
    private int[] _items;
    private long[] _itemQ;

    @Override
    protected void readImpl() {
        _manorId = readD();
        _count = readD();

        if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }

        _items = new int[_count];
        _itemQ = new long[_count];

        for (int i = 0; i < _count; i++) {
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

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
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

        long totalPrice = 0;
        int slots = 0;
        long weight = 0;

        try {
            for (int i = 0; i < _count; i++) {
                final int seedId = _items[i];
                final long count = _itemQ[i];
                final long price;
                final long residual;

                final SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
                price = seed.getPrice();
                residual = seed.getCanProduce();

                if (price < 1) {
                    return;
                }

                if (residual < count) {
                    return;
                }

                totalPrice = Math.addExact(totalPrice, Math.multiplyExact(count, price));

                final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(seedId);
                if (item == null) {
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(count, item.getWeight()));
                if (!item.isStackable() || activeChar.getInventory().getItemByItemId(seedId) == null) {
                    slots++;
                }
            }

        } catch (ArithmeticException ae) {
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

            if (!activeChar.reduceAdena(totalPrice, true)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            // Adding to treasury for Manor Castle
            castle.addToTreasuryNoTax(totalPrice, false, true);

            // Proceed the purchase
            for (int i = 0; i < _count; i++) {
                final int seedId = _items[i];
                final long count = _itemQ[i];

                // Update Castle Seeds Amount
                final SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
                seed.setCanProduce(seed.getCanProduce() - count);
                castle.updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

                // Add item to Inventory and adjust update packet
                activeChar.getInventory().addItem(seedId, count);
                activeChar.sendPacket(SystemMessage.obtainItems(seedId, count, 0));
            }
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.sendChanges();
    }
}
