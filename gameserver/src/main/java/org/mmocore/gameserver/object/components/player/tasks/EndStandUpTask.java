package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.object.Player;

public class EndStandUpTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public EndStandUpTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        player.sittingTaskLaunched = false;
        player.setSitting(false);
        if (!player.getAI().setNextIntention()) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
}