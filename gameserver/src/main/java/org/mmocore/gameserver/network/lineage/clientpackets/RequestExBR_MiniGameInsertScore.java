package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.manager.games.MiniGameScoreManager;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 19:55:45/25.05.2010
 */
public class RequestExBR_MiniGameInsertScore extends L2GameClientPacket {
    private int _score;

    @Override
    protected void readImpl() {
        _score = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || !ExtConfig.EX_JAPAN_MINIGAME) {
            return;
        }

        MiniGameScoreManager.getInstance().addScore(player, _score);
    }
}