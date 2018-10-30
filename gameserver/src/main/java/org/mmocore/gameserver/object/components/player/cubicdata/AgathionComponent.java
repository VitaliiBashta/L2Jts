package org.mmocore.gameserver.object.components.player.cubicdata;

import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.action.CMasterAction;
import org.mmocore.gameserver.object.components.player.cubicdata.action.ICActionCubic;
import org.mmocore.gameserver.object.components.player.cubicdata.message.TimeSkill1Message;
import org.mmocore.gameserver.object.components.player.cubicdata.message.TimeSkill2Message;
import org.mmocore.gameserver.object.components.player.cubicdata.task.CActionTask;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Create by Mangol on 22.09.2015.
 */
public class AgathionComponent extends CubicComponent {
    private final Agathion data;
    private final int npcId;

    public AgathionComponent(final Player owner, final Agathion template) {
        super(owner, template);
        data = template;
        npcId = LinkerFactory.getInstance().findClearValue(data.npc_name) - 1000000;
    }

    @Override
    public void initAction() {
        switch (_targetType) {
            case master:
                _action = Optional.of(new CMasterAction(this));
                break;
            default:
                super.initAction();
                break;
        }
    }

    @Override
    public Agathion getTemplate() {
        return data;
    }

    public int getNpcId() {
        return npcId;
    }

    @Override
    protected void stopAllTask() {
        super.stopAllTask();
        final Optional<CActionTask> task = getActionTask();
        if (task.isPresent()) {
            final Optional<CMasterAction> actionMaster = getAction().get().getMasterAction();
            if (actionMaster.isPresent()) {
                actionMaster.get().stopAllTask();
            }
        }
    }

    public void delete() {
        stopAllTask();
    }

    @Override
    protected boolean isAgathion() {
        return true;
    }

    public void sendMessage(final int idMessage) {
        final Optional<CActionTask> task = getActionTask();
        if (task.isPresent()) {
            final Optional<ICActionCubic> action = getAction();
            if (action.isPresent()) {
                final Optional<CMasterAction> actionMaster = action.get().getMasterAction();
                if (actionMaster.isPresent()) {
                    final Optional<ScheduledFuture<?>> time_skill1 = actionMaster.get().getTimeSkill1Task();
                    final Optional<ScheduledFuture<?>> time_skill2 = actionMaster.get().getTimeSkill2Task();
                    if (time_skill1.isPresent() && !time_skill1.get().isDone()) {
                        final long[] time = getTimeTask(time_skill1.get());
                        final Optional<TimeSkill1Message> message = TimeSkill1Message.valueOf(idMessage);
                        if (message.isPresent()) {
                            message.get().sendMessage(getPlayer().get(), time);
                        }
                    }
                    if (time_skill2.isPresent() && !time_skill2.get().isDone()) {
                        final long[] time = getTimeTask(time_skill2.get());
                        final Optional<TimeSkill2Message> message = TimeSkill2Message.valueOf(idMessage);
                        if (message.isPresent()) {
                            message.get().sendMessage(getPlayer().get(), time);
                        }
                    }
                }
            }
        }
    }

    private long[] getTimeTask(final ScheduledFuture<?> task) {
        final long time = task.getDelay(TimeUnit.SECONDS);
        final long hours = time / 3600;
        final long minutes = (time % 3600) / 60;
        final long seconds = time % 60;
        return new long[]{hours, minutes, seconds};
    }
}
