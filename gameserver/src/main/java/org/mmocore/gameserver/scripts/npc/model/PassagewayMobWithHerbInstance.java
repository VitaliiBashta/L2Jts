package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Моб при смерти дропает херб "Fiery Demon Blood"
 *
 * @author SYS
 */
public final class PassagewayMobWithHerbInstance extends MonsterInstance {
    public static final int FieryDemonBloodHerb = 9849;

    public PassagewayMobWithHerbInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void calculateRewards(Creature lastAttacker) {
        if (lastAttacker == null) {
            return;
        }

        super.calculateRewards(lastAttacker);

        if (lastAttacker.isPlayable()) {
            dropItem(lastAttacker.getPlayer(), FieryDemonBloodHerb, 1);
        }
    }
}