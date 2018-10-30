package org.mmocore.gameserver.listener.game;

import org.mmocore.gameserver.listener.GameListener;

/**
 * @author VISTALL
 * @date 7:12/19.05.2011
 */
@FunctionalInterface
public interface OnSSPeriodListener extends GameListener {
    void onPeriodChange(int val);
}
