package org.mmocore.gameserver.model.entity.events.actions;


import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.List;


/**
 * @author VISTALL
 * @date 16:25/06.01.2011
 */
public class PlaySoundAction implements EventAction {
    private final int _range;
    private final String _sound;
    private final PlaySound.Type _type;

    public PlaySoundAction(final int range, final String s, final PlaySound.Type type) {
        _range = range;
        _sound = s;
        _type = type;
    }

    @Override
    public void call(final Event event) {
        final GameObject object = event.getCenterObject();
        PlaySound packet = null;
        if (object != null) {
            packet = new PlaySound(_type, _sound, 1, object.getObjectId(), object.getLoc());
        } else {
            packet = new PlaySound(_type, _sound, 0, 0, 0, 0, 0);
        }

        final List<Player> players = event.broadcastPlayers(_range);
        for (final Player player : players) {
            if (player != null) {
                player.sendPacket(packet);
            }
        }
    }
}
