package org.mmocore.gameserver.scripts.npc.model.residences.clanhall;

import org.mmocore.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:37/22.04.2011
 */
public class MatchBerserkerInstance extends CTBBossInstance {
    public MatchBerserkerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        if (attacker.isPlayer()) {
            damage = ((damage / getMaxHp()) / 0.05) * 1000;
        } else {
            damage = ((damage / getMaxHp()) / 0.05) * 10;
        }

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }
}
