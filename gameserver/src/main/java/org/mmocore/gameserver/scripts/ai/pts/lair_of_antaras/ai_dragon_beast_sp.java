package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.PlayerGroup;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList.HateInfo;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

import java.util.Map;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dragon_beast_sp extends Fighter {
    private static int i_ai4;

    public ai_dragon_beast_sp(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        i_ai4 = 0;
        AddTimerEx(1000, 10700000L);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() == null || getActor().isDead()) {
            return;
        }

        if (timer_id == 1000) {
            i_ai4 = 0;
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        i_ai4++;
        if (i_ai4 == 2) {
            for (final NpcInstance npc : getActor().getAroundNpc(5000, 1000)) {
                if (!npc.isDead() && npc.getNpcId() == 25732) {
                    npc.setNpcState(2);
                    npc.decayOrDelete();
                }
            }
            final NpcInstance npc = NpcUtils.spawnSingle(32886, getActor().getLoc(), 3600000L); // corpse_dragon_beast
            if (killer != null && killer.isPlayable()) {
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
            i_ai4 = 0;
        }
        super.onEvtDead(killer);
    }
}