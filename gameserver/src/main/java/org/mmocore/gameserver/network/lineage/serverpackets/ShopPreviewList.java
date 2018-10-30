package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class ShopPreviewList extends GameServerPacket {
    private final int listId;
    private final List<ItemTemplate> itemList;
    private final long money;

    public ShopPreviewList(final BuyList list, final long currentMoney, final int expertiseIndex) {
        listId = list.getListId();
        money = currentMoney;
        itemList = new ArrayList<>();
        list.getProducts().stream().forEach(m -> {
            final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(m.getItemId());
            if ((item != null ? item.getCrystalType().externalOrdinal : 0) <= expertiseIndex) {
                itemList.add(item);
            }
        });
    }

    public static int getWearPrice(final ItemTemplate item) {
        switch (item.getItemGrade()) {
            case D:
                return 50;
            case C:
                return 100;
            //TODO: Не известно сколько на оффе стоит примерка B - S84 ранга.
            case B:
                return 200;
            case A:
                return 500;
            case S:
                return 1000;
            case S80:
                return 2000;
            case S84:
                return 2500;
            default:
                return 10;
        }
    }

    @Override
    protected final void writeData() {
        writeD(0x13c0); //?
        writeQ(money);
        writeD(listId);
        writeH(itemList.size());

        // item type2
        itemList.stream().filter(ItemTemplate::isEquipable).forEach(item -> {
            writeD(item.getItemId());
            writeH(item.getType2ForPackets()); // item type2
            writeH(item.isEquipable() ? item.getBodyPart() : 0x00);
            writeQ(getWearPrice(item));
        });
    }
}