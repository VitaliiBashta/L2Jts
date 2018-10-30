package org.mmocore.gameserver.model.instances.residences;

import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.Set;
//import org.mmocore.gameserver.model.Skill;

/**
 * @author VISTALL
 * @date 5:47/07.06.2011
 */
public abstract class SiegeToggleNpcInstance extends NpcInstance {
    private NpcInstance _fakeInstance;
    private int _maxHp;

    public SiegeToggleNpcInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
        setUndying(false);
    }

    public void setZoneList(final Set<String> set) {
    }

    public void register(final Spawner spawn) {
    }

    public void initFake(final int fakeNpcId) {
        _fakeInstance = NpcHolder.getInstance().getTemplate(fakeNpcId).getNewInstance();
        _fakeInstance.setCurrentHpMp(1, _fakeInstance.getMaxMp());
        _fakeInstance.setHasChatWindow(false);
    }

    public abstract void onDeathImpl(Creature killer);

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp, final boolean directHp, final boolean lethal) {
        setCurrentHp(Math.max(getCurrentHp() - damage, 0), false);
        if (getCurrentHp() < 0.5) {
            doDie(attacker);
            onDeathImpl(attacker);
            decayMe();
            _fakeInstance.spawnMe(getLoc());
        }
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (attacker == null) {
            return false;
        }
        final Player player = attacker.getPlayer();
        if (player == null) {
            return false;
        }
        final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent == null || !siegeEvent.isInProgress()) {
            return false;
        }
        if (siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan()) != null) {
            return false;
        }
        if (siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    public void decayFake() {
        _fakeInstance.decayMe();
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public double getMaxHp() {
        return _maxHp;
    }

    public void setMaxHp(final int maxHp) {
        _maxHp = maxHp;
    }

    @Override
    protected void onDecay() {
        decayMe();
        _spawnAnimation = 2;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}
