package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;

/**
 * @author VISTALL
 * @date 9:37/15.04.2011
 */
public interface OnAnswerListener extends PlayerListener {
    void sayYes();

    default void sayNo() {
    }

    /**
     * От тупого хака
     */
    default long expireTime() {
        return 0;
    }
}
