package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: chddd
 * <p/>
 * d: Arena
 * d: Answer
 */
public final class RequestExCubeGameReadyAnswer extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameReadyAnswer.class);

    int _arena;
    int _answer;

    @Override
    protected void readImpl() {
        _arena = readD() + 1;
        _answer = readD();
    }

    @Override
    public void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }
        switch (_answer) {
            case 0:
                // Cancel
                break;
            case 1:
                // OK or Time Over
                HandysBlockCheckerManager.getInstance().increaseArenaVotes(_arena);
                break;
            default:
                _log.warn("Unknown Cube Game Answer ID: " + _answer);
                break;
        }
    }
}
