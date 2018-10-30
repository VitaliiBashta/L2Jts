package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * sample
 * <p/>
 * 4b
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p/>
 * <p/>
 * format
 * cdd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinParty extends GameServerPacket {
    private final String requestorName;
    private final int itemDistribution;

    /**
     * @param int objectId of the target
     * @param int
     */
    public AskJoinParty(final String requestorName, final int itemDistribution) {
        this.requestorName = requestorName;
        this.itemDistribution = itemDistribution;
    }

    @Override
    protected final void writeData() {
        writeS(requestorName);
        writeD(itemDistribution);
    }
}