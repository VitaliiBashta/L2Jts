package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class HellboundRemnantInstance extends MonsterInstance {
    public HellboundRemnantInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect,
                                boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        super.reduceCurrentHp(Math.min(i, getCurrentHp() - 1), attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot,
                sendMessage, lethal);
    }

    public void onUseHolyWater(Creature user) {
        if (getCurrentHp() < 100) {
            doDie(user);
        }
    }
}