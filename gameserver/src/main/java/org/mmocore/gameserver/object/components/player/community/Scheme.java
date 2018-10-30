package org.mmocore.gameserver.object.components.player.community;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scheme {
    private String name;
    private Map<Integer, Integer> buffs = new LinkedHashMap<>();

    public Scheme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addBuff(int id, int level) {
        buffs.put(id, level);
    }

    public Map<Integer, Integer> getBuffs() {
        return buffs;
    }
}