package org.mmocore.gameserver.scripts.ai.residences.fortress.siege;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.ai.residences.SiegeGuardMystic;
import org.mmocore.gameserver.scripts.npc.model.residences.SiegeGuardInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author VISTALL
 * @date 16:41/17.04.2011
 */
public class SupportUnitCaption extends SiegeGuardMystic {
    public SupportUnitCaption(NpcInstance actor) {
        super(actor);

        actor.addListener(FortressSiegeEvent.RESTORE_BARRACKS_LISTENER);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        super.onEvtAttacked(attacker, skill, damage);
        SiegeGuardInstance actor = getActor();

        if (Rnd.chance(1)) {
            ChatUtils.shout(actor, NpcString.SPIRIT_OF_FIRE_UNLEASH_YOUR_POWER_BURN_THE_ENEMY);
        }
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        SiegeGuardInstance actor = getActor();

        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null) {
            return;
        }

        if (siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0) {
            actor.doCast(SkillTable.getInstance().getSkillEntry(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF)), actor, false);
        }

        siegeEvent.barrackAction(2, false);
    }

    @Override
    public void onEvtDead(Creature killer) {
        SiegeGuardInstance actor = getActor();
        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null) {
            return;
        }

        siegeEvent.barrackAction(2, true);

        siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

        ChatUtils.shout(actor, NpcString.AT_LAST_THE_MAGIC_FIELD_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK);

        super.onEvtDead(killer);

        siegeEvent.checkBarracks();
    }
}
