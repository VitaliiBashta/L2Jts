package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class PrivateStoreManageListSell extends GameServerPacket {
    private final int sellerId;
    private final long adena;
    private final boolean pkg;
    private final List<TradeItem> sellList;
    private final List<TradeItem> sellList0;

    /**
     * Окно управления личным магазином продажи
     *
     * @param seller
     */
    public PrivateStoreManageListSell(final Player seller, final boolean pkg) {
        sellerId = seller.getObjectId();
        adena = seller.getAdena();
        this.pkg = pkg;
        sellList0 = seller.getSellList(this.pkg);
        sellList = new ArrayList<>();

        // Проверяем список вещей в инвентаре, если вещь остутствует - убираем из списка продажи
        for (final TradeItem si : sellList0) {
            if (si.getCount() <= 0) {
                sellList0.remove(si);
                continue;
            }

            ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
            if (item == null)
            //вещь недоступна, пробуем найти такую же по itemId
            {
                item = seller.getInventory().getItemByItemId(si.getItemId());
            }

            if (item == null || !item.canBeTraded(seller) || item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                sellList0.remove(si);
                continue;
            }

            //корректируем количество
            si.setCount(Math.min(item.getCount(), si.getCount()));
        }

        final ItemInstance[] items = seller.getInventory().getItems();
        // Проверяем список вещей в инвентаре, если вещь остутствует в списке продажи, добавляем в список доступных для продажи
        loop:
        for (final ItemInstance item : items) {
            if (item.canBeTraded(seller) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA) {
                for (final TradeItem si : sellList0) {
                    if (si.getObjectId() == item.getObjectId()) {
                        if (si.getCount() == item.getCount()) {
                            continue loop;
                        }
                        // Показывает остаток вещей для продажи
                        final TradeItem ti = new TradeItem(item);
                        ti.setCount(item.getCount() - si.getCount());
                        sellList.add(ti);
                        continue loop;
                    }
                }
                sellList.add(new TradeItem(item));
            }
        }
    }

    @Override
    protected final void writeData() {
        //section 1
        writeD(sellerId);
        writeD(pkg ? 1 : 0);
        writeQ(adena);

        //Список имеющихся вещей
        writeD(sellList.size());
        for (final TradeItem si : sellList) {
            writeItemInfo(si);
            writeQ(si.getStorePrice());
        }

        //Список вещей уже поставленых на продажу
        writeD(sellList0.size());
        for (final TradeItem si : sellList0) {
            writeItemInfo(si);
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
        }
    }
}