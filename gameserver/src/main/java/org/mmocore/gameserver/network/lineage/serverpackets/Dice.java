package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class Dice extends GameServerPacket {
    private final int playerId;
    private final int itemId;
    private final int number;
    private final int x;
    private final int y;
    private final int z;

    /**
     * 0xd4 Dice         dddddd
     *
     * @param _characters
     */
    public Dice(final int playerId, final int itemId, final int number, final int x, final int y, final int z) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected final void writeData() {
        writeD(playerId); // object id of player
        writeD(itemId); //	item id of dice (spade)  4625,4626,4627,4628
        writeD(number); // number rolled
        writeD(x); // x
        writeD(y); // y
        writeD(z); // z
    }
}