package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.TerritoryWardObject;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:38/11.04.2011
 */
public class TerritoryWardInstance extends NpcInstance {
    private static final long serialVersionUID = -8304927670018465563L;

    private final TerritoryWardObject _territoryWard;

    public TerritoryWardInstance(final int objectId, final NpcTemplate template, final TerritoryWardObject territoryWardObject) {
        super(objectId, template);
        setHasChatWindow(false);
        _territoryWard = territoryWardObject;
    }

    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp,
                                final boolean directHp, final boolean canReflect, final boolean transferDamage, final boolean isDot,
                                final boolean sendMessage, final boolean lethal) {
        if (skill != null) {
            return;
        }

        super.reduceCurrentHp(damage, attacker, null, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    @Override
    public boolean isUndying(final Creature attacker) {
        if (attacker.isDead() || !attacker.isPlayer() || ((Player) attacker).isTerritoryFlagEquipped()) {
            return true;
        }

        return !isAutoAttackable(attacker);
    }

    @Override
    public void onDeath(final Creature killer) {
        super.onDeath(killer);
        final Player player = killer.getPlayer();
        if (player == null) {
            return;
        }

        if (_territoryWard.canPickUp(player)) {
            _territoryWard.pickUp(player);
            decayMe();
        }
    }

    @Override
    protected void onDecay() {
        decayMe();

        _spawnAnimation = 2;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        if (attacker.getPlayer() == null) {
            return false;
        }
        final DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null) {
            return false;
        }
        final DominionSiegeEvent siegeEvent2 = attacker.getPlayer().getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (attacker.getPlayer() == null) {
            return false;
        }
        final DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null) {
            return false;
        }
        final DominionSiegeEvent siegeEvent2 = attacker.getPlayer().getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null) {
            return false;
        }
        if (siegeEvent == siegeEvent2) {
            return false;
        }
        if (siegeEvent2.getResidence().getOwner() != attacker.getPlayer().getClan()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}
