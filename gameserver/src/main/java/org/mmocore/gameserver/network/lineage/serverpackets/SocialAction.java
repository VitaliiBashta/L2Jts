package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class SocialAction extends GameServerPacket {
    // Это для фрея.
    public static final int UNKNOW = 1;
    public static final int GREETING = 2;
    public static final int VICTORY = 3;
    public static final int ADVANCE = 4;
    public static final int NO = 5;
    public static final int YES = 6;
    public static final int BOW = 7;
    public static final int UNAWARE = 8;
    public static final int WAITING = 9;
    public static final int LAUGH = 10;
    public static final int APPLAUD = 11;
    public static final int DANCE = 12;
    public static final int SORROW = 13;
    public static final int CHARM = 14;
    public static final int SHYNESS = 15;
    public static final int COUPLE_BOW = 16;
    public static final int COUPLE_HIGH_FIVE = 17;
    public static final int COUPLE_DANCE = 18;
    public static final int LEVEL_UP = 2122;
    public static final int GIVE_HERO = 20016;
    private final int playerId;
    private final int actionId;

    public SocialAction(final int playerId, final int actionId) {
        this.playerId = playerId;
        this.actionId = actionId;
    }

    @Override
    protected final void writeData() {
        writeD(playerId);
        writeD(actionId);
    }
}