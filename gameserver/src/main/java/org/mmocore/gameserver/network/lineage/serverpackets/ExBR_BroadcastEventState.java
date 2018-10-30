package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExBR_BroadcastEventState extends GameServerPacket {
    public static final int APRIL_FOOLS = 20090401;
    public static final int EVAS_INFERNO = 20090801; // event state (0 - hide, 1 - show), day (1-14), percent (0-100)
    public static final int HALLOWEEN_EVENT = 20091031; // event state (0 - hide, 1 - show)
    public static final int RAISING_RUDOLPH = 20091225; // event state (0 - hide, 1 - show)
    public static final int LOVERS_JUBILEE = 20100214; // event state (0 - hide, 1 - show)
    public static final int APRIL_FOOLS_10 = 20100401; // event state (0 - hide, 1 - show)
    private final int eventId;
    private final int eventState;
    private int param0;
    private int param1;
    private int param2;
    private int param3;
    private int param4;
    private String param5;
    private String param6;

    public ExBR_BroadcastEventState(final int eventId, final int eventState) {
        this.eventId = eventId;
        this.eventState = eventState;
    }

    public ExBR_BroadcastEventState(final int eventId, final int eventState, final int param0, final int param1, final int param2, final int param3, final int param4, final String param5, final String param6) {
        this.eventId = eventId;
        this.eventState = eventState;
        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
    }

    @Override
    protected void writeData() {
        writeD(eventId);
        writeD(eventState);
        writeD(param0);
        writeD(param1);
        writeD(param2);
        writeD(param3);
        writeD(param4);
        writeS(param5);
        writeS(param6);
    }
}