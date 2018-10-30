package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class HourlyTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public HourlyTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        // Каждый час в игре оповещаем персонажу сколько часов он играет.
        int hoursInGame = player.getHoursInGame();
        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_BEEN_PLAYING_FOR_AN_EXTENDED_PERIOD_OF_TIME_S1_PLEASE_CONSIDER_TAKING_A_BREAK).addNumber(hoursInGame));
        player.sendPacket(new SystemMessage(SystemMsg.YOU_OBTAINED_S1_RECOMMENDS).addNumber(player.getRecommendationComponent().addRecomLeft()));
    }
}