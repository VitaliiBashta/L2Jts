package org.mmocore.gameserver.model.instances.residences.clanhall;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import org.mmocore.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.CTBTeamObject;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:55/21.04.2011
 */
public abstract class CTBBossInstance extends MonsterInstance {
    public static final SkillEntry SKILL = SkillTable.getInstance().getSkillEntry(5456, 1);
    private CTBTeamObject _matchTeamObject;

    public CTBBossInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp,
                                final boolean directHp, final boolean canReflect, final boolean transferDamage, final boolean isDot,
                                final boolean sendMessage, final boolean lethal) {
        if (attacker.getLevel() > (getLevel() + 8) && attacker.getEffectList().getEffectsCountForSkill(SKILL.getId()) == 0) {
            doCast(SKILL, attacker, false);
            return;
        }

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        final CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
        if (clan != null && attacker.isPlayable()) {
            final Player player = attacker.getPlayer();
            if (player.getClan() == clan.getClan()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return isAttackable(attacker);
    }

    @Override
    public void onDeath(final Creature killer) {
        final ClanHallTeamBattleEvent event = getEvent(ClanHallTeamBattleEvent.class);
        event.processStep(_matchTeamObject);

        super.onDeath(killer);
    }

    @Override
    public String getTitle() {
        final CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
        return clan == null ? StringUtils.EMPTY : clan.getClan().getName();
    }

    public void setMatchTeamObject(final CTBTeamObject matchTeamObject) {
        _matchTeamObject = matchTeamObject;
    }
}
