package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.BossConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.BelethManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Java-man
 */
public class NaiaCubeInstance extends NpcInstance {
    final ScheduledFuture<?> _despawnTask;

    public NaiaCubeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _despawnTask = ThreadPoolManager.getInstance().schedule(new DespawnTask(this), 120000L);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("tryenter")) {
            if (!player.isInParty() || !player.getParty().isInCommandChannel()
                    || !player.getParty().getCommandChannel().getGroupLeader().equals(player)
                    || player.getParty().getCommandChannel().getMemberCount() < BossConfig.BelethMinCcSize && !(player.isGM() && player.isDebug())
                    || player.getParty().getCommandChannel().getMemberCount() > 45) {
                player.sendPacket(new HtmlMessage(this).setFile("default/32376-1.htm"));
                return;
            }
            if (!BelethManager.checkBossSpawnCond()) {
                player.sendPacket(new HtmlMessage(this).setFile("default/32376-2.htm"));
                return;
            }

            if (BelethManager.isBelethAlive() || BelethManager.getCC() != null) {
                player.sendPacket(new HtmlMessage(this).setFile("default/32376-3.htm"));
                return;
            }

            BelethManager.setCC(player.getParty().getCommandChannel());
            for (Player m : player.getParty().getCommandChannel()) {
                if (this.getDistance(m) > 3000)
                    continue;
                m.teleToLocation(16328, 209557, -9352, 0);
            }
            BelethManager.startSpawn();
        } else
            super.onBypassFeedback(player, command);
    }

    private class DespawnTask extends RunnableImpl {
        private final NpcInstance _npc;

        public DespawnTask(NpcInstance npc) {
            _npc = npc;
        }

        @Override
        public void runImpl() {
            _npc.deleteMe();
            ReflectionUtils.getDoor(18250025).openMe(); // Epidos Door
        }
    }
}