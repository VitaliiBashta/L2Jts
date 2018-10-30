package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

/**
 * @author VISTALL
 */
@FunctionalInterface
public interface OnReviveListener extends CharListener {
    public void onRevive(Creature actor);
}