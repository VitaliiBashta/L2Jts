package org.mmocore.gameserver.data.client.holder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.utils.Language;

import java.util.Collection;

/**
 * @author VISTALL
 * @date 20:44/02.09.2011
 */
public class ItemNameLineHolder extends AbstractHolder {
    private static final ItemNameLineHolder INSTANCE = new ItemNameLineHolder();
    private final Table<Language, Integer, ItemNameLine> names = HashBasedTable.create();

    public static ItemNameLineHolder getInstance() {
        return INSTANCE;
    }

    public ItemNameLine get(final Language lang, final int itemId) {
        return names.get(lang, itemId);
    }

    public void put(final Language lang, final ItemNameLine itemName) {
        names.put(lang, itemName.getItemId(), itemName);
    }

    public Collection<ItemNameLine> getItems(final Language lang) {
        return names.row(lang).values();
    }

    @Override
    public void log() {
        names.rowMap().entrySet().stream().forEach(entry -> info("load itemname line(s): " + entry.getValue().size() + " for lang: " + entry.getKey()));
    }

    @Override
    public int size() {
        return names.size();
    }

    @Override
    public void clear() {
        names.clear();
    }
}
