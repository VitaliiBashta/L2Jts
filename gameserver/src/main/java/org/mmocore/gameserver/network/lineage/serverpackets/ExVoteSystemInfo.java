package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.player.recomendation.RecommendationComponent;

public class ExVoteSystemInfo extends GameServerPacket {
    private final int receivedRec;
    private final int givingRec;
    private final int time;
    private final int bonusPercent;
    private final boolean showTimer;

    public ExVoteSystemInfo(final RecommendationComponent vote) {
        receivedRec = vote.getRecomLeft();
        givingRec = vote.getRecomHave();
        time = vote.getRecomBonusTime();
        bonusPercent = vote.getRecomBonus();
        showTimer = !vote.isRecomTimerActive() || vote.isHourglassEffected();
    }

    @Override
    protected void writeData() {
        writeD(receivedRec);                //полученые реки
        writeD(givingRec);                    //отданые реки
        writeD(time);                        //таймер скок секунд осталось
        writeD(bonusPercent);                // процент бонуса
        writeD(showTimer ? 0x01 : 0x00);    //если ноль то таймера нету 1 - пишет чтоли "Работает"
    }
}