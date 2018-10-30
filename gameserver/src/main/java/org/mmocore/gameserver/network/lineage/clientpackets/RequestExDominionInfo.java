package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyDominionInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowOwnthingPos;
import org.mmocore.gameserver.object.Player;

public class RequestExDominionInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExReplyDominionInfo());

        final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        if (runnerEvent.isInProgress()) {
            activeChar.sendPacket(new ExShowOwnthingPos());
        }
    }
}