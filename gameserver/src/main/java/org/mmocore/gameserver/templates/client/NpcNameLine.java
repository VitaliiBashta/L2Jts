package org.mmocore.gameserver.templates.client;

public class NpcNameLine {
    private final int npcId;
    private final String description;

    private final String name;

    public NpcNameLine(final int npcId, final String name, final String description) {
        this.npcId = npcId;
        this.description = description;
        this.name = name;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}