package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 12:53/09.05.2011
 */
public class GiveItemAction implements EventAction {
    private final int _itemId;
    private final long _count;

    public GiveItemAction(final int itemId, final long count) {
        _itemId = itemId;
        _count = count;
    }

    @Override
    public void call(final Event event) {
        for (final Player player : event.itemObtainPlayers()) {
            event.giveItem(player, _itemId, _count);
        }
    }
}
