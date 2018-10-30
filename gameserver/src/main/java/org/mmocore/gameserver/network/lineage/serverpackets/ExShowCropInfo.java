package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.manor.CropProcure;

import java.util.List;

/**
 * Format:
 * cddd[ddddcdc[d]c[d]]
 * cddd[dQQQcdc[d]c[d]] - Gracia Final
 */

public class ExShowCropInfo extends GameServerPacket {
    private final List<CropProcure> crops;
    private final int manorId;

    public ExShowCropInfo(final int manorId, final List<CropProcure> crops) {
        this.manorId = manorId;
        this.crops = crops;
    }

    @Override
    protected void writeData() {
        writeC(0);
        writeD(manorId); // Manor ID
        writeD(0);
        writeD(crops.size());
        for (final CropProcure crop : crops) {
            writeD(crop.getId()); // Crop id
            writeQ(crop.getAmount()); // Buy residual
            writeQ(crop.getStartAmount()); // Buy
            writeQ(crop.getPrice()); // Buy price
            writeC(crop.getReward()); // Reward
            writeD(Manor.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level

            writeC(1); // rewrad 1 Type
            writeD(Manor.getInstance().getRewardItem(crop.getId(), 1)); // Rewrad 1 Type Item Id

            writeC(1); // rewrad 2 Type
            writeD(Manor.getInstance().getRewardItem(crop.getId(), 2)); // Rewrad 2 Type Item Id
        }
    }
}