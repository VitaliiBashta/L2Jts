package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.jts.dataparser.data.holder.cubicdata.Agathion.AgathionTimeSkill;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.AgathionComponent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CMasterAction extends AbstractCAction<AgathionComponent> {
    private Optional<ScheduledFuture<?>> _timeSkill1Task = Optional.empty();
    private Optional<ScheduledFuture<?>> _timeSkill2Task = Optional.empty();
    private Optional<ScheduledFuture<?>> _timeSkill3Task = Optional.empty();

    public CMasterAction(final AgathionComponent component) {
        super(component);
    }

    @Override
    public void useAction(final Player player) {
        final Optional<AgathionTimeSkill> time1 = Optional.ofNullable(getAgathionTemplate().timeskill1);
        final Optional<AgathionTimeSkill> time2 = Optional.ofNullable(getAgathionTemplate().timeskill2);
        final Optional<AgathionTimeSkill> time3 = Optional.ofNullable(getAgathionTemplate().timeskill3);
        if (time1.isPresent()) {
            if (!_timeSkill1Task.isPresent() || _timeSkill1Task.get().isDone()) {
                _timeSkill1Task = Optional.of(ThreadPoolManager.getInstance().schedule(() -> {
                    final int skill_id1 = getLinker().skillPchIdfindClearValue(time1.get().skill_name)[0];
                    final int skill_lvl1 = getLinker().skillPchIdfindClearValue(time1.get().skill_name)[1];
                    final SkillEntry skill1 = SkillTable.getInstance().getSkillEntry(skill_id1, skill_lvl1);
                    getAgathionComponent().useSkill(skill1, player, true);
                }, time1.get().time * 1000));
            }
        }
        if (time2.isPresent()) {
            if (!_timeSkill2Task.isPresent() || _timeSkill2Task.get().isDone()) {
                _timeSkill2Task = Optional.of(ThreadPoolManager.getInstance().schedule(() -> {
                    final int skill_id2 = getLinker().skillPchIdfindClearValue(time2.get().skill_name)[0];
                    final int skill_lvl2 = getLinker().skillPchIdfindClearValue(time2.get().skill_name)[1];
                    final SkillEntry skill2 = SkillTable.getInstance().getSkillEntry(skill_id2, skill_lvl2);
                    getAgathionComponent().useSkill(skill2, player, true);
                }, time2.get().time * 1000));
            }
        }
        if (time3.isPresent()) {
            if (!_timeSkill3Task.isPresent() || _timeSkill3Task.get().isDone()) {
                _timeSkill3Task = Optional.of(ThreadPoolManager.getInstance().schedule(() -> {
                    final int skill_id3 = getLinker().skillPchIdfindClearValue(time3.get().skill_name)[0];
                    final int skill_lvl3 = getLinker().skillPchIdfindClearValue(time3.get().skill_name)[1];
                    final SkillEntry skill3 = SkillTable.getInstance().getSkillEntry(skill_id3, skill_lvl3);
                    getAgathionComponent().useSkill(skill3, player, true);
                }, time3.get().time * 1000));
            }
        }
    }

    public Optional<ScheduledFuture<?>> getTimeSkill1Task() {
        return _timeSkill1Task;
    }

    public Optional<ScheduledFuture<?>> getTimeSkill2Task() {
        return _timeSkill2Task;
    }

    public Optional<ScheduledFuture<?>> getTimeSkill3Task() {
        return _timeSkill3Task;
    }

    public void stopAllTask() {
        if (_timeSkill1Task.isPresent()) {
            _timeSkill1Task.get().cancel(true);
            _timeSkill1Task = Optional.empty();
        }
        if (_timeSkill2Task.isPresent()) {
            _timeSkill2Task.get().cancel(true);
            _timeSkill2Task = Optional.empty();
        }
        if (_timeSkill3Task.isPresent()) {
            _timeSkill3Task.get().cancel(true);
            _timeSkill3Task = Optional.empty();
        }
    }

    @Override
    public Optional<CMasterAction> getMasterAction() {
        return Optional.of(this);
    }
}
