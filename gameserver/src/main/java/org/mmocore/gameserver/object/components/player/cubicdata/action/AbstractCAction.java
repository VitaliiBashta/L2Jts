package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.AgathionComponent;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;

import java.util.Optional;

/**
 * Create by Mangol on 22.09.2015.
 */
abstract class AbstractCAction<T extends CubicComponent> implements ICActionCubic {
    private final LinkerFactory linker;
    private final T component;

    protected AbstractCAction(final T component) {
        this.component = component;
        this.linker = LinkerFactory.getInstance();
    }

    protected Optional<Creature> getTarget(final Player player, final Object... params) {
        return Optional.empty();
    }

    protected boolean isCond(final Creature creature, final Object... params) {
        return true;
    }

    protected void notifyNpcPower(final Creature target, final Player player, final int cubicPower) {
        if (target.isNpc()) {
            if (target.paralizeOnAttack(player)) {
                if (ServerConfig.PARALIZE_ON_RAID_DIFF) {
                    player.paralizeMe(target);
                }
            } else {
                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, null, cubicPower);
            }
        }
    }

    protected LinkerFactory getLinker() {
        return linker;
    }

    protected AgathionComponent getAgathionComponent() {
        return (AgathionComponent) component;
    }

    protected CubicComponent getCubicComponent() {
        return component;
    }

    protected DefaultCubicData getCubicTemplate() {
        return getCubicComponent().getTemplate();
    }

    protected Agathion getAgathionTemplate() {
        return getAgathionComponent().getTemplate();
    }

    protected boolean isCanAttackTarget(Creature attacker, Creature target) {
        Player targetPlayer = target.getPlayer();
        if (targetPlayer == null)
            return true;
        return targetPlayer.isAttackable(attacker, false, false);
    }

}
