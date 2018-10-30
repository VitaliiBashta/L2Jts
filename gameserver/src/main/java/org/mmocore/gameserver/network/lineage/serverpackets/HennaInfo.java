package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.LinkedList;
import java.util.List;

public class HennaInfo extends GameServerPacket {
    private final List<DyeData> dyes;
    private final int str, con, dex, _int, wit, men;
    private final int classId;

    public HennaInfo(final Player player) {
        str = player.getDyeStatSTR();
        con = player.getDyeStatCON();
        dex = player.getDyeStatDEX();
        _int = player.getDyeStatINT();
        wit = player.getDyeStatWIT();
        men = player.getDyeStatMEN();
        dyes = new LinkedList<>();
        for (final DyeData dyeData : player.getDyes()) {
            if (dyeData != null)
                dyes.add(dyeData);
        }
        classId = player.getPlayerClassComponent().getActiveClassId();
    }

    @Override
    protected final void writeData() {
        writeC(_int); //equip INT
        writeC(str); //equip STR
        writeC(con); //equip CON
        writeC(men); //equip MEM
        writeC(dex); //equip DEX
        writeC(wit); //equip WIT
        writeD(3); //interlude, slots?
        writeD(dyes.size());
        for (final DyeData dye : dyes) {
            writeD(dye.dye_id);
            writeD(ArrayUtils.contains(dye.wear_class, classId));
        }
    }
}