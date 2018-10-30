package org.mmocore.gameserver.scripts.npc.model.residences.clanhall;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.ai.residences.clanhall.MatchTrief;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:55/22.04.2011
 */
public class MatchTriefInstance extends CTBBossInstance {
    private long _massiveDamage;

    public MatchTriefInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp,
                                boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        if (_massiveDamage > System.currentTimeMillis()) {
            damage = 10000;
            if (Rnd.chance(10)) {
                ((MatchTrief) getAI()).hold();
            }
        } else if (getCurrentHpPercents() > 50) {
            if (attacker.isPlayer()) {
                damage = ((damage / getMaxHp()) / 0.05) * 100;
            } else {
                damage = ((damage / getMaxHp()) / 0.05) * 10;
            }
        } else if (getCurrentHpPercents() > 30) {
            if (Rnd.chance(90)) {
                if (attacker.isPlayer()) {
                    damage = ((damage / getMaxHp()) / 0.05) * 100;
                } else {
                    damage = ((damage / getMaxHp()) / 0.05) * 10;
                }
            } else {
                _massiveDamage = System.currentTimeMillis() + 5000L;
            }
        } else {
            _massiveDamage = System.currentTimeMillis() + 5000L;
        }

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }
}
