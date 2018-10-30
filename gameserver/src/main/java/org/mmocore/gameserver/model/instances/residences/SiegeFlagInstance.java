package org.mmocore.gameserver.model.instances.residences;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class SiegeFlagInstance extends NpcInstance {
    private SiegeClanObject _owner;
    private long _lastAnnouncedAttackedTime = 0;

    public SiegeFlagInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        setUndying(false);
        setHasChatWindow(false);
    }

    @Override
    public String getName() {
        return _owner.getClan().getName();
    }

    @Override
    public Clan getClan() {
        return _owner.getClan();
    }

    public void setClan(final SiegeClanObject owner) {
        _owner = owner;
    }

    @Override
    public String getTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        final Player player = attacker.getPlayer();
        if (player == null || isInvul()) {
            return false;
        }
        final Clan clan = player.getClan();
        return clan == null || _owner.getClan() != clan;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return true;
    }

    @Override
    protected void onDeath(final Creature killer) {
        _owner.setFlag(null);
        super.onDeath(killer);
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake,
                                     final boolean standUp, final boolean directHp, final boolean lethal) {
        if (System.currentTimeMillis() - _lastAnnouncedAttackedTime > 120000) {
            _lastAnnouncedAttackedTime = System.currentTimeMillis();
            _owner.getClan().broadcastToOnlineMembers(SystemMsg.YOUR_BASE_IS_BEING_ATTACKED);
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
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

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }
}