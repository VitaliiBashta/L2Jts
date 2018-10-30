package org.mmocore.gameserver.templates.item.support;

import gnu.trove.set.TIntSet;
import org.mmocore.gameserver.model.entity.SevenSigns;

/**
 * @author VISTALL
 * @date 14:02/14.07.2011
 */
public class MerchantGuard {
    private final int itemId;
    private final int npcId;
    private final int max;
    private final TIntSet ssq;

    public MerchantGuard(final int itemId, final int npcId, final int max, final TIntSet ssq) {
        this.itemId = itemId;
        this.npcId = npcId;
        this.max = max;
        this.ssq = ssq;
    }

    public int getItemId() {
        return itemId;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getMax() {
        return max;
    }

    public boolean isValidSSQPeriod() {
        return SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION
                && ssq.contains(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE));
    }
}
