package org.mmocore.gameserver.model.mail.task;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExNoticePostArrived;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author Mangol
 * @since 18.09.2016
 */
public final class ReceiverNotifyTask extends RunnableImpl {
    private final int targetObjId;

    private ReceiverNotifyTask(final int targetObjId) {
        this.targetObjId = targetObjId;
    }

    public static ReceiverNotifyTask createTask(final int targetObjId) {
        return new ReceiverNotifyTask(targetObjId);
    }

    @Override
    protected void runImpl() throws Exception {
        final Player target = GameObjectsStorage.getPlayer(targetObjId);
        if (target == null) {
            return;
        }
        target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
        target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
    }
}
