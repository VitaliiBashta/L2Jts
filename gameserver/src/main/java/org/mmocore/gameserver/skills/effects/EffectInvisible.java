package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.world.World;

public final class EffectInvisible extends Effect {
    private InvisibleType _invisibleType = InvisibleType.NONE;

    public EffectInvisible(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (!_effected.isPlayer()) {
            return false;
        }
        final Player player = (Player) _effected;
        if (player.isInvisible()) {
            return false;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Player player = (Player) _effected;

        _invisibleType = player.getInvisibleType();

        player.setInvisibleType(InvisibleType.EFFECT);

        World.removeObjectFromPlayers(player);
    }

    @Override
    public void onExit() {
        super.onExit();
        final Player player = (Player) _effected;
        if (!player.isInvisible()) {
            return;
        }

        player.setInvisibleType(_invisibleType);

        player.broadcastUserInfo(true);
        if (player.getServitor() != null) {
            player.getServitor().broadcastCharInfo();
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}