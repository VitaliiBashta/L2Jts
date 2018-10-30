package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

@FunctionalInterface
public interface OnAttackHitListener extends CharListener {
    void onAttackHit(Creature actor, Creature attacker);
}
