package org.mmocore.gameserver.object.components.player.cubicdata;

import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.TargetType;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.action.CBySkillAction;
import org.mmocore.gameserver.object.components.player.cubicdata.action.CHealAction;
import org.mmocore.gameserver.object.components.player.cubicdata.action.CTargetAction;
import org.mmocore.gameserver.object.components.player.cubicdata.action.ICActionCubic;
import org.mmocore.gameserver.object.components.player.cubicdata.task.CActionTask;
import org.mmocore.gameserver.object.components.player.cubicdata.task.CDurationTask;
import org.mmocore.gameserver.skills.SkillEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * Create by Mangol on 15.09.2015.
 */
public class CubicComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(CubicComponent.class);
    protected final TargetType _targetType;
    protected final DefaultCubicData _data;
    private final int _id;
    private final int _slot;
    private final int _duration;
    private final int _delay;
    private final Player _player;
    protected Optional<ICActionCubic> _action;
    private int _max_count;
    private Optional<ScheduledFuture<?>> _scheduledActionTask = Optional.empty();
    private Optional<ScheduledFuture<?>> _scheduledDuration = Optional.empty();
    private Optional<CActionTask> _actionTask = Optional.empty();
    private Optional<CDurationTask> _durationTask = Optional.empty();

    public CubicComponent(final Player owner, final DefaultCubicData template) {
        _player = owner;
        _id = template.id;
        _slot = template.slot;
        _duration = template.duration;
        _data = template;
        _max_count = template.max_count;
        _delay = template.delay;
        _targetType = template.target_type.type;
        initAction();
        startTask();
        owner.broadcastCharInfo();
    }

    public void initAction() {
        switch (_targetType) {
            case heal:
                _action = Optional.of(new CHealAction(this));
                break;
            case target:
                _action = Optional.of(new CTargetAction(this));
                break;
            case by_skill:
                _action = Optional.of(new CBySkillAction(this));
                break;
            default:
                LOGGER.error("action type " + _targetType + " not registered");
                _action = Optional.empty();
                break;
        }
    }

    private void startTask() {
        if (_delay > 0) {
            if (!_actionTask.isPresent()) {
                if (_action.isPresent()) {
                    _actionTask = Optional.of(new CActionTask(_player, _action.get()));
                }
            }
            _scheduledActionTask = Optional.of(ThreadPoolManager.getInstance().scheduleAtFixedRate(_actionTask.get(), _delay * 1000, _delay * 1000));
        }
        if (_duration > 0) {
            if (!_durationTask.isPresent()) {
                _durationTask = Optional.of(new CDurationTask(this));
            }
            _scheduledDuration = Optional.of(ThreadPoolManager.getInstance().schedule(_durationTask.get(), _duration * 1000));
        }
    }

    protected void stopAllTask() {
        stopScheduledAction();
        stopScheduleDuration();
    }

    public void delete(final boolean broadCast) {
        stopAllTask();
        final Optional<Player> player = getPlayer();
        if (!player.isPresent()) {
            return;
        }
        player.get().removeCubicSlot(_slot, broadCast);
    }

    public void stopScheduledAction() {
        if (_scheduledActionTask.isPresent()) {
            _scheduledActionTask.get().cancel(true);
            _scheduledActionTask = Optional.empty();
        }
    }

    public void stopScheduleDuration() {
        if (_scheduledDuration.isPresent()) {
            _scheduledDuration.get().cancel(true);
            _scheduledDuration = Optional.empty();
        }
    }

    public void useSkill(final SkillEntry entry, final Creature target, final boolean ignoreRange) {
        final Optional<Player> player = getPlayer();
        if (!player.isPresent()) {
            return;
        }
        _max_count--;
        if (_max_count == 0) {
            stopScheduledAction();
            return;
        }
        if (isAgathion()) {
            player.get().altUseSkill(entry, target, false);
        } else if (ignoreRange) {
            player.get().altUseSkill(entry, target);
        } else if (player.get().isInRangeZ(target, entry.getTemplate().getCastRange())) {
            player.get().altUseSkill(entry, target);
        }
    }

    protected boolean isAgathion() {
        return false;
    }

    public Optional<CActionTask> getActionTask() {
        return _actionTask;
    }

    public DefaultCubicData getTemplate() {
        return _data;
    }

    public int getId() {
        return _id;
    }

    public int getSlot() {
        return _slot;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(_player);
    }

    public Optional<ICActionCubic> getAction() {
        return _action;
    }
}
