package org.mmocore.gameserver.scripts.ai.residences.clanhall;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.ai.residences.SiegeGuardMystic;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 18:25/10.05.2011
 * 35631
 * lidia_zone1
 * lidia_zone2
 */
public class GiselleVonHellmann extends SiegeGuardMystic {
    private static final SkillEntry DAMAGE_SKILL = SkillTable.getInstance().getSkillEntry(5003, 1);

    private static final Zone ZONE_1 = ReflectionUtils.getZone("lidia_zone1");
    private static final Zone ZONE_2 = ReflectionUtils.getZone("lidia_zone2");

    public GiselleVonHellmann(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        ZONE_1.setActive(true);
        ZONE_2.setActive(true);

        ChatUtils.shout(getActor(), NpcString.ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        super.onEvtDead(killer);

        ZONE_1.setActive(false);
        ZONE_2.setActive(false);

        ChatUtils.shout(actor, NpcString.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL);

        ClanHallSiegeEvent siegeEvent = actor.getEvent(ClanHallSiegeEvent.class);
        if (siegeEvent == null) {
            return;
        }
        SpawnExObject spawnExObject = siegeEvent.getFirstObject(ClanHallSiegeEvent.BOSS);
        NpcInstance lidiaNpc = spawnExObject.getFirstSpawned();

        if (lidiaNpc.getCurrentHpRatio() == 1.) {
            lidiaNpc.setCurrentHp(lidiaNpc.getMaxHp() / 2, true);
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        super.onEvtAttacked(attacker, skill, damage);

        if (PositionUtils.calculateDistance(attacker, actor, false) > 300. && Rnd.chance(0.13)) {
            addTaskCast(attacker, DAMAGE_SKILL);
        }
    }
}
