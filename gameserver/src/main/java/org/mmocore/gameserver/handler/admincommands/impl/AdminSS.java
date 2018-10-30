package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.object.Player;

public class AdminSS implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        switch (command) {
            case admin_ssq_change: {
                if (wordList.length > 2) {
                    final int period = Integer.parseInt(wordList[1]);
                    final int minutes = Integer.parseInt(wordList[2]);
                    SevenSigns.getInstance().changePeriod(period, minutes * 60);
                } else if (wordList.length > 1) {
                    final int period = Integer.parseInt(wordList[1]);
                    SevenSigns.getInstance().changePeriod(period);
                } else {
                    SevenSigns.getInstance().changePeriod();
                }
                break;
            }
            case admin_ssq_time: {
                if (wordList.length > 1) {
                    final int time = Integer.parseInt(wordList[1]);
                    SevenSigns.getInstance().setTimeToNextPeriodChange(time);
                }
                break;
            }
            case admin_ssq_cabal: {
                if (wordList.length > 3) {
                    final int player = Integer.parseInt(wordList[1]); // player objectid
                    final int cabal = Integer.parseInt(wordList[2]); // null dusk dawn
                    final int seal = Integer.parseInt(wordList[3]); // null avarice gnosis strife
                    SevenSigns.getInstance().setPlayerInfo(player, cabal, seal);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_ssq_change,
        admin_ssq_time,
        admin_ssq_cabal,
    }
}