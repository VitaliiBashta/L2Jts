package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.listener.actor.OnDeathFromUndyingListener;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 18:03/22.08.2011
 * Базовый класс для евентов, которые могу быть на игроке ток в одном виде, толи это Твт, или Оллимпиада, или Ктф, или Дуель
 */
public abstract class SingleMatchEvent extends Event {
    protected SingleMatchEvent(final MultiValueSet<String> set) {
        super(set);
    }

    protected SingleMatchEvent(final int id, final String name) {
        super(id, name);
    }

    public void onDeath(Creature actor, Creature killer) {
    }

    /**
     * Проверяет может ли флагаться владелец эвента при атаке цели. Участники одной дуэли не флагаются.
     */
    public boolean checkPvPFlag(final Creature target) {
        final SingleMatchEvent targetEvent = target.getEvent(SingleMatchEvent.class);
        return targetEvent != this;
    }

    public void onStatusUpdate(final Player player) {
    }

    public void onEffectIconsUpdate(final Player player, final Effect[] effects) {
    }

    public void onDie(final Player player) {
    }

    public void onKill(Creature killer) {
    }

    public void sendPacket(IBroadcastPacket packet) {
    }

    public void sendPackets(IBroadcastPacket... packet) {
    }

    public class OnDeathFromUndyingListenerImpl implements OnDeathFromUndyingListener {
        @Override
        public void onDeathFromUndying(final Creature actor, final Creature killer) {
            onDie((Player) actor);
            onKill(killer);
            onDeath(actor, killer);
        }
    }
}
