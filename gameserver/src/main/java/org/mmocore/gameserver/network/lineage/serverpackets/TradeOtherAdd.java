package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInfo;

public class TradeOtherAdd extends GameServerPacket {
    private final ItemInfo temp;
    private final long amount;

    public TradeOtherAdd(final ItemInfo item, final long amount) {
        temp = item;
        this.amount = amount;
    }

    @Override
    protected final void writeData() {
        writeH(1); // item count
        writeH(temp.getItem().getType1());
        writeD(temp.getObjectId());
        writeD(temp.getItemId());
        writeQ(amount);
        writeH(temp.getItem().getType2ForPackets());
        writeH(temp.getCustomType1());
        writeD(temp.getItem().getBodyPart());
        writeH(temp.getEnchantLevel());
        writeH(0x00);
        writeH(temp.getCustomType2());
        writeH(temp.getAttackElement());
        writeH(temp.getAttackElementValue());
        writeH(temp.getDefenceFire());
        writeH(temp.getDefenceWater());
        writeH(temp.getDefenceWind());
        writeH(temp.getDefenceEarth());
        writeH(temp.getDefenceHoly());
        writeH(temp.getDefenceUnholy());
        writeH(AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED ? temp.getVariation1Id() : 0);
        writeH(AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED ? temp.getVariation2Id() : 0);
        writeH(0);
    }
}