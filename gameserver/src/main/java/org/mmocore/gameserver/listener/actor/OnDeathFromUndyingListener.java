package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

/**
 * @author VISTALL
 * @date 20:50/13.03.2012
 */
public interface OnDeathFromUndyingListener extends CharListener {
    void onDeathFromUndying(final Creature actor, final Creature killer);
}
