package org.mmocore.gameserver.scripts.events.custom;

import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 03.08.2016 5:45
 */
class RegisterListener implements OnAnswerListener {
    private Player player;
    private CustomInstantTeamEvent event;

    public RegisterListener(Player player, CustomInstantTeamEvent event) {
        this.player = player;
        this.event = event;
    }

    @Override
    public void sayYes() {
        event.registerPlayer(player);
    }
}
