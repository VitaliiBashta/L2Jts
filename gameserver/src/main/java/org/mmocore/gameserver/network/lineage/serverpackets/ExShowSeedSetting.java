package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.Manor.SeedData;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.manor.SeedProduction;

import java.util.List;

/**
 * format
 * dd[ddc[d]c[d]dddddddd]
 * dd[ddc[d]c[d]ddddQQQQ] - Gracia Final
 */
public class ExShowSeedSetting extends GameServerPacket {
    private final int manorId;
    private final int count;
    private final long[] seedData; // data to send, size:_count*12

    public ExShowSeedSetting(final int manorId) {
        this.manorId = manorId;
        final Castle c = ResidenceHolder.getInstance().getResidence(Castle.class, manorId);
        final List<SeedData> seeds = Manor.getInstance().getSeedsForCastle(manorId);
        count = seeds.size();
        seedData = new long[count * 12];
        int i = 0;
        for (final SeedData s : seeds) {
            seedData[i * 12 + 0] = s.getId();
            seedData[i * 12 + 1] = s.getLevel();
            seedData[i * 12 + 2] = s.getReward(1);
            seedData[i * 12 + 3] = s.getReward(2);
            seedData[i * 12 + 4] = s.getSeedLimit();
            seedData[i * 12 + 5] = Manor.getInstance().getSeedBuyPrice(s.getId());
            final int price = Manor.getInstance().getSeedBasicPrice(s.getId());
            seedData[i * 12 + 6] = price * 60 / 100;
            seedData[i * 12 + 7] = price * 10;
            SeedProduction seedPr = c.getSeed(s.getId(), CastleManorManager.PERIOD_CURRENT);
            if (seedPr != null) {
                seedData[i * 12 + 8] = seedPr.getStartProduce();
                seedData[i * 12 + 9] = seedPr.getPrice();
            } else {
                seedData[i * 12 + 8] = 0;
                seedData[i * 12 + 9] = 0;
            }
            seedPr = c.getSeed(s.getId(), CastleManorManager.PERIOD_NEXT);
            if (seedPr != null) {
                seedData[i * 12 + 10] = seedPr.getStartProduce();
                seedData[i * 12 + 11] = seedPr.getPrice();
            } else {
                seedData[i * 12 + 10] = 0;
                seedData[i * 12 + 11] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeData() {
        writeD(manorId); // manor id
        writeD(count); // size

        for (int i = 0; i < count; i++) {
            writeD((int) seedData[i * 12 + 0]); // seed id
            writeD((int) seedData[i * 12 + 1]); // level

            writeC(1);
            writeD((int) seedData[i * 12 + 2]); // reward 1 id

            writeC(1);
            writeD((int) seedData[i * 12 + 3]); // reward 2 id

            writeD((int) seedData[i * 12 + 4]); // next sale limit
            writeD((int) seedData[i * 12 + 5]); // price for castle to produce 1
            writeD((int) seedData[i * 12 + 6]); // min seed price
            writeD((int) seedData[i * 12 + 7]); // max seed price

            writeQ(seedData[i * 12 + 8]); // today sales
            writeQ(seedData[i * 12 + 9]); // today price
            writeQ(seedData[i * 12 + 10]); // next sales
            writeQ(seedData[i * 12 + 11]); // next price
        }
    }
}