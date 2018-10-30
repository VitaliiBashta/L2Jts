package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.LinkedList;
import java.util.List;

//ccccccdd[dd]
public class GMHennaInfo extends GameServerPacket {
    private final int str;
    private final int con;
    private final int dex;
    private final int _int;
    private final int wit;
    private final int men;
    private final List<DyeData> dyes;
    private final int classId;

    public GMHennaInfo(final Player cha) {
        str = cha.getDyeStatSTR();
        con = cha.getDyeStatCON();
        dex = cha.getDyeStatDEX();
        _int = cha.getDyeStatINT();
        wit = cha.getDyeStatWIT();
        men = cha.getDyeStatMEN();
        dyes = new LinkedList<>();
        for (final DyeData dyeData : cha.getDyes()) {
            if (dyeData != null)
                dyes.add(dyeData);
        }
        classId = cha.getPlayerClassComponent().getActiveClassId();
    }

    @Override
    protected final void writeData() {
        writeC(_int);
        writeC(str);
        writeC(con);
        writeC(men);
        writeC(dex);
        writeC(wit);
        writeD(3);
        writeD(dyes.size());
        for (final DyeData dye : dyes) {
            writeD(dye.dye_id);
            writeD(ArrayUtils.contains(dye.wear_class, classId));
        }
    }
}