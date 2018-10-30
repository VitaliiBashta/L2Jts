package org.mmocore.gameserver.object.components.player.community;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.community.CBufferConfig;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.List;

/**
 * Create by Mangol on 14.12.2015.
 */
public class BuffTask extends RunnableImpl {
    private static final double enchantedMultiplier = 2;
    private final List<Buff> _buffList;
    private int _index;
    private Playable _playable;

    public BuffTask(final List<Buff> buffList, final Playable playable) {
        this._buffList = buffList;
        this._playable = playable;
    }

    private static void buff0(int id, int level, Playable playable) {
        if (playable == null || id < 20)
            return;

        if (id == 1363) // если ков, то не даем хп, чтоб не регенились им
            decreaseHealth(playable);

        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level > 0 ? level : SkillTable.getInstance().getMaxLevel(id));
        long time = CBufferConfig.time * 60000;
        if (skill.getLevelWithoutEnchant() < level)
            time *= enchantedMultiplier;
        skill.getEffects(playable, playable, false, false, time, 0, 5, false);
    }

    private static void decreaseHealth(Playable target) {
        double hp = target.getCurrentHp() - target.getMaxHp() * 0.20;
        target.setCurrentHp(hp > 0 ? hp : 1, true);
    }

    public void runImpl() throws Exception {
        final Buff poll = CollectionUtils.safeGet(_buffList, _index);
        if (poll == null) {
            return;
        }
        buff0(poll.getId(), poll.getLevel(), _playable);

        _index += 1;

        ThreadPoolManager.getInstance().schedule(this, 50);
    }
}

