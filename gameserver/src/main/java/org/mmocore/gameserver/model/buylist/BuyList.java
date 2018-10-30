package org.mmocore.gameserver.model.buylist;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create by Mangol
 * За идею спасибо l2j
 */
public final class BuyList {
    private final int listId;
    private final Map<Integer, Product> products = new LinkedHashMap<>();
    private int npcId;

    public BuyList(final int listId, final int npcId) {
        this.listId = listId;
        this.npcId = npcId;
    }

    public BuyList(final int listId) {
        this.listId = listId;
    }

    public int getListId() {
        return listId;
    }

    public int getNpcId() {
        return npcId;
    }

    public Collection<Product> getProducts() {
        return products.values();
    }

    public Product getProductByItemId(final int itemId) {
        return products.get(itemId);
    }

    public void addProduct(final Product product) {
        products.put(product.getItemId(), product);
    }
}
