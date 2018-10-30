package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bbs.IBbsHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;

public class RequestShowBoard extends L2GameClientPacket {

    @Override
    public void readImpl() {
        int _unknown = readD();
    }

    @Override
    public void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (CBasicConfig.COMMUNITYBOARD_ENABLED) {
            final Optional<IBbsHandler> handler = BbsHandlerHolder.getInstance().getCommunityHandler(CBasicConfig.BBS_DEFAULT);
            if (handler.isPresent()) {
                handler.get().onBypassCommand(activeChar, CBasicConfig.BBS_DEFAULT);
            }
        } else {
            activeChar.sendPacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
        }
    }
}
