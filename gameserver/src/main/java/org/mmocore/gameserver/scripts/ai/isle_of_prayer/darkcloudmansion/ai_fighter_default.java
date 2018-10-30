package org.mmocore.gameserver.scripts.ai.isle_of_prayer.darkcloudmansion;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author KilRoy
 */
public class ai_fighter_default extends Fighter {
    public ai_fighter_default(final NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 1000;
        AI_TASK_ACTIVE_DELAY = 1000;
    }

    @Override
    public int getMaxAttackTimeout() {
        return 1000;
    }
}