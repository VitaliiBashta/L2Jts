package org.mmocore.gameserver.scripts.ai;


import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.Earthquake;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Creature;

import java.util.List;

/**
 * AI каменной статуи Байума.<br>
 * Раз в 15 минут устраивает замлятрясение а ТОИ.
 *
 * @author SYS
 */
public class BaiumNpc extends DefaultAI {
    private static final int BAIUM_EARTHQUAKE_TIMEOUT = 1000 * 60 * 15; // 15 мин
    private long _wait_timeout = 0;

    public BaiumNpc(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        // Пора устроить землятрясение
        if (_wait_timeout < System.currentTimeMillis()) {
            _wait_timeout = System.currentTimeMillis() + BAIUM_EARTHQUAKE_TIMEOUT;
            L2GameServerPacket eq = new Earthquake(actor.getLoc(), 40, 10);
            List<Creature> chars = actor.getAroundCharacters(5000, 10000);
            for (Creature character : chars) {
                if (character.isPlayer()) {
                    character.sendPacket(eq);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}