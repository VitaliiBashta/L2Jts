package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * const EDGE_GRAY = 15;
 * const EDGE_BLUE = 12;
 * const EDGE_ORANGE = 11;
 * const EDGE_BUFFRED = 9;
 * const EDGE_RED = 8;
 * const EDGE_SSQGRAY = 13;
 * const EDGE_PVPGREEN = 14;
 */
public class ExSetCompassZoneCode extends GameServerPacket {
    public static final int ZONE_ALTERED = 8;
    public static final int ZONE_ALTERED2 = 9;
    public static final int ZONE_REMINDER = 10;
    public static final int ZONE_SIEGE = 11;
    public static final int ZONE_PEACE = 12;
    public static final int ZONE_SSQ = 13;
    public static final int ZONE_PVP = 14;
    public static final int ZONE_GENERAL_FIELD = 15;

    public static final int ZONE_PVP_FLAG = 1 << ZONE_PVP;
    public static final int ZONE_ALTERED_FLAG = 1 << ZONE_ALTERED;
    public static final int ZONE_SIEGE_FLAG = 1 << ZONE_SIEGE;
    public static final int ZONE_PEACE_FLAG = 1 << ZONE_PEACE;
    public static final int ZONE_SSQ_FLAG = 1 << ZONE_SSQ;

    private final int zone;

    public ExSetCompassZoneCode(final Player player) {
        this(player.getZoneMask());
    }

    public ExSetCompassZoneCode(final int zoneMask) {
        if ((zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG) {
            zone = ZONE_ALTERED;
        } else if ((zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG) {
            zone = ZONE_SIEGE;
        } else if ((zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG) {
            zone = ZONE_PVP;
        } else if ((zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG) {
            zone = ZONE_PEACE;
        } else if ((zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG) {
            zone = ZONE_SSQ;
        } else {
            zone = ZONE_GENERAL_FIELD;
        }
    }

    @Override
    protected final void writeData() {
        writeD(zone);
    }
}