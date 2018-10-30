package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

@FunctionalInterface
public interface OnMagicHitListener extends CharListener {
    void onMagicHit(Creature actor, SkillEntry skill, Creature caster);
}
