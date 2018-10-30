package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

@FunctionalInterface
public interface OnDeathListener extends CharListener {
    void onDeath(Creature actor, Creature killer);
}