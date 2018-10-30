package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.ai.pts.hellbound.ai_quarry_slave;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

public final class QuarrySlaveInstance extends NpcInstance {
    private int sm_flag;
    private Creature i_ai4;

    public QuarrySlaveInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        setUndying(false);
        setFollowTarget(null);
        sm_flag = 0;
        i_ai4 = null;
    }

    @Override
    protected void onDeath(Creature killer) {
        if (killer != null && (killer.isSummon() || killer.isPet() || killer.isMonster())) {
            final int i0 = HellboundManager.getHellboundLevel();
            final long i1 = HellboundManager.getConfidence();
            if (i0 >= 1 && i0 <= 100 && i1 >= 100050 && i1 <= 1000000000) {
                HellboundManager.reduceConfidence(20);
            }
        }
        sm_flag = 0;
        i_ai4 = null;
        setFollowTarget(null);
        super.onDeath(killer);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();
        if (i0 == 5) {
            showChatWindow(talker, "pts/hellbound/quarry_slave001.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/quarry_slave002.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1006&")) {
            if (command.endsWith("reply=1")) {
                if (sm_flag == 0) {
                    final int i0 = HellboundManager.getHellboundLevel();
                    if (i0 == 5) {
                        showChatWindow(player, "pts/hellbound/quarry_slave002a.htm");
                        sm_flag = 1;
                        i_ai4 = player;
                        setRHandId(9136);
                        broadcastCharInfo();
                        ((ai_quarry_slave) getAI()).setFollowMode(player);
                        ThreadPoolManager.getInstance().schedule(new TimerId(1001, this), 1000L);
                        ThreadPoolManager.getInstance().schedule(new TimerId(1002, this), 15 * 60 * 1000L);
                    } else {
                        showChatWindow(player, "pts/hellbound/quarry_slave002.htm");
                    }
                } else if (sm_flag == 1) {
                    if (i_ai4 == null) {
                        showChatWindow(player, "pts/hellbound/quarry_slave002a.htm");
                        sm_flag = 1;
                        i_ai4 = player;
                        setRHandId(9136);
                        broadcastCharInfo();
                        ((ai_quarry_slave) getAI()).setFollowMode(player);
                        ThreadPoolManager.getInstance().schedule(new TimerId(1001, this), 1000L);
                    } else {

                    }
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return attacker.isMonster();
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    private class TimerId extends RunnableImpl {
        final int timer_id;
        final NpcInstance actor;

        public TimerId(final int timer_id, final NpcInstance actor) {
            this.timer_id = timer_id;
            this.actor = actor;
        }

        @Override
        public void runImpl() {
            if (actor != null && !actor.isDead()) {
                switch (timer_id) {
                    case 1001:
                        ((ai_quarry_slave) actor.getAI()).broadcastSE(1000001);
                        if (i_ai4 != null) {
                            ThreadPoolManager.getInstance().schedule(new TimerId(1001, actor), 1000L);
                        }
                        break;
                    case 1002:
                        if (actor.isInRangeZ(actor.getSpawnedLoc(), 500)) {
                            sm_flag = 0;
                            i_ai4 = null;
                            actor.setFollowTarget(null);
                            actor.setRHandId(0);
                            actor.teleToLocation(actor.getSpawnedLoc());
                        } else {
                            ChatUtils.say(actor, NpcString.HUN_HUNGRY);
                            actor.suicide(null);
                        }
                        break;
                }
            }
        }
    }
}