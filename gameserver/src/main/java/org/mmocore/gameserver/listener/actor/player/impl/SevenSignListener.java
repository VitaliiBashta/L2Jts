package org.mmocore.gameserver.listener.actor.player.impl;

import org.mmocore.gameserver.listener.actor.OnAttackListener;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

/**
 * @author KilRoy
 */
public class SevenSignListener implements OnAttackListener {
    private static final String MESSAGE = "Invalid value entered in SSQLoserTeleport value = ";

    @Override
    public void onAttack(Creature actor, Creature target) {
        if (actor.isPlayer() && target instanceof NpcInstance) {
            final NpcInstance npc = (NpcInstance) target;
            if (npc.isSevenSignsMonster()) {
                if ((SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod())) {
                    final int pcabal = SevenSigns.getInstance().getPlayerCabal(actor.getPlayer());
                    final int wcabal = SevenSigns.getInstance().getCabalHighestScore();
                    if (pcabal != wcabal && wcabal != SevenSigns.CABAL_NULL) {
                        actor.getPlayer().sendDebugMessage(MESSAGE + wcabal);
                        actor.getPlayer().teleToClosestTown();
                        actor.sendActionFailed();
                        return;
                    }
                }
            }
        }
    }
}