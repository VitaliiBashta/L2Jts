package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExNoticePostArrived;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.List;

import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;

/**
 * @author G1ta0
 */
public class DeleteExpiredMailTask extends AutomaticTask {
    public DeleteExpiredMailTask() {
    }

    @Override
    public void doTask() throws Exception {
        final int expireTime = (int) (System.currentTimeMillis() / 1000L);

        final List<Mail> mails = MailDAO.getInstance().getExpiredMail(expireTime);

        for (final Mail mail : mails) {
            if (!mail.getAttachments().isEmpty()) {
                if (mail.getType() == Mail.SenderType.NORMAL) {
                    final Player player = World.getPlayer(mail.getSenderId());

                    final Mail reject = mail.reject();
                    mail.delete();
                    reject.setExpireTime(expireTime + 360 * 3600);
                    reject.save();

                    if (player != null) {
                        player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
                        player.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
                    }
                } else {
                    //TODO [G1ta0] возврат вещей в инвентарь игрока
                    mail.setExpireTime(expireTime + 86400);
                    mail.setJdbcState(JdbcEntityState.UPDATED);
                    mail.update();
                }
            } else {
                mail.delete();
            }
        }
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(repeatMinutelyForever(10))
                .build();
    }
}
