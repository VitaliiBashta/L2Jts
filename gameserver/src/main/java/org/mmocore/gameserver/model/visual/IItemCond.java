package org.mmocore.gameserver.model.visual;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Created by Hack
 * Date: 08.06.2017 0:37
 */
public interface IItemCond {
    boolean check(Player player, ItemInstance item);
}
