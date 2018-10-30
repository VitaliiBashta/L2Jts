package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

@FunctionalInterface
public interface OnAttackListener extends CharListener {
    void onAttack(Creature actor, Creature target);
}
