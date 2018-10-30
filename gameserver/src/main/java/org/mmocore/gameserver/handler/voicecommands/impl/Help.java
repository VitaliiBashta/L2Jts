package org.mmocore.gameserver.handler.voicecommands.impl;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.PlayerUtils;
import org.mmocore.gameserver.world.World;

/**
 * @Author: Abaddon
 */
public class Help implements IVoicedCommandHandler {
    private final String[] _commandList = {"help", "exp", "whereis"};

    @Override
    public boolean useVoicedCommand(String command, final Player activeChar, final String args) {
        command = command.intern();
        if ("help".equalsIgnoreCase(command)) {
            return help(command, activeChar, args);
        }
        if ("whereis".equalsIgnoreCase(command)) {
            return whereis(command, activeChar, args);
        }
        if ("exp".equalsIgnoreCase(command)) {
            return exp(command, activeChar, args);
        }

        return false;
    }

    private boolean exp(final String command, final Player activeChar, final String args) {
        if (activeChar.getLevel() >= (activeChar.getPlayerClassComponent().isSubClassActive() ? PlayerUtils.getMaxSubLevel() : PlayerUtils.getMaxLevel())) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.MaxLevel"));
        } else {
            final long exp = ExpDataHolder.getInstance().getExpForLevel(activeChar.getLevel() + 1) - activeChar.getExp();
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.ExpLeft").addNumber(exp));
        }
        return true;
    }

    private boolean whereis(final String command, final Player activeChar, final String args) {
        final Player friend = World.getPlayer(args);
        if (friend == null) {
            return false;
        }

        if (friend.getParty().equals(activeChar.getParty()) || friend.getClan().equals(activeChar.getClan())) {
            final RadarControl rc = new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, friend.getLoc());
            activeChar.sendPacket(rc);
            return true;
        }

        return false;
    }

    private boolean help(final String command, final Player activeChar, final String args) {
        final String dialog = HtmCache.getInstance().getHtml("command/help.htm", activeChar);
        Functions.show(dialog, activeChar, null);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}