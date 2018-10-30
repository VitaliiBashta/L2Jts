package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExBR_LoadEventTopRankers extends GameServerPacket {
    private final int eventId;
    private final int eventState;
    private final int playerCount;
    private final int bestScore;
    private final int myScore;

    public ExBR_LoadEventTopRankers(int eventId, int eventState, int playerCount, int bestScore, int myScore) {
        this.eventId = eventId;
        this.eventState = eventState;
        this.playerCount = playerCount;
        this.bestScore = bestScore;
        this.myScore = myScore;
    }

    @Override
    protected void writeData() {
        writeD(eventId);
        writeD(eventState);
        writeD(playerCount);
        writeD(bestScore);
        writeD(myScore);
    }
}