package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class ExBlockUpSetState extends GameServerPacket {
    private int blockUpStateType;
    private int timeLeft;
    private int bluePoints;
    private int redPoints;
    private boolean isRedTeam;
    private int objectId;
    private int playerPoints;

    public ExBlockUpSetState(final int timeLeft, final int bluePoints, final int redPoints, final boolean isRedTeam, final Player player, final int playerPoints) {
        this.timeLeft = timeLeft;
        this.bluePoints = bluePoints;
        this.redPoints = redPoints;
        this.isRedTeam = isRedTeam;
        this.objectId = player.getObjectId();
        this.playerPoints = playerPoints;
        blockUpStateType = 0;
    }

    public ExBlockUpSetState(final int timeLeft, final int bluePoints, final int redPoints) {
        this.timeLeft = timeLeft;
        this.bluePoints = bluePoints;
        this.redPoints = redPoints;
        blockUpStateType = 2;
    }

    public ExBlockUpSetState(final boolean isRedTeamWin) {
        isRedTeam = isRedTeamWin;
        blockUpStateType = 1;
    }

    @Override
    protected void writeData() {
        writeD(blockUpStateType);
        switch (blockUpStateType) {
            case 0:
                writeD(timeLeft);
                writeD(bluePoints);
                writeD(redPoints);
                writeD(isRedTeam ? 0x01 : 0x00);
                writeD(objectId);
                writeD(playerPoints);
                break;
            case 1:
                writeD(isRedTeam ? 0x01 : 0x00);
                break;
            case 2:
                writeD(timeLeft);
                writeD(bluePoints);
                writeD(redPoints);
                break;
        }
    }
}