package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

@FunctionalInterface
public interface OnCurrentHpDamageListener extends CharListener {
    void onCurrentHpDamage(Creature actor, double damage, Creature attacker, SkillEntry skill);
}
