package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.Optional;

public final class ArtefactInstance extends NpcInstance {
    private Optional<Clan> clanCast = Optional.empty();

    public ArtefactInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public boolean isArtefact() {
        return true;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return false;
    }

    /**
     * Артефакт нельзя убить
     */
    @Override
    public boolean isInvul() {
        return true;
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
    protected void onDecay() {
        clanCast = Optional.empty();
        super.onDecay();
    }

    public void clanCastArtefact(final Clan clan) {
        if (!clanCast.isPresent()) {
            clanCast = Optional.ofNullable(clan);
        } else if (clanCast.get() != clan) {
            clanCast = Optional.ofNullable(clan);
        }
    }

    public boolean isEqualsClanCast(final Clan clan) {
        return clanCast.isPresent() && clan != null && clan == clanCast.get();
    }
}