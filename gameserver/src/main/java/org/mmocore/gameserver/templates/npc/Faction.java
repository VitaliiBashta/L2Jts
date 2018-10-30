package org.mmocore.gameserver.templates.npc;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.mmocore.commons.utils.TroveUtils;

public class Faction {
    public static final String none = "none";
    public static final Faction NONE = new Faction(none);

    public final String factionId;
    public int factionRange;
    public TIntList ignoreId = TroveUtils.EMPTY_INT_LIST;

    public Faction(final String factionId) {
        this.factionId = factionId;
    }

    public String getName() {
        return factionId;
    }

    public int getRange() {
        return factionRange;
    }

    public void setRange(final int factionRange) {
        this.factionRange = factionRange;
    }

    public void addIgnoreNpcId(final int npcId) {
        if (ignoreId.isEmpty()) {
            ignoreId = new TIntArrayList();
        }
        ignoreId.add(npcId);
    }

    public boolean isIgnoreNpcId(final int npcId) {
        return ignoreId.contains(npcId);
    }

    public boolean isNone() {
        return factionId.isEmpty() || factionId.equals(none);
    }

    public boolean equals(final Faction faction) {
        return !(isNone() || !faction.getName().equalsIgnoreCase(factionId));
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return equals((Faction) o);
    }

    public String toString() {
        return isNone() ? none : factionId;
    }
}
