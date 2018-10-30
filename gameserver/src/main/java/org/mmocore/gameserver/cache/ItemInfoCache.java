package org.mmocore.gameserver.cache;

import org.infinispan.Cache;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.world.World;

public class ItemInfoCache {
    private final Cache<Integer, ItemInfo> cache = GameServer.getInstance().getCacheManager().getCache(getClass().getName());

    private ItemInfoCache() {
    }

    public static ItemInfoCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void put(final ItemInstance item) {
        cache.put(item.getObjectId(), new ItemInfo(item));
    }

    /**
     * Получить информацию из кеша, по objecId предмета. Если игрок онлайн и все еще владеет этим предметом
     * информация будет обновлена.
     *
     * @param objectId - идентификатор предмета
     * @return возвращает описание вещи, или null если описания нет, или уже удалено из кеша
     */
    public ItemInfo get(final int objectId) {
        ItemInfo info = null;

        if (cache.containsKey(objectId)) {
            info = cache.get(objectId);
            final Player player = World.getPlayer(info.getOwnerId());

            ItemInstance item = null;

            if (player != null) {
                item = player.getInventory().getItemByObjectId(objectId);
            }

            if (item != null) {
                if (item.getItemId() == info.getItemId()) {
                    put(item);
                }
            }
        }

        return info;
    }

    private static class LazyHolder {
        private static final ItemInfoCache INSTANCE = new ItemInfoCache();
    }
}
