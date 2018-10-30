package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestTutorialClientEvent extends L2GameClientPacket {
    // format: cd
    private int _event;

    /**
     * Пакет от клиента, если вы в туториале подергали мышкой как надо - клиент пришлет его со значением 1 ну или нужным ивентом
     */
    @Override
    protected void readImpl() {
        _event = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.processQuestEvent(255, "CE" + _event, null);
    }
}