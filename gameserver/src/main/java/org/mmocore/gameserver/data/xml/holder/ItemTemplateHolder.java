package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.support.CapsuledItemsContainer;

public final class ItemTemplateHolder extends AbstractHolder {
    private static final ItemTemplateHolder INSTANCE = new ItemTemplateHolder();
    private static final TIntObjectHashMap<CapsuledItemsContainer> capsule = new TIntObjectHashMap<CapsuledItemsContainer>();
    private final TIntObjectHashMap<ItemTemplate> items = new TIntObjectHashMap<>();
    private ItemTemplate[] allTemplates;

    private ItemTemplateHolder() {
    }

    public static ItemTemplateHolder getInstance() {
        return INSTANCE;
    }

    public void addItem(final ItemTemplate template) {
        items.put(template.getItemId(), template);
    }

    private void buildFastLookupTable() {
        int highestId = 0;

        for (final int id : items.keys()) {
            if (id > highestId) {
                highestId = id;
            }
        }

        allTemplates = new ItemTemplate[highestId + 1];

        for (TIntObjectIterator<ItemTemplate> iterator = items.iterator(); iterator.hasNext(); ) {
            iterator.advance();
            allTemplates[iterator.key()] = iterator.value();
        }
    }

    /**
     * Returns the item corresponding to the item ID
     *
     * @param id : int designating the item
     */
    public ItemTemplate getTemplate(final int id) {
        final ItemTemplate item = ArrayUtils.valid(allTemplates, id);
        if (item == null) {
            warn("Not defined item id : " + id + ", or out of range!", new Exception());
            return null;
        }
        return item;
    }

    public ItemTemplate[] getAllTemplates() {
        return allTemplates;
    }

    public void capsuledItemsPut(int key, CapsuledItemsContainer ei) {
        capsule.put(key, ei);
    }

    @Override
    protected void process() {
        buildFastLookupTable();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public void clear() {
        items.clear();
    }
}