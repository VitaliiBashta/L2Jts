package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;

public class AttackRequest extends L2GameClientPacket {
    // cddddc
    private int _objectId;
    private int _originX;
    private int _originY;
    private int _originZ;
    private int _attackId;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _originX = readD();
        _originY = readD();
        _originZ = readD();
        _attackId = readC(); // 0 for simple click   1 for shift-click
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT && activeChar.getBotPunishComponent().isBeingPunished()) {
            if (activeChar.getBotPunishComponent().getPlayerPunish().canAttack() && activeChar.getBotPunishComponent().getBotPunishType() == Punish.ATTACKBAN) {
                activeChar.getBotPunishComponent().endPunishment();
            } else {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                return;
            }
        }

        activeChar.setActive();

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!activeChar.getPlayerAccess().CanAttack) {
            activeChar.sendActionFailed();
            return;
        }

        final GameObject target = activeChar.getVisibleObject(_objectId);
        if (target == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != target && !activeChar.getAggressionTarget().isDead()) {
            activeChar.sendActionFailed();
            return;
        }

        if (target.isPlayer() && (activeChar.isInBoat() || target.isInBoat())) {
            activeChar.sendActionFailed();
            return;
        }

        if (target.isPlayer() && (EventsConfig.TVTCantAttackOurTeam || EventsConfig.CFTCantAttackOurTeam)) {
            if (activeChar.isInActivePvpEvent() && (target.getPlayer().getTeam() == activeChar.getTeam())) {
                activeChar.sendMessage("Can not be Attack our team!");
                activeChar.sendActionFailed();
                return;
            }
        }

        if (activeChar.getTarget() != target) {
            target.onAction(activeChar, _attackId == 1);
            return;
        }

        if (target.getObjectId() != activeChar.getObjectId() && !activeChar.isInStoreMode() && !activeChar.isProcessingRequest()) {
            target.onForcedAttack(activeChar, _attackId == 1);
        }
    }
}