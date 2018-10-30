package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public final class NihilInvaderChestInstance extends MonsterInstance {
    public NihilInvaderChestInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(true);
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect,
                                boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        super.reduceCurrentHp(1, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
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
    public boolean isLethalImmune() {
        return true;
    }
}