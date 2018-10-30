package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class EventJackpotBagInstance extends NpcInstance {
    public EventJackpotBagInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp, final boolean directHp, final boolean lethal) {
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }
}