package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.mmocore.gameserver.object.Player;

import java.util.Optional;

/**
 * Create by Mangol
 */
@FunctionalInterface
public interface ICActionCubic {
    void useAction(final Player player);

    default Optional<CMasterAction> getMasterAction() {
        return Optional.empty();
    }

    default Optional<CBySkillAction> getBySkillAction() {
        return Optional.empty();
    }

    default Optional<CHealAction> getHealAction() {
        return Optional.empty();
    }

    default Optional<CTargetAction> getTargetAction() {
        return Optional.empty();
    }
}
