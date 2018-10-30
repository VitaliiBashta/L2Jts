package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

import java.util.List;


/**
 * Format: (ch) d[ddddd]
 * Живой пример с оффа:
 * FE 46 00 01 00 00 00 FE 1F 00 00 01 00 00 00 03 A9 FF FF E7 5C FF FF 60 D5 FF FF
 */
public class ExCursedWeaponLocation extends GameServerPacket {
    private final List<CursedWeaponInfo> cursedWeaponInfo;

    public ExCursedWeaponLocation(final List<CursedWeaponInfo> cursedWeaponInfo) {
        this.cursedWeaponInfo = cursedWeaponInfo;
    }

    @Override
    protected final void writeData() {
        if (cursedWeaponInfo.isEmpty())
            writeD(0);
        else {
            writeD(cursedWeaponInfo.size());
            for (final CursedWeaponInfo w : cursedWeaponInfo) {
                writeD(w.id);
                writeD(w.status);

                writeD(w.pos.x);
                writeD(w.pos.y);
                writeD(w.pos.z);
            }
        }
    }

    public static class CursedWeaponInfo {
        public final Location pos;
        public final int id;
        public final int status;

        public CursedWeaponInfo(final Location pos, final int id, final int status) {
            this.pos = pos;
            this.id = id;
            this.status = status;
        }
    }
}