package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

/**
 * @author KilRoy
 */
public class RadarControl extends GameServerPacket {
    private final int x;
    private final int y;
    private final int z;
    private final RadarType radarType;
    private final RadarState radarState;

    public RadarControl(final RadarState radarState, final RadarType radarType, final Location loc) {
        this(radarState, radarType, loc.x, loc.y, loc.z);
    }

    public RadarControl(final RadarState radarState, final RadarType radarType, final int x, final int y, final int z) {
        this.radarState = radarState;
        this.radarType = radarType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected final void writeData() {
        writeD(radarState.getState());
        writeD(radarType.getId());
        writeD(x);
        writeD(y);
        writeD(z);
    }

    public static enum RadarType {
        ARROW(0x01), // Используется при установке радара\указателя (всегда при установке)
        FLAG_ON_MAP(0x02); // Используется при удалении радара, дабы отчистить клиент от указателя(всегда при удалении)

        private final int typeId;

        RadarType(final int typeId) {
            this.typeId = typeId;
        }

        public final int getId() {
            return typeId;
        }
    }

    public static enum RadarState {
        SHOW_RADAR(0x00),
        DELETE_ARROW(0x01),
        DELETE_FLAG(0x02);

        private final int state;

        RadarState(final int state) {
            this.state = state;
        }

        public final int getState() {
            return state;
        }
    }
}