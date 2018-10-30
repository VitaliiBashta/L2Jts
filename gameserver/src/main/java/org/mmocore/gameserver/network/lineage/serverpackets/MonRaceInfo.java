package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class MonRaceInfo extends GameServerPacket {
    private final int unknown1;
    private final int unknown2;
    private final NpcInstance[] monsters;
    private final int[][] speeds;

    public MonRaceInfo(final int unknown1, final int unknown2, final NpcInstance[] monsters, final int[][] speeds) {
        /*
         * -1 0 to initial the race
         * 0 15322 to start race
         * 13765 -1 in middle of race
         * -1 0 to end the race
         */
        this.unknown1 = unknown1;
        this.unknown2 = unknown2;
        this.monsters = monsters;
        this.speeds = speeds;
    }

    @Override
    protected final void writeData() {
        writeD(unknown1);
        writeD(unknown2);
        writeD(8);

        for (int i = 0; i < 8; i++) {
            //LOGGER.info.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
            writeD(monsters[i].getObjectId()); //npcObjectID
            writeD(monsters[i].getTemplate().npcId + 1000000); //npcID
            writeD(14107); //origin X
            writeD(181875 + 58 * (7 - i)); //origin Y
            writeD(-3566); //origin Z
            writeD(12080); //end X
            writeD(181875 + 58 * (7 - i)); //end Y
            writeD(-3566); //end Z
            writeF(monsters[i].getColHeight()); //coll. height
            writeF(monsters[i].getColRadius()); //coll. radius
            writeD(120); // ?? unknown
            for (int j = 0; j < 20; j++) {
                writeC(unknown1 == 0 ? speeds[i][j] : 0);
            }
            writeD(0);
            writeD(0x00); // ? GraciaFinal
        }
    }
}