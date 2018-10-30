package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillInfo extends GameServerPacket {
    private final SkillLearn learn;
    private final AcquireType type;
    private List<Require> reqs = Collections.emptyList();

    public AcquireSkillInfo(final AcquireType type, final SkillLearn learn) {
        this.type = type;
        this.learn = learn;
        if (learn.getItemId() != 0) {
            reqs = new ArrayList<>(1);
            reqs.add(new Require(99, learn.getItemId(), learn.getItemCount(), 50));
        }
    }

    @Override
    public void writeData() {
        writeD(learn.getId());
        writeD(learn.getLevel());
        writeD(learn.getCost()); // sp/rep
        writeD(type.ordinal());

        writeD(reqs.size()); //requires size

        for (final Require temp : reqs) {
            writeD(temp.type);
            writeD(temp.itemId);
            writeQ(temp.count);
            writeD(temp.unk);
        }
    }

    private static class Require {
        public final int itemId;
        public final long count;
        public final int type;
        public final int unk;

        public Require(final int pType, final int pItemId, final long pCount, final int pUnk) {
            itemId = pItemId;
            type = pType;
            count = pCount;
            unk = pUnk;
        }
    }
}