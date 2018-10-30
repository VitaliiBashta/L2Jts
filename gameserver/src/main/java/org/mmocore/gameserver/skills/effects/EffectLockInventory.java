package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.LockType;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 14:13/16.05.2011
 */
public class EffectLockInventory extends Effect {
    private final LockType _lockType;
    private final int[] _lockItems;

    public EffectLockInventory(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _lockType = template.getParam().getEnum("lockType", LockType.class);
        _lockItems = template.getParam().getIntegerArray("lockItems");
    }

    @Override
    public void onStart() {
        super.onStart();

        final Player player = _effector.getPlayer();

        player.getInventory().lockItems(_lockType, _lockItems);
    }

    @Override
    public void onExit() {
        super.onExit();

        final Player player = _effector.getPlayer();

        player.getInventory().unlock();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
