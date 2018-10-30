package org.mmocore.gameserver.listener.game;

import org.mmocore.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener {
    void onDay();

    void onNight();
}
