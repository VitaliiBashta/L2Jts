package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;

/**
 * Support for command: /clanpenalty
 */
public class ClanPenalty implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {100};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_IDS[0] != id) {
            return false;
        }

        long leaveClan = 0;
        if (activeChar.getLeaveClanTime() != 0) {
            leaveClan = activeChar.getLeaveClanTime() + Clan.JOIN_PLEDGE_PENALTY;
        }
        long deleteClan = 0;
        if (activeChar.getDeleteClanTime() != 0) {
            deleteClan = activeChar.getDeleteClanTime() + Clan.CREATE_PLEDGE_PENALTY;
        }

        String html = HtmCache.getInstance().getHtml("command/penalty.htm", activeChar);

        if (activeChar.getClanId() == 0) {
            if (leaveClan == 0 && deleteClan == 0) {
                html = html.replaceFirst("%reason%", activeChar.isLangRus() ? "Штраф не налагается." : "No penalty is imposed.");
                html = html.replaceFirst("%expiration%", " ");
            } else if (leaveClan > 0 && deleteClan == 0) {
                html = html.replaceFirst("%reason%", activeChar.isLangRus() ? "Штраф за выход из клана." : "Penalty for leaving clan.");
                final Instant instant = Instant.ofEpochMilli(leaveClan);
                html = html.replaceFirst("%expiration%", TimeUtils.dateTimeFormat(instant));
            } else if (deleteClan > 0) {
                html = html.replaceFirst("%reason%", activeChar.isLangRus() ? "Штраф за роспуск клана." : "Penalty for dissolving clan.");
                final Instant instant = Instant.ofEpochMilli(deleteClan);
                html = html.replaceFirst("%expiration%", TimeUtils.dateTimeFormat(instant));
            }
        } else if (activeChar.getClan().canInvite()) {
            html = html.replaceFirst("%reason%", activeChar.isLangRus() ? "Штраф не налагается." : "No penalty is imposed.");
            html = html.replaceFirst("%expiration%", " ");
        } else {
            html = html.replaceFirst("%reason%", activeChar.isLangRus() ? "Штраф за изгнание члена клана." : "Penalty for expelling clan member.");
            final Instant instant = Instant.ofEpochMilli(activeChar.getClan().getExpelledMemberTime() + Clan.EXPELLED_MEMBER_PENALTY);
            html = html.replaceFirst("%expiration%", TimeUtils.dateTimeFormat(instant));
        }

        HtmlMessage msg = new HtmlMessage(5);
        msg.setHtml(HtmlUtils.bbParse(html));
        activeChar.sendPacket(msg);
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}