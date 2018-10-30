package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.DyeDataHolder;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HennaEquipList extends GameServerPacket {
    private final int emptySlots;
    private final long adena;
    private final List<DyeData> dyes = new ArrayList<>();
    private final int classId;

    public HennaEquipList(final Player player) {
        adena = player.getAdena();
        emptySlots = player.getDyeEmptySlots();
        classId = player.getPlayerClassComponent().getActiveClassId();

        final Collection<DyeData> dyesList = DyeDataHolder.getInstance().getDyes();
        dyes.addAll(dyesList.stream().filter(dye -> player.getInventory().getItemByItemId(dye.dye_item_id) != null).collect(Collectors.toList()));
    }

    @Override
    protected final void writeData() {
        writeQ(adena);
        writeD(emptySlots);
        if (!dyes.isEmpty()) {
            writeD(dyes.size());
            for (final DyeData dye : dyes) {
                writeD(dye.dye_id); //symbolid
                writeD(dye.dye_item_id); //itemid of dye
                writeQ(dye.need_count);
                writeQ(dye.wear_fee);
                writeD(ArrayUtils.contains(dye.wear_class, classId)); //meet the requirement or not
            }
        } else {
            writeD(0x01);
            writeD(0x00);
            writeD(0x00);
            writeQ(0x00);
            writeQ(0x00);
            writeD(0x00);
        }
    }
}