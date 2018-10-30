package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

public class RequestVoteNew extends L2GameClientPacket {
    private int _targetObjectId;

    @Override
    protected void readImpl() {
        _targetObjectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!activeChar.getPlayerAccess().CanEvaluate) {
            return;
        }

        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer() || target.getObjectId() != _targetObjectId) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (target.getObjectId() == activeChar.getObjectId()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECOMMEND_YOURSELF);
            return;
        }

        final Player targetPlayer = (Player) target;

        if (activeChar.getRecommendationComponent().getRecomLeft() <= 0) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_OUT_OF_RECOMMENDATIONS);
            return;
        }

        if (targetPlayer.getRecommendationComponent().getRecomHave() >= 255) {
            activeChar.sendPacket(SystemMsg.YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION);
            return;
        }

        activeChar.getRecommendationComponent().giveRecom(targetPlayer);
        SystemMessage sm = new SystemMessage(SystemMsg.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
        sm.addName(target);
        sm.addNumber(activeChar.getRecommendationComponent().getRecomLeft());
        activeChar.sendPacket(sm);

        sm = new SystemMessage(SystemMsg.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
        sm.addName(activeChar);
        targetPlayer.sendPacket(sm);
    }
}