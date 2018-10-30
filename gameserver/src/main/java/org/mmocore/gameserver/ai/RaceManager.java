package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.RaceManagerInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MonRaceInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RaceManager extends DefaultAI {
    private final AtomicBoolean thinking = new AtomicBoolean(false); // to prevent recursive thinking
    private List<Player> knownPlayers = new ArrayList<>();

    public RaceManager(final NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 5000;
    }

    @Override
    public void runImpl() {
        onEvtThink();
    }

    @Override
    protected void onEvtThink() {
        final RaceManagerInstance actor = getActor();
        if (actor == null) {
            return;
        }

        final MonRaceInfo packet = actor.getPacket();
        if (packet == null) {
            return;
        }

        if (!thinking.compareAndSet(false, true))
            return;

        try {
            final List<Player> newPlayers = new ArrayList<>();
            for (final Player player : World.getAroundObservers(actor)) {
                newPlayers.add(player);
                if (!knownPlayers.contains(player)) {
                    player.sendPacket(packet);
                }
                knownPlayers.remove(player);
            }

            knownPlayers.forEach(actor::removeKnownPlayer);

            knownPlayers = newPlayers;
        } finally {
            // Stop thinking action
            thinking.set(false);
        }
    }

    @Override
    public RaceManagerInstance getActor() {
        return (RaceManagerInstance) super.getActor();
    }
}