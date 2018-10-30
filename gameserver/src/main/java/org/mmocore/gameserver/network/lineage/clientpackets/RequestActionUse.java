package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.UserBasicActionHolder;
import org.jts.dataparser.data.holder.userbasicaction.UserBasicAction;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * packet type id 0x56
 * format:		cddc
 */
public class RequestActionUse extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestActionUse.class);
    private int _actionId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    protected void readImpl() {
        _actionId = readD();
        _ctrlPressed = readD() == 1;
        _shiftPressed = readC() == 1;
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT && activeChar.getBotPunishComponent().isBeingPunished()) {
            if (activeChar.getBotPunishComponent().getPlayerPunish().canPerformAction() && activeChar.getBotPunishComponent().getBotPunishType() == Punish.ACTIONBAN) {
                activeChar.getBotPunishComponent().endPunishment();
            } else {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                return;
            }
        }
        final Optional<UserBasicAction> actionOptional = UserBasicActionHolder.getInstance().getAction(_actionId);
        if (!actionOptional.isPresent()) {
            _log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
            activeChar.sendActionFailed();
            activeChar.kick();
            return;
        }
        final UserBasicAction action = actionOptional.get();
        final Optional<IUserBasicActionHandler> handler = action.getHandler();
        if (handler.isPresent()) {
            final Optional<GameObject> target = Optional.ofNullable(activeChar.getTarget());
            handler.get().useAction(activeChar, _actionId, action.getOption(), action.getUseSkill(), target, _ctrlPressed, _shiftPressed);
        }
        activeChar.sendActionFailed();
    }
}