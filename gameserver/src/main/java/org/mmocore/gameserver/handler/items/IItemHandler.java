package org.mmocore.gameserver.handler.items;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;

/**
 * Mother class of all itemHandlers.<BR><BR>
 * an IItemHandler implementation has to be stateless
 */
public interface IItemHandler {
    IItemHandler NULL = new IItemHandler() {
        @Override
        public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
            return false;
        }

        @Override
        public void dropItem(Player player, ItemInstance item, long count, Location loc) {
            if (item.isEquipped()) {
                player.getInventory().unEquipItem(item);
                player.sendUserInfo(true);
            }

            item = player.getInventory().removeItemByObjectId(item.getObjectId(), count);
            if (item == null) {
                player.sendActionFailed();
                return;
            }

            Log.items(player, Log.Drop, item);

            item.dropToTheGround(player, loc);
            player.disableDrop(1000);

            player.sendChanges();
        }

        @Override
        public boolean pickupItem(Playable playable, ItemInstance item) {
            return true;
        }

        @Override
        public int[] getItemIds() {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
    };

    /**
     * Launch task associated to the item.
     *
     * @param playable
     * @param item     : L2ItemInstance designating the item to use
     * @param ctrl
     */
    boolean useItem(Playable playable, ItemInstance item, boolean ctrl);

    /**
     * Check can drop or not
     *
     * @param playable
     * @param item
     * @param count
     * @param loc
     * @return can drop
     */
    void dropItem(Player player, ItemInstance item, long count, Location loc);

    /**
     * Check if can pick up item
     *
     * @param playable
     * @param item
     * @return
     */
    boolean pickupItem(Playable playable, ItemInstance item);

    /**
     * Returns the list of item IDs corresponding to the type of item.<BR><BR>
     * <B><I>Use :</I></U><BR>
     * This method is called at initialization to register all the item IDs automatically
     *
     * @return int[] designating all itemIds for a type of item.
     */
    int[] getItemIds();
}
