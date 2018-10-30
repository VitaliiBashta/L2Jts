package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetPremiumItemList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.PremiumItem;
import org.mmocore.gameserver.utils.Log;

//FIXME [G1ta0] item-API
public final class RequestWithDrawPremiumItem extends L2GameClientPacket {
    private int _itemNum;
    private int _charId;
    private long _itemcount;

    @Override
    protected void readImpl() {
        _itemNum = readD();
        _charId = readD();
        _itemcount = readQ();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }
        if (_itemcount <= 0) {
            return;
        }

        if (activeChar.getObjectId() != _charId) {
            Log.audit("[WithDrawPremiumItem]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " active char is not correct for this operation!");
            return;
        }
        if (activeChar.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
            Log.audit("[WithDrawPremiumItem]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " this char not have premium item!");
            return;
        }
        if (activeChar.getWeightPenalty() >= 3 || activeChar.getInventoryLimit() * 0.8 <= activeChar.getInventory().getSize()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT);
            return;
        }
        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
            return;
        }

        final PremiumItem _item = activeChar.getPremiumAccountComponent().getPremiumItemList().get(_itemNum);
        if (_item == null) {
            return;
        }
        final boolean stackable = ItemTemplateHolder.getInstance().getTemplate(_item.getItemId()).isStackable();
        if (_item.getCount() < _itemcount) {
            return;
        }
        if (!stackable) {
            for (int i = 0; i < _itemcount; i++) {
                addItem(activeChar, _item.getItemId(), 1);
            }
        } else {
            addItem(activeChar, _item.getItemId(), _itemcount);
        }
        if (_itemcount < _item.getCount()) {
            activeChar.getPremiumAccountComponent().getPremiumItemList().get(_itemNum).updateCount(_item.getCount() - _itemcount);
            AccountBonusDAO.getInstance().updatePremiumItem(_itemNum, _item.getCount() - _itemcount, activeChar.getObjectId());
        } else {
            activeChar.getPremiumAccountComponent().getPremiumItemList().remove(_itemNum);
            AccountBonusDAO.getInstance().deletePremiumItem(_itemNum, activeChar.getObjectId());
        }

        if (activeChar.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
            activeChar.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
        } else {
            activeChar.sendPacket(new ExGetPremiumItemList(activeChar));
        }
    }

    private void addItem(final Player player, final int itemId, final long count) {
        player.getInventory().addItem(itemId, count);
        player.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
    }
}