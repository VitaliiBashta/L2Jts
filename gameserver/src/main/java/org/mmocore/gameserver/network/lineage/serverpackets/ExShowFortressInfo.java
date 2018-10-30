package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExShowFortressInfo extends GameServerPacket {
    private List<FortressInfo> infos = Collections.emptyList();

    public ExShowFortressInfo() {
        final List<Fortress> forts = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
        infos = new ArrayList<>(forts.size());
        for (final Fortress fortress : forts) {
            final Clan owner = fortress.getOwner();
            infos.add(new FortressInfo(owner == null ? StringUtils.EMPTY : owner.getName(), fortress.getId(),
                    fortress.getSiegeEvent().isInProgress(),
                    owner == null
                            ? 0 : (int) ((System.currentTimeMillis() - fortress.getOwnDate().toEpochSecond() * 1000) / 1000L)
            ));
        }
    }

    @Override
    protected final void writeData() {
        writeD(infos.size());
        for (final FortressInfo info : infos) {
            writeD(info.id);
            writeS(info.owner);
            writeD(info.status);
            writeD(info.siege);
        }
    }

    static class FortressInfo {
        public final int id;
        public final int siege;
        public final String owner;
        public final boolean status;

        public FortressInfo(final String owner, final int id, final boolean status, final int siege) {
            this.owner = owner;
            this.id = id;
            this.status = status;
            this.siege = siege;
        }
    }
}