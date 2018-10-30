package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectDisarm extends Effect {
    public EffectDisarm(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (!_effected.isPlayer()) {
            return false;
        }
        final Player player = _effected.getPlayer();
        // Нельзя снимать/одевать проклятое оружие и флаги
        if (player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Player player = (Player) _effected;

        final ItemInstance wpn = player.getActiveWeaponInstance();
        if (wpn != null) {
            player.getInventory().unEquipItem(wpn);
            player.sendDisarmMessage(wpn);
        }
        player.startWeaponEquipBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopWeaponEquipBlocked();
    }

    @Override
    public boolean onActionTime() {
        // just stop this effect
        return false;
    }
}