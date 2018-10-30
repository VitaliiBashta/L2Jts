package org.mmocore.gameserver.scripts.npc.model.events;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.events.custom.CaptureZoneEvent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Mangol
 * @since 04.12.2016
 */
public class CaptureZoneInstance extends NpcInstance {
    private CaptureZoneEvent _event = null;

    public CaptureZoneInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setUndying(false);
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean crit) {
        if (_event == null) {
            _event = EventHolder.getInstance().getEvent(CaptureZoneEvent.class);
        }
        if (_event != null) {
            if (_event.isOwner(attacker)) {
                return;
            }
            _event.damage(damage, attacker);
            super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, crit);
        }
    }

    @Override
    public void onDeath(Creature killer) {
        _event.calcWinner();
        super.onDeath(killer);
        _event.respawnObject();
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return !attacker.isMonster();
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isDebuffImmune() {
        return true;
    }

    @Override
    public boolean isHasChatWindow() {
        return false;
    }
}
