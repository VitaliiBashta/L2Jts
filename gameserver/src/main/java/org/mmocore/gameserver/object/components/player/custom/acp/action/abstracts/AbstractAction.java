package org.mmocore.gameserver.object.components.player.custom.acp.action.abstracts;

import org.mmocore.gameserver.configuration.config.custom.AcpConfig;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.action.interfaces.IUseAction;
import org.mmocore.gameserver.templates.custom.AcpTemplate;

/**
 * Create by Mangol on 30.12.2015.
 */
public abstract class AbstractAction implements IUseAction {
    private final Player player;
    private final AcpComponent component;
    private final AcpTemplate template;

    public AbstractAction(final AcpComponent component) {
        this.player = component.getPlayer();
        this.template = component.getTemplate();
        this.component = component;
    }

    protected static boolean isCondition(final Player player) {
        if (player == null) {
            return false;
        } else if (!AcpConfig.allowAcp) {
            player.getAcpComponent().stopAllTask();
            return false;
        } else if (!AcpConfig.acpUseSiege && (player.isOnSiegeField() || player.isInZoneBattle())) {
            return false;
        } else if (!AcpConfig.acpUseDuel && player.isInDuel()) {
            return false;
        } else if (!AcpConfig.acpUseOlympiad && player.isInOlympiadMode()) {
            return false;
        } else if (!AcpConfig.acpUseEvent && player.getTeam() != TeamType.NONE) {
            return false;
        } else if (!AcpConfig.acpUseTransform && player.isTransformed()) {
            return false;
        } else if (!AcpConfig.acpUseWater && player.isInWater()) {
            return false;
        } else if (!AcpConfig.acpUseInvisible && player.isInvisible()) {
            return false;
        } else if (!AcpConfig.acpUseInvul && player.isInvul()) {
            return false;
        }
        return true;
    }

    protected Player getPlayer() {
        return player;
    }

    protected AcpComponent getComponent() {
        return component;
    }

    protected AcpTemplate getTemplate() {
        return template;
    }
}
