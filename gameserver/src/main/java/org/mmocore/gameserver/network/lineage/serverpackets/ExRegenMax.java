package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExRegenMax extends GameServerPacket {
    public static final int POTION_HEALING_GREATER = 16457;
    public static final int POTION_HEALING_MEDIUM = 16440;
    public static final int POTION_HEALING_LESSER = 16416;
    private final double max;
    private final int count;
    private final int time;
    public ExRegenMax(final double max, final int count, final int time) {
        this.max = max * .66;
        this.count = count;
        this.time = time;
    }

    /**
     * Пример пакета - Пришло после использования Healing Potion (инфа для Interlude, в Kamael пакет не изменился)
     * <p/>
     * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 38 40 // Healing Potion
     * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 49 40 // Greater Healing Potion
     * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 20 40 // Lesser Healing Potion
     * <p/>
     * FE - тип
     * 01 00 - субтип
     * 01 00 00 00 - хз что
     * 0F 00 00 00 - count?
     * 03 00 00 00 - время?
     * 00 00 00 00 00 00 38 40 - максимум?
     */
    @Override
    protected void writeData() {
        writeD(1);
        writeD(count);
        writeD(time);
        writeF(max);
    }
}