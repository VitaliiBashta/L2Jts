package org.mmocore.gameserver.object.components.player.community;

import java.util.ArrayList;
import java.util.List;

public class BuffScheme {
    private final int id;
    private final String name;
    private final int priceId;
    private final long priceCount;
    private final List<Buff> buffIds = new ArrayList<>();

    public BuffScheme(final int id, final String name, final int priceId, final long priceCount) {
        this.id = id;
        this.name = name;
        this.priceId = priceId;
        this.priceCount = priceCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriceId() {
        return priceId;
    }

    public long getPriceCount() {
        return priceCount;
    }

    public void addBuff(final Buff buff) {
        buffIds.add(buff);
    }

    public List<Buff> getBuffIds() {
        return buffIds;
    }
}