package org.mmocore.gameserver.object.components.player.community;

public class Buff {
    private int id;
    private int level;

    public Buff(int id, int level) {
        this.id = id;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}