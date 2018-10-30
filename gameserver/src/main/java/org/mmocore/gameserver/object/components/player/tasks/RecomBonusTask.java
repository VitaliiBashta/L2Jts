package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.network.lineage.serverpackets.ExVoteSystemInfo;
import org.mmocore.gameserver.object.Player;

public class RecomBonusTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public RecomBonusTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        player.getRecommendationComponent().setRecomBonusTime(0);
        player.sendPacket(new ExVoteSystemInfo(player.getRecommendationComponent()));
    }
}