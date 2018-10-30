package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Монстр Guzen в лабиритне Стальной Цитадели после смерти саммонит нпц Kendal
 *
 * @author VAVAN
 */
public class Guzen extends Fighter {
    private static final int Kendal = 32301;

    public Guzen(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcUtils.spawnSingle(Kendal, new Location(16104, 278136, -9718, 16384), 600000L);

        super.onEvtDead(killer);
    }
} 