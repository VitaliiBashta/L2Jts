package org.mmocore.gameserver.listeners.impl;

import org.mmocore.gameserver.listeners.Listener;

/**
 * Created by Hack
 * Date: 22.05.2017 1:13
 */
public interface OnCastleDataLoaded extends Listener {
    void onLoad(int castleId);
}
