package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class HennaUnequipInfo extends GameServerPacket {
    private final int str;
    private final int con;
    private final int dex;
    private final int _int;
    private final int wit;
    private final int men;
    private final long adena;
    private final DyeData dye;

    private final int classId;

    public HennaUnequipInfo(final DyeData dye, final Player player) {
        this.dye = dye;
        adena = player.getAdena();
        str = player.getSTR();
        dex = player.getDEX();
        con = player.getCON();
        _int = player.getINT();
        wit = player.getWIT();
        men = player.getMEN();
        classId = player.getPlayerClassComponent().getActiveClassId();
    }

    @Override
    protected final void writeData() {
        writeD(dye.dye_id); //symbol Id
        writeD(dye.dye_item_id); //item id of dye

        writeQ(dye.need_count);
        writeQ(dye.wear_fee);
        writeD(ArrayUtils.contains(dye.wear_class, classId)); //able to draw or not 0 is false and 1 is true
        writeQ(adena);

        writeD(_int); //current INT
        writeC(_int + dye._int); //equip INT
        writeD(str); //current STR
        writeC(str + dye.str); //equip STR
        writeD(con); //current CON
        writeC(con + dye.con); //equip CON
        writeD(men); //current MEM
        writeC(men + dye.men); //equip MEM
        writeD(dex); //current DEX
        writeC(dex + dye.dex); //equip DEX
        writeD(wit); //current WIT
        writeC(wit + dye.wit); //equip WIT
    }
}