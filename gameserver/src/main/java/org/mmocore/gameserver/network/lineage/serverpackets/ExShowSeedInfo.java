package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.manor.SeedProduction;

import java.util.List;


/**
 * format
 * cddd[dddddc[d]c[d]]
 * cddd[dQQQdc[d]c[d]] - Gracia Final
 */
public class ExShowSeedInfo extends GameServerPacket {
    private final List<SeedProduction> seeds;
    private final int manorId;

    public ExShowSeedInfo(final int manorId, final List<SeedProduction> seeds) {
        this.manorId = manorId;
        this.seeds = seeds;
    }

    @Override
    protected void writeData() {
        writeC(0);
        writeD(manorId); // Manor ID
        writeD(0);
        writeD(seeds.size());
        for (final SeedProduction seed : seeds) {
            writeD(seed.getId()); // Seed id

            writeQ(seed.getCanProduce()); // Left to buy
            writeQ(seed.getStartProduce()); // Started amount
            writeQ(seed.getPrice()); // Sell Price
            writeD(Manor.getInstance().getSeedLevel(seed.getId())); // Seed Level

            writeC(1); // reward 1 Type
            writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id

            writeC(1); // reward 2 Type
            writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
        }
    }
}