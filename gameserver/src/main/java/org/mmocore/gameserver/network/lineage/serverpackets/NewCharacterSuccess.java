package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.SettingHolder;
import org.jts.dataparser.data.holder.setting.model.NewPlayerBaseStat;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.List;

public class NewCharacterSuccess extends GameServerPacket {
    public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();
    private static final List<NewPlayerBaseStat> newPlayerBaseStats = SettingHolder.getInstance().getNewPlayerBaseStats();

    @Override
    protected final void writeData() {
        writeD(newPlayerBaseStats.size());
        for (final NewPlayerBaseStat baseStat : newPlayerBaseStats) {
            writeD(baseStat.getRaceId());
            writeD(baseStat.getClassId());
            writeD(baseStat.getMaxSTR());
            writeD(baseStat.getBaseSTR());
            writeD(baseStat.getMinSTR());
            writeD(baseStat.getMaxDEX());
            writeD(baseStat.getBaseDEX());
            writeD(baseStat.getMinDEX());
            writeD(baseStat.getMaxCON());
            writeD(baseStat.getBaseCON());
            writeD(baseStat.getMinCON());
            writeD(baseStat.getMaxINT());
            writeD(baseStat.getBaseINT());
            writeD(baseStat.getMinINT());
            writeD(baseStat.getMaxWIT());
            writeD(baseStat.getBaseWIT());
            writeD(baseStat.getMinWIT());
            writeD(baseStat.getMaxMEN());
            writeD(baseStat.getBaseMEN());
            writeD(baseStat.getMinMEN());
        }
    }
}