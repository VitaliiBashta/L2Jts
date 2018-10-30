package org.mmocore.gameserver.scripts.ai.fantasy_island;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound.Type;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

/**
 * @author PaInKiLlEr - AI для Music Box (32437). - Проигровает музыку. - AI проверен и работает.
 */
public class MusicBox extends CharacterAI {
    public MusicBox(final NpcInstance actor) {
        super(actor);
        ThreadPoolManager.getInstance().schedule(new ScheduleMusic(), 1000);
    }

    private class ScheduleMusic extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = (NpcInstance) getActor();
            for (final Player player : World.getAroundPlayers(actor, 5000, 5000))
                player.broadcastPacket(new PlaySound(Type.MUSIC, "TP04_F"));
        }
    }
}