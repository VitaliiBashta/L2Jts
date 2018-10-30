package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.EffectType;

/**
 * Create by Mangol on 16.10.2015.
 */
public class WaterEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
    public static final WaterEnterLeaveListenerImpl STATIC = new WaterEnterLeaveListenerImpl();

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (actor.isPlayer()) {
            final Player player = actor.getPlayer();
            if (player.isTransformed() && player.getTransformation().getData().can_swim == 0 && !player.isCursedWeaponEquipped()) {
                final Effect effect = player.getEffectList().getEffectByType(EffectType.p_transform);
                if (effect != null) {
                    effect.exit();
                }
            }
        }
    }

    @Override
    public void onZoneLeave(Zone zone, Creature actor) {
    }
}
