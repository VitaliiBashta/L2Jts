package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * <p/>
 * sample  b9 73 5d 30 49 01 00 00 00 00 00
 * <p/>
 * format dhd	(objectid, color, unk)
 * <p/>
 * color 	-xx -> -9 	red<p>
 * -8  -> -6	light-red<p>
 * -5	-> -3	yellow<p>
 * -2	-> 2    white<p>
 * 3	-> 5	green<p>
 * 6	-> 8	light-blue<p>
 * 9	-> xx	blue<p>
 * <p/>
 * usually the color equals the level difference to the selected target
 */
public class MyTargetSelected extends GameServerPacket {
    private final int objectId;
    private final int color;

    /**
     * @param int objectId of the target
     * @param int level difference to the target. name color is calculated from that
     */
    public MyTargetSelected(final int objectId, final int color) {
        this.objectId = objectId;
        this.color = color;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeH(color);
        writeD(0x00);
    }
}