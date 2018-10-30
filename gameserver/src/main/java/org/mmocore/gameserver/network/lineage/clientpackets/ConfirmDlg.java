package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.object.Player;

public class ConfirmDlg extends L2GameClientPacket {
    private int _answer, _requestId;

    @Override
    protected void readImpl() {
        readD();
        _answer = readD();
        _requestId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
        if (entry == null || entry.getKey() != _requestId) {
            return;
        }

        final OnAnswerListener listener = entry.getValue();
        if (listener.expireTime() > 0 && (System.currentTimeMillis() > listener.expireTime())) {
            return;
        }
        if (_answer == 1) {
            listener.sayYes();
        } else {
            listener.sayNo();
        }
    }
}