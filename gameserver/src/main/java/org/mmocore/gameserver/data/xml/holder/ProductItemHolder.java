package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.xml.parser.ProductItemParser;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;
import org.mmocore.gameserver.templates.item.ProductItemTemplate.EventFlag;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author VISTALL, KilRoy
 */
public final class ProductItemHolder extends AbstractHolder {
    private static final ProductItemHolder INSTANCE = new ProductItemHolder();
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Comparator<ProductItemTemplate> COMPARATOR = new BuyComparator();
    private final Map<Integer, ProductItemTemplate> items = new TreeMap<>();
    private ProductItemHolder() {
    }

    public static ProductItemHolder getInstance() {
        return INSTANCE;
    }

    public void productBought(final Player player, final ProductItemTemplate product, final boolean db) {
        LOCK.lock();

        try {
            if (player != null)
                player.getPremiumAccountComponent().addBoughtProduct(product, db);

            product.incBuyCount();

            if (product.isFlag(EventFlag.BEST) || product.isFlag(EventFlag.EVENT))
                return;

            final ProductItemTemplate[] list = items.values().toArray(new ProductItemTemplate[items.size()]);

            Arrays.parallelSort(list, COMPARATOR);

            for (int i = 0; i < list.length; i++) {
                final ProductItemTemplate p = list[i];
                if (p == null)
                    continue;

                if (i < ServicesConfig.SERVICES_PREMIUMSHOP_BEST_PRODUCT_COUNT)
                    p.enableBestFlag();
                else
                    p.disableBestFlag();
            }
        } finally {
            LOCK.unlock();
        }
    }

    public void loadBestProduct(int productId) {
        if (Rnd.chance(60) && calcStartEndTime(productId))
            items.get(productId).enableBestFlag();
    }

    public void clearBoughtProducts() {
        LOCK.lock();

        try {
            getProducts().forEach(ProductItemTemplate::disableBestFlag);
        } finally {
            LOCK.unlock();
        }
    }

    public ProductItemTemplate getItem(final int productId) {
        return items.get(productId);
    }

    public Collection<ProductItemTemplate> getProducts() {
        return items.values();
    }

    public void addTemplate(final ProductItemTemplate template) {
        items.put(template.getProductId(), template);
    }

    public void reload() {
        LOCK.lock();

        try {
            clear();
            ProductItemParser.getInstance().load();
        } finally {
            LOCK.unlock();
        }
    }

    public boolean calcStartEndTime(int productId) {
        if (items.get(productId) == null)
            return false;
        if (items.get(productId).getStartSaleDate() * 1000 >= System.currentTimeMillis())
            return false;
        if (items.get(productId).getEndSaleDate() * 1000 <= System.currentTimeMillis())
            return false;
        return true;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public void log() {
        info("load " + size() + " product(Premium Shop) items.");
    }

    private static class BuyComparator implements Comparator<ProductItemTemplate>, Serializable {
        @Override
        public int compare(final ProductItemTemplate template1, final ProductItemTemplate template2) {
            return Integer.compare(template1.getBuyCount(), template2.getBuyCount());
        }
    }
}