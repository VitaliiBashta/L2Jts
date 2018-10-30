package org.mmocore.gameserver.network.lineage.serverpackets;


import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.LinkedList;
import java.util.List;

public class HennaUnequipList extends GameServerPacket {
    private final int emptySlots;
    private final long adena;
    private final List<DyeData> dyes;
    private final int classId;

    public HennaUnequipList(final Player player) {
        adena = player.getAdena();
        emptySlots = player.getDyeEmptySlots();
        dyes = new LinkedList<>();
        for (final DyeData dyeData : player.getDyes()) {
            if (dyeData != null)
                dyes.add(dyeData);
        }
        classId = player.getPlayerClassComponent().getActiveClassId();
    }

    @Override
    protected final void writeData() {
        writeQ(adena);
        writeD(emptySlots);
        writeD(dyes.size());
        for (final DyeData dye : dyes) {
            writeD(dye.dye_id); //symbolid
            writeD(dye.dye_item_id); //itemid of dye
            writeQ(dye.need_count);
            writeQ(dye.wear_fee);
            writeD(ArrayUtils.contains(dye.wear_class, classId)); //meet the requirement or not
        }
    }
}