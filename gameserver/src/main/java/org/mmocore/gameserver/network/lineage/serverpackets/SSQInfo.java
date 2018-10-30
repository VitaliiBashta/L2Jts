package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Seven Signs Info
 * <p/>
 * packet id 0x73
 * format: cc
 * <p/>
 * Пример пакета с оффа (828 протокол):
 * 73 01 01
 * <p/>
 * Возможные варианты использования данного пакета:
 * 0 0 - Обычное небо???
 * 1 1 - Dusk Sky
 * 2 2 - Dawn Sky???
 * 3 3 - Небо постепенно краснеет (за 10 секунд)
 * <p/>
 * Возможно и другие вариации, эффект не совсем понятен.
 * 1 0
 * 0 1
 *
 * @author SYS
 */
public class SSQInfo extends GameServerPacket {
    private int state = 0;

    public SSQInfo() {
        final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
        if (SevenSigns.getInstance().isSealValidationPeriod()) {
            if (compWinner == SevenSigns.CABAL_DAWN) {
                state = 2;
            } else if (compWinner == SevenSigns.CABAL_DUSK) {
                state = 1;
            }
        }
    }

    public SSQInfo(final int state) {
        this.state = state;
    }

    @Override
    protected final void writeData() {
        switch (state) {
            case 1:
                writeH(257);
                break;
            case 2:
                writeH(258);
                break;
            default:
                writeH(256);
                break;
        }
    }
}