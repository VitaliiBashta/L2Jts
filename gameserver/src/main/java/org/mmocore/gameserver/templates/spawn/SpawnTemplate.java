package org.mmocore.gameserver.templates.spawn;

import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 15:22/15.12.2010
 */
public class SpawnTemplate {
    private final PeriodOfDay periodOfDay;
    private final int count;
    private final int respawn;
    private final int respawnRandom;
    private final List<SpawnNpcInfo> npcList = new ArrayList<>(1);
    private final List<SpawnRange> spawnRangeList = new ArrayList<>(1);
    private SuperPoint superPoint;

    public SpawnTemplate(final PeriodOfDay periodOfDay, final int count, final int respawn, final int respawnRandom) {
        this.periodOfDay = periodOfDay;
        this.count = count;
        this.respawn = respawn;
        this.respawnRandom = respawnRandom;
    }

    //----------------------------------------------------------------------------------------------------------
    public void addSpawnRange(final SpawnRange range) {
        spawnRangeList.add(range);
    }

    public SpawnRange getSpawnRange(final int index) {
        return spawnRangeList.get(index);
    }

    //----------------------------------------------------------------------------------------------------------
    public void addNpc(final SpawnNpcInfo info) {
        npcList.add(info);
    }

    public SpawnNpcInfo getNpcInfo(final int index) {
        return npcList.get(index);
    }
    //----------------------------------------------------------------------------------------------------------

    public int getNpcSize() {
        return npcList.size();
    }

    public int getSpawnRangeSize() {
        return spawnRangeList.size();
    }

    public int getCount() {
        return count;
    }

    public int getRespawn() {
        return respawn;
    }

    public int getRespawnRandom() {
        return respawnRandom;
    }

    public PeriodOfDay getPeriodOfDay() {
        return periodOfDay;
    }

    public SuperPoint getSuperPoint() {
        return superPoint;
    }

    public void setSuperPoint(final SuperPoint superPoint) {
        this.superPoint = superPoint;
    }
}