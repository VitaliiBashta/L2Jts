package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.TimeUtils;

import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * Support for /time command
 */
public class Time implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {77};

    private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);

    static {
        df.setMinimumIntegerDigits(2);
    }

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_IDS[0] != id) {
            return false;
        }

        int h = GameTimeManager.getInstance().getGameHour();
        int m = GameTimeManager.getInstance().getGameMin();

        SystemMessage sm = GameTimeManager.getInstance().isNowNight()
                ? new SystemMessage(SystemMsg.THE_CURRENT_TIME_IS_S1S2_)
                : new SystemMessage(SystemMsg.THE_CURRENT_TIME_IS_S1S2);
        sm.addString(df.format(h)).addString(df.format(m));

        activeChar.sendPacket(sm);

        if (AllSettingsConfig.ALT_SHOW_SERVER_TIME) {
            final ZonedDateTime time = ZonedDateTime.now();
            activeChar.sendMessage(new CustomMessage("usercommandhandlers.Time.ServerTime")
                    .addString(TimeUtils.timeFormat(time)));
        }

        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
