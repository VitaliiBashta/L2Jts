package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInfo;

public class TradeOwnAdd extends GameServerPacket {
    private final ItemInfo item;
    private final long amount;

    public TradeOwnAdd(final ItemInfo item, final long amount) {
        this.item = item;
        this.amount = amount;
    }

    @Override
    protected final void writeData() {
        writeH(1); // item count
        writeH(item.getItem().getType1());
        writeD(item.getObjectId());
        writeD(item.getItemId());
        writeQ(amount);
        writeH(item.getItem().getType2ForPackets());
        writeH(item.getCustomType1());
        writeD(item.getItem().getBodyPart());
        writeH(item.getEnchantLevel());
        writeH(0x00);
        writeH(item.getCustomType2());
        writeH(item.getAttackElement());
        writeH(item.getAttackElementValue());
        writeH(item.getDefenceFire());
        writeH(item.getDefenceWater());
        writeH(item.getDefenceWind());
        writeH(item.getDefenceEarth());
        writeH(item.getDefenceHoly());
        writeH(item.getDefenceUnholy());
        writeH(AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED ? item.getVariation1Id() : 0);
        writeH(AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED ? item.getVariation2Id() : 0);
        writeH(0);
    }
}