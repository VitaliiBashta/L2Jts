package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExEnchantSkillResult extends GameServerPacket {
    public static final GameServerPacket SUCCESS = new ExEnchantSkillResult(1);
    public static final GameServerPacket FAIL = new ExEnchantSkillResult(0);

    private final int result;

    public ExEnchantSkillResult(final int result) {
        this.result = result;
    }

    @Override
    protected void writeData() {
        writeD(result);
    }
}