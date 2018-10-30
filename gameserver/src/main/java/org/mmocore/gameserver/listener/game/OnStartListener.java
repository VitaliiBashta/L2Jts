package org.mmocore.gameserver.listener.game;

import org.mmocore.gameserver.listener.GameListener;

@FunctionalInterface
public interface OnStartListener extends GameListener {
    void onStart();
}
