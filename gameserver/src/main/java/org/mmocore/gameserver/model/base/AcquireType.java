package org.mmocore.gameserver.model.base;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.object.Player;

/**
 * Author: VISTALL
 * Date:  11:53/01.12.2010
 */
public enum AcquireType {
    NORMAL,   // 0
    FISHING,   // 1
    CLAN,      // 2
    SUB_UNIT,   // 3
    TRANSFORMATION, // 4
    CERTIFICATION,   // 5
    COLLECTION,       // 6
    TRANSFER_CARDINAL,  // 7
    TRANSFER_EVA_SAINTS, // 8
    TRANSFER_SHILLIEN_SAINTS, // 9
    FISHING_NON_DWARF; //10

    public static final AcquireType[] VALUES = AcquireType.values();

    public static AcquireType fishingType(final Player player) {
        return player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? FISHING : FISHING_NON_DWARF;
    }

    public static AcquireType transferType(final int classId) {
        switch (classId) {
            case 97:
                return TRANSFER_CARDINAL;
            case 105:
                return TRANSFER_EVA_SAINTS;
            case 112:
                return TRANSFER_SHILLIEN_SAINTS;
        }

        return null;
    }

    public int transferClassId() {
        switch (this) {
            case TRANSFER_CARDINAL:
                return 97;
            case TRANSFER_EVA_SAINTS:
                return 105;
            case TRANSFER_SHILLIEN_SAINTS:
                return 112;
        }

        return 0;
    }
}
