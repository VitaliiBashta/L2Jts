package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

public class ExSendUIEvent extends NpcStringContainer {
    private final int objectId;
    private final boolean isHide;
    private final boolean isIncrease;
    private final int startTime;
    private final int endTime;

    public ExSendUIEvent(final Player player, final boolean isHide, final boolean isIncrease, final int startTime, final int endTime, final String... params) {
        this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
    }

    public ExSendUIEvent(final Player player, final boolean isHide, final boolean isIncrease, final int startTime, final int endTime, final NpcString npcString, final String... params) {
        super(npcString, params);
        objectId = player.getObjectId();
        this.isHide = isHide;
        this.isIncrease = isIncrease;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeD(isHide ? 0x01 : 0x00); // 0: show timer, 1: hide timer
        writeD(0x00); // unknown
        writeD(0x00); // unknown
        writeS(isIncrease ? "1" : "0"); // "0": count negative, "1": count positive
        writeS(String.valueOf(startTime / 60)); // timer starting minute(s)
        writeS(String.valueOf(startTime % 60)); // timer starting second(s)
        writeS(String.valueOf(endTime / 60)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
        writeS(String.valueOf(endTime % 60)); // timer length second(s) (timer will disappear 10 seconds before it ends)
        writeElements();
    }
}