package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.MultiSellListContainer;
import org.mmocore.gameserver.model.base.MultiSellEntry;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class MultiSellList extends GameServerPacket {
    private final int page;
    private final int finished;
    private final int listId;
    private final List<MultiSellEntry> list;

    public MultiSellList(final MultiSellListContainer list, final int page, final int finished) {
        this.list = list.getEntries();
        listId = list.getListId();
        this.page = page;
        this.finished = finished;
    }

    //FIXME временная затычка, пока NCSoft не починят в клиенте отображение мультиселов где кол-во больше Integer.MAX_VALUE
    private static List<MultiSellIngredient> fixIngredients(final List<MultiSellIngredient> ingredients) {
        int needFix = 0;
        for (final MultiSellIngredient ingredient : ingredients) {
            if (ingredient.getItemCount() > Integer.MAX_VALUE) {
                needFix++;
            }
        }

        if (needFix == 0) {
            return ingredients;
        }

        MultiSellIngredient temp;
        final List<MultiSellIngredient> result = new ArrayList<>(ingredients.size() + needFix);
        for (MultiSellIngredient ingredient : ingredients) {
            ingredient = ingredient.clone();
            while (ingredient.getItemCount() > Integer.MAX_VALUE) {
                temp = ingredient.clone();
                temp.setItemCount(2000000000);
                result.add(temp);
                ingredient.setItemCount(ingredient.getItemCount() - 2000000000);
            }
            if (ingredient.getItemCount() > 0) {
                result.add(ingredient);
            }
        }

        return result;
    }

    @Override
    protected final void writeData() {
        writeD(listId); // list id
        writeD(page); // page
        writeD(finished); // finished
        writeD(OtherConfig.MULTISELL_SIZE); // size of pages
        writeD(list.size()); //list length
        List<MultiSellIngredient> ingredients;
        for (final MultiSellEntry ent : list) {
            ingredients = fixIngredients(ent.getIngredients());

            writeD(ent.getEntryId());
            writeC(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
            writeH(0x00); // unknown
            writeD(0x00); // инкрустация
            writeD(0x00); // инкрустация

            writeItemElements();

            writeH(ent.getProduction().size());
            writeH(ingredients.size());

            for (final MultiSellIngredient prod : ent.getProduction()) {
                final int itemId = prod.getItemId();
                final ItemTemplate template = itemId > 0 ? ItemTemplateHolder.getInstance().getTemplate(prod.getItemId()) : null;
                writeD(itemId);
                writeD(itemId > 0 ? template.getBodyPart() : 0);
                writeH(itemId > 0 ? template.getType2ForPackets() : 0);
                writeQ(prod.getItemCount());
                writeH(prod.getItemEnchant());
                writeD(0x00); // инкрустация
                writeD(0x00); // инкрустация
                writeItemElements(prod);
            }

            for (final MultiSellIngredient i : ingredients) {
                final int itemId = i.getItemId();
                final ItemTemplate item = itemId > 0 ? ItemTemplateHolder.getInstance().getTemplate(i.getItemId()) : null;
                writeD(itemId); //ID
                writeH(itemId > 0 ? item.getType2() : 0xffff);
                writeQ(i.getItemCount()); //Count
                writeH(i.getItemEnchant()); //Enchant Level
                writeD(0x00); // инкрустация
                writeD(0x00); // инкрустация
                writeItemElements(i);
            }
        }
    }
}