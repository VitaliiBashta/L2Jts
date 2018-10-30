package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.buylist.BuyList;

/**
 * @author Mangol
 * @TODO: Временный для гм шопа админки... запилить генерацию байлиста на лету.
 */
public class CustomBuyListHolder extends AbstractHolder {
    private static final CustomBuyListHolder INSTANCE = new CustomBuyListHolder();
    private final TIntObjectMap<BuyList> _lists = new TIntObjectHashMap<>();

    public static CustomBuyListHolder getInstance() {
        return INSTANCE;
    }

    public BuyList getBuyList(final int listId) {
        return _lists.get(listId);
    }

    public void addToBuyList(final int listId, final BuyList list) {
        _lists.put(listId, list);
    }

    @Override
    public int size() {
        return _lists.size();
    }

    @Override
    public void clear() {
        _lists.clear();
    }
}
