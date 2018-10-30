package org.mmocore.gameserver.scripts.npc.model;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * У монстров в Seed of Annihilation список минионов может быть разный.
 *
 * @author Bonux
 */
public class SeedOfAnnihilationInstance extends MonsterInstance {
    private static final int[] BISTAKON_MOBS = new int[]{22750, 22751, 22752, 22753};
    private static final int[] COKRAKON_MOBS = new int[]{22763, 22764, 22765};
    private static final int[][] BISTAKON_MINIONS = new int[][]{
            {22746, 22746, 22746},
            {22747, 22747, 22747},
            {22748, 22748, 22748},
            {22749, 22749, 22749}};
    private static final int[][] COKRAKON_MINIONS = new int[][]{
            {22760, 22760, 22761},
            {22760, 22760, 22762},
            {22761, 22761, 22760},
            {22761, 22761, 22762},
            {22762, 22762, 22760},
            {22762, 22762, 22761}};

    public SeedOfAnnihilationInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private static void addMinions(int[] minions, NpcInstance template) {
        if (minions != null && minions.length > 0) {
            for (int id : minions) {
                template.getPrivatesList().createMinion(id, 1, 0, true);
            }
        }
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        if (ArrayUtils.contains(BISTAKON_MOBS, getNpcId())) {
            addMinions(BISTAKON_MINIONS[Rnd.get(BISTAKON_MINIONS.length)], this);
        } else if (ArrayUtils.contains(COKRAKON_MOBS, getNpcId())) {
            addMinions(COKRAKON_MINIONS[Rnd.get(COKRAKON_MINIONS.length)], this);
        }
    }

    @Override
    protected void onDeath(Creature killer) {
        if (hasPrivates()) {
            getPrivatesList().deletePrivates();
        }
        super.onDeath(killer);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}