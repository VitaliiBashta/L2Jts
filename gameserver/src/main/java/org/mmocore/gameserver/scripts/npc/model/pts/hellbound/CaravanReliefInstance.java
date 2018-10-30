package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class CaravanReliefInstance extends NpcInstance {
    public CaravanReliefInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp, final boolean directHp, final boolean lethal) {
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    @Override
    protected void onDeath(final Creature killer) {
        HellboundManager.reduceConfidence(10);
        super.onDeath(killer);
    }
}