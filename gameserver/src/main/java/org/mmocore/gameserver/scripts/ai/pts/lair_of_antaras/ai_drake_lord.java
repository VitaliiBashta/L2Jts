package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.PlayerGroup;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList.HateInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

import java.util.Map;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_drake_lord extends ai_antaras_cave_raid_basic {
    private static final int USE_SKILL03_TIME = 4000;
    private static final int USE_SKILL03A_TIME = 4100;
    private int i_ai0;

    public ai_drake_lord(NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() == null || getActor().isDead()) {
            return;
        }

        if (timer_id == USE_SKILL03_TIME) {
            if (i_ai0 == 1) {
                for (final Creature creature : getActor().getAggroList().getHateList(1000)) {
                    if (!creature.isDead() && creature.isPlayable()) {
                        final Location loc = Location.findPointToStay(Location.coordsRandomize(getActor().getLoc(), 100, 300), 300, getActor().getGeoIndex());
                        creature.setTarget(null);
                        creature.teleToLocation(loc);
                    }
                }
                getActor().getAggroList().clear(true);
                getActor().setAggressionTarget(null);
            }
        } else if (timer_id == USE_SKILL03A_TIME) {
            i_ai0 = 0;
        }
        super.onEvtTimerFiredEx(timer_id, arg1, arg2);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (i_ai0 == 0) {
            i_ai0 = 1;
            AddTimerEx(USE_SKILL03_TIME, (120 + Rnd.get(60)) * 1000);
            AddTimerEx(USE_SKILL03A_TIME, 300000L);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final NpcInstance npc = NpcUtils.spawnSingle(32884, getActor().getLoc(), 3600000L);
        if (killer != null) {
            npc.setTitleNpcString(NpcString.KILLED_BY_S1);
            npc.setTitle(killer.getPlayer().getName());
            npc.broadcastCharInfo();
            final Player player = killer.getPlayer();
            if (player != null) {
                final PlayerGroup pg = player.getPlayerGroup();
                if (pg != null) {
                    QuestState qs;
                    final Map<Playable, HateInfo> aggro = getActor().getAggroList().getPlayableMap();
                    for (Player pl : pg) {
                        if (pl != null && !pl.isDead() && aggro.containsKey(pl) && (getActor().isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) || getActor().isInRangeZ(killer, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE))) {
                            qs = pl.getQuestState(456);
                            if (qs != null && qs.getCond() == 1) {
                                qs.setMemoState("RaidKilled", npc.getObjectId());
                            }
                        }
                    }
                }
            }
        }
        super.onEvtDead(killer);
        getActor().endDecayTask();
    }
}