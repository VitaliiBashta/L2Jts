package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class QueenAntLarvaInstance extends MonsterInstance {
    public QueenAntLarvaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        damage = getCurrentHp() - damage > 1 ? damage : getCurrentHp() - 1;
        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public boolean isImmobilized() {
        return true;
    }
}