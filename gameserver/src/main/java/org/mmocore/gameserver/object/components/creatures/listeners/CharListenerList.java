package org.mmocore.gameserver.object.components.creatures.listeners;

import org.mmocore.commons.listener.Listener;
import org.mmocore.commons.listener.ListenerList;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.listener.actor.*;
import org.mmocore.gameserver.listener.actor.ai.OnAiEventListener;
import org.mmocore.gameserver.listener.actor.ai.OnAiIntentionListener;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author G1ta0
 */
public class CharListenerList extends ListenerList<Creature> {
    protected static final ListenerList<Creature> global = new ListenerList<>();

    protected final Creature actor;

    public CharListenerList(final Creature actor) {
        this.actor = actor;
    }

    public static boolean addGlobal(final Listener<Creature> listener) {
        return global.add(listener);
    }

    public static boolean removeGlobal(final Listener<Creature> listener) {
        return global.remove(listener);
    }

    public Creature getActor() {
        return actor;
    }

    public void onAiIntention(final CtrlIntention intention, final Object arg0, final Object arg1) {
        for (final OnAiIntentionListener listener : getListeners(OnAiIntentionListener.class)) {
            listener.onAiIntention(getActor(), intention, arg0, arg1);
        }
    }

    public void onAiEvent(final CtrlEvent evt, final Object... args) {
        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnAiEventListener.class::isInstance).forEach(listener -> ((OnAiEventListener) listener).onAiEvent(getActor(), evt, args));
        }
    }

    public void onAttack(final Creature target) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnAttackListener.class::isInstance).forEach(listener -> ((OnAttackListener) listener).onAttack(getActor(), target));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnAttackListener.class::isInstance).forEach(listener -> ((OnAttackListener) listener).onAttack(getActor(), target));
        }
    }

    public void onAttackHit(final Creature attacker) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnAttackHitListener.class::isInstance).forEach(listener -> ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnAttackHitListener.class::isInstance).forEach(listener -> ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker));
        }
    }

    public void onMagicUse(final SkillEntry skill, final Creature target, final boolean alt) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnMagicUseListener.class::isInstance).forEach(listener -> ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnMagicUseListener.class::isInstance).forEach(listener -> ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt));
        }
    }

    public void onMagicHit(final SkillEntry skill, final Creature caster) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnMagicHitListener.class::isInstance).forEach(listener -> ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnMagicHitListener.class::isInstance).forEach(listener -> ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster));
        }
    }

    public void onDeath(final Creature killer) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnDeathListener.class::isInstance).forEach(listener -> ((OnDeathListener) listener).onDeath(getActor(), killer));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnDeathListener.class::isInstance).forEach(listener -> ((OnDeathListener) listener).onDeath(getActor(), killer));
        }
    }

    public void onKill(final Creature victim) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(listener -> OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
                    .forEach(listener -> ((OnKillListener) listener).onKill(getActor(), victim));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(listener -> OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon()).forEach(listener -> ((OnKillListener) listener).onKill(getActor(), victim));
        }
    }

    public void onKillIgnorePetOrSummon(final Creature victim) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(listener -> OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
                    .forEach(listener -> ((OnKillListener) listener).onKill(getActor(), victim));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(listener -> OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon()).forEach(listener -> ((OnKillListener) listener).onKill(getActor(), victim));
        }
    }

    public void onCurrentHpDamage(final double damage, final Creature attacker, final SkillEntry skill) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnCurrentHpDamageListener.class::isInstance).forEach(listener -> ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnCurrentHpDamageListener.class::isInstance).forEach(listener -> ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill));
        }
    }

    public void onRevive() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnReviveListener.class::isInstance).forEach(listener -> ((OnReviveListener) listener).onRevive(getActor()));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnReviveListener.class::isInstance).forEach(listener -> ((OnReviveListener) listener).onRevive(getActor()));
        }
    }

    public void onDeathFromUndying(Creature killer) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnDeathFromUndyingListener.class::isInstance).forEach(listener -> ((OnDeathFromUndyingListener) listener).onDeathFromUndying(getActor(), killer));
        }
        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnDeathFromUndyingListener.class::isInstance).forEach(listener -> ((OnDeathFromUndyingListener) listener).onDeathFromUndying(getActor(), killer));
        }
    }
}