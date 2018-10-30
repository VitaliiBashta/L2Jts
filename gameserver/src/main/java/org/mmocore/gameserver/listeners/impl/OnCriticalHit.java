package org.mmocore.gameserver.listeners.impl;

import org.mmocore.gameserver.listeners.Listener;
import org.mmocore.gameserver.object.Creature;

/**
 * Created by Hack
 * Date: 30.05.2017 21:19
 */
public interface OnCriticalHit extends Listener {
    void onAction(Creature attacker, Creature victim);
}
