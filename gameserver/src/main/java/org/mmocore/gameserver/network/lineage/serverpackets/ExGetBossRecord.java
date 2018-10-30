package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.List;

/**
 * Format: ch ddd [ddd]
 */
public class ExGetBossRecord extends GameServerPacket {
    private final List<BossRecordInfo> bossRecordInfo;
    private final int ranking;
    private final int totalPoints;

    public ExGetBossRecord(final int ranking, final int totalPoints, final List<BossRecordInfo> bossRecordInfo) {
        this.ranking = ranking; // char ranking
        this.totalPoints = totalPoints; // char total points
        this.bossRecordInfo = bossRecordInfo;
    }

    @Override
    protected final void writeData() {
        writeD(ranking); // char ranking
        writeD(totalPoints); // char total points

        writeD(bossRecordInfo.size()); // list size
        for (final BossRecordInfo info : bossRecordInfo) {
            writeD(info.bossId);
            writeD(info.points);
            writeD(info.unk1);// don`t know
        }
    }

    public static class BossRecordInfo {
        public final int bossId;
        public final int points;
        public final int unk1;

        public BossRecordInfo(final int bossId, final int points, final int unk1) {
            this.bossId = bossId;
            this.points = points;
            this.unk1 = unk1;
        }
    }
}