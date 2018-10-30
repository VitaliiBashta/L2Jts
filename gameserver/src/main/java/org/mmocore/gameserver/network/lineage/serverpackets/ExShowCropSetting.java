package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.manor.CropProcure;

import java.util.List;


/**
 * format
 * dd[ddc[d]c[d]ddddddcddc]
 * dd[ddc[d]c[d]ddddQQcQQc] - Gracia Final
 */
public class ExShowCropSetting extends GameServerPacket {
    private final int manorId;
    private final int count;
    private final long[] cropData; // data to send, size:_count*14

    public ExShowCropSetting(final int manorId) {
        this.manorId = manorId;
        final Castle c = ResidenceHolder.getInstance().getResidence(Castle.class, this.manorId);
        final List<Integer> crops = Manor.getInstance().getCropsForCastle(this.manorId);
        count = crops.size();
        cropData = new long[count * 14];
        int i = 0;
        for (final int cr : crops) {
            cropData[i * 14 + 0] = cr;
            cropData[i * 14 + 1] = Manor.getInstance().getSeedLevelByCrop(cr);
            cropData[i * 14 + 2] = Manor.getInstance().getRewardItem(cr, 1);
            cropData[i * 14 + 3] = Manor.getInstance().getRewardItem(cr, 2);
            cropData[i * 14 + 4] = Manor.getInstance().getCropPuchaseLimit(cr);
            cropData[i * 14 + 5] = 0; // Looks like not used
            cropData[i * 14 + 6] = Manor.getInstance().getCropBasicPrice(cr) * 60 / 100;
            cropData[i * 14 + 7] = Manor.getInstance().getCropBasicPrice(cr) * 10;
            CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
            if (cropPr != null) {
                cropData[i * 14 + 8] = cropPr.getStartAmount();
                cropData[i * 14 + 9] = cropPr.getPrice();
                cropData[i * 14 + 10] = cropPr.getReward();
            } else {
                cropData[i * 14 + 8] = 0;
                cropData[i * 14 + 9] = 0;
                cropData[i * 14 + 10] = 0;
            }
            cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
            if (cropPr != null) {
                cropData[i * 14 + 11] = cropPr.getStartAmount();
                cropData[i * 14 + 12] = cropPr.getPrice();
                cropData[i * 14 + 13] = cropPr.getReward();
            } else {
                cropData[i * 14 + 11] = 0;
                cropData[i * 14 + 12] = 0;
                cropData[i * 14 + 13] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeData() {
        writeD(manorId); // manor id
        writeD(count); // size

        for (int i = 0; i < count; i++) {
            writeD((int) cropData[i * 14 + 0]); // crop id
            writeD((int) cropData[i * 14 + 1]); // seed level

            writeC(1);
            writeD((int) cropData[i * 14 + 2]); // reward 1 id

            writeC(1);
            writeD((int) cropData[i * 14 + 3]); // reward 2 id

            writeD((int) cropData[i * 14 + 4]); // next sale limit
            writeD((int) cropData[i * 14 + 5]); // ???
            writeD((int) cropData[i * 14 + 6]); // min crop price
            writeD((int) cropData[i * 14 + 7]); // max crop price

            writeQ(cropData[i * 14 + 8]); // today buy
            writeQ(cropData[i * 14 + 9]); // today price
            writeC((int) cropData[i * 14 + 10]); // today reward
            writeQ(cropData[i * 14 + 11]); // next buy
            writeQ(cropData[i * 14 + 12]); // next price

            writeC((int) cropData[i * 14 + 13]); // next reward
        }
    }
}