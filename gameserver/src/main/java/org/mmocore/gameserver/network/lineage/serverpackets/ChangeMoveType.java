package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

/**
 * 0000: 3e 2a 89 00 4c 01 00 00 00                         .|...
 * <p/>
 * format   dd
 */
public class ChangeMoveType extends GameServerPacket {
    private final int chaId;
    private final boolean running;
    private final EnvType envType;

    public ChangeMoveType(final Creature cha, final EnvType envType) {
        chaId = cha.getObjectId();
        running = cha.isRunning();
        this.envType = envType;
    }

    @Override
    protected final void writeData() {
        writeD(chaId);
        writeD(running ? 1 : 0);
        writeD(envType.ordinal());
    }

    public enum EnvType {
        ET_NONE(-1),
        ET_GROUND(0x00),
        ET_UNDERWATER(0x01),
        ET_AIR(0x02),
        ET_HOVER(0x03);

        private final int type;

        EnvType(final int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}