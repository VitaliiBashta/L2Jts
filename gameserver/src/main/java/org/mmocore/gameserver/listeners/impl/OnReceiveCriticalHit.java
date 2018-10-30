package org.mmocore.gameserver.listeners.impl;

import org.mmocore.gameserver.listeners.Listener;
import org.mmocore.gameserver.object.Creature;

/**
 * Created by Hack
 * Date: 08.06.2017 0:19
 */
public interface OnReceiveCriticalHit extends Listener {
    void onAction(Creature attacker, Creature victim);
}
