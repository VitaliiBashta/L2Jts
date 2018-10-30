package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

@FunctionalInterface
public interface OnMagicUseListener extends CharListener {
    void onMagicUse(Creature actor, SkillEntry skill, Creature target, boolean alt);
}
