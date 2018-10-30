package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class EnchantResult extends GameServerPacket {
    public static final EnchantResult SUCESS = new EnchantResult(0, 0, 0); // вещь заточилась
    //public static final EnchantResult FAILED = new EnchantResult(1, 0, 0); // вещь разбилась, требует указания получившихся кристаллов, в статичном виде не используется
    public static final EnchantResult CANCEL = new EnchantResult(2, 0, 0); // заточка невозможна
    public static final EnchantResult BLESSED_FAILED = new EnchantResult(3, 0, 0); // заточка не удалась, уровень заточки сброшен на 0
    public static final EnchantResult FAILED_NO_CRYSTALS = new EnchantResult(4, 0, 0);
    // вещь разбилась, но кристаллов не получилось (видимо для эвента, сейчас использовать невозможно, там заглушка)
    public static final EnchantResult ANCIENT_FAILED = new EnchantResult(5, 0, 0);
    private final int resultId, crystalId;
    private final long count;
    // заточка не удалась, уровень заточки не изменен (для Ancient Enchant Crystal из итем молла)

    public EnchantResult(final int resultId, final int crystalId, final long count) {
        this.resultId = resultId;
        this.crystalId = crystalId;
        this.count = count;
    }

    @Override
    protected final void writeData() {
        writeD(resultId);
        writeD(crystalId); // item id кристаллов
        writeQ(count); // количество кристаллов
    }
}