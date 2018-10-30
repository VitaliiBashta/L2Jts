package org.mmocore.gameserver.listener.actor;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.object.Creature;

public interface OnKillListener extends CharListener {
    void onKill(Creature actor, Creature victim);

    /**
     * FIXME [VISTALL]
     * Когда на игрока добавить OnKillListener, он не добавляется суммону, и нужно вручну добавлять
     * но при ресумоне, проследить трудно
     * Если возратить тру, то с убийцы будет братся игрок, и на нем вызывать onKill
     *
     * @return
     */
    boolean ignorePetOrSummon();
}
